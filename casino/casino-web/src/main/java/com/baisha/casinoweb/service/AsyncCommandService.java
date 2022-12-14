package com.baisha.casinoweb.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.baisha.modulecommon.BigDecimalConstants;
import com.baisha.modulecommon.enums.OpenCardConvertSettleEnum;
import com.baisha.modulecommon.enums.OpenCardConvertTgEnum;
import com.baisha.modulecommon.vo.GameDesk;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.TgGameInfo;
import com.baisha.modulecommon.vo.mq.OpenVO;
import com.baisha.modulecommon.vo.mq.PairImageVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.business.DeskBusiness;
import com.baisha.casinoweb.business.GameInfoBusiness;
import com.baisha.casinoweb.model.vo.response.BetResponseVO;
import com.baisha.casinoweb.model.vo.response.DeskVO;
import com.baisha.casinoweb.util.ValidateUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.dto.SysTelegramDto;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import com.beust.jcommander.internal.Maps;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class AsyncCommandService {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;

	@Value("${project.server-url.video-post-server-domain}")
	private String videoPostServerDomain;

	@Value("${project.server-url.upload-server-domain}")
	private String uploadServerDomain;

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

	@Autowired
	private OpenCardVideoService openCardVideoService;

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
    	
    	// 预存开牌资料
		// 准备桌台、tg群资料，用来初始game info
		Map<String, Object> params = new HashMap<>();
		params.put("noActive", newActive);
		params.put("tableId", deskId);
		String result = HttpClient4Util.doPost(
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
	 *
	 * @param desk     桌台对象
	 * @param noActive 游戏局号
	 * @return 下注倒计时结果
	 */
    @Async
    public Future<Boolean> betting (final DeskVO desk, final String noActive) {
    	
    	log.info("\r\n================= 下注中 倒数计时");
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
		log.info("\r\n================= gameInfo : {}", gameInfo);
//    	Date beginTime = newGameInfo.getBeginTime();
    	Date endTime = gameInfo.getEndTime();
		Date now = new Date();
//		log.info("\n================= 局号 {} ,当前时间 {}, 结束时间 {} ", noActive, now, endTime);
		if(endTime.after(now)){
			int timeDiff = Integer.parseInt(String.valueOf((endTime.getTime()
					- now.getTime()) / BigDecimalConstants.THOUSAND.intValue()));
			try {
				CountDown(noActive, timeDiff);
			} catch (InterruptedException e) {
				log.error("倒计时计算发生异常", e);
			}
		}

		// 视频截留桌台流
		String streamVideoCode = desk.getCloseVideoAddress();
		//开始发送截屏数据流
		sendVideoStartScreenRecording(noActive, streamVideoCode);

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
		// 给tg发送封盘线
    	gameInfoBusiness.closeGame(noActive);
		// 给tg发送开牌图片和近景远景视频
		SysTelegramDto sysTg = telegramService.getSysTelegram();
		sendVideoAddressToTg("开牌结果", sysTg.getOpenCardUrl(),
				desk.getId(), desk.getNearVideoAddress(),
				desk.getVideoAddress(), null, null, null);
		return CompletableFuture.completedFuture(true);
    }

	/**
	 * 视频流开始录屏
	 * @param noActive 局号
	 * @param streamVideoCode 视频流code
	 */
	private void sendVideoStartScreenRecording(final String noActive, final String streamVideoCode) {

		String qTime = String.valueOf(new Date().getTime() / 1000);
		// 获取视频流
		Map<String, Object> gameVideoParam = Maps.newHashMap();
		gameVideoParam.put("period", noActive);
		gameVideoParam.put("rtmpurl", streamVideoCode);
		gameVideoParam.put("qtime", qTime);

		HttpClient4Util.doPost(
				videoPostServerDomain + RequestPathEnum.VIDEO_SNAP.getApiName(),
				gameVideoParam);
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

		final String consequences = openVO.getConsequences();
		final String noActive = openVO.getGameNo();
//		final String openingTime = openVO.getEndTime();
    	String action = "开牌";
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
    	if ( gameInfo==null ) {
    		log.warn("开牌 失败, 查无局号");
    		return;
    	}

		// 获取开牌结果(结算需要的结果)
		String openCardResult = OpenCardConvertSettleEnum.getAllOpenCardResult(consequences);

    	Long deskId = gameInfo.getDeskId();

		gameInfoBusiness.setGameResult(noActive, openCardResult);

    	// 预存开牌资料
		Map<String, Object> params = new HashMap<>();
		params.put("noActive", noActive);
		params.put("tableId", deskId);
		params.put("awardOption", openCardResult);
		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.BET_RESULT_UPDATE.getApiName(),
				params);

		if (!ValidateUtil.checkHttpResponse(action, result)) {
    		return;
		}

		// 截屏结束时间发送
		sendVideoEndScreenRecording(noActive, gameInfo.getStreamVideoCode());

        String settlement = JSONObject.toJSONString(BetSettleVO.builder().noActive(noActive)
				.awardOption(openCardResult).build());
		// 结算MQ
        rabbitTemplate.convertAndSend(MqConstants.BET_SETTLEMENT, settlement);
    }

	/**
	 * 视频流截屏结束并发送TG端
	 * @param action 功能标识
	 * @param deskId 当前桌子
	 * @param nearVideoAddress 近景视频地址
	 * @param videoAddress 远景视频地址
	 * @param videoResultAddress 视频截图地址
	 * @param picRoadAddress 图片截图地址
	 */
	private void sendVideoAddressToTg(
			final String action, final String getOpenCardUrl,
			final Long deskId, final String nearVideoAddress,
			final String videoAddress, final String videoResultAddress,
			final String picRoadAddress, final String recordingChartAddress) {


		// 开牌 5 request parameter
		Map<String, Object> params = Maps.newHashMap();
		params.put("openCardAddress", getOpenCardUrl);
		params.put("tableId", deskId);
		params.put("frontAddress", nearVideoAddress); // TODO for test
		params.put("lookDownAddress", videoAddress); // TODO for test
		params.put("videoResultAddress", videoResultAddress); // TODO for test
		params.put("picRoadAddress", recordingChartAddress); // TODO for test
		params.put("picResultAddress", picRoadAddress);

		String result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN.getApiName(),
				params);
		ValidateUtil.checkHttpResponse(action, result);
	}

	/**
	 * 获取视频流地址
	 *
	 * @param noActive
	 * @param streamVideoCode
	 */
	public void sendVideoEndScreenRecording(final String noActive, final String streamVideoCode){
		// 获取视频流
		Map<String, Object> gameVideoParam = Maps.newHashMap();
		gameVideoParam.put("period", noActive);
		gameVideoParam.put("rtmpurl", streamVideoCode);

		HttpClient4Util.doPost(
				videoPostServerDomain + RequestPathEnum.VIDEO_STOP.getApiName(),
				gameVideoParam);

	}


	@Async
    public void settlement (final SettleFinishVO settleFinishVO) {

		final String consequences = settleFinishVO.getConsequences();
		final String noActive = settleFinishVO.getGameNo();
		final GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
		log.info("开牌结果 gameInfo :{}", gameInfo);

    	String action = "结算";
    	Map<String, Object> params = new HashMap<>();
		params.put("noActive", noActive);

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_SETTLEMENT.getApiName(),
				params);
		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return;
		}

		// 获取开牌结果(转换成tg端结果)
		String openCardResult = OpenCardConvertTgEnum.getAllOpenCardTgResult(consequences);
		// 开牌结果不符合规则直接返回
		if(StringUtils.isEmpty(openCardResult)){
			return ;
		}

		JSONObject betJson = JSONObject.parseObject(result);
		JSONArray betArray = betJson.getJSONArray("data");
		Map<Long, List<BetResponseVO>> betMap = null;
		TgGameInfo tgGameInfo = gameInfoBusiness.getTgGameInfo(noActive);
		
		if ( betArray!=null && betArray.size()>0 ) {
			List<BetResponseVO> betList = betArray.toJavaList(BetResponseVO.class);
			betMap = betList.stream().sorted(Comparator.comparingDouble(bet ->
					(-bet.getWinAmount().doubleValue()))).collect(Collectors.groupingBy(BetResponseVO::getTgChatId));
		}
		
		Set<Long> allGroupIdSet = tgGameInfo.getTgGroupMap().keySet();
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
				betHistoryList.forEach(obj -> obj.setWinStrAmount(BigDecimal.valueOf(obj.getWinAmount())
						.stripTrailingZeros().toPlainString()));
				top20WinUsers.put(tgGroupId, betHistoryList.subList(0, Math.min(betHistoryList.size(), 20)));
			}
		}
    	
		allGroupIdSet.removeAll(groupIdSet);
		for ( Long tgGroupId: allGroupIdSet ) {
			top20WinUsers.put(tgGroupId, new ArrayList<>());
		}
		//通知tg结算结果
		asyncApiService.tgSettlement(noActive, openCardResult, top20WinUsers);
    }

	public void pairImage(PairImageVO pairImageVO) {
		final String dealerIp = pairImageVO.getDealerIp();
		final String noActive = pairImageVO.getGameNo();

		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
		// 开牌图片地址
		String picAddress = gameInfo.getPicAddress();
		// 开牌视频地址
		String videoAddress = gameInfo.getVideoAddress();
		InputStream inputStream = new ByteArrayInputStream(pairImageVO.getImageContent());
		String result;
		try {
			MultipartFile file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
			String url = uploadServerDomain + RequestPathEnum.UPLOAD_PIC.getApiName() + "/telegram/jpeg";
			result = HttpClient4Util.postMultipartFile
					(url, file);
		} catch (IOException e) {
			log.error("上传文件发生异常", e);
			throw new RuntimeException(e);
		}
		if ( !ValidateUtil.checkHttpResponse("录单图上传", result) ) {
			return;
		}
		JSONObject json = JSONObject.parseObject(result);
		JSONObject resultJson = json.getJSONObject("data");
		String recordingChartAddress = resultJson.getString("url");
		gameInfo.setRecordingChartAddress(recordingChartAddress);
		gameInfoBusiness.setGameInfo(noActive, gameInfo);
		//发送视频地址给TG
		sendVideoAddressToTg("截屏", null, gameInfo.getDeskId(), null, null,
				videoAddress, picAddress, recordingChartAddress);
		openCardVideoService.saveOpenCardVideoAndPic(videoAddress, picAddress, recordingChartAddress, noActive);
	}

	@Data
    public class BetHistory {
    	
    	private String username;
    	private Double winAmount;
		private String winStrAmount;
    }
    
}
