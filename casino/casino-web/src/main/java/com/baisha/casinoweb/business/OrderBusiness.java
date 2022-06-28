package com.baisha.casinoweb.business;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.enums.GameStatusEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulecommon.util.SnowFlakeUtils;
import com.baisha.modulecommon.vo.GameInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class OrderBusiness {

	@Value("${project.server-url.game-server-domain}")
	private String gameServerDomain;
	
	@Autowired
	private GameInfoBusiness gameInfoBusiness;
	
	public boolean bet ( boolean isTgRequest, Long tableId, Long tgChatId, String clientIP, Long userId, BetOption betOption, 
			Long amount, String noRun, String noActive ) {
		
		log.info("下注");
		JSONObject desk = queryDesk(tableId);
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(desk.getJSONObject("data").getString("deskCode"));
		
		if ( gameInfo==null ) {
    		log.warn("下注 失败 无游戏资讯");
			return false;
		}
		
		if ( StringUtils.equals(noActive, gameInfo.getCurrentActive())==false ) {
    		log.warn("下注 失败 局号不符, {}", noActive);
			return false;
		}
		
		if ( gameInfo.getStatus()!=GameStatusEnum.Betting) {
    		log.warn("下注 失败 非下注状态, {}", gameInfo.getStatus().toString());
			return false;
		}

		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());

		params.put("isTgRequest", isTgRequest);
		params.put("tgChatId", tgChatId);
		params.put("clientIP", ip);
		params.put("userId", userId);  
		params.put("betOption", betOption);
		params.put("amount", amount);
		params.put("noRun", noRun);
		params.put("noActive", noActive);
		params.put("status", 1);
		params.put("orderNo", SnowFlakeUtils.getSnowId());

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_BET.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
    		log.warn("下注 失败");
            return false;
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		Integer code = betJson.getInteger("code");

		if ( code!=0 ) {
    		log.warn("下注 失败, {}", betJson.toString());
            return false;
		}

		log.info("下注 成功");
		return true;
	}
	
    
    /**
     * game server查桌台号
     * @return
     */
	private JSONObject queryDesk ( Long tableId ) {

    	log.info("查桌台号");
//    	Map<String, Object> params = new HashMap<>();

		log.info("tableId: {}", tableId);
//		params.put("tableId", tableId);

		String result = null;
		try {
			result = HttpClient4Util.doGet(
					gameServerDomain + RequestPathEnum.DESK_QUERY_BY_ID.getApiName() +"?tableId=" +URLEncoder.encode(String.valueOf(tableId), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
    		log.error("查桌台号 失败", e);
            return null;
		}

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
