package com.baisha.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TelegramMyChatMemberHandler {

//    private final TgChatService tgChatService = new TgChatService();
    public TgChatService getTgChatService() {
        return TelegramBotUtil.getTgChatService();
    }

    public void myChatMemberHandler(MyTelegramLongPollingBot bot, Update update) {
        ChatMemberUpdated myChatMember = update.getMyChatMember();
        Chat chat = myChatMember.getChat();
        User from = myChatMember.getFrom();

        String chatId = chat.getId().toString();
        String chatName = chat.getTitle();

        // 一个TG群只绑定一个机器人
        TgChat tgChat = getTgChatService().findByChatId(chatId);
        if(ObjectUtils.isEmpty(tgChat) || StringUtils.isEmpty(tgChat.getChatId())){
            // 新增
            tgChat = new TgChat();
            tgChat.setChatId(chatId)
                    .setChatName(chatName)
                    .setBotName(bot.getUsername())
                    .setStatus(Constants.close);
            getTgChatService().save(tgChat);
            // 绑定
            bot.setChatId(chatId);
            bot.setChatName(chatName);
        }
    }
}
