package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.constant.Constants;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.casinoweb.vo.UserVO;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

@Component
public class AssetsBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;
	
	public boolean withdraw ( Long userId, Long amount ) {

    	//	会员管理-下分api
    	Map<String, Object> params = new HashMap<>();
    	params.put("userId", userId);
    	params.put("amount", amount);
    	params.put("balanceType", Constants.BALANCE_TYPE_WITHDRAW);
    	params.put("remark", "下注");

    	String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.ASSETS_BALANCE.getApiName(),
				params);
		
        if (CommonUtil.checkNull(result)) {
            return false;
        }

		JSONObject balanceJson = JSONObject.parseObject(result);
		Integer code = balanceJson.getInteger("code");

		if ( code!=0 ) {
            return false;
		}

        return true;
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
