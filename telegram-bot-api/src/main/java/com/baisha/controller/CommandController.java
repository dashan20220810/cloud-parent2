package com.baisha.controller;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.business.TgBotBusiness;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.StartNewBureauVO;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
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

    @ApiOperation("开始新局")
    @PostMapping("startNewBureau")
    public ResponseEntity startNewBureau(StartNewBureauVO vo) throws MalformedURLException, IllegalAccessException {

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
            myBot.sendMessage(message, tgChat.getChatId()+"");
            // 倒计时视频
            myBot.SendAnimation(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(countdownAddress))), tgChat.getChatId()+"");
        }
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
}
