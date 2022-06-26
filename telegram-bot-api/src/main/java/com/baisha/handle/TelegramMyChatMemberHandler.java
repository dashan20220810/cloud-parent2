package com.baisha.handle;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

@Slf4j
@Component
public class TelegramMyChatMemberHandler {

    public static TgChatService getTgChatService() {
        return TelegramBotUtil.getTgChatService();
    }

    public void myChatMemberHandler(MyTelegramLongPollingBot bot, Update update) {
        ChatMemberUpdated myChatMember = update.getMyChatMember();
        Chat chat = myChatMember.getChat();

        String chatId = chat.getId().toString();
        String chatName = chat.getTitle();
        // 新增TG群
        TgChat tgChat = getTgChatService().findByChatIdAndBotName(chatId, bot.getUsername());
        if (null == tgChat) {
            tgChat = new TgChat();
            tgChat.setChatId(chatId)
                    .setChatName(chatName)
                    .setBotName(bot.getUsername())
                    .setStatus(Constants.close);
            getTgChatService().save(tgChat);
        }
    }
}
