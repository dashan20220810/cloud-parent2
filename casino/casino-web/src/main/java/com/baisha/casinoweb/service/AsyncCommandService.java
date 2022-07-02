package com.baisha.casinoweb.service;

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
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.business.DeskBusiness;
import com.baisha.casinoweb.business.GameInfoBusiness;
import com.baisha.casinoweb.model.vo.response.BetResponseVO;
import com.baisha.casinoweb.util.ValidateUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.core.dto.SysTelegramDto;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncCommandService {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;

    @Value("${project.game.count-down-seconds}")
    private Integer gameCountDownSeconds;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private RedisUtil redisUtil;

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

    	log.info("开新局");
    	String action = "开新局";
    	SysTelegramDto sysTg = telegramService.getSysTelegram();
    	String openNewGameUrl = sysTg.getStartBetPicUrl();
    	
    	// 准备桌台、tg群资料，用来初始game info
    	Map<String, Object> params = new HashMap<>();
    	Date beginTime = new Date();
    	Date endTime = DateUtils.addSeconds(beginTime, gameCountDownSeconds);
//    	Date endTime = DateUtils.addSeconds(beginTime, 20); // TODO
    	GameInfo gameInfo = new GameInfo();
    	gameInfo.setCurrentActive(newActive);
    	gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中
    	gameInfo.setBeginTime(beginTime);
    	gameInfo.setEndTime(endTime);

		log.info("局号、桌台id、新局图片url: {}, {}, {}", newActive, deskId, openNewGameUrl);
		String result = HttpClient4Util.doGet(
				telegramServerDomain + RequestPathEnum.TG_GET_GROUP_ID_LIST.getApiName() + "?tableId=" +deskId);
		
		if ( ValidateUtil.checkHttpResponse(action, result)==false ) {
    		return CompletableFuture.completedFuture(false);
		}
		
		JSONObject groupListJson = JSONObject.parseObject(result);
		JSONArray groupJsonList = groupListJson.getJSONArray("data");
		List<Long> groupIdList = new ArrayList<>();
		
		for ( int index=0; index<groupJsonList.size(); index++ ) {
			JSONObject groupJson = groupJsonList.getJSONObject(index);
			groupIdList.add(groupJson.getLong("chatId"));
		}
		gameInfo.initTgGRoupMap(groupIdList);
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);
    	
    	// 预存开牌资料
    	params = new HashMap<>();
		params.put("noActive", newActive);
		params.put("tableId", deskId);
		result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.BET_RESULT_ADD.getApiName(),
				params);

		if ( ValidateUtil.checkHttpResponse(action, result)==false ) {
    		return CompletableFuture.completedFuture(false);
		}
    	
    	// call telegram server
    	params = new HashMap<>();
		params.put("bureauNum", newActive);
		params.put("tableId", deskId);
		params.put("imageAddress", openNewGameUrl);
		params.put("countdownAddress", sysTg.getSeventySecondsUrl());

		log.info("局号、桌台id、新局图片url: {}, {}, {}", newActive, deskId, openNewGameUrl);
		result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
				params);

		if ( ValidateUtil.checkHttpResponse(action, result)==false ) {
    		return CompletableFuture.completedFuture(false);
		}

    	log.info("开新局 成功");
		return CompletableFuture.completedFuture(true);
    }
    
    /**
     * TODO 切成两个thread，一个倒数，另一个封盘(stopping status在此set)
     * @param deskCode
     * @param newActive
     * @return
     */
    @Async
    public Future<Boolean> betting ( String deskCode, String newActive) {
    	
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	Date beginTime = gameInfo.getBeginTime();
    	Date endTime = gameInfo.getEndTime();

    	log.info("下注中 倒数计时");
    	
    	Date now = new Date();
    	while (endTime.after(now)) {
    		Long timeDiff = (now.getTime() - beginTime.getTime());
    		if ( timeDiff%10000 < 150 ) {
    	    	log.info("下注中 计时 {}秒", timeDiff/1000);
    		}

    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("下注中 计时 失败", e);
			}
    		now = new Date();
    	}

    	gameInfoBusiness.closeGame(deskCode);
    	
    	log.info("下注中 倒数计时 结束");
		return CompletableFuture.completedFuture(true);
    }
    
    @Async
    public void open ( String dealerIp, String awardOption ) {

    	String action = "开牌";
    	JSONObject desk = deskBusiness.queryDeskByIp(dealerIp);
    	if ( desk==null ) {
    		log.warn("开牌 失败, 查无桌台");
    		return;
    	}
    	
    	Long deskId = desk.getLong("id");
    	String deskCode = desk.getString("deskCode");
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	
		redisUtil.hset(RedisKeyConstants.SYS_GAME_RESULT, gameInfo.getCurrentActive(), awardOption);
		redisUtil.hset(RedisKeyConstants.SYS_GAME_DESK, gameInfo.getCurrentActive(), deskCode);

    	// 预存开牌资料
		Map<String, Object> params = new HashMap<>();
		params.put("noActive", gameInfo.getCurrentActive());
		params.put("tableId", deskId);
		params.put("awardOption", awardOption);
		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.BET_RESULT_UPDATE.getApiName(),
				params);

		if ( ValidateUtil.checkHttpResponse(action, result)==false ) {
    		return;
		}

    	SysTelegramDto sysTg = telegramService.getSysTelegram();
        // 开牌 5 request parameter 
    	params = new HashMap<>();
		params.put("openCardAddress", sysTg.getOpenCardUrl());
		params.put("tableId", deskId);
		params.put("frontAddress", "https://www.google.com"); // TODO for test
		params.put("lookDownAddress", "https://tw.yahoo.com"); // TODO for test
		params.put("resultAddress", "http://192.168.26.24:9000/user/kaipaijieguo.png"); // TODO for test
		params.put("roadAddress", "http://192.168.26.24:9000/user/lutu.png"); // TODO for test

		result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN.getApiName(),
				params);
		if ( ValidateUtil.checkHttpResponse(action, result)==false ) {
    		return;
		}

        BetSettleVO vo = BetSettleVO.builder().noActive(gameInfo.getCurrentActive()).awardOption(awardOption).build();
        rabbitTemplate.convertAndSend(MqConstants.BET_SETTLEMENT, vo);
    }
    
    @Async
    public void settlement ( String noActive ) {
    	 
    	String action = "结算";
    	Map<String, Object> params = new HashMap<>();
		params.put("noActive", noActive);

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_SETTLEMENT.getApiName(),
				params);
		if ( ValidateUtil.checkHttpResponse(action, result)==false ) {
    		return;
		}

		JSONObject betJson = JSONObject.parseObject(result);
		JSONArray betArray = betJson.getJSONArray("data");
		Map<Long, List<BetResponseVO>> betMap = null;
		String betResult = (String) redisUtil.hget(RedisKeyConstants.SYS_GAME_RESULT, noActive);
		String deskCode = (String) redisUtil.hget(RedisKeyConstants.SYS_GAME_DESK, noActive);
		String betDisplay = BetOption.getBetOption(betResult).getDisplay();
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
		
		if ( betArray!=null && betArray.size()>0 ) {
			List<BetResponseVO> betList = betArray.toJavaList(BetResponseVO.class);
			betMap = betList.stream().sorted(Comparator.comparingDouble(bet -> { 
				return (bet.getWinAmount().doubleValue());
			})).collect(Collectors.groupingBy(BetResponseVO::getTgChatId));
		}
		
		Set<Long> allGroupIdSet = gameInfo.getTgGroupMap().keySet();
		Set<Long> groupIdSet = new HashSet<>();

		Map<Long, List<Map<String, Object>>> top20WinUsers = new HashMap<>();
		if ( betMap!=null ) {
			groupIdSet = betMap.keySet();
			for ( Long tgGroupId: betMap.keySet() ) {
				List<BetResponseVO> list = betMap.get(tgGroupId);
				List<Map<String, Object>> betHistoryList = list.stream().map(betRes -> {
					Map<String, Object> betHistory = new HashMap<>();
					betHistory.put("username", betRes.getNickName());
					betHistory.put("winAmount", betRes.getTotalAmount());
					return betHistory;
				}).limit(20).collect(Collectors.toList());
				
				top20WinUsers.put(tgGroupId, betHistoryList);
			}
		}
    	
		allGroupIdSet.removeAll(groupIdSet);
		for ( Long tgGroupId: allGroupIdSet ) {
			top20WinUsers.put(tgGroupId, new ArrayList<>());
		}
		asyncApiService.tgSettlement(noActive, betDisplay, top20WinUsers);
    }
    
}