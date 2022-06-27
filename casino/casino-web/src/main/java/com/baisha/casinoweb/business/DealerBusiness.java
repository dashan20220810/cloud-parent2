package com.baisha.casinoweb.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.ThreadPool;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.casinoweb.util.enums.TgImageEnum;
import com.baisha.casinoweb.util.task.SendTg;
import com.baisha.core.service.TelegramService;
import com.baisha.core.vo.response.LimitStakesVO;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DealerBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;

    @Autowired
    private TelegramService telegramService;
    
    @Autowired
    private GamblingBusiness gamblingBusiness;
    
    @Autowired
    private GameInfoBusiness gameInfoBusiness;

    public boolean openNewGame ( Long tgChatId ) {

    	log.info("开新局");
    	Map<Object, Object> sysTgMap = telegramService.getTelegramSet();
    	String openNewGameUrl = (String) sysTgMap.get(TgImageEnum.OpenNewGame.getKey());
    	LimitStakesVO limitStakesVO = telegramService.getLimitStakes(String.valueOf(tgChatId));
    	String currentActive = gamblingBusiness.currentActive(tgChatId);
    	
		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		params.put("bureauNum", currentActive);
		params.put("chatId", tgChatId);
		params.put("imageAddress", openNewGameUrl);
		params.put("minAmount", limitStakesVO.getMinAmount());
		params.put("maxAmount", limitStakesVO.getMaxAmount());
		params.put("maxShoeAmount", limitStakesVO.getMaxShoeAmount());

		String result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_OPEN_NEW_GAME.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
        	log.warn("开新局 失败");
            return false;
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		Integer code = betJson.getInteger("code");

		if ( code!=0 ) {
        	log.warn("开新局 失败, {}", result);
            return false;
		}
		
		betting(tgChatId, currentActive);

    	log.info("开新局 成功");
		return true;
    }
    
    private boolean betting ( Long tgChatId, String currentActive ) {
    	
    	Date now = new Date();

    	log.info("下注中 倒数计时");
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(tgChatId);
    	gameInfo.setCurrentActive(currentActive);
    	gameInfo.setBeginTime(now);
    	gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中
    	gameInfoBusiness.setGameInfo(tgChatId, gameInfo);

    	Map<Object, Object> tgSet = telegramService.getTelegramSet();
    	Integer counterInit = (Integer) tgSet.get("startBetSeventySeconds");
    	
    	for ( int counter= counterInit; counter >= 0; counter-- ) {
    		if ( counter%10==0 ) {
    	    	log.info("下注中 倒数计时 {}秒", counter);
    		}
    		
    		if ( counter==counterInit ) {
    			String s70Url = "http://192.168.26.24:9000/user/s70.mp4"; // TODO get s70.mp4
    			ThreadPool.getInstance().putThread(new SendTg(telegramServerDomain +RequestPathEnum.TG_SEND_ANIMATION.getApiName()
    				, s70Url));
    		}
    		
//    		if ( counter==20 ) {
//    			String s20Url = "http://192.168.26.24:9000/user/s20.mp4"; // TODO get s20.mp4
//    			ThreadPool.getInstance().putThread(new SendTg(telegramServerDomain +RequestPathEnum.TG_SEND_ANIMATION.getApiName()
//    				, s20Url));
//    		}
    		
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error("下注中 倒数计时 失败", e);
			}
    	}

		gameInfo.setStatus(GameStatusEnum.StopBetting);
//    	String stopImgUrl = "http://192.168.26.24:9000/user/stop.jpg"; // TODO get stop image
//		ThreadPool.getInstance().putThread(new SendTg(telegramServerDomain +RequestPathEnum.TG_SEND_PHOTO.getApiName()
//			, stopImgUrl));
    	gameInfoBusiness.setGameInfo(tgChatId, gameInfo);
    	
    	// TODO
    	log.info("下注中 倒数计时 结束");
    	return true;
    }
    
}
