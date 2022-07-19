package com.baisha.casinoweb.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baisha.casinoweb.model.vo.response.TgChatVO;
import com.baisha.casinoweb.util.ValidateUtil;
import com.baisha.casinoweb.util.constant.Constants;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.BigDecimalConstants;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameDesk;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.NewGameInfo;
import com.baisha.modulecommon.vo.mq.OpenNewGameVO;
import com.baisha.modulecommon.vo.mq.OpenVO;
import com.baisha.modulecommon.vo.mq.PairImageVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.model.vo.response.DeskVO;
import com.baisha.casinoweb.service.AsyncCommandService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DealerBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;

	@Value("${project.game.count-down-seconds}")
	private Integer gameCountDownSeconds;

	@Value("${project.server-url.video-server-domain}")
	private String videoServerDomain;
    
    @Autowired
    private GamblingBusiness gamblingBusiness;

    @Autowired
    private DeskBusiness deskBusiness;

	@Autowired
	private GameInfoBusiness gameInfoBusiness;

	@Autowired
	private RedissonClient redisUtil;

    @Autowired
    private AsyncCommandService asyncCommandService;

    /**
	 * 开新局
	 *
	 * @param openNewGameVO 开局对象
	 */
    @Async
    public void openNewGame (final OpenNewGameVO openNewGameVO) {

		String action = "开新局";
    	log.info(action);

		String dealerIp = openNewGameVO.getDealerIp();
		Integer gameNo = openNewGameVO.getGameNo();
    	DeskVO desk = deskBusiness.queryDeskByIp(dealerIp);
    	if ( desk==null ) {
    		log.warn("开新局 失败, 查无桌台");
			CompletableFuture.completedFuture(false);
			return;
    	}
    	
    	Long deskId = desk.getId();
    	String deskCode = desk.getDeskCode();
		GameDesk gameDesk = new GameDesk();
    	String newActive = gamblingBusiness.generateNewActive(deskCode, gameNo);
		gameDesk.setDeskId(deskId);
		gameDesk.setDeskCode(deskCode);
		gameDesk.setCurrentActive(newActive);
		gameDesk.setStreamVideoCode(desk.getCloseVideoAddress());
		deskBusiness.setGameDesk(dealerIp + "_" + gameNo, gameDesk);
		Date beginTime = new Date();
		// 预计这次gameInfo开牌结束时间
		Date endTime = DateUtils.addSeconds(beginTime, gameCountDownSeconds);
		NewGameInfo newGameInfo = new NewGameInfo();
		newGameInfo.setDeskCode(deskCode);
		newGameInfo.setNoActive(newActive);
		newGameInfo.setBeginTime(beginTime);
		newGameInfo.setEndTime(endTime);
		// 存储视频截屏地址和录单图地址
		String openCardVideoAddress = videoServerDomain + Constants.IMAGE +  gameDesk.getStreamVideoCode() + "/" +
				newActive + "/1" +Constants.MP4;
		newGameInfo.setVideoAddress(openCardVideoAddress);
		gameInfoBusiness.setGameTime(deskCode + "_" + gameNo ,newGameInfo);
		log.info("=======开局时间 newGameInfo: {}", newGameInfo);
		GameInfo gameInfo = new GameInfo();
		gameInfo.setCurrentActive(newActive);
		gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中

		log.info("局号、桌台id、{}, {}", newActive, deskId);
		String result = HttpClient4Util.doGet(
				telegramServerDomain + RequestPathEnum.TG_GET_GROUP_ID_LIST.getApiName() + "?tableId=" +deskId);

		if ( !ValidateUtil.checkHttpResponse(action, result) ) {
			CompletableFuture.completedFuture(false);
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

    	Future<Boolean> openNewGameResult = asyncCommandService.openNewGame(deskId, deskCode, newActive);
    	Future<Boolean> bettingResult = asyncCommandService.betting(desk, gameNo, newActive);

    	if (!handleFuture(openNewGameResult)) {
			CompletableFuture.completedFuture(false);
			return;
    	}
    	if (!handleFuture(bettingResult)) {
			CompletableFuture.completedFuture(false);
			return;
    	}

		CompletableFuture.completedFuture(true);
	}

    @Async
    public void open (final OpenVO openVO) {

    	log.info("\r\n================= 开牌");
    	asyncCommandService.open(openVO);
    	log.info("开牌成功");
    }

    @Async
    public void settlement (final SettleFinishVO settleFinishVO) {
    	
    	log.info("\r\n================= 结算");
		log.info("结算参数 :{}", settleFinishVO);
    	asyncCommandService.settlement(settleFinishVO);
    	log.info("结算成功");
    }
    
    private boolean handleFuture( Future<Boolean> future ) {
    	while ( future.isDone()==false ) {
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error("error ", e);
			}
    	}
    	
    	try {
			if ( future.get()==false ) {
				return false;
			}
		} catch (InterruptedException e) {
			log.error("error ", e);
			return false;
		} catch (ExecutionException e) {
			log.error("error ", e);
			return false;
		}
    	
    	return true;
    }
	@Async
	public void pairImage(PairImageVO pairImageVO) {
		log.info("\r\n================= 截屏");
		asyncCommandService.pairImage(pairImageVO);
		log.info("截屏成功");
	}
}
