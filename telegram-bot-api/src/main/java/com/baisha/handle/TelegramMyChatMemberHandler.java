package com.baisha.handle;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
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

        String chatName = chat.getTitle();
        // 新增TG群
        TgBot tgBot = tgBotService.findByBotName(bot.getBotUsername());
        TgChat tgChat = tgChatService.findByChatIdAndBotId(chat.getId(),tgBot.getId());
        if (null == tgChat) {
            tgChat = new TgChat();
            tgChat.setChatId(chat.getId()+"")
                    .setChatName(chatName)
                    .setBotName(bot.getBotUsername())
                    .setBotId(tgBot.getId())
                    .setStatus(Constants.close);
            tgChatService.save(tgChat);
        }
    }
}
