package com.baisha.handle;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.constants.RedisConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TelegramMyChatMemberHandler {

    @Autowired
    private RedissonClient redisson;

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
        // TG群id 使用redisson 公平锁
        RLock fairLock = redisson.getFairLock(RedisConstants.TG_CHAT_ID_PREFIX + chat.getId());
        try {
            boolean res = fairLock.tryLock(RedisConstants.WAIT_TIME, RedisConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (res) {
                TgChat tgChat = tgChatService.findByChatId(chat.getId());
                if (null == tgChat) {
                    tgChat = new TgChat();
                    tgChat.setChatId(chat.getId())
                            .setChatName(chat.getTitle())
                            .setBotName(tgBot.getBotName())
                            .setBotId(tgBot.getId())
                            .setStatus(Constants.close);
                    tgChatService.save(tgChat);
                }
                fairLock.unlock();
            }
        } catch (Exception e) {
            fairLock.unlock();
            log.error("机器人绑定群-异常,机器人id:{},机器人名称:{},群id:{},群名称:{}",
                    tgBot.getId(),
                    tgBot.getBotName(),
                    chat.getId(),
                    chat.getTitle(),
                    e);
        }
    }
}
