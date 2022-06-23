package com.baisha.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
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
import org.telegram.telegrambots.meta.api.objects.*;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

@Slf4j
public class TelegramMyChatMemberHandler {

    public void myChatMemberHandler(MyTelegramLongPollingBot bot, Update update) {
        ChatMemberUpdated myChatMember = update.getMyChatMember();
        Chat chat = myChatMember.getChat();
        User from = myChatMember.getFrom();

        String chatId = chat.getId().toString();
        String chatName = chat.getTitle();
        bot.setChatId(chatId);
        bot.setChatName(chatName);
        // 保存TG群
        TgChat tgChat = new TgChat()
                .setChatId(chatId)
                .setChatName(chatName);
        new TgChatService().save(tgChat);

//        @Column(name = "status", nullable = false)Constants.open
//        @ApiModelProperty("状态 1正常 2禁用")
//        private Integer status;

    }



//    public void registerUser(String id, MyTelegramLongPollingBot bot, String userName) {
//        String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_REGISTER_USER.getApiName();
//        // 设置请求参数
//        Map<String, Object> param = Maps.newHashMap();
//        param.put("name", id);
//        param.put("nickname", userName);
//        param.put("groupId", bot.getChatId());
//        param.put("groupName", bot.getChatId());
//        // 远程调用
//        String forObject = TgHttpClient4Util.doPost(requestUrl, param, id);
//        if (StrUtil.isNotEmpty(forObject)) {
//            ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
//            // 在telegram中提示文字
//            if (result.getCode() == 0) {
//                bot.sendMessage(userName + " 注册会员成功！");
//            } else {
//                bot.sendMessage(userName + " 注册会员失败！" + unicodeToString(result.getMsg()));
//            }
//            return;
//        }
//        bot.sendMessage(userName + " 注册会员失败！服务异常！");
//    }
}
