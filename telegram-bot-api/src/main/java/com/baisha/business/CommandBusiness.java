package com.baisha.business;

import cn.hutool.core.collection.CollUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.BetUserAmountVO;
import com.baisha.model.vo.BetUserVO;
import com.baisha.model.vo.ConfigInfo;
import com.baisha.model.vo.SealingLineVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static com.baisha.util.constants.BotConstant.*;

@Slf4j
@Service
public class CommandBusiness {

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
        //3.2 组装 局号+限红
        StringBuilder gameRule = new StringBuilder();
        gameRule.append(bureauNum);
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
        return gameRule.toString();
    }

    public String buildSealingLine(SealingLineVO vo) {
        // 封盘线
        StringBuilder sealingLine = new StringBuilder();
        sealingLine.append(vo.getBureauNum());
        sealingLine.append(SEALING_BET_INFO1);
        sealingLine.append(SEALING_BET_INFO2);
        return sealingLine.toString();
    }

    public String buildSealingLineMessage(ConfigInfo configInfo, SealingLineVO vo, BetUserAmountVO betUserAmountVO) {
        // 封盘线
        StringBuilder sealingLine = new StringBuilder();
        sealingLine.append(SEALING_BET_INFO3);
        sealingLine.append(SEALING_BET_INFO4);
        sealingLine.append(SEALING_BET_INFO5);
        sealingLine.append(SEALING_BET_INFO6);
        sealingLine.append(SEALING_BET_INFO7);
        sealingLine.append(SEALING_BET_INFO7);
        sealingLine.append(SEALING_BET_INFO7);
        sealingLine.append(SEALING_BET_INFO8);
        sealingLine.append(SEALING_BET_INFO9);
        sealingLine.append(SEALING_BET_INFO10);
        sealingLine.append(configInfo.getOnlyFinance());
        sealingLine.append(SEALING_BET_INFO11);
        sealingLine.append(SEALING_BET_INFO12);
        sealingLine.append(configInfo.getOnlyCustomerService());
        sealingLine.append(SEALING_BET_INFO13);
        sealingLine.append(SEALING_BET_INFO14);
        sealingLine.append(SEALING_BET_INFO15);
        sealingLine.append(SEALING_BET_INFO16);
        sealingLine.append(betUserAmountVO.getTotalBetAmount());
        sealingLine.append(SEALING_BET_INFO17);
        sealingLine.append(SEALING_BET_INFO18);
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
}
