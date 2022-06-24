package com.baisha.bot;

import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Data
public class MyTelegramLongPollingBot extends TelegramLongPollingBot {
    // 机器人username
    private String username;

    // 机器人token
    private String token;

    // 机器人对应的TG群id
    private String chatId;

    // 机器人对应的TG群名称

    private String chatName;

    public MyTelegramLongPollingBot(String username, String token) {
        this.username = username;
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // 进群监听1(绑定)机器人绑定群
        if (update.hasMyChatMember()) {
            new TelegramMyChatMemberHandler().myChatMemberHandler(this, update);
            return;
        }
        // 进群监听2(注册) + 消息监听
        if (update.hasMessage()) {
            // 消息处理
            new TelegramMessageHandler().messageHandler(this, update);
            return;
        }

        if (update.hasCallbackQuery()) {

            return;
        }

        if (update.hasCallbackQuery()) {
            log.info("===============hasCallbackQuery===================");
            return;
        }
        if (update.hasChannelPost()) {
            log.info("===============hasChannelPost===================");
            return;
        }
        if (update.hasChatMember()) {
            log.info("===============hasChatMember===================");
            return;
        }
        if (update.hasPoll()) {
            log.info("===============hasPoll===================");
            return;
        }
        if (update.hasChatJoinRequest()) {
            log.info("===============hasChatJoinRequest===================");
            return;
        }
    }

    /**
     * 发送文字
     *
     * @param msg
     */
    public void sendMessage(String msg) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setAllowSendingWithoutReply(true);
        sm.setText(msg);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param sm
     */
    public void SendMessage(SendMessage sm) {
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 傳送 html
     *
     * @param msg
     */
    public void SendMessageHtml(String msg) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setParseMode(ParseMode.HTML);
        sm.setText(msg);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void SendMessageHtml(SendMessage sm) {
//		SendMessage sm = new SendMessage();
//		sm.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
//		sm.setParseMode(ParseMode.HTML);
//		sm.setText(msg);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 傳送照片
     *
     * @param file
     */
    public void SendPhoto(InputFile file) {
        SendPhoto sp = new SendPhoto();
        sp.setChatId(chatId);
        sp.setPhoto(file);
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void SendPhoto(SendPhoto sp) {
//		SendPhoto sp = new SendPhoto();
//		sp.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
//		sp.setPhoto(file);
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * 傳送影片
     *
     * @param file
     */
    public void SendAnimation(InputFile file) {
        SendAnimation sa = new SendAnimation();
        sa.setChatId(chatId);
        sa.setAnimation(file);

        try {
            execute(sa);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void SendAnimation(SendAnimation sa) {
//		SendAnimation sa = new SendAnimation();
//		sa.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
//		sa.setAnimation(file);
        try {
            execute(sa);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
