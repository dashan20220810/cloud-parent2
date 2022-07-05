package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

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
        // 按钮名字
        String data = callbackQuery.getData();
        switch (data) {
            case "查看余额":
                String userBalanceMessage = checkUserBalance(user);
                bot.showAlert(callbackQuery, userBalanceMessage);
                break;
            case "唯一财务":
                String onlyFinanceMessage = getOnlyFinance(user);
                bot.showAlert(callbackQuery, onlyFinanceMessage);
                break;
            case "查看流水":
                String runningWaterMessage = checkRunningWater(user);
                bot.showAlert(callbackQuery, runningWaterMessage);
                break;
            case "最近注单":
                String recentBetMessage = checkRunningWater(user);
                bot.showAlert(callbackQuery, recentBetMessage);
                break;
            default:
                break;
        }
    }

    private String checkRunningWater(User user) {
        StringBuilder reply = new StringBuilder();
        reply.append(RUNNING_WATER1);
        // 当日流水
        reply.append("11000");
        reply.append(SEALING_BET_INFO17);
        reply.append(RUNNING_WATER2);
        // 当日盈利
        reply.append("11000");
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        String userBalance = commonHandler.checkUserBalance(user.getId());
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
        String userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private String getOnlyFinance(User user) {
        StringBuilder reply = new StringBuilder();
        reply.append(ONLY_FINANCE1);
        // 配置信息
        ConfigInfo configInfo = commonHandler.getConfigInfo(user.getId());
        reply.append(configInfo.getOnlyFinance());
        return reply.toString();
    }
}
