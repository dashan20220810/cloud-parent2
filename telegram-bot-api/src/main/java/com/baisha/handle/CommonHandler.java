package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class CommonHandler {

    @Autowired
    TgChatService tgChatService;

    @Autowired
    TgBotService tgBotService;

    public boolean checkChatIsAudit(MyTelegramLongPollingBot bot, Chat chat) {
        // 判断此群是否通过审核，未通过不处理消息。
        TgBot tgBot = tgBotService.findByBotName(bot.getBotUsername());
        TgChat tgChat = tgChatService.findByChatIdAndBotId(chat.getId(), tgBot.getId());

        if (tgChat == null || Constants.close == tgChat.getStatus()) {
            return false;
        }
        return true;
    }

    public BigDecimal checkUserBalance(Long userId) {
        String userBalanceUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_USER_BALANCE.getApiName();
        Map<String, Object> userBalanceParam = Maps.newHashMap();
        String userBalance = TgHttpClient4Util.doPost(userBalanceUrl, userBalanceParam, userId);
        BigDecimal userBalanceResult = BigDecimal.ZERO;
        if (StrUtil.isNotEmpty(userBalance)) {
            ResponseEntity response = JSONObject.parseObject(userBalance, ResponseEntity.class);
            if (response.getCode() == 0) {
                userBalanceResult = (BigDecimal) response.getData();
            }
        }
        return userBalanceResult;
    }

    public BigDecimal flowOfDay(Long userId, Long chatId) {
        String flowOfDayUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_DAY_FLOW.getApiName();
        String flowOfDay = TgHttpClient4Util.doGet(flowOfDayUrl+"?tgChatId="+chatId, userId);
        BigDecimal flowOfDayResult = BigDecimal.ZERO;
        if (StrUtil.isNotEmpty(flowOfDay)) {
            ResponseEntity response = JSONObject.parseObject(flowOfDay, ResponseEntity.class);
            if (response.getCode() == 0) {
                flowOfDayResult = (BigDecimal) response.getData();
            }
        }
        return flowOfDayResult;
    }

    public BigDecimal profitOfDay(Long userId, Long chatId) {
        String profitOfDayUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_DAY_PROFIT.getApiName();
        String profitOfDay = TgHttpClient4Util.doGet(profitOfDayUrl+"?tgChatId="+chatId, userId);
        BigDecimal profitOfDayResult = BigDecimal.ZERO;
        if (StrUtil.isNotEmpty(profitOfDay)) {
            ResponseEntity response = JSONObject.parseObject(profitOfDay, ResponseEntity.class);
            if (response.getCode() == 0) {
                profitOfDayResult = (BigDecimal) response.getData();
            }
        }
        return profitOfDayResult;
    }

    public BigDecimal returnWaterAmount(Long userId, Long chatId) {
        String returnWaterUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_RETURN_AMOUNT.getApiName();
        String returnWater = TgHttpClient4Util.doGet(returnWaterUrl+"?tgChatId="+chatId, userId);
        BigDecimal returnWaterResult = BigDecimal.ZERO;
        if (StrUtil.isNotEmpty(returnWater)) {
            ResponseEntity response = JSONObject.parseObject(returnWater, ResponseEntity.class);
            if (response.getCode() == 0) {
                returnWaterResult = (BigDecimal) response.getData();
            }
        }
        return returnWaterResult;
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
