package com.baisha.casinoweb.business;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.service.AsyncCommandService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DealerBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
    
    @Autowired
    private GamblingBusiness gamblingBusiness;
    
    @Autowired
    private DeskBusiness deskBusiness;
    
    @Autowired
    private AsyncCommandService asyncCommandService;

    /**
	 * 开新局
	 *
	 * @param deskCode 桌台号
	 */
    @Async
    public void openNewGame (String dealerIp, Integer gameNo) {

    	log.info("开新局");

    	JSONObject desk = deskBusiness.queryDeskByIp(dealerIp);
    	if ( desk==null ) {
    		log.warn("开新局 失败, 查无桌台");
			CompletableFuture.completedFuture(false);
			return;
    	}
    	
    	Long deskId = desk.getLong("id");
    	String deskCode = desk.getString("deskCode");
    	String newActive = gamblingBusiness.generateNewActive(deskCode, gameNo);
    	
    	Future<Boolean> openNewGameResult = asyncCommandService.openNewGame(deskId, deskCode, newActive);
    	Future<Boolean> bettingResult = asyncCommandService.betting(deskCode, newActive);

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
    public void open (String dealerIp, String awardOption, String openingTime) {

    	log.info("\r\n================= 开牌");
    	asyncCommandService.open(dealerIp, awardOption, openingTime);
    	log.info("开牌成功");
    }

    @Async
    public void settlement ( String noActive ) {
    	
    	log.info("\r\n================= 结算");
    	asyncCommandService.settlement(noActive);
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
