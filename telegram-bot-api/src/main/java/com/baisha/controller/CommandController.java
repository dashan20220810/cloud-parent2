package com.baisha.controller;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.business.TgBotBusiness;
import com.baisha.model.TgBot;
import com.baisha.model.vo.StartNewBureauVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.service.TgBotService;
import com.baisha.util.Base64Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.net.URL;
import java.util.Objects;

import static com.baisha.util.constants.BotConstant.*;
import static com.baisha.util.constants.BotConstant.GAME_RULE11;

@Api(tags = "游戏指令")
@Slf4j
@RestController
@RequestMapping("command")
public class CommandController {

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private TgBotBusiness tgBotBusiness;

    private static final MyTelegramLongPollingBot myTelegramLongPollingBot = new MyTelegramLongPollingBot();

    @ApiOperation("开始新局")
    @PostMapping("startNewBureau")
    public ResponseEntity receiveCommand(StartNewBureauVO startNewBureauVO) {
        // 获取参数
        String chatId = startNewBureauVO.getChatId();
        String username = startNewBureauVO.getUsername();
        String imageAddress = startNewBureauVO.getImageAddress();
        String bureauNum = startNewBureauVO.getBureauNum();
        Integer minAmount = startNewBureauVO.getMinAmount();
        Integer maxAmount = startNewBureauVO.getMaxAmount();
        Integer maxShoeAmount = startNewBureauVO.getMaxShoeAmount();
        // 初始化Telegram长链接
        TgBot tgBot = tgBotService.findByBotName(username);
        myTelegramLongPollingBot.setChatId(chatId);
        myTelegramLongPollingBot.setToken(tgBot.getBotToken());
        // "开始新局"图片
        URL url = tgBotBusiness.getTgURL(imageAddress);
        myTelegramLongPollingBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(url))));
        // 局号+限红
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
        myTelegramLongPollingBot.sendMessage(gameRule.toString());
        return ResponseUtil.success();
    }
}
