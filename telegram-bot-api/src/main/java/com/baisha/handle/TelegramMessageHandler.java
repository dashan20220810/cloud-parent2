package com.baisha.handle;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.model.vo.TgBetVO;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.Map;

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

    public boolean registerEvery(User user, Chat chat, User from) {
        String userName = (user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName());
        // 设置请求参数
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", user.getId());
        param.put("nickname", userName);
        param.put("tgUserName", user.getUserName());
        param.put("groupId", chat.getId());
        if (!ObjectUtils.isEmpty(from) && from.getId() != null) {
            param.put("inviteTgUserId", from.getId());
        }
        param.put("tgGroupName", chat.getTitle());
        // 是否下注机器人(1正式 2测试 3机器人)
        param.put("userType", user.getIsBot() ? 3 : 1);
        // 远程调用
        String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_REGISTER_USER.getApiName();
        String forObject = TgHttpClient4Util.doPost(requestUrl, param, user.getId());

        if (ObjectUtils.isEmpty(forObject)) {
            log.error("{}群{}用户绑定失败，原因HTTP请求 NULL", chat.getId(), user.getId());
            return false;
        }
        ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
        if (result.getCode() == 0) {
            return true;
        }
        log.error("{}群{}用户绑定失败，原因:{}", chat.getId(), user.getId(), result.getMsg());
        return false;
    }

    public boolean leftChat(User leftChatMember, Chat chat) {
        // 设置请求参数
        Map<String, Object> param = Maps.newHashMap();
        param.put("id", leftChatMember.getId());
        param.put("groupId", chat.getId());
        // 远程调用
        String requestUrl = TelegramBotUtil.getCasinoWebDomain() + RequestPathEnum.TELEGRAM_LEFT_USER.getApiName();
        String forObject = TgHttpClient4Util.doPost(requestUrl, param, leftChatMember.getId());

        if (ObjectUtils.isEmpty(forObject)) {
            log.error("{}群{}用户离群失败，原因HTTP请求 NULL", chat.getId(), leftChatMember.getId());
            return false;
        }
        ResponseEntity result = JSONObject.parseObject(forObject, ResponseEntity.class);
        if (result.getCode() == 0) {
            return true;
        }
        log.error("{}群{}用户离群失败，原因:{}", chat.getId(), leftChatMember.getId(), result.getMsg());
        return false;
    }

    public void messageHandler(MyTelegramLongPollingBot bot, Update update) {
        Message message = update.getMessage();
        Chat chat = message.getChat();
        User from = message.getFrom();

        TgBot myBot = tgBotService.findByBotName(bot.getBotUsername());
        if (null == myBot) {
            return;
        }
        // 判断此群是否通过审核，未通过不处理消息。
        if (!commonHandler.checkChatIsAudit(chat)) {
            return;
        }

        // 新会员绑定事件
        List<User> users = message.getNewChatMembers();
        if (!CollectionUtils.isEmpty(users)) {
            for (User user : users) {
                boolean isSuccess = registerEvery(user, chat, from);
                // 注册成功推送消息
                if (isSuccess) {
                    showWords(user, chat, bot);
                }
            }
            return;
        }
        // 会员离群事件
        User leftChatMember = message.getLeftChatMember();
        if (null != leftChatMember) {
            // 分两种：1、群管理机器人离群 2、会员离群
            if (leftChatMember.getIsBot()) {
                TgBot tgBot = tgBotService.findByBotName(leftChatMember.getUserName());
                if (null != tgBot) {
                    TgChat tgChat = tgChatService.findByChatIdAndBotId(chat.getId(), tgBot.getId());
                    if (null != tgChat) {
                        tgChatService.deleteByChatIdAndBotId(chat.getId(), tgBot.getId());
                    }
                    return;
                }
            }
            leftChat(leftChatMember, chat);
            return;
        }

        // 下面都是：发送消息事件
        String originText = message.getText();
        if (StrUtil.isEmpty(originText)) {
            return;
        }
        if (originText.replace(" ", "").equals("+")) {
            boolean isSuccess = registerEvery(from, chat, null);
            // 注册成功推送消息
            if (isSuccess) {
                showWords(from, chat, bot);
            }
            return;
        }

        // 如果当前时间秒数 - 消息时间秒数 > 70，那么不再处理消息事件。
        long messageTime = message.getDate();
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - messageTime > 70) {
            return;
        }

        if (originText.replace(" ", "").contains("余额")) {
            // 查询余额，并拼接信息
            String userBalanceMessage = checkUserBalance(from);
            // 发送消息，展示按钮
            showButtonBalance(chat, bot, userBalanceMessage, message.getMessageId());
            return;
        }
        if (originText.replace(" ", "").contains("流水")) {
            // 查询流水，并拼接信息
            String runningWaterMessage = checkRunningWater(from, chat);
            // 发送消息，展示按钮
            showButtonBalance(chat, bot, runningWaterMessage, message.getMessageId());
            return;
        }
        if (originText.replace(" ", "").contains("返水")) {
            // 查询返水，并拼接信息
            String returnWaterMessage = checkReturnWater(from, chat);
            // 发送消息，展示按钮
            showButtonBalance(chat, bot, returnWaterMessage, message.getMessageId());
            return;
        }
        boolean isSuccess = tgUserBet(message, bot);
        if (isSuccess) {
            // 下注成功，回复会员
            String textParam = getTextParam(from);
            // 展示按钮
            showButton(chat, bot, textParam, message.getMessageId());
        }
    }

    private void showButton(Chat chat, MyTelegramLongPollingBot bot, String textParam, Integer messageId) {
        // 获取配置信息
        ConfigInfo configInfo = commonHandler.getConfigInfo(DEFAULT_USER_ID);
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
        onlyFinance.setUrl("tg://user?id=" + configInfo.getOnlyFinanceTgId());

        firstRow.add(checkUserBalance);
        firstRow.add(onlyFinance);

        List<InlineKeyboardButton> secondRow = Lists.newArrayList();
        // 白沙集团-博彩官方频道
        InlineKeyboardButton officialChannel = new InlineKeyboardButton();
        officialChannel.setText("白沙集团-博彩官方频道");
        officialChannel.setCallbackData("白沙集团-博彩官方频道");
        officialChannel.setUrl(configInfo.getOfficialGamingChannel());
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
        reply.append(USER_BALANCE1);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        BigDecimal userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private String checkUserBalance(User user) {
        StringBuilder reply = new StringBuilder();
        reply.append(USER_BALANCE1);
        reply.append(USER_BALANCE2);
        reply.append(user.getId());
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE3);
        reply.append((user.getFirstName() == null ? "" : user.getFirstName()) + (user.getLastName() == null ? "" : user.getLastName()));
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        BigDecimal userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private void showButtonBalance(Chat chat, MyTelegramLongPollingBot bot, String userBalanceMessage, Integer messageId) {
        // 获取配置信息
        ConfigInfo configInfo = commonHandler.getConfigInfo(DEFAULT_USER_ID);

        SendMessage sp = new SendMessage();
        sp.setChatId(chat.getId()+"");
        sp.setText(userBalanceMessage);
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
        onlyFinance.setUrl("tg://user?id=" + configInfo.getOnlyFinanceTgId());

        firstRow.add(checkUserBalance);
        firstRow.add(onlyFinance);

        List<InlineKeyboardButton> secondRow = Lists.newArrayList();
        // 查看流水
        InlineKeyboardButton getRunningWater = new InlineKeyboardButton();
        getRunningWater.setText("查看流水");
        getRunningWater.setCallbackData("查看流水");
        // 最近注单
        InlineKeyboardButton getRecentBet = new InlineKeyboardButton();
        getRecentBet.setText("最近注单");
        getRecentBet.setCallbackData("最近注单");

        secondRow.add(getRunningWater);
        secondRow.add(getRecentBet);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(firstRow);
        rowList.add(secondRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        sp.setReplyMarkup(inlineKeyboardMarkup);
        // 展示
        bot.SendMessage(sp);
    }

    private String checkRunningWater(User user, Chat chat) {
        StringBuilder reply = new StringBuilder();
        reply.append(USER_BALANCE1);
        reply.append(RUNNING_WATER1);
        // 当日流水
        BigDecimal flowOfDay = commonHandler.flowOfDay(user.getId(), chat.getId());
        reply.append(flowOfDay);
        reply.append(SEALING_BET_INFO17);
        reply.append(RUNNING_WATER2);
        // 当日盈利
        BigDecimal profitOfDay = commonHandler.profitOfDay(user.getId(), chat.getId());
        reply.append(profitOfDay);
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        BigDecimal userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private String checkReturnWater(User user, Chat chat) {
        StringBuilder reply = new StringBuilder();
        reply.append(USER_BALANCE1);
        reply.append(RUNNING_WATER1);
        // 当日流水
        BigDecimal flowOfDay = commonHandler.flowOfDay(user.getId(), chat.getId());
        reply.append(flowOfDay);
        reply.append(SEALING_BET_INFO17);
        reply.append(RETURN_WATER1);
        // 返水金额
        BigDecimal returnWaterAmount = commonHandler.returnWaterAmount(user.getId(), chat.getId());
        reply.append(returnWaterAmount);
        reply.append(SEALING_BET_INFO17);
        reply.append(USER_BALANCE4);
        // 查询用户余额
        BigDecimal userBalance = commonHandler.checkUserBalance(user.getId());
        reply.append(userBalance);
        return reply.toString();
    }

    private void showWords(User user, Chat chat, MyTelegramLongPollingBot bot) {
        // 获取配置信息
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
        welcome.append(configInfo.getOnlyFinance());
        welcome.append(SEALING_BET_INFO17);
        welcome.append(WELCOME6);
        welcome.append(configInfo.getOnlyCustomerService());
        welcome.append(SEALING_BET_INFO17);
        welcome.append(WELCOME7);
        bot.sendMessage(welcome.toString(), chat.getId()+"");
    }

    public boolean tgUserBet(Message message, MyTelegramLongPollingBot bot) {
        String originText = message.getText();
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
//            bot.sendMessage(username + " 下注信息错误，请参照下注规则", chatId+"", message.getMessageId());
            return false;
        }
        param.put("betOption", tgBetVO.getCommand());
        param.put("amount", tgBetVO.getAmount());
        param.put("tgChatId", chatId);
        TgChat tgChat = tgChatService.findByChatId(chatId);
        if (!commonHandler.parseChat(tgChat)) {
            return false;
        }
        param.put("tableId", tgChat.getTableId());
//        param.put("minAmount", tgChat.getMinAmount());
//        param.put("maxAmount", tgChat.getMaxAmount());
//        param.put("maxShoeAmount", tgChat.getMaxShoeAmount());

        log.info("下注请求开始:下注参数:{},下注会员:{}", param, userId);
        // 远程调用
        String forObject = TgHttpClient4Util.doPost(requestUrl, param, userId);

        log.info("下注请求结束:下注参数:{},下注会员:{},返回结果:{}", param, userId, forObject);

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
        bot.sendMessage(username + " 下注失败，原因：" + result.getMsg(), chatId+"", message.getMessageId());
        return false;
    }

    private TgBetVO parseBet(String originText) {
        TgBetVO result = new TgBetVO();
        try {
            String text = originText.toUpperCase().replace(" ", "");
            for (BetOption betOption : BetOption.getList()) {
                List<String> commands = betOption.getCommands();
                for (String command : commands) {
                    if (text.contains(command)) {
                        if (0 != text.indexOf(command)) {
                            return result;
                        }
                        if (0 != text.lastIndexOf(command)) {
                            return result;
                        }
                        long amount = Long.parseLong(text.replace(command, ""));
                        if (amount <= 0) {
                            return result;
                        }
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
}
