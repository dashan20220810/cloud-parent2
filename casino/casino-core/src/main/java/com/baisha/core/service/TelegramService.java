package com.baisha.core.service;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class TelegramService {

    @Autowired
    private RedisUtil redisUtil;

    public Map<Object, Object> getTelegramSet() {
        return redisUtil.hmget(RedisKeyConstants.SYS_TELEGRAM);
    }


}
