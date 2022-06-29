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
                String userBalance = commonHandler.checkUserBalance(user.getId());
                String userName = (user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName());
                String text = "用户: " + userName + "\n" + "余额: " + userBalance;
                bot.showAlert(callbackQuery, text);
                break;
            case "唯一财务":
                ConfigInfo configInfo = commonHandler.getConfigInfo(user.getId());
                bot.showAlert(callbackQuery, configInfo.getOnlyCustomerService());
                break;
            default:
                break;
        }
    }
}
