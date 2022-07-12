package com.baisha.casinoweb.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.vo.GameInfo;
import com.baisha.modulecommon.vo.NewGameInfo;
import com.baisha.modulecommon.vo.mq.OpenVO;
import com.baisha.modulecommon.vo.mq.SettleFinishVO;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
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
    
    @Autowired
    private GamblingBusiness gamblingBusiness;

    @Autowired
    private DeskBusiness deskBusiness;

	@Autowired
	private RedissonClient redisUtil;

    @Autowired
    private AsyncCommandService asyncCommandService;

    /**
	 * 开新局
	 *
	 * @param dealerIp 桌台ip
	 * @param gameNo 荷官端游戏号
	 */
    @Async
    public void openNewGame (String dealerIp, Integer gameNo) {

    	log.info("开新局");

    	DeskVO desk = deskBusiness.queryDeskByIp(dealerIp);
    	if ( desk==null ) {
    		log.warn("开新局 失败, 查无桌台");
			CompletableFuture.completedFuture(false);
			return;
    	}
    	
    	Long deskId = desk.getId();
    	String deskCode = desk.getDeskCode();
    	String newActive = gamblingBusiness.generateNewActive(deskCode, gameNo);
		Date beginTime = new Date();
		// 预计这次gameInfo开牌结束时间
		Date endTime = DateUtils.addSeconds(beginTime, gameCountDownSeconds);
		RMap<String, NewGameInfo> map = redisUtil.getMap(RedisKeyConstants.SYS_GAME_TIME);
		NewGameInfo newGameInfo = new NewGameInfo();
		newGameInfo.setDeskCode(deskCode);
		newGameInfo.setNoActive(newActive);
		newGameInfo.setBeginTime(beginTime);
		newGameInfo.setEndTime(endTime);
		map.expire(BigDecimal.TEN.longValue(), TimeUnit.MINUTES);
		map.put(deskCode + "_" + gameNo, newGameInfo);
    	
    	Future<Boolean> openNewGameResult = asyncCommandService.openNewGame(deskId, deskCode, newActive);
    	Future<Boolean> bettingResult = asyncCommandService.betting(deskCode, gameNo, newActive);

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
    
}
