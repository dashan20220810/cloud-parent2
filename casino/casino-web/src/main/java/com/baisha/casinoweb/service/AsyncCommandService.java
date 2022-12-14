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
     * ?????????
     * @param deskCode	?????????
     * @return
     */
    @Async
    public Future<Boolean> openNewGame ( Long deskId, String deskCode, String newActive ) {

    	log.info("\r\n================= ?????????");
    	String action = "?????????";
    	SysTelegramDto sysTg = telegramService.getSysTelegram();
		// ??????????????????
//    	String openNewGameUrl = sysTg.getStartBetPicUrl();
    	
    	// ??????????????????
		// ???????????????tg????????????????????????game info
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

		log.info("???????????????id {}, {}", newActive, deskId);
		result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
				params);

		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return CompletableFuture.completedFuture(false);
		}

    	log.info("????????? ??????");
		return CompletableFuture.completedFuture(true);
    }
    
    /**
	 * TODO ????????????thread?????????????????????????????????(stopping status??????set)
	 *
	 * @param desk     ????????????
	 * @param noActive ????????????
	 * @return ?????????????????????
	 */
    @Async
    public Future<Boolean> betting (final DeskVO desk, final String noActive) {
    	
    	log.info("\r\n================= ????????? ????????????");
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
		log.info("\r\n================= gameInfo : {}", gameInfo);
//    	Date beginTime = newGameInfo.getBeginTime();
    	Date endTime = gameInfo.getEndTime();
		Date now = new Date();
//		log.info("\n================= ?????? {} ,???????????? {}, ???????????? {} ", noActive, now, endTime);
		if(endTime.after(now)){
			int timeDiff = Integer.parseInt(String.valueOf((endTime.getTime()
					- now.getTime()) / BigDecimalConstants.THOUSAND.intValue()));
			try {
				CountDown(noActive, timeDiff);
			} catch (InterruptedException e) {
				log.error("???????????????????????????", e);
			}
		}

		// ?????????????????????
		String streamVideoCode = desk.getCloseVideoAddress();
		//???????????????????????????
		sendVideoStartScreenRecording(noActive, streamVideoCode);

//		while (endTime.after(now)) {
//			Long timeDiff = (now.getTime() - beginTime.getTime());
//			if ( timeDiff%10000 < 150 ) {
//				log.info("????????? ?????? {}???", timeDiff/1000);
//			}
//
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				log.error("????????? ?????? ??????", e);
//			}
//			now = new Date();
//		}
		// ???tg???????????????
    	gameInfoBusiness.closeGame(noActive);
		// ???tg???????????????????????????????????????
		SysTelegramDto sysTg = telegramService.getSysTelegram();
		sendVideoAddressToTg("????????????", sysTg.getOpenCardUrl(),
				desk.getId(), desk.getNearVideoAddress(),
				desk.getVideoAddress(), null, null, null);
		return CompletableFuture.completedFuture(true);
    }

	/**
	 * ?????????????????????
	 * @param noActive ??????
	 * @param streamVideoCode ?????????code
	 */
	private void sendVideoStartScreenRecording(final String noActive, final String streamVideoCode) {

		String qTime = String.valueOf(new Date().getTime() / 1000);
		// ???????????????
		Map<String, Object> gameVideoParam = Maps.newHashMap();
		gameVideoParam.put("period", noActive);
		gameVideoParam.put("rtmpurl", streamVideoCode);
		gameVideoParam.put("qtime", qTime);

		HttpClient4Util.doPost(
				videoPostServerDomain + RequestPathEnum.VIDEO_SNAP.getApiName(),
				gameVideoParam);
	}

	public void CountDown(String noActive, int limitSec) throws InterruptedException{
		log.info("?????????:{}, ??????????????????: {}", noActive, limitSec);
		while(limitSec > BigDecimal.ZERO.intValue()){
			--limitSec;
			if(limitSec % BigDecimal.TEN.intValue() == BigDecimal.ZERO.intValue()){
				log.info("?????????:{}, ????????????: {}", noActive, limitSec);
			}
			if(limitSec == BigDecimal.ZERO.intValue()){
				log.info("?????????:{} ???????????????", noActive);
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
    	String action = "??????";
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
    	if ( gameInfo==null ) {
    		log.warn("?????? ??????, ????????????");
    		return;
    	}

		// ??????????????????(?????????????????????)
		String openCardResult = OpenCardConvertSettleEnum.getAllOpenCardResult(consequences);

    	Long deskId = gameInfo.getDeskId();

		gameInfoBusiness.setGameResult(noActive, openCardResult);

    	// ??????????????????
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

		// ????????????????????????
		sendVideoEndScreenRecording(noActive, gameInfo.getStreamVideoCode());

        String settlement = JSONObject.toJSONString(BetSettleVO.builder().noActive(noActive)
				.awardOption(openCardResult).build());
		// ??????MQ
        rabbitTemplate.convertAndSend(MqConstants.BET_SETTLEMENT, settlement);
    }

	/**
	 * ??????????????????????????????TG???
	 * @param action ????????????
	 * @param deskId ????????????
	 * @param nearVideoAddress ??????????????????
	 * @param videoAddress ??????????????????
	 * @param videoResultAddress ??????????????????
	 * @param picRoadAddress ??????????????????
	 */
	private void sendVideoAddressToTg(
			final String action, final String getOpenCardUrl,
			final Long deskId, final String nearVideoAddress,
			final String videoAddress, final String videoResultAddress,
			final String picRoadAddress, final String recordingChartAddress) {


		// ?????? 5 request parameter
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
	 * ?????????????????????
	 *
	 * @param noActive
	 * @param streamVideoCode
	 */
	public void sendVideoEndScreenRecording(final String noActive, final String streamVideoCode){
		// ???????????????
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
		log.info("???????????? gameInfo :{}", gameInfo);

    	String action = "??????";
    	Map<String, Object> params = new HashMap<>();
		params.put("noActive", noActive);

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_SETTLEMENT.getApiName(),
				params);
		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
    		return;
		}

		// ??????????????????(?????????tg?????????)
		String openCardResult = OpenCardConvertTgEnum.getAllOpenCardTgResult(consequences);
		// ???????????????????????????????????????
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
		//??????tg????????????
		asyncApiService.tgSettlement(noActive, openCardResult, top20WinUsers);
    }

	public void pairImage(PairImageVO pairImageVO) {
		final String dealerIp = pairImageVO.getDealerIp();
		final String noActive = pairImageVO.getGameNo();

		GameInfo gameInfo = gameInfoBusiness.getGameInfo(noActive);
		// ??????????????????
		String picAddress = gameInfo.getPicAddress();
		// ??????????????????
		String videoAddress = gameInfo.getVideoAddress();
		InputStream inputStream = new ByteArrayInputStream(pairImageVO.getImageContent());
		String result;
		try {
			MultipartFile file = new MockMultipartFile(ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
			String url = uploadServerDomain + RequestPathEnum.UPLOAD_PIC.getApiName() + "/telegram/jpeg";
			result = HttpClient4Util.postMultipartFile
					(url, file);
		} catch (IOException e) {
			log.error("????????????????????????", e);
			throw new RuntimeException(e);
		}
		if ( !ValidateUtil.checkHttpResponse("???????????????", result) ) {
			return;
		}
		JSONObject json = JSONObject.parseObject(result);
		JSONObject resultJson = json.getJSONObject("data");
		String recordingChartAddress = resultJson.getString("url");
		gameInfo.setRecordingChartAddress(recordingChartAddress);
		gameInfoBusiness.setGameInfo(noActive, gameInfo);
		//?????????????????????TG
		sendVideoAddressToTg("??????", null, gameInfo.getDeskId(), null, null,
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
