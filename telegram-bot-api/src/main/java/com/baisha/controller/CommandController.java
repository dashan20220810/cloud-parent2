package com.baisha.controller;

import static com.baisha.util.constants.BotConstant.DEFAULT_USER_ID;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.baisha.business.BetCommandBusiness;
import com.baisha.business.CommandBusiness;
import com.baisha.model.vo.*;
import com.baisha.modulespringcacheredis.util.RedisUtil;
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
    private RedisUtil redisUtil;

    @Autowired
    private Executor asyncExecutor;

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private CommandBusiness commandBusiness;

    @Autowired
    private BetCommandBusiness betCommandBusiness;

    @Autowired
    private CommonHandler commonHandler;

    @ApiOperation("开始新局")
    @PostMapping("startNewBureau")
    public ResponseEntity startNewBureau(StartNewBureauVO vo) throws Exception {
        log.info("===============开始新局==============={}", vo);

        // 第一步，验证参数有效性
        if (!StartNewBureauVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        // 局号存入redis
        redisUtil.set(vo.getTableId().toString(), vo.getBureauNum());

        // 第二步:根据参数中的桌台ID,找到绑定该桌台的有效的群
        List<TgChat> chatList = tgChatService.findByTableId(vo.getTableId());
        if (CollectionUtils.isEmpty(chatList)) {
            return ResponseUtil.success();
        }
        log.info("桌台ID:{},局号:{}======群数量{}个", vo.getTableId(), vo.getBureauNum(), chatList.size());

        // 第三步: 循环不同的桌群配置，组装不同的推送消息并发送
        URL countdownAddress = null;
        if (StrUtil.isNotEmpty(vo.getCountdownAddress())) {
            countdownAddress = new URL(vo.getCountdownAddress());
        }
        URL finalCountdownAddress = countdownAddress;

        List<CompletableFuture<TgChat>> futures = chatList.stream()
                .map(tgChat -> CompletableFuture.supplyAsync(() -> {
                    commandBusiness.startNewBureauLoop(vo, finalCountdownAddress, tgChat);
                    return tgChat;
                }, asyncExecutor))
                .collect(Collectors.toList());
        chatList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());

        log.info("桌台ID:{},局号:{}======已发送的群数量{}个", vo.getTableId(), vo.getBureauNum(), chatList.size());

        // 机器人-异步投注
        betCommandBusiness.botStartBet(chatList);

        return ResponseUtil.success();
    }

    @ApiOperation("封盘线")
    @PostMapping("sealingLine")
    public ResponseEntity sealingLine(@RequestBody SealingLineVO vo) throws Exception {
        log.info("===============封盘线==============={}", vo);
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
        log.info("===============开牌==============={}", vo);

        // 根据参数中的桌台ID，找到绑定该桌台的有效群
        List<TgChat> chatList = tgChatService.findByTableId(vo.getTableId());
        if (CollectionUtils.isEmpty(chatList)) {
            return ResponseUtil.success();
        }

        // 循环不同的群配置，组装不同的推送消息并发送
        URL openCardAddress = null;
        if (StrUtil.isNotEmpty(vo.getOpenCardAddress())) {
            openCardAddress = new URL(vo.getOpenCardAddress());
        }
        URL videoResultAddress = null;
        if (StrUtil.isNotEmpty(vo.getVideoResultAddress())) {
            videoResultAddress = new URL(vo.getVideoResultAddress());
        }
        URL picResultAddress = null;
        if (StrUtil.isNotEmpty(vo.getPicResultAddress())) {
            picResultAddress = new URL(vo.getPicResultAddress());
        }
        URL picRoadAddress = null;
        if (StrUtil.isNotEmpty(vo.getPicRoadAddress())) {
            picRoadAddress = new URL(vo.getPicRoadAddress());
        }
        for (TgChat tgChat : chatList) {
            commandBusiness.openCardLoop(vo, openCardAddress, videoResultAddress, picResultAddress, picRoadAddress, tgChat);
        }
        return ResponseUtil.success();
    }

    @ApiOperation("结算")
    @PostMapping("settlement")
    public ResponseEntity settlement(@RequestBody SettlementVO vo) throws Exception {
        log.info("===============结算==============={}", vo);
        // 验证参数有效性
        if (!SettlementVO.check(vo)) {
            return ResponseUtil.parameterNotNull();
        }

        Map<Long, List<UserWinVO>> settlementInfo = vo.getSettlementInfo();
        settlementInfo.forEach((chatId, userWinVOs) -> commandBusiness.settlementLoop(vo, chatId, userWinVOs));
        return ResponseUtil.success();
    }
}
