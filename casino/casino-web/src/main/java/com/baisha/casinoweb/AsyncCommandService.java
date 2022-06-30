package com.baisha.casinoweb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.business.GameInfoBusiness;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.dto.SysTelegramDto;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsyncCommandService {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;

    @Value("${project.game.count-down-seconds}")
    private Integer gameCountDownSeconds;

    @Autowired
    private TelegramService telegramService;
    
    @Autowired
    private GameInfoBusiness gameInfoBusiness;

    /**
     * 开新局
     * @param deskCode	桌台号
     * @return
     */
    @Async
    public Future<Boolean> openNewGame ( Long deskId, String deskCode, String newActive ) {

    	log.info("开新局");
    	SysTelegramDto sysTg = telegramService.getSysTelegram();
    	String openNewGameUrl = sysTg.getStartBetPicUrl();
    	
    	Map<String, Object> params = new HashMap<>();
		params.put("bureauNum", newActive);
		params.put("tableId", deskId);
		params.put("imageAddress", openNewGameUrl);
		params.put("countdownAddress", sysTg.getSeventySecondsUrl());

		log.info("局号、桌台id、新局图片url: {}, {}, {}", newActive, deskId, openNewGameUrl);
		String result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
        	log.warn("开新局 失败");
    		return CompletableFuture.completedFuture(false);
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		Integer code = betJson.getInteger("code");

		if ( code!=0 ) {
        	log.warn("开新局 失败, {}", result);
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
    	
    	Date beginTime = new Date();
//    	Date endTime = DateUtils.addSeconds(beginTime, gameCountDownSeconds);
    	Date endTime = DateUtils.addSeconds(beginTime, 20);

    	log.info("下注中 倒数计时");
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	gameInfo.setCurrentActive(newActive);
    	gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中
    	gameInfo.setBeginTime(beginTime);
    	gameInfo.setEndTime(endTime);
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);
    	
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
    
}
