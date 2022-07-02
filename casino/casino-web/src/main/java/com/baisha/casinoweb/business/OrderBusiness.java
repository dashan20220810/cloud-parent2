package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.List;
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
	

	
	public String bet ( boolean isTgRequest, BetVO betVO, UserVO userVO, String noRun ) {
		return bet(isTgRequest, betVO.getTableId(), betVO.getTgChatId(), betVO.getBetOptionList(), betVO.getAmount()
				, noRun, userVO.getId(), userVO.getUserName(), userVO.getNickName());
	}
	
	public String bet ( boolean isTgRequest, Long tableId, Long tgChatId, List<String> betOptionList, 
			Long amount, String noRun, Long userId, String userName, String nickName ) {
		
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

		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());

		params.put("isTgRequest", isTgRequest);
		params.put("tgChatId", tgChatId);
		params.put("clientIP", ip);
		params.put("userId", userId);  
		params.put("userName", userName); 
		params.put("nickName", nickName); 
		params.put("noRun", noRun);
		params.put("noActive", gameInfo.getCurrentActive());
		params.put("status", 1);
		params.put("orderNo", SnowFlakeUtils.getSnowId());
		
		for ( String betOption: betOptionList ) {
			if ( StringUtils.equalsIgnoreCase(betOption, BetOption.X.toString()) ) {
				params.put("amountX", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.Z.toString()) ) {
				params.put("amountZ", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.H.toString()) ) {
				params.put("amountH", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.XD.toString()) ) {
				params.put("amountXd", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.ZD.toString()) ) {
				params.put("amountZd", amount);
			} else if ( StringUtils.equalsIgnoreCase(betOption, BetOption.SS.toString()) ) {
				params.put("amountSs", amount);
			}
		}

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

		gameInfo = gameInfoBusiness.calculateBetAmount(deskCode, tgChatId, userId, nickName, betOptionList, amount);
		
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
