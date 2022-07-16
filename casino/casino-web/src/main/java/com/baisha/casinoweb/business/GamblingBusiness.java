package com.baisha.casinoweb.business;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baisha.casinoweb.model.vo.response.DeskVO;
import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class GamblingBusiness {


    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private DeskBusiness deskBusiness;

    public Map<Object, Object> getTelegramSet() {
        return redisUtil.hmget(RedisKeyConstants.SYS_TELEGRAM);
    }

	/**
	 * 产生新局号
	 * @param tgChatId
	 * @return
	 */
	public String generateNewActive ( String deskCode, Integer gameNo ) {
		
		log.info("产生新局号, deskCode:{}", deskCode);
		DeskVO desk = deskBusiness.queryDeskByDeskCode(deskCode);
		if ( desk==null ) {
			log.warn("产生新局号 失败 无桌台资料");
			return null;
		}
		
		Date now = new Date();
    	String yyyyMMdd = DateUtil.dateToyyyyMMdd(now);
    	String result = deskCode +yyyyMMdd +StringUtils.leftPad(String.valueOf(gameNo), 4, "0");
		return result;
	}

}
