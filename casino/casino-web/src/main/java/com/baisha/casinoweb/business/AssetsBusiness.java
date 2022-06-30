package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.constant.Constants;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.model.vo.UserVO;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.GameInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AssetsBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;

	@Autowired
	private GameInfoBusiness gameInfoBusiness;
	
	@Autowired
	private DeskBusiness deskBusiness;
	
	public String withdraw ( Long userId, Long amount, Long tableId ) {

		JSONObject desk = deskBusiness.queryDeskById(tableId);
		String deskCode = desk.getString("deskCode");
		GameInfo gameInfo = gameInfoBusiness.getGameInfo(deskCode);

		log.info("呼叫下分, userID:{}", userId);
    	//	会员管理-下分api
    	Map<String, Object> params = new HashMap<>();
    	params.put("userId", userId);
    	params.put("amount", amount);
    	params.put("balanceType", Constants.BALANCE_TYPE_WITHDRAW);
    	params.put("remark", String.format("user:%d ,局号:%s 下注", userId, gameInfo.getCurrentActive()));

    	String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.ASSETS_BALANCE.getApiName(),
				params);
		
        if (CommonUtil.checkNull(result)) {
    		log.warn("呼叫下分api 无返回资料");
            return "failed";
        }

		JSONObject balanceJson = JSONObject.parseObject(result);
		Integer code = balanceJson.getInteger("code");

		if ( code!=0 ) {
    		log.warn("呼叫下分api失败: {}", balanceJson.getString("msg"));
            return balanceJson.getString("msg");
		}

        return null;
	}
	
	
	public String balance () {
    	String userIdOrName = CasinoWebUtil.getCurrentUserId();
    	Long userId = null;
    	boolean isTgRequest = CasinoWebUtil.isTelegramRequest();
    	
    	if ( isTgRequest ) {
    		UserVO userVO = CasinoWebUtil.getUserVO(userServerDomain, userIdOrName);
    		if ( userVO==null ) {
    			return null;
    		}
    		userId = userVO.getId();
    	} else {
    		userId = Long.parseLong(userIdOrName);
    	}

    	String result = HttpClient4Util.doGet(
				userServerDomain + RequestPathEnum.ASSETS_QUERY.getApiName() +"?userId=" + userId);
        if (CommonUtil.checkNull(result)) {
            return null;
        }

		JSONObject balanceJson = JSONObject.parseObject(result);
		Integer code = balanceJson.getInteger("code");
		if ( code!=0 ) {
            return null;
		}
		
		return balanceJson.getJSONObject("data").getString("balance");
	}
}
