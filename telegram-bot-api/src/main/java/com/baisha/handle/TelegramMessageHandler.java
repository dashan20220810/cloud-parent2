package com.baisha.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.util.Base64Utils;
import com.baisha.util.enums.RequestPathEnum;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.baisha.handle.TelegramMyChatMemberHandler.getTgChatService;
import static com.baisha.util.constants.BotConstant.*;

@Slf4j
@Component
public class TelegramMessageHandler {

    public void messageHandler(MyTelegramLongPollingBot bot, Update update) {
        Message message = update.getMessage();
        String chatId = message.getChat().getId().toString();
        String title = message.getChat().getTitle();

        // 根据审核状态进行判断
        TgChat isAudit =
                getTgChatService().findByChatIdAndBotNameAndStatus(chatId, bot.getBotUsername(), Constants.open);
        if (null != isAudit) {
            // 多群动态绑定
            bot.setChatId(chatId);
            bot.setChatName(title);

            List<User> users = message.getNewChatMembers();
            // 新用户注册
            if (CollUtil.isNotEmpty(users)) {
                for (User user : users) {
                    if (!user.getIsBot()) {
                        // 只注册用户
                        registerEvery(bot, user);
                    }
                }
                return;
            }
            // 下注
//        tgUserBet(message);
        }
    }

    public void registerEvery(MyTelegramLongPollingBot bot, User user) {
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
        String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_REGISTER_USER.getApiName();
        // 设置请求参数
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", id);
        param.put("nickname", userName);
        param.put("groupId", bot.getChatId());
        // 远程调用
        String forObject = TgHttpClient4Util.doPost(requestUrl, param, id);
        if (StrUtil.isNotEmpty(forObject)) {
            ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
            // 在telegram中提示文字
            if (result.getCode() == 0) {
                // 注册成功欢迎语
                showWords(id, bot, userName);
            } else {
                bot.sendMessage(userName + " 注册会员失败！" + unicodeToString(result.getMsg()));
            }
            return;
        }
        bot.sendMessage(userName + " 注册会员失败！服务异常！");
    }

    private void showWords(String id, MyTelegramLongPollingBot bot, String userName) {
        // 获取唯一财务
        String customerResult = getCustomer(id);
        // 获取唯一客服
        String financeResult = getFinance(id);
        // 注册成功之后的欢迎词
        StringBuilder welcome = new StringBuilder();
        welcome.append(WELCOME1);
        welcome.append(userName);
        welcome.append(WELCOME2);
        welcome.append(bot.getChatName());
        welcome.append(WELCOME3);
        welcome.append(WELCOME4);
        welcome.append(WELCOME5);
        welcome.append(customerResult);
        welcome.append("\n");
        welcome.append(WELCOME6);
        welcome.append(financeResult);
        welcome.append("\n");
        welcome.append(WELCOME7);
        bot.sendMessage(welcome.toString());
        // 获取"开始新局"图片
        URL url = getTgImage(OPEN_NEW_GAME, id);
        bot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(url))));
        // 游戏规则
        String currentActive = getCurrentActive(bot.getChatId(), id);
        // 限红
        Map<String, Object> limitStakes = getLimitStakes(bot.getChatId(), id);
        Integer minAmount = (Integer) limitStakes.get("minAmount");
        Integer maxAmount = (Integer) limitStakes.get("maxAmount");
        Integer maxShoeAmount = (Integer) limitStakes.get("maxShoeAmount");
        StringBuilder gameRule = new StringBuilder();
        gameRule.append(currentActive);
        gameRule.append(GAME_RULE1);
        gameRule.append(GAME_RULE2);
        gameRule.append(GAME_RULE3);
        gameRule.append(GAME_RULE4);
        gameRule.append(GAME_RULE5);
        gameRule.append(GAME_RULE6);
        gameRule.append(minAmount);
        gameRule.append(GAME_RULE7);
        gameRule.append(maxAmount);
        gameRule.append(GAME_RULE8);
        gameRule.append(maxShoeAmount);
        gameRule.append(GAME_RULE9);
        gameRule.append(GAME_RULE10);
        gameRule.append(GAME_RULE11);
        bot.sendMessage(gameRule.toString());
    }

    private URL getTgImage (String tgImage, String id) {
        String imageUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_TG_IMAGE.getApiName();
        Map<String, Object> imageParam = Maps.newHashMap();
        imageParam.put("tgImageEnum", tgImage);
        String image = TgHttpClient4Util.doPost(imageUrl, imageParam, id);
        String imageResult = "";
        if (StrUtil.isNotEmpty(image)) {
            ResponseEntity response = JSONObject.parseObject(image, ResponseEntity.class);
            imageResult = (String) response.getData();
        }
        URL url;
        try {
            url = new URL(imageResult);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    private String getFinance(String id) {
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

    private String getCustomer(String id) {
        String customerUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_PROP_CUSTOMER.getApiName();
        Map<String, Object> customerParam = Maps.newHashMap();
        String customer = TgHttpClient4Util.doPost(customerUrl, customerParam, id);
        String customerResult = "";
        if (StrUtil.isNotEmpty(customer)) {
            ResponseEntity response = JSONObject.parseObject(customer, ResponseEntity.class);
            customerResult = (String) response.getData();
        }
        return customerResult;
    }

    private String getCurrentActive(String chatId, String id) {
        String currentActiveUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_TG_CURRENT_ACTIVE.getApiName();
        Map<String, Object> currentActiveParam = Maps.newHashMap();
        currentActiveParam.put("tgChatId", chatId);
        String currentActive = TgHttpClient4Util.doPost(currentActiveUrl, currentActiveParam, id);
        String currentActiveResult = "";
        if (StrUtil.isNotEmpty(currentActive)) {
            ResponseEntity response = JSONObject.parseObject(currentActive, ResponseEntity.class);
            currentActiveResult = (String) response.getData();
        }
        return currentActiveResult;
    }
    
    private Map<String, Object> getLimitStakes(String chatId, String id) {
        String limitStakesUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_TG_LIMIT_STAKES.getApiName();
        Map<String, Object> limitStakesParam = Maps.newHashMap();
        limitStakesParam.put("tgChatId", chatId);
        String limitStakes = TgHttpClient4Util.doPost(limitStakesUrl, limitStakesParam, id);
        Map<String, Object> limitStakesResult = Maps.newHashMap();
        if (StrUtil.isNotEmpty(limitStakes)) {
            ResponseEntity response = JSONObject.parseObject(limitStakes, ResponseEntity.class);
            limitStakesResult = (Map<String, Object>) response.getData();
        }
        return limitStakesResult;
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

        File file = new File( "192.168.26.24:9000/user/open_new_game.jpg");
        System.out.println(file);
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
