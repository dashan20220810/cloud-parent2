package com.baisha.business;

import cn.hutool.core.collection.CollUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.handle.CommonHandler;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.*;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.Base64Utils;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.baisha.util.constants.BotConstant.*;

@Slf4j
@Service
public class CommandBusiness {

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private CommonHandler commonHandler;

    public void startNewBureauLoop(StartNewBureauVO vo, URL imageAddress, URL countdownAddress, TgChat tgChat) {
        //验证群审核通过，才发消息
        if(!Constants.open.equals(tgChat.getStatus())){
            return;
        }
        //3.1。  找出机器人实例。
        TgBot tgBot = tgBotService.findById(tgChat.getBotId());
        if (tgBot == null) {
            return;
        }
        MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgBot.getBotName());
        if (myBot == null) {
            return;
        }
        // TG群-全员解禁
        this.unmuteAllUser(tgChat, myBot);

        String message = this.buildStartMessage(vo.getBureauNum(), tgChat.getMinAmount() + "",
                tgChat.getMaxAmount() + "", tgChat.getMaxShoeAmount() + "");

        //3.3： 每个桌台推送开局消息
        myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(imageAddress))), tgChat.getChatId()+"");
        myBot.SendMessageHtml(message, tgChat.getChatId()+"");
        // 倒计时视频
        myBot.SendAnimation(new InputFile(Objects.requireNonNull(Base64Utils.videoToFile(countdownAddress))), tgChat.getChatId()+"");
    }

    @Async
    public void sealingLineLoop(SealingLineVO vo, ConfigInfo configInfo, Long chatId, BetUserAmountVO betUserAmountVO) {
        TgChat tgChat = tgChatService.findByChatId(chatId);
        // 群审核通过，才发消息
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgChat.getBotName());
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
    public void openCardLoop(OpenCardVO vo, URL openCardAddress, URL resultAddress, URL roadAddress, TgChat tgChat) {
        // 群审核通过，才发消息
        if(!Constants.open.equals(tgChat.getStatus())){
            return;
        }
        MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }

        this.showOpenCardButton(vo, openCardAddress, tgChat, myBot);
        myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(resultAddress))), tgChat.getChatId()+"");
        myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(roadAddress))), tgChat.getChatId()+"");
    }

    @Async
    public void settlementLoop(SettlementVO vo, Long chatId, List<UserWinVO> userWinVOs) {
        TgChat tgChat = tgChatService.findByChatId(chatId);
        // 群审核通过，才发消息
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // 组装结算信息
        String settlementMessage = this.buildSettlementMessage(vo, userWinVOs);
        myBot.sendMessage(settlementMessage, tgChat.getChatId()+"");
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
        sealingLine.append(SEALING_BET_INFO19);

        List<BetUserVO> top20Users = betUserAmountVO.getTop20Users();
        if (CollUtil.isEmpty(top20Users)) {
            sealingLine.append(SEALING_BET_INFO22);
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
