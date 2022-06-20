package com.baisha.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.enums.RequestPathEnum;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulejjwt.JjwtUtil;
import com.baisha.util.TelegramBotUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class TelegramMessageHandler {

    public void messageHandler(MyTelegramLongPollingBot bot, Update update) {
        List<User> users = update.getMessage().getNewChatMembers();
        // 新用户注册
        if (CollUtil.isNotEmpty(users)) {
            for (User user : users) {
                registerEvery(bot, user);
            }
            return;
        }

        //TODO 下注
    }

    private void registerEvery(MyTelegramLongPollingBot bot, User user) {
        String userName = "";
        String id = user.getId().toString();
        if (StrUtil.isNotEmpty(user.getFirstName())) {
            userName += user.getFirstName();
        }
        if (StrUtil.isNotEmpty(user.getLastName())) {
            userName += user.getLastName();
        }

        // 注册账号
        registerUser(id, bot, userName);
    }

    public void registerUser(String id, MyTelegramLongPollingBot bot, String userName) {
        String requestUrl= TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_REGISTER_USER.getApiName();
        // 设置请求参数
        Map<String, Object> param = Maps.newHashMap();
        param.put("userName", id);
        param.put("nickName", userName);

//        JjwtUtil.Subject subject=new JjwtUtil.Subject();
//        subject.setUserId(id);
//        String jwtToken=JjwtUtil.generic(subject, Constants.TELEGRAM_BOT_API);

        // 远程调用
        String forObject = HttpClient4Util.doPost(requestUrl, param);
        if (StrUtil.isNotEmpty(forObject)) {
            ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
            // 在telegram中提示文字
            if (result.getCode() == 0) {
                bot.sendMessage(userName + " 注册会员成功！");
            } else {
                bot.sendMessage(userName + " 注册会员失败！" + unicodeToString(result.getMsg()));
            }
            return;
        }
        bot.sendMessage(userName + " 注册会员失败！服务异常！");
    }

    /**
     * Unicode轉 漢字字串
     *
     * @param str \u6728
     * @return '木' 26408
     */
    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            // group 6728
            String group = matcher.group(2);
            // ch:'木' 26408
            ch = (char) Integer.parseInt(group, 16);
            // group1 \u6728
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + "");
        }
        return str;
    }

    /**
     * 查询余额
     * @param bucketName
     * @param id
     * @param chatId
     * @return String
     */
//    public String checkMemberBalance(String bucketName, Long id, String chatId) {
//        String text;
//        try {
//            // 组装参数
//            String member = bucketName + "@TG" + id + "_" + chatId.substring(1);
//            // 调用接口
//            JSONObject result = APIHandler.checkBalance(member);
//            String code = result.getString("ErrorCode");
//            if (!code.equals("0")) {
//                text = result.getString("ErrorMessage");
//            } else {
//                // 调用成功
//                String memberName = result.getString("MemberName");
//                String currency = result.getString("Currency");
//                String balance = result.getString("Balance");
//                text = "用户: " + memberName + ",\n" + "币种: " + currency + ",\n" + "余额: " + balance;
//            }
//        } catch (Throwable e) {
//            text = e.getMessage();
//        }
//        return text;
//    }

//    int ready_counter = 0;
//
//    public void ready() {
//        Timer timer = new Timer("ready");//
//
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
////				System.out.println("ready_counter is: " + ready_counter);
//                ready_counter++;
//
//                if (ready_counter == 1) {
//                    // TelegramExtension.telegramExtension.SendMessage("★★ 📝 核對表格 📝 ★★");
//                    InputFile file_jpg = new InputFile();
//                    java.io.File jpg;
//
//                    jpg = new java.io.File("resources/image/check_table.jpg");
//                    file_jpg.setMedia(jpg);
//                    TelegramBotUtil.telegramExtension.SendPhoto(file_jpg);
//
//                } else if (ready_counter == 2) {
//
//                    if (version == 1) {
//                        ThreadPool.getInstance().putThread(new BaccDrawTable1());
//                    } else {
//                        ThreadPool.getInstance().putThread(new BaccDrawTable1V2());
//                    }
//
//                } else if (ready_counter == 3) {
//                    InputFile file_jpg = new InputFile();
//                    java.io.File jpg;
//                    jpg = new java.io.File("resources/image/open.jpg");
//                    file_jpg.setMedia(jpg);
//
//                    SendPhoto sp = new SendPhoto();
//                    sp.setChatId(TelegramUtil.getStringResourceByKey("chat_id").trim());
//                    sp.setPhoto(file_jpg);
//                    if (version == 2) {
//                        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
////				        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
//                        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();
//                        InlineKeyboardButton inlineKeyboardButton3 = new InlineKeyboardButton();
//                        InlineKeyboardButton inlineKeyboardButton4 = new InlineKeyboardButton();
////				        inlineKeyboardButton1.setText("玩法说明");
////				        inlineKeyboardButton1.setCallbackData("how_to_play");
////				        inlineKeyboardButton1.setUrl("https://t.me/+Fav9bXn40Eo2Mzg1");
//                        inlineKeyboardButton2.setText("近景视频");
//                        inlineKeyboardButton2.setCallbackData("近景视频");
//                        inlineKeyboardButton2.setUrl(
//                                "https://play.dtlive.net/players/rtc_player.html?schema=https&api=1990&stream=G26&autostart=true");
//                        inlineKeyboardButton3.setText("远景视频");
//                        inlineKeyboardButton3.setCallbackData("vision_video");
//                        inlineKeyboardButton3.setUrl("http://wstgst-bc.live-gameclub.com/#/?G26");
//                        inlineKeyboardButton4.setText("上分唯一客服");
//                        inlineKeyboardButton4.setCallbackData("how_to_play2");
//                        inlineKeyboardButton4.setUrl("https://t.me/+7bBFwkj2bwliZGI1");
//
////				        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
//                        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
////				        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
//                        List<InlineKeyboardButton> keyboardButtonsRow4 = new ArrayList<>();
////				        keyboardButtonsRow1.add(inlineKeyboardButton1);
//                        // keyboardButtonsRow1.add(new
//                        // InlineKeyboardButton().setText("Fi4a").setCallbackData("CallFi4a"));
//                        keyboardButtonsRow2.add(inlineKeyboardButton2);
//                        keyboardButtonsRow2.add(inlineKeyboardButton3);
//                        keyboardButtonsRow4.add(inlineKeyboardButton4);
//                        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
////				        rowList.add(keyboardButtonsRow1);
//                        rowList.add(keyboardButtonsRow2);
////				        rowList.add(keyboardButtonsRow3);
//                        rowList.add(keyboardButtonsRow4);
//
//                        inlineKeyboardMarkup.setKeyboard(rowList);
//                        // message.setReplyMarkup(inlineKeyboardMarkup);
//                        sp.setReplyMarkup(inlineKeyboardMarkup);
//                    }
//                    TelegramBotUtil.telegramExtension.SendPhoto(sp);
//                    ready_counter = 0;
//                    timer.cancel();
//                }
//            }
//        };
//        timer.scheduleAtFixedRate(timerTask, 1000, 1000);//
//    }
}
