package com.baisha.controller;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.business.TgBotBusiness;
import com.baisha.model.vo.ReceiveCommandVO;
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

import java.net.MalformedURLException;
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
    public ResponseEntity receiveCommand(ReceiveCommandVO receiveCommandVO) {
        // 参数校验
//        if (CommonUtil.checkNull(username, token)) {
//            return ResponseUtil.parameterNotNull();
//        }
        // 获取参数
        String chatId = receiveCommandVO.getChatId();
//        String imageAddress = receiveCommandVO.getImageAddress();
        String bureauNum = receiveCommandVO.getBureauNum();
        Integer minAmount = receiveCommandVO.getMinAmount();
        Integer maxAmount = receiveCommandVO.getMaxAmount();
        Integer maxShoeAmount = receiveCommandVO.getMaxShoeAmount();

        // 获取"开始新局"图片
//        URL url = getTgURL(imageAddress);
        myTelegramLongPollingBot.setChatId(chatId);
//        myTelegramLongPollingBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(url))));
        // 限红
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

    private URL getTgURL (String imageAddress) {
        URL url;
        try {
            url = new URL(imageAddress);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }
}
