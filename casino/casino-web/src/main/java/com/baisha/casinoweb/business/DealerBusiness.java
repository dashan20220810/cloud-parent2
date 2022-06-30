package com.baisha.casinoweb.business;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.AsyncCommandService;

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
    private AsyncCommandService asyncCommandService;

    /**
     * 开新局
     * @param deskCode	桌台号
     * @return
     */
    public boolean openNewGame (String dealerIp) {

    	log.info("开新局");

    	JSONObject desk = deskBusiness.queryDeskByIp(dealerIp);
    	if ( desk==null ) {
    		log.warn("开新局 失败, 查无桌台");
    		return false;
    	}
    	
    	Long deskId = desk.getLong("id");
    	String deskCode = desk.getString("deskCode");
    	String newActive = gamblingBusiness.generateNewActive(deskCode);
    	
    	Future<Boolean> openNewGameResult = asyncCommandService.openNewGame(deskId, deskCode, newActive);
    	
    	if ( handleFuture(openNewGameResult)==false ) {
    		return false;
    	}

    	Future<Boolean> bettingResult = asyncCommandService.betting(deskCode, newActive);

    	if ( handleFuture(bettingResult)==false ) {
    		return false;
    	}
    	
		return true;
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
