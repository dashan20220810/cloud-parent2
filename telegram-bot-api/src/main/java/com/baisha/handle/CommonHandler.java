package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.model.vo.OddsAndLimitVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.vo.mq.tgBotServer.BotGroupVO;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.baisha.util.constants.BotConstant.DEFAULT_USER_ID;

@Slf4j
@Component
public class CommonHandler {

    @Value("${project.server-url.bet-bot-send-message-domain}")
    private String betBotSendMessageDomain;

    @Autowired
    TgChatService tgChatService;

    @Autowired
    TgBotService tgBotService;

    public boolean checkChatIsAudit(Chat chat, MyTelegramLongPollingBot bot) {
        // 判断此群是否通过审核，未通过不处理消息。
        TgBot tgBot = tgBotService.findByBotName(bot.getBotUsername());
        TgChat tgChat = tgChatService.findByChatIdAndBotId(chat.getId(), tgBot.getId());
        if (tgChat == null || Constants.close.equals(tgChat.getStatus())) {
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
            if (response.getCode() == 0 && null != response.getData()) {
                userBalanceResult = (BigDecimal) response.getData();
                if (userBalanceResult.compareTo(BigDecimal.ZERO) <= 0) {
                    userBalanceResult = BigDecimal.ZERO;
                }
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
            if (response.getCode() == 0 && null != response.getData()) {
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
            if (response.getCode() == 0 && null != response.getData()) {
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
            if (response.getCode() == 0 && null != response.getData()) {
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
            if (response.getCode() == 0 && null != response.getData()) {
                configInfo = JSONObject.parseObject(response.getData().toString(), ConfigInfo.class);
            }
        }
        return configInfo;
    }

    public List<OddsAndLimitVO> getRedLimit(Long userId) {
        List<OddsAndLimitVO> result = Lists.newArrayList();
        String redLimitUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_RED_LIMIT.getApiName();
        String redLimit = TgHttpClient4Util.doGet(redLimitUrl, userId);
        if (StrUtil.isNotEmpty(redLimit)) {
            ResponseEntity response = JSONObject.parseObject(redLimit, ResponseEntity.class);
            if (response.getCode() == 0 && null != response.getData()) {
                result = JSONArray.parseArray(response.getData().toString(), OddsAndLimitVO.class);
            }
        }
        return result;
    }

    public List<BotGroupVO> getBetBotsByChatId(Long chatId) {
        List<BotGroupVO> result = Lists.newArrayList();
        String betBotsUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_GET_BET_BOTS_BY_CHAT_ID.getApiName();
        Map<String, Object> betBotsParam = Maps.newHashMap();
        betBotsParam.put("groupId", chatId);
        String betBots = TgHttpClient4Util.doPost(betBotsUrl, betBotsParam, DEFAULT_USER_ID);
        if (StrUtil.isNotEmpty(betBots)) {
            ResponseEntity response = JSONObject.parseObject(betBots, ResponseEntity.class);
            if (response.getCode() == 0 && null != response.getData()) {
                result = JSONArray.parseArray(response.getData().toString(), BotGroupVO.class);
            }
        }
        return result;
    }

    public void betBotSendMessage(String phone, String msg, String chatName) {
        String betBotSendMessageUrl = betBotSendMessageDomain + RequestPathEnum.TELEGRAM_BET_BOT_SEND_MESSAGE.getApiName();
        Map<String, Object> betBotSendMessageParam = Maps.newHashMap();
        betBotSendMessageParam.put("phone", phone);
        betBotSendMessageParam.put("msg", msg);
        betBotSendMessageParam.put("groups", chatName);
        // 直接发送，不需要返回值
        TgHttpClient4Util.doPost(betBotSendMessageUrl, betBotSendMessageParam, DEFAULT_USER_ID);
    }

    public Integer getMinAmountLimit(String betContent, List<OddsAndLimitVO> redLimits) {
        Integer minAmount = 20;
        for (OddsAndLimitVO redLimit : redLimits) {
            if (betContent.equalsIgnoreCase(redLimit.getRuleCode())) {
                minAmount = redLimit.getMinAmount();
                break;
            }
        }
        return minAmount;
    }

    public boolean parseChat(TgChat tgChat) {
        if (null == tgChat || tgChat.getStatus() == Constants.close) {
            return false;
        }
        if (null == tgChat.getTableId()) {
            return false;
        }
//        if (null == tgChat.getMinAmount()) {
//            return false;
//        }
//        if (null == tgChat.getMaxAmount()) {
//            return false;
//        }
//        if (null == tgChat.getMaxShoeAmount()) {
//            return false;
//        }
        return true;
    }
}
