package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.model.vo.TgBetVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.TgHttpClient4Util;
import com.baisha.util.enums.RequestPathEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
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
    @Autowired
    CommonHandler commonHandler;

    public boolean registerEvery(MyTelegramLongPollingBot bot, User user, Chat chat, User from) {
        String userName = (user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName());
        // 设置请求参数
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", user.getId());
        param.put("nickname", userName);
        param.put("groupId", chat.getId());
        if(!ObjectUtils.isEmpty(from) && from.getId()!=null){
            param.put("inviteTgUserId",from.getId());
        }
        param.put("tgGroupName", chat.getTitle());
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
        User from = message.getFrom();

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
                boolean isSuccess = registerEvery(bot, user, chat, from);
                //注册成功推送消息
                if (isSuccess) {
                    showWords(user, bot, chat);
                }
            }
            return;
        }
        // 下注
        boolean isSuccess = tgUserBet(message, bot);
        if (isSuccess) {
            // 下注成功，回复会员
            String textParam = getTextParam(from);
            // 展示按钮
            showButton(chat, bot, textParam, message.getMessageId());
        }
    }

    private void showButton(Chat chat, MyTelegramLongPollingBot bot, String textParam, Integer messageId) {
        SendMessage sp = new SendMessage();
        sp.setChatId(chat.getId()+"");
        sp.setText(textParam);
        sp.setAllowSendingWithoutReply(false);
        sp.setReplyToMessageId(messageId);

        List<InlineKeyboardButton> firstRow = Lists.newArrayList();
        // 查看余额
        InlineKeyboardButton checkUserBalance = new InlineKeyboardButton();
        checkUserBalance.setText("查看余额");
        checkUserBalance.setCallbackData("查看余额");
        // 唯一财务
        InlineKeyboardButton onlyFinance = new InlineKeyboardButton();
        onlyFinance.setText("唯一财务");
        onlyFinance.setCallbackData("唯一财务");

        firstRow.add(checkUserBalance);
        firstRow.add(onlyFinance);

        List<InlineKeyboardButton> secondRow = Lists.newArrayList();
        // 白沙集团-博彩官方频道
        InlineKeyboardButton officialChannel = new InlineKeyboardButton();
        officialChannel.setText("白沙集团-博彩官方频道");
        officialChannel.setCallbackData("白沙集团-博彩官方频道");
        officialChannel.setUrl("http://wstgst-bc.live-gameclub.com/#/?G26");
        secondRow.add(officialChannel);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(secondRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        sp.setReplyMarkup(inlineKeyboardMarkup);
        // 展示
        bot.SendMessage(sp);
    }

    private String getTextParam(User user) {
        StringBuilder reply = new StringBuilder();
        reply.append(BET_SUCCESS);
        reply.append("----------------------------\n");
        reply.append("用户余额：");
        // 查询用户余额
        String userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private void showWords(User user, MyTelegramLongPollingBot bot, Chat chat) {
        // 获取唯一财务
        ConfigInfo configInfo = commonHandler.getConfigInfo(user.getId());
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
        welcome.append(configInfo.getOnlyCustomerService());
        welcome.append("\n");
        welcome.append(WELCOME6);
        welcome.append(configInfo.getOnlyFinance());
        welcome.append("\n");
        welcome.append(WELCOME7);
        bot.sendMessage(welcome.toString(), chat.getId()+"");
    }

    public boolean tgUserBet(Message message, MyTelegramLongPollingBot bot) {
        String originText = message.getText();
        if (StrUtil.isEmpty(originText)) {
            return false;
        }
        User user = message.getFrom();
        Long userId = user.getId();
        String username = (user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName());
        Long chatId = message.getChatId();
        // 开始调用
        String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_ORDER_BET.getApiName();
        // 设置参数
        Map<String, Object> param = Maps.newHashMap();
        TgBetVO tgBetVO = parseBet(originText);
        if (StrUtil.isEmpty(tgBetVO.getCommand()) || null == tgBetVO.getAmount()) {
            bot.sendMessage(username + " 下注信息错误，请参照下注规则", chatId+"");
            return false;
        }
        param.put("betOption", tgBetVO.getCommand());
        param.put("amount", tgBetVO.getAmount());
        param.put("tgChatId", chatId);
        TgChat tgChat = tgChatService.findByChatId(chatId);
        if (!parseChat(tgChat)) {
            return false;
        }
        param.put("tableId", tgChat.getTableId());
        param.put("minAmount", tgChat.getMinAmount());
        param.put("maxAmount", tgChat.getMaxAmount());
        param.put("maxShoeAmount", tgChat.getMaxShoeAmount());

        // 远程调用
        String forObject = TgHttpClient4Util.doPost(requestUrl, param, userId);
        log.info("下注参数:{},下注会员:{},返回结果:{}", param, userId, forObject);
        if (ObjectUtils.isEmpty(forObject)) {
            log.error("{}桌{}群{}用户,下注:{},下注失败,原因HTTP请求 NULL",
                    tgChat.getTableId(),
                    chatId,
                    userId,
                    originText);
            return false;
        }
        ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
        if (result.getCode() == 0) {
            return true;
        }
        log.error("{}桌{}群{}用户,下注:{},下注失败,原因:{}",
                tgChat.getTableId(),
                chatId,
                userId,
                originText,
                result.getMsg());
        // 输出错误原因
        bot.sendMessage(username + " 下注失败，原因：" + result.getMsg(), chatId+"");
        return false;
    }

    private boolean parseChat(TgChat tgChat) {
        if (null == tgChat || tgChat.getStatus() == Constants.close) {
            return false;
        }
        if (null == tgChat.getTableId()) {
            return false;
        }
        if (null == tgChat.getMinAmount()) {
            return false;
        }
        if (null == tgChat.getMaxAmount()) {
            return false;
        }
        if (null == tgChat.getMaxShoeAmount()) {
            return false;
        }
        return true;
    }

    private TgBetVO parseBet(String originText) {
        TgBetVO result = new TgBetVO();
        try {
            String text = originText.toUpperCase().replace(" ", "");
            for (BetOption betOption : BetOption.getList()) {
                Set<String> commands = betOption.getCommands();
                for (String command : commands) {
                    if (text.contains(command)) {
                        Long amount = Long.parseLong(text.replace(command, ""));
                        result.setCommand(betOption.name());
                        result.setAmount(amount);
                        return result;
                    }
                }
            }
        } catch (Throwable e) {
            return result;
        }
        return result;
    }

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
