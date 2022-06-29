package com.baisha.casinoweb.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.casinoweb.util.enums.TgImageEnum;
import com.baisha.core.service.TelegramService;
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
    
    @Autowired
    private DeskBusiness deskBusiness;

    /**
     * 开新局
     * @param deskCode	桌台号
     * @return
     */
    public boolean openNewGame () {

    	log.info("开新局");
    	Map<Object, Object> sysTgMap = telegramService.getTelegramSet();
    	String openNewGameUrl = (String) sysTgMap.get(TgImageEnum.OpenNewGame.getKey());
//    	LimitStakesVO limitStakesVO = telegramService.getLimitStakes(String.valueOf(gameId));
    	JSONObject desk = deskBusiness.queryDeskByIp();
    	if ( desk==null ) {
    		log.warn("开新局 失败, 查无桌台");
    		return false;
    	}
    	
    	Long deskId = desk.getLong("id");
    	String deskCode = desk.getString("deskCode");
    	String newActive = gamblingBusiness.generateNewActive(deskCode);
    	Map<Object, Object> tgSet = telegramService.getTelegramSet();
    	Integer counterInit = (Integer) tgSet.get("startBetSeventySeconds");
    	
		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		params.put("bureauNum", newActive);
		params.put("tableId", deskId);
		params.put("imageAddress", openNewGameUrl);
		params.put("countdownAddress", (String) tgSet.get("seventySecondsUrl"));
//		params.put("minAmount", limitStakesVO.getMinAmount());
//		params.put("maxAmount", limitStakesVO.getMaxAmount());
//		params.put("maxShoeAmount", limitStakesVO.getMaxShoeAmount());

		log.info("局号、桌台id、新局图片url: {}, {}, {}", newActive, deskId, openNewGameUrl);
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
		
		betting(deskCode, newActive, deskId, counterInit); 

    	log.info("开新局 成功");
		return true;
    }
    
    private boolean betting ( String deskCode, String newActive, Long tableId, Integer counterInit ) {
    	
    	Date now = new Date();

    	log.info("下注中 倒数计时");
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	gameInfo.setCurrentActive(newActive);
    	gameInfo.setBeginTime(now);
    	gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);
    	
    	for ( int counter= counterInit; counter >= 0; counter-- ) {
    		if ( counter%10==0 ) {
    	    	log.info("下注中 倒数计时 {}秒", counter);
    		}
    		
//    		if ( counter==counterInit ) {
//    			String s70Url = (String) tgSet.get("seventySecondsUrl");
//    			Map<String, Object> requestParams = new HashMap<>();
//    			requestParams.put("imageAddress", s70Url);
//    			requestParams.put("tableId", tableId);
//    			ThreadPool.getInstance().putThread(new SendTg(telegramServerDomain +RequestPathEnum.TG_SEND_ANIMATION.getApiName()
//    				, requestParams));
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
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);
    	
    	// TODO
    	log.info("下注中 倒数计时 结束");
    	return true;
    }
    
    
}
