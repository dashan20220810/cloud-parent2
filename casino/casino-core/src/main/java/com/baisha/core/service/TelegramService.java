package com.baisha.core.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.core.vo.response.LimitStakesVO;
import com.baisha.modulespringcacheredis.util.RedisUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TelegramService {

    @Autowired
    private RedisUtil redisUtil;

    public Map<Object, Object> getTelegramSet() {
        return redisUtil.hmget(RedisKeyConstants.SYS_TELEGRAM);
    }

    public LimitStakesVO getLimitStakes( String tgGroupdId ) {
    	@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) redisUtil.hget(RedisKeyConstants.GROUP_TELEGRAM_BOUND, tgGroupdId);
    	LimitStakesVO result = new LimitStakesVO();
    	
    	if ( map!=null ) {
    		result.setMinAmount((Integer) map.get("minAmount"));
    		result.setMaxAmount((Integer) map.get("maxAmount"));
    		result.setMaxShoeAmount((Integer) map.get("maxShoeAmount"));
    	}
    	
    	return result;
    }

}
