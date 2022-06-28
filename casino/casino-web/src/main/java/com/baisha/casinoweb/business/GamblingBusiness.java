package com.baisha.casinoweb.business;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.baisha.casinoweb.util.enums.RequestPathEnum;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.util.CommonUtil;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GamblingBusiness {

	@Value("${project.server-url.backend-server-domain}")
	private String backendServerDomain;
	
    @Value("${project.server-url.game-server-domain}")
    private String gameServerDomain;

    @Autowired
    private RedisUtil redisUtil;

    public Map<Object, Object> getTelegramSet() {
        return redisUtil.hmget(RedisKeyConstants.SYS_TELEGRAM);
    }

//    @Autowired
//    private TelegramService telegramService;
	
	/**
	 * 当前局号
	 * @param tgChatId
	 * @return
	 */
	public String currentActive ( String deskCode ) {
		
		JSONObject desk = queryDesk(deskCode);
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

    
    /**
     * game server查桌台号
     * @return
     */
    private JSONObject queryDesk( String deskCode ) {

    	log.info("查桌台号");
//    	Map<String, Object> params = new HashMap<>();
//		params.put("deskCode", deskCode);

		String result = null;
		try {
			result = HttpClient4Util.doGet(
					gameServerDomain + RequestPathEnum.DESK_QUERY_BY_DESK_CODE.getApiName() +"?deskCode=" +URLEncoder.encode(deskCode, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
    		log.error("查桌台号 失败", e);
            return null;
		}

        if (CommonUtil.checkNull(result)) {
    		log.warn("查桌台号 失败");
            return null;
        }
        
		JSONObject json = JSONObject.parseObject(result);
		Integer code = json.getInteger("code");

		if ( code!=0 ) {
    		log.warn("查桌台号 失败, {}", json.toString());
            return null;
		}

    	log.info("查桌台号 成功");
		return json.getJSONObject("data");
    }
}
