package com.baisha.handle;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

@Slf4j
@Component
public class TelegramMyChatMemberHandler {

    @Autowired
    TgChatService tgChatService;

    @Autowired
    TgBotService tgBotService;

    public void myChatMemberHandler(MyTelegramLongPollingBot bot, Update update) {
        ChatMemberUpdated myChatMember = update.getMyChatMember();
        Chat chat = myChatMember.getChat();

        // 新增TG群
        TgBot tgBot = tgBotService.findByBotName(bot.getBotUsername());
        if (null == tgBot) {
            return;
        }
        TgChat tgChat = tgChatService.findByChatId(chat.getId());
        if (null == tgChat) {
            tgChat = new TgChat();
            tgChat.setChatId(chat.getId())
                    .setChatName(chat.getTitle())
                    .setBotName(bot.getBotUsername())
                    .setBotId(tgBot.getId())
                    .setStatus(Constants.close);
            tgChatService.save(tgChat);
        }
    }
}
