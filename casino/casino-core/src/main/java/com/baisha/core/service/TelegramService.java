package com.baisha.core.service;

import com.baisha.core.constants.RedisKeyConstants;
import com.baisha.core.dto.SysTelegramDto;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TelegramService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取 电报系统参数
     *
     * @return
     */
    public SysTelegramDto getSysTelegram() {
        SysTelegramDto dto = (SysTelegramDto) redisUtil.get(RedisKeyConstants.SYS_TELEGRAM);
        return dto;
    }

    /**
     * 设置 电报系统参数
     *
     * @param dto
     */
    public void setSysTelegram(SysTelegramDto dto) {
        redisUtil.setValue(RedisKeyConstants.SYS_TELEGRAM, dto);
    }

}
