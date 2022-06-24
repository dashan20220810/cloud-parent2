package com.baisha.casinoweb.business;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.baisha.core.service.TelegramService;
import com.baisha.core.vo.response.LimitStakesVO;
import com.baisha.modulecommon.util.DateUtil;

@Component
public class GamblingBusiness {

	private static final String TEMP_TABLE_ID = "G01";
	private static Integer tempActiveCounter = 1;

	@Value("${project.server-url.backend-server-domain}")
	private String backendServerDomain;

    @Autowired
    private TelegramService telegramService;
	
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
	public LimitStakesVO limitStakes ( Long tgChatId ) {

		return telegramService.getLimitStakes( tgChatId.toString() );
	}
}
