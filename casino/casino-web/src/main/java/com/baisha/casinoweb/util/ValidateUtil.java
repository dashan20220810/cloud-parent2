package com.baisha.casinoweb.util;

import com.alibaba.fastjson.JSONObject;
import com.baisha.modulecommon.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateUtil {

	
	public static boolean checkHttpResponse ( String action, String result ) {
        if (CommonUtil.checkNull(result)) {
        	log.warn("{} 失败", action);
    		return false;
        }
        
		JSONObject newGameJson = JSONObject.parseObject(result);
		Integer code = newGameJson.getInteger("code");

		if ( code!=0 ) {
        	log.warn("{} 失败, {}", action, result);
    		return false;
		}
		
		return true;
	}
}
