package com.baisha.casinoweb.util.task;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendTg implements Runnable{
	
	private String domain;
	private Map<String, Object> postRequest;
	
	public SendTg ( String domain, Map<String, Object> postRequest ) {
		this.domain = domain;
		this.postRequest = postRequest;
	}
	
	@Override
	public void run() {

		log.info("呼叫SendTg, {}", domain);

		String result = HttpClient4Util.doPost(
				domain,
				postRequest);

        if (CommonUtil.checkNull(result)) {
    		log.warn("呼叫SendTg 失败");
        }
        
		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");

		if ( code!=0 ) {
    		log.warn("呼叫SendTg 失败, {}", json.toString());
		}

		log.info("呼叫SendTg 成功");
	}

}
