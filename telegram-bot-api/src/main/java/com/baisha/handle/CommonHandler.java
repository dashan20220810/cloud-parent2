package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
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
            userBalanceResult = (String) response.getData();
        }
        return userBalanceResult;
    }

    public String getCustomer(Long userId) {
        String customerUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_PROP_CUSTOMER.getApiName();
        Map<String, Object> customerParam = Maps.newHashMap();
        String customer = TgHttpClient4Util.doPost(customerUrl, customerParam, userId);
        String customerResult = "";
        if (StrUtil.isNotEmpty(customer)) {
            ResponseEntity response = JSONObject.parseObject(customer, ResponseEntity.class);
            customerResult = (String) response.getData();
        }
        return customerResult;
    }
}
