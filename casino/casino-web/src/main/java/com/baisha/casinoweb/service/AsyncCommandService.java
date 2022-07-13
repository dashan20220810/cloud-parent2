package com.baisha.casinoweb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baisha.casinoweb.util.constant.Constants;
import com.baisha.modulecommon.BigDecimalConstants;
import com.baisha.modulecommon.enums.OpenCardConvertEnum;
import com.baisha.modulecommon.vo.NewGameInfo;
import com.baisha.modulecommon.vo.mq.OpenVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baisha.casinoweb.business.DeskBusiness;
import com.baisha.casinoweb.business.GameInfoBusiness;
import com.baisha.casinoweb.model.vo.response.BetResponseVO;
import com.baisha.casinoweb.model.vo.response.DeskVO;
import com.baisha.casinoweb.model.vo.response.TgChatVO;
import com.baisha.casinoweb.util.ValidateUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.core.dto.SysTelegramDto;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import com.beust.jcommander.internal.Maps;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncCommandService {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;

	@Value("${project.server-url.video-server-domain}")
	private String videoServerDomain;

    @Value("${project.game.count-down-seconds}")
    private Integer gameCountDownSeconds;

    @Value("${project.game.settle-buffer-time-seconds}")
    private Integer gameSettleBufferTimeSeconds;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private RedisUtil redisUtil;

	@Autowired
	private RedissonClient redissonClient;

    @Autowired
    private TelegramService telegramService;
    
    @Autowired
    private GameInfoBusiness gameInfoBusiness;
    
    @Autowired
    private DeskBusiness deskBusiness;
    
    @Autowired
    private AsyncApiService asyncApiService;

    /**
     * 开新局
     * @param deskCode	桌台号
     * @return
     */
    @Async
    public Future<Boolean> openNewGame ( Long deskId, String deskCode, String newActive ) {

    	log.info("\r\n================= 开新局");
    	String action = "开新局";
    	SysTelegramDto sysTg = telegramService.getSysTelegram();
		// 开始新局图片
//    	String openNewGameUrl = sysTg.getStartBetPicUrl();
    	
    	GameInfo gameInfo = new GameInfo();
    	gameInfo.setCurrentActive(newActive);
    	gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中

		log.info("局号、桌台id、{}, {}", newActive, deskId);
		String result = HttpClient4Util.doGet(
				telegramServerDomain + RequestPathEnum.TG_GET_GROUP_ID_LIST.getApiName() + "?tableId=" +deskId);
		
		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return CompletableFuture.completedFuture(false);
		}

		JSONObject groupListJson = JSONObject.parseObject(result);
		List<TgChatVO> groupJsonList = JSONObject.parseObject(groupListJson.getString("data"), new TypeReference<List<TgChatVO>>(){});
		List<Long> groupIdList = new ArrayList<>();
		
		for ( TgChatVO vo : groupJsonList ) {
			groupIdList.add(vo.getChatId());
		}
		gameInfo.initTgGRoupMap(groupIdList);
		log.info("=======开局 gameInfo: {}", gameInfo);
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);
    	
    	// 预存开牌资料
		// 准备桌台、tg群资料，用来初始game info
		Map<String, Object> params = new HashMap<>();
		params.put("noActive", newActive);
		params.put("tableId", deskId);
		result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.BET_RESULT_ADD.getApiName(),
				params);

		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return CompletableFuture.completedFuture(false);
		}
    	
    	// call telegram server
    	params = new HashMap<>();
		params.put("bureauNum", newActive);
		params.put("tableId", deskId);
		params.put("countdownAddress", sysTg.getSeventySecondsUrl());

		log.info("局号、桌台id {}, {}", newActive, deskId);
		result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
				params);

		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return CompletableFuture.completedFuture(false);
		}

    	log.info("开新局 成功");
		return CompletableFuture.completedFuture(true);
    }
    
    /**
     * TODO 切成两个thread，一个倒数，另一个封盘(stopping status在此set)
     * @param deskCode 桌台code
     * @param noActive 游戏局号
	 * @param gameNo 荷官端游戏局号
     * @return 下注倒计时结果
     */
    @Async
    public Future<Boolean> betting ( final String deskCode, final Integer gameNo, final String noActive) {
    	
    	log.info("\r\n================= 下注中 倒数计时");

		RMapCache<String, NewGameInfo> map = redissonClient.getMapCache(RedisKeyConstants.SYS_GAME_TIME);
		NewGameInfo newGameInfo = map.get(deskCode + "_" + gameNo);
		log.info("\r\n================= newGameInfo : {}", newGameInfo);
//    	Date beginTime = newGameInfo.getBeginTime();
    	Date endTime = newGameInfo.getEndTime();

    	Date now = new Date();
		if(endTime.after(now)){
			int timeDiff = Integer.parseInt(String.valueOf((endTime.getTime()
					- now.getTime()) / BigDecimalConstants.THOUSAND.intValue()));
			try {
				CountDown(noActive, timeDiff);
			} catch (InterruptedException e) {
				log.error("倒计时计算发生异常", e);
			}
		}

//		while (endTime.after(now)) {
//			Long timeDiff = (now.getTime() - beginTime.getTime());
//			if ( timeDiff%10000 < 150 ) {
//				log.info("下注中 计时 {}秒", timeDiff/1000);
//			}
//
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				log.error("下注中 计时 失败", e);
//			}
//			now = new Date();
//		}
    	gameInfoBusiness.closeGame(deskCode);
    	
		return CompletableFuture.completedFuture(true);
    }

	public void CountDown(String noActive, int limitSec) throws InterruptedException{
		log.info("游戏号:{}, 起始剩余秒数: {}", noActive, limitSec);
		while(limitSec > BigDecimal.ZERO.intValue()){
			--limitSec;
			if(limitSec % BigDecimal.TEN.intValue() == BigDecimal.ZERO.intValue()){
				log.info("游戏号:{}, 剩余秒数: {}", noActive, limitSec);
			}
			if(limitSec == BigDecimal.ZERO.intValue()){
				log.info("游戏号:{} 倒计时完成", noActive);
				break;
			}
			TimeUnit.SECONDS.sleep(BigDecimalConstants.ONE.intValue());
		}
	}
    
    @Async
    public void open (final OpenVO openVO) {

		final String dealerIp = openVO.getDealerIp();
		final String consequences = openVO.getConsequences();
		final String openingTime = openVO.getEndTime();
    	String action = "开牌";
    	DeskVO desk = deskBusiness.queryDeskByIp(dealerIp);
    	if ( desk==null ) {
    		log.warn("开牌 失败, 查无桌台");
    		return;
    	}

		// 获取开牌结果
		String openCardResult = OpenCardConvertEnum.getAllOpenCardResult(consequences);

    	Long deskId = desk.getId();
    	String deskCode = desk.getDeskCode();
		String closeUpVideoSteam = desk.getVideoAddress();
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	
		redisUtil.hset(RedisKeyConstants.SYS_GAME_RESULT, gameInfo.getCurrentActive(), openCardResult);
		redisUtil.hset(RedisKeyConstants.SYS_GAME_DESK, gameInfo.getCurrentActive(), deskCode);

    	// 预存开牌资料
		Map<String, Object> params = new HashMap<>();
		params.put("noActive", gameInfo.getCurrentActive());
		params.put("tableId", deskId);
		params.put("awardOption", openCardResult);
		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.BET_RESULT_UPDATE.getApiName(),
				params);

		if (!ValidateUtil.checkHttpResponse(action, result)) {
    		return;
		}

        String settlement = JSONObject.toJSONString(BetSettleVO.builder().noActive(gameInfo.getCurrentActive())
				.awardOption(openCardResult).build());
        rabbitTemplate.convertAndSend(MqConstants.BET_SETTLEMENT, settlement);

		// 获取荷官开始时间unix时间戳
		String qTime = String.valueOf(DateUtil.parse(openingTime).getTime() / 1000);
		//发送视频地址给TG
		sendVideoAddressToTg(gameInfo.getCurrentActive(), closeUpVideoSteam, qTime, action, desk);
    }

//	private String getAwardOption(final String[] awardOption) {
//		switch(awardOption){
//			case "0" : return TgBaccRuleEnum.SS2.getCode();
//			case "1" : return TgBaccRuleEnum.Z.getCode();
//			case "2" : return TgBaccRuleEnum.H.getCode();
//			case "3" : return TgBaccRuleEnum.X.getCode();
//			case "4" : return TgBaccRuleEnum.ZD.getCode();
//			case "5" : return TgBaccRuleEnum.XD.getCode();
//			case "6" : return TgBaccRuleEnum.SS3.getCode();
//			default: log.error("没有该开牌类型"); break;
//		}
//		return null;
//	}

	private void sendVideoAddressToTg(
			final String currentActive, final String closeUpVideoSteam,
			final String qTime, final String action,
			final DeskVO deskVO) {
		// 获取视频流
		Map<String, Object> gameVideoParam = Maps.newHashMap();
		gameVideoParam.put("period", currentActive);
		gameVideoParam.put("rtmpurl", BigDecimalConstants.ONE.toString());
		gameVideoParam.put("qtime", qTime);

		String result = HttpClient4Util.doPost(
				videoServerDomain + RequestPathEnum.VIDEO_SNAP.getApiName(),
				gameVideoParam);
		if (StringUtils.isEmpty((String)JSONObject.parseObject(result).get("model"))) {
			return;
		}

		SysTelegramDto sysTg = telegramService.getSysTelegram();
		// 开牌 5 request parameter
		Map<String, Object> params = Maps.newHashMap();
		params.put("openCardAddress", sysTg.getOpenCardUrl());
		params.put("tableId", deskVO.getId());
		params.put("frontAddress", deskVO.getVideoAddress()); // TODO for test
		params.put("lookDownAddress", deskVO.getNearVideoAddress()); // TODO for test
		params.put("videoResultAddress", videoServerDomain + Constants.IMAGE + qTime + Constants.FLV); // TODO for test
		params.put("picRoadAddress", videoServerDomain + Constants.IMAGE + qTime + Constants.JPEG); // TODO for test

		result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN.getApiName(),
				params);
		ValidateUtil.checkHttpResponse(action, result);
	}

	@Async
    public void settlement (final SettleFinishVO settleFinishVO) {

		final String dealerIp = settleFinishVO.getDealerIp();
		final String consequences = settleFinishVO.getConsequences();
		final Integer gameNo = settleFinishVO.getGameNo();
		DeskVO desk = deskBusiness.queryDeskByIp(dealerIp);

		final String deskCode = desk.getDeskCode();
		RMap<String, NewGameInfo> map = redissonClient.getMap(RedisKeyConstants.SYS_GAME_TIME);
		String noActive = map.get(deskCode + "_" + gameNo).getNoActive();
    	String action = "结算";
    	Map<String, Object> params = new HashMap<>();
		params.put("noActive", noActive);

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_SETTLEMENT.getApiName(),
				params);
		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return;
		}

		// 获取开牌结果
		String openCardResult = OpenCardConvertEnum.getAllOpenCardResult(consequences);

		JSONObject betJson = JSONObject.parseObject(result);
		JSONArray betArray = betJson.getJSONArray("data");
		Map<Long, List<BetResponseVO>> betMap = null;
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
		
		if ( betArray!=null && betArray.size()>0 ) {
			List<BetResponseVO> betList = betArray.toJavaList(BetResponseVO.class);
			betMap = betList.stream().sorted(Comparator.comparingDouble(bet ->
					(-bet.getWinAmount().doubleValue()))).collect(Collectors.groupingBy(BetResponseVO::getTgChatId));
		}
		
		Set<Long> allGroupIdSet = gameInfo.getTgGroupMap().keySet();
		Set<Long> groupIdSet = new HashSet<>();

		Map<Long, List<BetHistory>> top20WinUsers = new HashMap<>();
		if ( betMap!=null ) {
			groupIdSet = betMap.keySet();
			for ( Long tgGroupId: betMap.keySet() ) {
				List<BetResponseVO> list = betMap.get(tgGroupId);
				Map<String, Double> sumMap = list.stream().map(betRes -> {
					BetHistory betHistory = new BetHistory();
					betHistory.setUsername(betRes.getNickName());
					betHistory.setWinAmount(betRes.getWinAmount().doubleValue());
					return betHistory;
				}).collect(Collectors.groupingBy(BetHistory::getUsername, Collectors.summingDouble(BetHistory::getWinAmount))); //.limit(20)

				List<BetHistory> betHistoryList = new ArrayList<>();
				for ( String nickName: sumMap.keySet() ) {
					BetHistory betHistory = new BetHistory();
					betHistory.setUsername(nickName);
					betHistory.setWinAmount(sumMap.get(nickName));
					betHistoryList.add(betHistory);
				}
				
				betHistoryList = betHistoryList.stream().sorted(Comparator
						.comparingDouble(bet -> (-bet.getWinAmount()))).collect(Collectors.toList());
				top20WinUsers.put(tgGroupId, betHistoryList.subList(0, Math.min(betHistoryList.size(), 20)));
			}
		}
    	
		allGroupIdSet.removeAll(groupIdSet);
		for ( Long tgGroupId: allGroupIdSet ) {
			top20WinUsers.put(tgGroupId, new ArrayList<>());
		}
		asyncApiService.tgSettlement(noActive, openCardResult, top20WinUsers);
    }
    
    @Data
    public class BetHistory {
    	
    	private String username;
    	private Double winAmount;
    }
    
}
