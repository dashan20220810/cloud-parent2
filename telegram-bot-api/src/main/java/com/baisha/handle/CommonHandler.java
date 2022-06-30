package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.model.TgChat;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CommonHandler {

    public String checkUserBalance(Long userId) {
        String userBalanceUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_USER_BALANCE.getApiName();
        Map<String, Object> userBalanceParam = Maps.newHashMap();
        String userBalance = TgHttpClient4Util.doPost(userBalanceUrl, userBalanceParam, userId);
        String userBalanceResult = "";
        if (StrUtil.isNotEmpty(userBalance)) {
            ResponseEntity response = JSONObject.parseObject(userBalance, ResponseEntity.class);
            if (response.getCode() == 0) {
                userBalanceResult = (String) response.getData();
            }
        }
        return userBalanceResult;
    }

    public ConfigInfo getConfigInfo(Long userId) {
        ConfigInfo configInfo = new ConfigInfo();
        String configUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_PROP_MAP.getApiName();
        Map<String, Object> configParam = Maps.newHashMap();
        String config = TgHttpClient4Util.doPost(configUrl, configParam, userId);
        if (StrUtil.isNotEmpty(config)) {
            ResponseEntity response = JSONObject.parseObject(config, ResponseEntity.class);
            if (response.getCode() == 0) {
                configInfo = JSONObject.parseObject(response.getData().toString(), ConfigInfo.class);
            }
        }
        return configInfo;
    }

    public boolean parseChat(TgChat tgChat) {
        if (null == tgChat || tgChat.getStatus() == Constants.close) {
            return false;
        }
        if (null == tgChat.getTableId()) {
            return false;
        }
        if (null == tgChat.getMinAmount()) {
            return false;
        }
        if (null == tgChat.getMaxAmount()) {
            return false;
        }
        if (null == tgChat.getMaxShoeAmount()) {
            return false;
        }
        return true;
    }
}
