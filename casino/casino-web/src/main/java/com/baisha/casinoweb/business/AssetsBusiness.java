package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.constant.Constants;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

@Component
public class AssetsBusiness {

	@Value("${project.server-url.user-server-domain}")
	private String userServerDomain;
	
	public boolean withdraw ( String userName, Long amount ) {

    	//	会员管理-下分api
    	Map<String, Object> params2 = new HashMap<>();
    	params2.put("userName", userName);
    	params2.put("amount", amount);
    	params2.put("balanceType", Constants.BALANCE_TYPE_WITHDRAW);
    	params2.put("remark", "下注");

    	String result = HttpClient4Util.doPost(
				userServerDomain + RequestPathEnum.ASSETS_BALANCE.getApiName(),
				params2);
		
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
}
