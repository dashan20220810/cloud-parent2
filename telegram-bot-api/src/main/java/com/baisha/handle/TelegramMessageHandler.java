package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.baisha.util.constants.BotConstant.*;

@Slf4j
@Component
public class TelegramMessageHandler {
    @Autowired
    TgChatService tgChatService;
    @Autowired
    TgBotService tgBotService;

    public boolean registerEvery(User user, Chat chat) {
        String userName = "";
        if (StrUtil.isNotEmpty(user.getFirstName())) {
            userName += user.getFirstName();
        }
        if (StrUtil.isNotEmpty(user.getLastName())) {
            userName += user.getLastName();
        }
        // 设置请求参数
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", user.getId());
        param.put("nickname", userName);
        param.put("groupId", chat.getId());
        // 远程调用
        String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_REGISTER_USER.getApiName();
        String forObject = TgHttpClient4Util.doPost(requestUrl, param, user.getId());

        if (ObjectUtils.isEmpty(forObject)) {
            log.error("{}群{}用户绑定失败，原因HTTP请求 NULL", chat.getId(), user.getId());
            return false;
        }
        ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
        // 在telegram中提示文字
        if (result.getCode() == 0) {
            // 注册成功欢迎语
            return true;
        }
        log.error("{}群{}用户绑定失败，原因:{}", chat.getId(), user.getId(), result.getMsg());
        return false;

    }

    public void messageHandler(MyTelegramLongPollingBot bot, Update update) {
        Message message = update.getMessage();
        Chat chat = message.getChat();

        // 判断此群消息，是否审核通过。未通过不处理
        TgBot tgBot = tgBotService.findByBotName(bot.getBotUsername());
        TgChat tgChat = tgChatService.findByChatIdAndBotId(chat.getId(), tgBot.getId());

        if (tgChat == null || Constants.close == tgChat.getStatus()) {
            return;
        }

        //新会员绑定事件
        List<User> users = message.getNewChatMembers();
        if (!CollectionUtils.isEmpty(users)) {
            for (User user : users) {
                boolean isSuccess = registerEvery( user, chat);
                //注册成功推送消息
                if (isSuccess) {
                    showWords(user, bot, chat);
                }
            }
            return;
        }

        // 下注
//        tgUserBet(message);

    }

    private void showWords(User user, MyTelegramLongPollingBot bot, Chat chat) {
        // 获取唯一财务
        String customerResult = getCustomer(user.getId());
        // 获取唯一客服
        String financeResult = getFinance(user.getId());
        // 注册成功之后的欢迎词
        StringBuilder welcome = new StringBuilder();
        welcome.append(WELCOME1);
        String username = (user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName());
        welcome.append(username);
        welcome.append(WELCOME2);
        welcome.append(chat.getTitle());
        welcome.append(WELCOME3);
        welcome.append(WELCOME4);
        welcome.append(WELCOME5);
        welcome.append(customerResult);
        welcome.append("\n");
        welcome.append(WELCOME6);
        welcome.append(financeResult);
        welcome.append("\n");
        welcome.append(WELCOME7);
        bot.sendMessage(welcome.toString(), chat.getId()+"");
    }

    private String getFinance(Long id) {
        String financeUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_PROP_FINANCE.getApiName();
        Map<String, Object> financeParam = Maps.newHashMap();
        String finance = TgHttpClient4Util.doPost(financeUrl, financeParam, id);
        String financeResult = "";
        if (StrUtil.isNotEmpty(finance)) {
            ResponseEntity response = JSONObject.parseObject(finance, ResponseEntity.class);
            financeResult = (String) response.getData();
        }
        return financeResult;
    }

    private String getCustomer(Long userId) {
        String customerUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_PROP_CUSTOMER.getApiName();
        Map<String, Object> customerParam = Maps.newHashMap();
        String customer = TgHttpClient4Util.doPost(customerUrl, customerParam, userId);
        String customerResult = "";
        if (StrUtil.isNotEmpty(customer)) {
            ResponseEntity response = JSONObject.parseObject(customer, ResponseEntity.class);
            customerResult = (String) response.getData();
        }
        return customerResult;
    }


//    public void tgUserBet(Message message) {
//        // 输入的text
//        String command = message.getText();
//        if (StrUtil.isNotEmpty(command)) {
//            User from = message.getFrom();
//            String userName = "";
//            String id = from.getId().toString();
//            if (StrUtil.isNotEmpty(from.getFirstName())) {
//                userName += from.getFirstName();
//            }
//            if (StrUtil.isNotEmpty(from.getLastName())) {
//                userName += from.getLastName();
//            }
//            // 开始调用
//            String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_BET.getApiName();
//            // 设置请求参数
//            Map<String, Object> param = Maps.newHashMap();
//            param.put("name", id);
//            param.put("nickname", userName);
//            param.put("groupId", bot.getChatId());
//            // 远程调用
//            String forObject = TgHttpClient4Util.doPost(requestUrl, param, id);
//            if (StrUtil.isNotEmpty(forObject)) {
//                ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
//                // 在telegram中提示文字
//                if (result.getCode() == 0) {
//                    bot.sendMessage(userName + " 注册会员成功！");
//                } else {
//                    bot.sendMessage(userName + " 注册会员失败！" + unicodeToString(result.getMsg()));
//                }
//                return;
//            }
//            bot.sendMessage(userName + " 注册会员失败！服务异常！");
//
//        }
//    }

    public static void main(String[] args) {
        String ss = "Z1000";
        String replace = ss.replace(" ", "");
        String[] split = replace.split("1");
        System.out.println(Arrays.toString(split));
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
