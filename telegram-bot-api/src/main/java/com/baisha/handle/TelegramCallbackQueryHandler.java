package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.model.vo.RecentBetVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.baisha.util.constants.BotConstant.*;
import static com.baisha.util.constants.BotConstant.USER_BALANCE4;

@Slf4j
@Component
public class TelegramCallbackQueryHandler {

    @Autowired
    TgChatService tgChatService;

    @Autowired
    TgBotService tgBotService;

    @Autowired
    CommonHandler commonHandler;

    public void callbackQueryHandler(MyTelegramLongPollingBot bot, Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        // 消息
        Message message = callbackQuery.getMessage();
        User user = callbackQuery.getFrom();
        Chat chat = message.getChat();

        TgBot tgBot = tgBotService.findByBotName(bot.getBotUsername());
        if (null == tgBot) {
            return;
        }
        // 判断此群是否通过审核，未通过不处理消息。
        if (!commonHandler.checkChatIsAudit(chat, bot)) {
            return;
        }

        // 按钮名字
        String data = callbackQuery.getData();
        switch (data) {
            case "查看余额":
                String userBalanceMessage = checkUserBalance(user);
                bot.showAlert(callbackQuery, userBalanceMessage);
                break;
            case "查看流水":
                String runningWaterMessage = checkRunningWater(user, chat);
                bot.showAlert(callbackQuery, runningWaterMessage);
                break;
            case "最近注单":
                String recentBetMessage = checkRecentBet(user, chat);
                bot.showAlert(callbackQuery, recentBetMessage);
                break;
            default:
                break;
        }
    }

    private String checkRecentBet(User user, Chat chat) {
        StringBuilder reply = new StringBuilder();
        reply.append(RECENT_BET1);
        reply.append(RECENT_BET2);
        reply.append(RECENT_BET3);
        reply.append(RECENT_BET4);
        reply.append(SEALING_BET_INFO17);

        String commandSb;
        String commandD;
        String commandZd;
        String commandXd;
        String commandZ;
        String commandX;
        String commandH;
        String commandSs;
        List<RecentBetVO> recentBetVOS = recentBet(user.getId(), chat.getId());
        for (RecentBetVO recentBetVO : recentBetVOS) {
            if (0 != recentBetVO.getAmountZd()
                    && 0 != recentBetVO.getAmountXd()
                    && 0 != recentBetVO.getAmountH()) {
                commandSb = "SB" + recentBetVO.getAmountZd().toString();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandSb);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountZd()
                    && 0 != recentBetVO.getAmountXd()) {
                commandD = "D" + recentBetVO.getAmountZd().toString();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length()-4)));
                reply.append(RECENT_BET5);
                reply.append(commandD);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountZd()) {
                commandZd = "ZD" + recentBetVO.getAmountZd().toString();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandZd);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountXd()) {
                commandXd = "XD" + recentBetVO.getAmountXd().toString();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandXd);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountZ()) {
                commandZ = "Z" + recentBetVO.getAmountZ();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandZ);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountX()) {
                commandX = "X" + recentBetVO.getAmountX();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandX);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountH()) {
                commandH = "H" + recentBetVO.getAmountH();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandH);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
                continue;
            }
            if (0 != recentBetVO.getAmountSs()) {
                commandSs = "SS" + recentBetVO.getAmountSs();
                reply.append(TelegramBotUtil.format(recentBetVO.getNoActive().substring(recentBetVO.getNoActive().length() - 4)));
                reply.append(RECENT_BET5);
                reply.append(commandSs);
                reply.append(RECENT_BET5);
                reply.append(recentBetVO.getTotalAmount());
                reply.append(RECENT_BET5);
                reply.append(StrUtil.isEmpty(recentBetVO.getWinStrAmount()) ? "-" : recentBetVO.getWinStrAmount());
                reply.append(SEALING_BET_INFO17);
            }
        }
        return reply.toString();
    }

    private List<RecentBetVO> recentBet(Long userId, Long chatId) {
        List<RecentBetVO> result = Lists.newArrayList();
        String recentBetUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_RECENT_BET.getApiName();
        Map<String, Object> recentBetParam = Maps.newHashMap();
        recentBetParam.put("tgChatId", chatId);
        recentBetParam.put("queryAmount", 5);
        String recentBet = TgHttpClient4Util.doPost(recentBetUrl, recentBetParam, userId);
        
        if (StrUtil.isNotEmpty(recentBet)) {
            ResponseEntity response = JSONObject.parseObject(recentBet, ResponseEntity.class);
            if (response.getCode() == 0 && null != response.getData()) {
                result = JSONArray.parseArray(response.getData().toString(), RecentBetVO.class);
            }
        }
        return result;
    }

    private String checkRunningWater(User user, Chat chat) {
        StringBuilder reply = new StringBuilder();
        reply.append(RUNNING_WATER1);
        // 当日流水
        BigDecimal flowOfDay = commonHandler.flowOfDay(user.getId(), chat.getId());
        reply.append(flowOfDay);
        reply.append(SEALING_BET_INFO17);
        reply.append(RUNNING_WATER2);
        // 当日盈利
        BigDecimal profitOfDay = commonHandler.profitOfDay(user.getId(), chat.getId());
        reply.append(profitOfDay);
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        BigDecimal userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private String checkUserBalance(User user) {
        StringBuilder reply = new StringBuilder();
        reply.append(USER_BALANCE2);
        reply.append(user.getId());
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE3);
        reply.append((user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName()));
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        BigDecimal userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }
}
