package com.baisha.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.handle.CommonHandler;
import com.baisha.model.TgBetBot;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.*;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.enums.BetOption;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.Base64Utils;
import com.baisha.util.TelegramBotUtil;
import com.baisha.util.constants.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

import static com.baisha.util.constants.BotConstant.*;
import static com.baisha.util.constants.CommonConstant.VIDEO_SUFFIX_MP4;

@Slf4j
@Service
public class CommandBusiness {

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private CommonHandler commonHandler;

    public void startNewBureauLoop(StartNewBureauVO vo, URL countdownAddress, TgChat tgChat) {
        // 验证群审核通过，才发消息
        if (!Constants.open.equals(tgChat.getStatus())) {
            return;
        }
        //3.1。  找出机器人实例
        TgBot tgBot = tgBotService.findById(tgChat.getBotId());
        if (tgBot == null) {
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgBot.getBotName());
        if (myBot == null) {
            return;
        }
        // TG群-全员解禁
        this.unmuteAllUser(tgChat, myBot);

        String message = this.buildStartMessage(vo.getBureauNum(), tgChat.getMinAmount() + "",
                tgChat.getMaxAmount() + "", tgChat.getMaxShoeAmount() + "");

        //3.3： 每个桌台推送开局消息
//        myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(imageAddress))), tgChat.getChatId()+"");
        // 倒计时视频
        myBot.SendAnimation(new InputFile(Objects.requireNonNull(Base64Utils.videoToFile(countdownAddress, VIDEO_SUFFIX_MP4))), tgChat.getChatId()+"");
        myBot.sendMessage(message, tgChat.getChatId()+"");
    }

    @Async
    public void sealingLineLoop(SealingLineVO vo, ConfigInfo configInfo, Long chatId, BetUserAmountVO betUserAmountVO) {
        TgChat tgChat = tgChatService.findByChatId(chatId);
        // 群审核通过，才发消息
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // TG群-全员禁言
        this.muteAllUser(chatId, myBot);
        // 组装TG信息
        String sealingLineMessage = this.buildSealingLineMessage(configInfo, vo, betUserAmountVO);
        myBot.sendMessage(sealingLineMessage, tgChat.getChatId()+"");
    }

    @Async
    public void openCardLoop(OpenCardVO vo, URL openCardAddress, URL videoResultAddress, URL picRoadAddress, TgChat tgChat) {
        // 群审核通过，才发消息
        if(!Constants.open.equals(tgChat.getStatus())){
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }

        if (null != openCardAddress) {
            this.showOpenCardButton(vo, openCardAddress, tgChat, myBot);
        }
        if (null != videoResultAddress) {
            myBot.SendAnimation(new InputFile(Objects.requireNonNull(Base64Utils.videoToFile(videoResultAddress, VIDEO_SUFFIX_MP4))), tgChat.getChatId()+"");
        }
        if (null != picRoadAddress) {
            myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(picRoadAddress))), tgChat.getChatId()+"");
        }
    }

    @Async
    public void settlementLoop(SettlementVO vo, Long chatId, List<UserWinVO> userWinVOs) {
        TgChat tgChat = tgChatService.findByChatId(chatId);
        // 群审核通过，才发消息
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // 组装结算信息
        String settlementMessage = this.buildSettlementMessage(vo, userWinVOs);
        myBot.sendMessage(settlementMessage, tgChat.getChatId()+"");
    }

    @Async
    public void botStartBet(TgChat tgChat) {
        // 群审核通过，才发消息
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // 根据chatId查询 所有的下注机器人


        List<TgBetBot> tgBetBots = Lists.newArrayList();
        tgBetBots.forEach(tgBetBot -> {
            int random = TelegramBotUtil.getRandom(10, 20);
            try {
                Thread.sleep(Long.parseLong(random + "000"));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 判断投注时间
            String startTime = tgBetBot.getBetStartTime();
            String endTime = tgBetBot.getBetEndTime();
            if (StrUtil.isEmpty(startTime) || StrUtil.isEmpty(endTime)) {
                return;
            }
            DateTime betStartTime = DateUtil.parse(startTime, "HH:mm:ss");
            DateTime betEndTime = DateUtil.parse(endTime, "HH:mm:ss");
            // 当前时间
            String now = DateUtil.format(new Date(), "HH:mm:ss");
            DateTime nowTime = DateUtil.parse(now, "HH:mm:ss");
            if (nowTime.isBefore(betStartTime) || nowTime.isAfter(betEndTime)) {
                // 不符合投注时间
                return;
            }
            // 判断投注频率
            Integer betFrequency = tgBetBot.getBetFrequency();
            if (CommonConstant.CONSTANT_0.equals(betFrequency)) {
                return;
            }
            int betFrequencyRandom = TelegramBotUtil.getRandom(1, 10);
            if (betFrequencyRandom > betFrequency) {
                // 投注频率 没有被随机上
                return;
            }
            // 判断投注内容
            String[] betContentStr = tgBetBot.getBetContents().split(",");
            List<String> betContents = Arrays.asList(betContentStr);
            // 自动投注机器人的乱序里，d,h,sb,ss   这些出现的频率设置的低一些
            if (betContents.contains(BetOption.ZD.name())) {
                betContents.add(BetOption.ZD.name());
                betContents.add(BetOption.ZD.name());
            }
            if (betContents.contains(BetOption.XD.name())) {
                betContents.add(BetOption.XD.name());
                betContents.add(BetOption.XD.name());
            }
            if (betContents.contains(BetOption.Z.name())) {
                betContents.add(BetOption.Z.name());
                betContents.add(BetOption.Z.name());
                betContents.add(BetOption.Z.name());
                betContents.add(BetOption.Z.name());
            }
            if (betContents.contains(BetOption.X.name())) {
                betContents.add(BetOption.X.name());
                betContents.add(BetOption.X.name());
                betContents.add(BetOption.X.name());
                betContents.add(BetOption.X.name());
            }
            int index = TelegramBotUtil.getRandom(0, betContents.size() - 1);
            // 随机的投注内容
            String betContent = betContents.get(index);
            // 计算限红、投注金额
            Long minAmountLimit;
            List<OddsAndLimitVO> redLimits = commonHandler.getRedLimit(DEFAULT_USER_ID);
            if (betContent.equals(BetOption.D.name())) {
                Long minAmountLimitZD = commonHandler.getMinAmountLimit(BetOption.ZD.name(), redLimits);
                Long minAmountLimitXD = commonHandler.getMinAmountLimit(BetOption.XD.name(), redLimits);
                minAmountLimit = minAmountLimitZD + minAmountLimitXD;
            } else if (betContent.equals(BetOption.SB.name())) {
                Long minAmountLimitZD = commonHandler.getMinAmountLimit(BetOption.ZD.name(), redLimits);
                Long minAmountLimitXD = commonHandler.getMinAmountLimit(BetOption.XD.name(), redLimits);
                Long minAmountLimitH = commonHandler.getMinAmountLimit(BetOption.H.name(), redLimits);
                minAmountLimit = minAmountLimitZD + minAmountLimitXD + minAmountLimitH;
            } else if (betContent.equals(BetOption.SS.name())) {
                betContent = BetOption.SS.name() + "2";
                minAmountLimit = commonHandler.getMinAmountLimit(betContent, redLimits);
            } else {
                minAmountLimit = commonHandler.getMinAmountLimit(betContent, redLimits);
            }
            List<BigDecimal> amounts = Lists.newArrayList();
            Integer minMultiple = tgBetBot.getMinMultiple();
            Integer maxMultiple = tgBetBot.getMaxMultiple();
            for (int i = minMultiple; i <= maxMultiple; i++) {
                amounts.add(NumberUtil.mul(minAmountLimit+"", i+""));
            }
            int indexAmount = TelegramBotUtil.getRandom(0, amounts.size() - 1);
            BigDecimal amount = amounts.get(indexAmount);
            // 下注机器人-开始下注
            myBot.sendMessage(betContent + amount, tgChat.getChatId()+"");
        });
    }

    public void muteAllUser(Long chatId, MyTelegramLongPollingBot myBot) {
        ChatPermissions chatPermissions = new ChatPermissions();
        chatPermissions.setCanSendMessages(false);
        chatPermissions.setCanSendMediaMessages(false);
        chatPermissions.setCanSendOtherMessages(false);
        chatPermissions.setCanSendPolls(false);
        SetChatPermissions setChatPermissions = new SetChatPermissions(chatId +"", chatPermissions);
        try {
            myBot.execute(setChatPermissions);
        } catch (TelegramApiException e) {
            log.error("TG群{}-全员禁言失败", chatId);
        }
    }

    public void unmuteAllUser(TgChat tgChat, MyTelegramLongPollingBot myBot) {
        ChatPermissions chatPermissions = new ChatPermissions();
        chatPermissions.setCanSendMessages(true);
        chatPermissions.setCanSendMediaMessages(true);
        chatPermissions.setCanSendOtherMessages(true);
        chatPermissions.setCanSendPolls(true);
        SetChatPermissions setChatPermissions = new SetChatPermissions(tgChat.getChatId() +"", chatPermissions);
        try {
            myBot.execute(setChatPermissions);
        } catch (TelegramApiException e) {
            log.error("TG群{}-全员解禁失败", tgChat.getChatId());
        }
    }

    public String buildStartMessage(String bureauNum, String minAmount, String maxAmount, String maxShoeAmount) {
        StringBuilder gameRule = new StringBuilder();
        gameRule.append(bureauNum);
        gameRule.append(GAME_RULE12);
        gameRule.append(GAME_RULE1);
        gameRule.append(GAME_RULE2);
        gameRule.append(GAME_RULE3);
        gameRule.append(GAME_RULE4);
        gameRule.append(GAME_RULE10);
        gameRule.append(GAME_RULE6);
        gameRule.append(minAmount);
        gameRule.append(GAME_RULE7);
        gameRule.append(maxAmount);
        gameRule.append(GAME_RULE8);
        gameRule.append(maxShoeAmount);
        gameRule.append(GAME_RULE9);
        gameRule.append(GAME_RULE10);
        gameRule.append(GAME_RULE11);
        return gameRule.toString();
    }

    public String buildSealingLineMessage(ConfigInfo configInfo, SealingLineVO vo, BetUserAmountVO betUserAmountVO) {
        // 封盘线
        StringBuilder sealingLine = new StringBuilder();
        sealingLine.append(vo.getBureauNum());
        sealingLine.append(SEALING_BET_INFO1);
        sealingLine.append(SEALING_BET_INFO2);
        sealingLine.append(SEALING_BET_INFO4);
        sealingLine.append(SEALING_BET_INFO5);
        sealingLine.append(SEALING_BET_INFO7);
        sealingLine.append(SEALING_BET_INFO7);
        sealingLine.append(SEALING_BET_INFO7);
        sealingLine.append(SEALING_BET_INFO5);
        sealingLine.append(SEALING_BET_INFO10);
        sealingLine.append(configInfo.getOnlyFinance());
        sealingLine.append(SEALING_BET_INFO17);
        sealingLine.append(SEALING_BET_INFO12);
        sealingLine.append(configInfo.getOnlyCustomerService());
        sealingLine.append(SEALING_BET_INFO17);
        sealingLine.append(SEALING_BET_INFO14);
        sealingLine.append(SEALING_BET_INFO16);
        sealingLine.append(betUserAmountVO.getTotalBetAmount());
        sealingLine.append(SEALING_BET_INFO17);
        sealingLine.append(GAME_RULE10);

        List<BetUserVO> top20Users = betUserAmountVO.getTop20Users();
        if (CollUtil.isEmpty(top20Users)) {
            sealingLine.append(SEALING_BET_INFO23);
            sealingLine.append(SEALING_BET_INFO22);
        } else if (0 < top20Users.size() && top20Users.size() <= 20) {
            sealingLine.append(SEALING_BET_INFO23);
        } else {
            sealingLine.append(SEALING_BET_INFO19);
        }
        top20Users.forEach(user -> {
            String username = user.getUsername();
            String betCommand = user.getBetCommand();
            sealingLine.append(SEALING_BET_INFO20);
            sealingLine.append(username);
            sealingLine.append(SEALING_BET_INFO21);
            sealingLine.append(betCommand);
            sealingLine.append(SEALING_BET_INFO17);
        });

        return sealingLine.toString();
    }

    public void showOpenCardButton(OpenCardVO vo, URL openCardAddress, TgChat tgChat, MyTelegramLongPollingBot myBot) {
        SendPhoto sp = new SendPhoto();
        sp.setChatId(tgChat.getChatId()+"");
        sp.setPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(openCardAddress))));
        // 设置按钮
        List<InlineKeyboardButton> firstRow = Lists.newArrayList();
        // 实时开牌俯视地址
        InlineKeyboardButton lookDownAddress = new InlineKeyboardButton();
        lookDownAddress.setText("实时开牌俯视地址");
        lookDownAddress.setCallbackData("实时开牌俯视地址");
        lookDownAddress.setUrl(vo.getLookDownAddress());
        // 实时开牌正面地址
        InlineKeyboardButton frontAddress = new InlineKeyboardButton();
        frontAddress.setText("实时开牌正面地址");
        frontAddress.setCallbackData("实时开牌正面地址");
        frontAddress.setUrl(vo.getFrontAddress());

        firstRow.add(lookDownAddress);
        firstRow.add(frontAddress);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(firstRow);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowList);
        sp.setReplyMarkup(inlineKeyboardMarkup);
        // 展示
        myBot.SendPhoto(sp);
    }

    public String buildSettlementMessage(SettlementVO vo, List<UserWinVO> userWinVOs) {
        // 结算
        StringBuilder settlement = new StringBuilder();
        settlement.append(SETTLEMENT1);
        settlement.append(vo.getBureauNum());
        settlement.append(SEALING_BET_INFO1);
        settlement.append(SETTLEMENT2);
        settlement.append(vo.getSettlementResult());
        settlement.append(SEALING_BET_INFO17);
        settlement.append(SEALING_BET_INFO14);

        if (CollUtil.isEmpty(userWinVOs)) {
            settlement.append(SEALING_BET_INFO22);
        }
        userWinVOs.forEach(userWinVO -> {
            String username = userWinVO.getUsername();
            String winAmount = userWinVO.getWinAmount();
            settlement.append(SEALING_BET_INFO20);
            settlement.append(username);
            settlement.append(SEALING_BET_INFO21);
            settlement.append(winAmount);
            settlement.append(SEALING_BET_INFO17);
        });

        return settlement.toString();
    }
}
