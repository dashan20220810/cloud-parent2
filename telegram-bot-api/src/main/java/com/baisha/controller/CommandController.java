package com.baisha.controller;

import static com.baisha.util.constants.BotConstant.DEFAULT_USER_ID;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.baisha.business.CommandBusiness;
import com.baisha.model.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.business.TgBotBusiness;
import com.baisha.handle.CommonHandler;
import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.Base64Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "游戏指令推送")
@Slf4j
@RestController
@RequestMapping("command")
public class CommandController {

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private CommandBusiness commandBusiness;

    @Autowired
    private CommonHandler commonHandler;

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
            // TG群-全员解禁
            commandBusiness.unmuteAllUser(tgChat, myBot);

            String message = commandBusiness.buildStartMessage(vo.getBureauNum(), tgChat.getMinAmount() + "",
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
    public ResponseEntity sealingLine(@RequestBody SealingLineVO vo) throws Exception {
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
            // TG群-全员禁言
            commandBusiness.muteAllUser(chatId, myBot);
            // 组装TG信息
            String sealingLine = commandBusiness.buildSealingLine(vo);
            String message = commandBusiness.buildSealingLineMessage(configInfo, vo, betUserAmountVO);

            myBot.sendMessage(sealingLine, tgChat.getChatId()+"");
            myBot.sendMessage(message, tgChat.getChatId()+"");
        });
        return ResponseUtil.success();
    }

    @ApiOperation("开牌")
    @PostMapping("openCard")
    public ResponseEntity openCard(OpenCardVO vo) throws Exception {

        // 验证参数有效性
        if (!OpenCardVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        // 根据参数中的桌台ID，找到绑定该桌台的有效群
        List<TgChat> chatList = tgChatService.findByTableId(vo.getTableId());
        if (CollectionUtils.isEmpty(chatList)) {
            return ResponseUtil.success();
        }

        // 循环不同的群配置，组装不同的推送消息并发送
        //TODO,找出所有需要发送的群ID。遍历执行发送（要求多线程）
        URL openCardAddress = new URL(vo.getOpenCardAddress());
        URL resultAddress = new URL(vo.getResultAddress());
        URL roadAddress = new URL(vo.getRoadAddress());
        for (TgChat tgChat : chatList) {
            // 群审核通过，才发消息
            if(!Constants.open.equals(tgChat.getStatus())){
                continue;
            }
            MyTelegramLongPollingBot myBot = TgBotBusiness.myBotMap.get(tgChat.getBotName());
            if (myBot == null) {
                continue;
            }

            commandBusiness.showOpenCardButton(vo, openCardAddress, tgChat, myBot);
            myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(resultAddress))), tgChat.getChatId()+"");
            myBot.SendPhoto(new InputFile(Objects.requireNonNull(Base64Utils.urlToFile(roadAddress))), tgChat.getChatId()+"");
        }
        return ResponseUtil.success();
    }

    @ApiOperation("结算")
    @PostMapping("settlement")
    public ResponseEntity settlement(@RequestBody SettlementVO vo) throws Exception {
        // 验证参数有效性
        if (!SettlementVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        Map<Long, List<UserWinVO>> settlementInfo = vo.getSettlementInfo();
        settlementInfo.forEach((chatId, userWinVOs) -> {
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
            String settlementMessage = commandBusiness.buildSettlementMessage(vo, userWinVOs);
            myBot.sendMessage(settlementMessage, tgChat.getChatId()+"");
        });
        return ResponseUtil.success();
    }
}
