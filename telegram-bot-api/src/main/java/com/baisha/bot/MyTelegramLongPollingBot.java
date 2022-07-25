package com.baisha.bot;

import com.baisha.handle.TelegramCallbackQueryHandler;
import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import com.baisha.util.TelegramBotUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class MyTelegramLongPollingBot extends TelegramLongPollingBot {

    // 机器人username
    private String username;

    // 机器人token
    private String token;

    public MyTelegramLongPollingBot(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public TelegramMyChatMemberHandler getTelegramMyChatMemberHandler() {
        return TelegramBotUtil.getTelegramMyChatMemberHandler();
    }

    public TelegramMessageHandler getTelegramMessageHandler() {
        return TelegramBotUtil.getTelegramMessageHandler();
    }

    public TelegramCallbackQueryHandler getTelegramCallbackQueryHandler() {
        return TelegramBotUtil.getTelegramCallbackQueryHandler();
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
        // 机器人绑定群
        if (update.hasMyChatMember()) {
            getTelegramMyChatMemberHandler().myChatMemberHandler(this, update);
            return;
        }
        // TG群会员的监听事件
        if (update.hasMessage()) {
            getTelegramMessageHandler().messageHandler(this, update);
            return;
        }
        // 按钮事件
        if (update.hasCallbackQuery()) {
            getTelegramCallbackQueryHandler().callbackQueryHandler(this, update);
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
     * TG弹框
     *
     * @param callbackQuery
     * @param text
     */
    public void showAlert(CallbackQuery callbackQuery, String text) {
        AnswerCallbackQuery callback = new AnswerCallbackQuery();
        callback.setCallbackQueryId(callbackQuery.getId());
        callback.setShowAlert(true);
        callback.setCacheTime(5);
        callback.setText(text);
        // answerCallbackQuery.setUrl("https://telegram.me/pengrad_test_bot?game=pengrad_test_game");
        try {
            execute(callback);
        } catch (TelegramApiException e) {
            log.error("TG弹框-异常:{}", text, e);
        }
    }

    /**
     * 发送消息(回复)
     *
     * @param msg
     */
    public void sendMessage(String msg, String chatId, Integer messageId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(msg);
        sm.setAllowSendingWithoutReply(false);
        sm.setReplyToMessageId(messageId);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("发送消息(回复)-异常,消息:{},群id:{},回复id:{}", msg, chatId, messageId, e);
        }
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMessage(String msg, String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setText(msg);
        sm.setAllowSendingWithoutReply(true);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("发送消息-异常,消息:{},群id:{}", msg, chatId, e);
        }
    }

    /**
     * 发送消息(带按钮)
     * @param sm
     */
    public void SendMessage(SendMessage sm) {
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("发送消息(带按钮)-异常:{}", sm, e);
        }
    }

    /**
     * 发送Html
     *
     * @param msg
     * @param chatId
     */
    public void SendMessageHtml(String msg, String chatId) {
        SendMessage sm = new SendMessage();
        sm.setChatId(chatId);
        sm.setParseMode(ParseMode.HTML);
        sm.setText(msg);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("发送Html-异常,消息:{},群id:{}", msg, chatId, e);
        }
    }

    /**
     * 发送Html(带按钮)
     *
     * @param sm
     */
    public void SendMessageHtml(SendMessage sm) {
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            log.error("发送Html(带按钮)-异常:{}", sm, e);
        }
    }

    /**
     * 发送照片
     *
     * @param file
     * @param chatId
     */
    public void SendPhoto(InputFile file, String chatId) {
        SendPhoto sp = new SendPhoto();
        sp.setChatId(chatId);
        sp.setPhoto(file);
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            log.error("发送照片-异常,照片:{},群id:{}", file, chatId, e);
        }
    }

    /**
     * 发送照片(带按钮)
     *
     * @param sp
     */
    public void SendPhoto(SendPhoto sp) {
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            log.error("发送照片(带按钮)-异常:{}", sp, e);
        }
    }

    /**
     * 发送影片
     *
     * @param file
     * @param chatId
     */
    public void SendAnimation(InputFile file, String chatId) {
        SendAnimation sa = new SendAnimation();
        sa.setChatId(chatId);
        sa.setAnimation(file);
        try {
            execute(sa);
        } catch (TelegramApiException e) {
            log.error("发送影片-异常,影片:{},群id:{}", file, chatId, e);
        }
    }

    /**
     * 发送影片(带按钮)
     *
     * @param sa
     */
    public void SendAnimation(SendAnimation sa) {
        try {
            execute(sa);
        } catch (TelegramApiException e) {
            log.error("发送影片(带按钮)-异常:{}", sa, e);
        }
    }
}
