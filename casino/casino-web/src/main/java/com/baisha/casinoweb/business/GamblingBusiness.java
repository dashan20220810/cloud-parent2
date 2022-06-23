package com.baisha.casinoweb.business;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.enums.RequestPathEnum;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.util.HttpClient4Util;

@Component
public class GamblingBusiness {

	private static final String TEMP_TABLE_ID = "G01";
	private static Integer tempActiveCounter = 1;

	@Value("${project.server-url.backend-server-domain}")
	private String backendServerDomain;

	
	/**
	 * 当前局号
	 * @param tgChatId
	 * @return
	 */
	public String currentActive ( Long tgChatId ) {
		
    	String yyyyMMdd = DateUtil.dateToyyyyMMdd(new Date());
    	String result = TEMP_TABLE_ID +yyyyMMdd +StringUtils.leftPad(String.valueOf(tempActiveCounter++), 4, "0");
		return result;
    	
		// TODO call api, query table id by tgChatId
	}

	/**
	 * 從後台查詢限紅
	 * @param tgChatId
	 * @return
	 */
	public String limitStakes ( Long tgChatId ) {

    	String result = HttpClient4Util.doGet(backendServerDomain + RequestPathEnum.LIMIT_STAKES.getApiName());
        if (CommonUtil.checkNull(result)) {
            return null;
        }

		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");
		if ( code==null || code!=0 ) {
			return null;
		}

		return json.getString("data");
	}
}
