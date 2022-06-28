package com.baisha.casinoweb.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.util.ThreadPool;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.casinoweb.util.enums.TgImageEnum;
import com.baisha.casinoweb.util.task.SendTg;
import com.baisha.core.service.TelegramService;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulecommon.vo.GameInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DealerBusiness {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;

    @Autowired
    private TelegramService telegramService;
    
    @Autowired
    private GamblingBusiness gamblingBusiness;
    
    @Autowired
    private GameInfoBusiness gameInfoBusiness;

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
    	JSONObject desk = queryDesk();
    	if ( desk==null ) {
    		log.warn("开新局 失败, 查无桌台");
    		return false;
    	}
    	
    	Long deskId = desk.getLong("id");
    	String deskCode = desk.getString("deskCode");
    	String currentActive = gamblingBusiness.currentActive(deskCode);
    	
		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		params.put("bureauNum", currentActive);
		params.put("tableNo", deskId);
		params.put("imageAddress", openNewGameUrl);
//		params.put("minAmount", limitStakesVO.getMinAmount());
//		params.put("maxAmount", limitStakesVO.getMaxAmount());
//		params.put("maxShoeAmount", limitStakesVO.getMaxShoeAmount());

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
		
//		betting(deskCode, currentActive); // TODO

    	log.info("开新局 成功");
		return true;
    }
    
    private boolean betting ( String deskCode, String currentActive ) {
    	
    	Date now = new Date();

    	log.info("下注中 倒数计时");
    	GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
    	gameInfo.setCurrentActive(currentActive);
    	gameInfo.setBeginTime(now);
    	gameInfo.setStatus(GameStatusEnum.Betting);		// 状态: 下注中
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);

    	Map<Object, Object> tgSet = telegramService.getTelegramSet();
    	Integer counterInit = (Integer) tgSet.get("startBetSeventySeconds");
    	
    	for ( int counter= counterInit; counter >= 0; counter-- ) {
    		if ( counter%10==0 ) {
    	    	log.info("下注中 倒数计时 {}秒", counter);
    		}
    		
    		if ( counter==counterInit ) {
    			String s70Url = (String) tgSet.get("seventySecondsUrl");
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
    	gameInfoBusiness.setGameInfo(deskCode, gameInfo);
    	
    	// TODO
    	log.info("下注中 倒数计时 结束");
    	return true;
    }
    
    /**
     * game server查桌台号
     * @return
     */
    private JSONObject queryDesk() {

    	log.info("查桌台号");
    	Map<String, Object> params = new HashMap<>();
		String localIp = IpUtil.getIp(CasinoWebUtil.getRequest());

		log.info("ip: {}", localIp);
		params.put("localIp", localIp);

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.DESK_QUERY_BY_LOCAL_IP.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
    		log.warn("查桌台号 失败");
            return null;
        }
        
		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");

		if ( code!=0 ) {
    		log.warn("查桌台号 失败, {}", json.toString());
            return null;
		}

    	log.info("查桌台号 成功");
		return json.getJSONObject("data");
    }
    
}
