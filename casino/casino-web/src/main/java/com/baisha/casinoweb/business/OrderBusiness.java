package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.model.vo.BetVO;
import com.baisha.casinoweb.model.vo.UserVO;
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
	
	@Autowired
	private UserBusiness userBusiness;
	
	@Autowired
	private DeskBusiness deskBusiness;
	
	public String bet ( boolean isTgRequest, Long tableId, Long tgChatId, String clientIP, Long userId, BetOption betOption, 
			Long amount, String noRun ) {
		
		log.info("下注");
		JSONObject desk = deskBusiness.queryDeskById(tableId);
		String deskCode = desk.getString("deskCode");
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);
		
		if ( gameInfo==null ) {
    		log.warn("下注 失败 无游戏资讯");
			return "下注 失败 无游戏资讯";
		}
		
		if ( StringUtils.isBlank(gameInfo.getCurrentActive()) ) {
    		log.warn("下注 失败 局号不符, {}");
			return "下注 失败 局号不符";
		}
		
		if ( gameInfo.getStatus()!=GameStatusEnum.Betting ) {
    		log.warn("下注 失败 非下注状态, {}", gameInfo.getStatus().toString());
			return "下注 失败 非下注状态";
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
		params.put("noActive", gameInfo.getCurrentActive());
		params.put("status", 1);
		params.put("orderNo", SnowFlakeUtils.getSnowId());

		String result = HttpClient4Util.doPost(
				gameServerDomain + RequestPathEnum.ORDER_BET.getApiName(),
				params);

        if (CommonUtil.checkNull(result)) {
    		log.warn("下注 失败");
            return "下注 失败";
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		Integer code = betJson.getInteger("code");

		if ( code!=0 ) {
    		log.warn("下注 失败, {}", betJson.toString());
            return String.format("下注 失败, %s", betJson.toString());
		}

		gameInfo = gameInfoBusiness.calculateBetAmount(deskCode, tgChatId, userId, amount);
		
		log.info("下注 成功");
		return null;
	}
	
	public JSONObject currentOrderList () {
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	//  user id查user
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	UserVO userVO = userBusiness.getUserVO(isTgRequest, userIdOrName);
    	
    	if ( userVO==null ) {
    		log.info("近期订单 失败 查不到玩家资料");
            return null;
    	}

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userVO.getId());
        String result = HttpClient4Util.doPost(gameServerDomain + RequestPathEnum.ORDER_CURRENT_LIST, params);
        if (CommonUtil.checkNull(result)) {
            return null;
        }
        return JSONObject.parseObject(result);
	}
	
}
