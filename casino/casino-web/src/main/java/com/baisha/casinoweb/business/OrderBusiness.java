package com.baisha.casinoweb.business;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.casinoweb.util.CasinoWebUtil;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.util.IpUtil;
import com.baisha.modulecommon.util.SnowFlakeUtils;

@Component
public class OrderBusiness {

	@Value("${project.server-url.game-server-domain}")
	private String gameServerDomain;
	
	public boolean bet ( String clientIP, Long userId, BetOption betOption, 
			Long amount, String noRun, String noActive ) {

		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		String ip = IpUtil.getIp(CasinoWebUtil.getRequest());
		
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
            return false;
        }
        
		JSONObject betJson = JSONObject.parseObject(result);
		Integer code = betJson.getInteger("code");

		if ( code!=0 ) {
            return false;
		}
		
		return true;
	}
	

}
