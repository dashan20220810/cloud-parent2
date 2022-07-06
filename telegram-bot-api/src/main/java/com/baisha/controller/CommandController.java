package com.baisha.controller;

import static com.baisha.util.constants.BotConstant.DEFAULT_USER_ID;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.baisha.business.CommandBusiness;
import com.baisha.model.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baisha.handle.CommonHandler;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;

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

        // 第一步，验证参数有效性
        if (!StartNewBureauVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        // 第二步:根据参数中的桌台ID,找到绑定该桌台的有效的群
        List<TgChat> chatList = tgChatService.findByTableId(vo.getTableId());
        if (CollectionUtils.isEmpty(chatList)) {
            return ResponseUtil.success();
        }
        log.info("桌台ID:{},局号:{}===群数量{}个", vo.getTableId(), vo.getBureauNum(), chatList.size());

        // 第三步: 循环不同的桌群配置，组装不同的推送消息并发送
        URL imageAddress = new URL(vo.getImageAddress());
        URL countdownAddress = new URL(vo.getCountdownAddress());
//        for (TgChat tgChat : chatList) {
//            commandBusiness.startNewBureauLoop(vo, imageAddress, countdownAddress, tgChat);
//        }
        List<CompletableFuture<TgChat>> futures = chatList.stream()
                .map(tgChat -> CompletableFuture.supplyAsync(() -> {
                    commandBusiness.startNewBureauLoop(vo, imageAddress, countdownAddress, tgChat);
                    return tgChat;
                }))
                .collect(Collectors.toList());
        chatList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        log.info("桌台ID:{},局号:{}===已发送的群数量{}个", vo.getTableId(), vo.getBureauNum(), chatList.size());

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
        tgBetInfo.forEach((chatId, betUserAmountVO) -> commandBusiness.sealingLineLoop(vo, configInfo, chatId, betUserAmountVO));
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
        URL openCardAddress = new URL(vo.getOpenCardAddress());
        URL resultAddress = new URL(vo.getResultAddress());
        URL roadAddress = new URL(vo.getRoadAddress());
        for (TgChat tgChat : chatList) {
            commandBusiness.openCardLoop(vo, openCardAddress, resultAddress, roadAddress, tgChat);
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
        settlementInfo.forEach((chatId, userWinVOs) -> commandBusiness.settlementLoop(vo, chatId, userWinVOs));
        return ResponseUtil.success();
    }
}
