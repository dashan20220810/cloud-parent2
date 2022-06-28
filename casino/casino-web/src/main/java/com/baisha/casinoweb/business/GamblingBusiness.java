package com.baisha.casinoweb.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GamblingBusiness {

	@Value("${project.server-url.backend-server-domain}")
	private String backendServerDomain;

    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private DeskBusiness deskBusiness;

    public Map<Object, Object> getTelegramSet() {
        return redisUtil.hmget(RedisKeyConstants.SYS_TELEGRAM);
    }

//    @Autowired
//    private TelegramService telegramService;
	
	/**
	 * 产生新局号
	 * @param tgChatId
	 * @return
	 */
	public String generateNewActive ( String deskCode ) {
		
		JSONObject desk = deskBusiness.queryDeskByDeskCode(deskCode);
		if ( desk==null ) {
			return null;
		}
		
		Date now = new Date();
		Integer counter = null;
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) redisUtil.hget(RedisKeyConstants.GAMBLING_ACTIVE_INFO, deskCode);
		if ( map==null ) {
			map = new HashMap<>();
			counter = 1;
		} else {
			counter = (Integer) map.get("counter") +1;
			Date lastUpdateDate = (Date) map.get("lastUpdateDate");
			
			if ( DateUtils.isSameDay(now, lastUpdateDate)==false ) {
				counter = 1;
			}
		}

		map.put("counter", counter);
		map.put("lastUpdateDate", now);
		redisUtil.hset(RedisKeyConstants.GAMBLING_ACTIVE_INFO, deskCode, map);
		
    	String yyyyMMdd = DateUtil.dateToyyyyMMdd(now);
    	String result = deskCode +yyyyMMdd +StringUtils.leftPad(String.valueOf(counter), 4, "0");
		return result;
	}

	/**
	 * 從後台查詢限紅
	 * @param tgChatId
	 * @return
	 */
//	@Deprecated  // 改为tg管理
//	public LimitStakesVO limitStakes ( Long tgChatId ) {
//
//		return telegramService.getLimitStakes( tgChatId.toString() );
//	}

}
