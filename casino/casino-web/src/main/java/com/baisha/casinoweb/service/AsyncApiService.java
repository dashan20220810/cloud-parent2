package com.baisha.casinoweb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.service.AsyncCommandService.BetHistory;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class AsyncApiService {
	
    @Value("${project.server-url.telegram-server-domain}")
    private String telegramServerDomain;
    
    @Async
    public void tgSettlement ( String noActive, String betDisplay, Map<Long, List<BetHistory>> betHistoryList ) {

		Map<String, Object> params = new HashMap<>();
		params.put("bureauNum", noActive);
		params.put("settlementResult", betDisplay); 
		params.put("settlementInfo", betHistoryList);
		
		String result = HttpClient4Util.doPost(
				telegramServerDomain + RequestPathEnum.TG_SETTLEMENT.getApiName(),
				JSONObject.toJSONString(params));

        if (CommonUtil.checkNull(result)) {
        	log.warn("结算 tg接口呼叫失败");
    		return;
        }
        
		JSONObject settlementJson = JSONObject.parseObject(result);
		Integer code = settlementJson.getInteger("code");

		if ( code!=0 ) {
        	log.warn("结算 tg接口呼叫失败, {}", result);
    		return;
		}
    }
}
