package com.baisha.controller;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.business.TgBotBusiness;
import com.baisha.handle.CommonHandler;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.*;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.Base64Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.baisha.util.constants.BotConstant.*;
import static com.baisha.util.constants.BotConstant.GAME_RULE11;

@Api(tags = "游戏指令推送")
@Slf4j
@RestController
@RequestMapping("command")
public class CommandController {

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    TgChatService tgChatService;

    @Autowired
    CommonHandler commonHandler;

    @ApiOperation("开始新局")
    @PostMapping("startNewBureau")
    public ResponseEntity startNewBureau(StartNewBureauVO vo) throws Exception {

        //第一步，验证参数有效性
        if (!StartNewBureauVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        //第二步:根据参数中的桌台ID,找到绑定该桌台的有效的群
        List<TgChat> chatList = tgChatService.findByTableId(vo.getTableId());
        if (CollectionUtils.isEmpty(chatList)) {
            return ResponseUtil.success();
        }

        //第三步: 循环不同的桌群配置，组装不同的推送消息并发送
        //TODO,找出所有需要发送的群ID。遍历执行发送（要求多线程）
        URL imageAddress = new URL(vo.getImageAddress());
        URL countdownAddress = new URL(vo.getCountdownAddress());
        for (TgChat tgChat : chatList) {
            //验证群审核通过，才发消息
            if(!Constants.open.equals(tgChat.getStatus())){
                continue;
            }
            //3.1。  找出机器人实例。
            TgBot tgBot = tgBotService.findById(tgChat.getBotId());
            if (tgBot == null) {
                continue;
            }
            MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgBot.getBotName());
            if (myBot == null) {
                continue;
            }
            String message = buildStartMessage(vo.getBureauNum(), tgChat.getMinAmount() + "",
                    tgChat.getMaxAmount() + "", tgChat.getMaxShoeAmount() + "");

            //3.3： 每个桌台推送开局消息
            myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(imageAddress))), tgChat.getChatId()+"");
            myBot.SendMessageHtml(message, tgChat.getChatId()+"");
            // 倒计时视频
            myBot.SendAnimation(new InputFile(Objects.requireNonNull(Base64Utils.videoToFile(countdownAddress))), tgChat.getChatId()+"");
        }
        return ResponseUtil.success();
    }

    @ApiOperation("封盘线")
    @PostMapping("sealingLine")
    public ResponseEntity sealingLine(SealingLineVO vo) throws Exception {
        // 验证参数有效性
        if (!SealingLineVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }
        // 获取配置信息
        ConfigInfo configInfo = commonHandler.getConfigInfo(DEFAULT_USER_ID);

        Map<Long, BetUserAmountVO> tgBetInfo = vo.getTgBetInfo();
        tgBetInfo.forEach((chatId, betUserAmountVO) -> {
            TgChat tgChat = tgChatService.findByChatId(chatId);
            // 群审核通过，才发消息
            if (!commonHandler.parseChat(tgChat)) {
                return;
            }
            MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgChat.getBotName());
            if (myBot == null) {
                return;
            }
            // 组装TG信息
            String sealingLine = buildSealingLine(vo);
            String message = buildSealingLineMessage(configInfo, vo, betUserAmountVO);

            myBot.sendMessage(sealingLine, tgChat.getChatId()+"");
            myBot.sendMessage(message, tgChat.getChatId()+"");
        });
        return ResponseUtil.success();
    }

    private String buildStartMessage(String bureauNum, String minAmount, String maxAmount, String maxShoeAmount) {
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

    private String buildSealingLine(SealingLineVO vo) {
        // 封盘线
        StringBuilder sealingLine = new StringBuilder();
        sealingLine.append(vo.getBureauNum());
        sealingLine.append(SEALING_BET_INFO1);
        sealingLine.append(SEALING_BET_INFO2);
        return sealingLine.toString();
    }

    private String buildSealingLineMessage(ConfigInfo configInfo, SealingLineVO vo, BetUserAmountVO betUserAmountVO) {
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
