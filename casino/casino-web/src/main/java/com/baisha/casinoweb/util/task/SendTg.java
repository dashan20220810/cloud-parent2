package com.baisha.casinoweb.util.task;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendTg implements Runnable{
	
	private String domain;
	private String aniUrl;
	
	public SendTg ( String domain, String aniUrl ) {
		this.domain = domain;
		this.aniUrl = aniUrl;
	}
	
	@Override
	public void run() {

		log.info("呼叫SendTg, {}", aniUrl);
		// 记录IP
    	Map<String, Object> params = new HashMap<>();
		params.put("", aniUrl);

		String result = HttpClient4Util.doPost(
				domain,
				params);

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
