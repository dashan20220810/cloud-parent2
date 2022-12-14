package com.baisha.business;

import cn.hutool.core.collection.CollUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.handle.CommonHandler;
import com.baisha.model.TgBetBot;
import com.baisha.model.TgChat;
import com.baisha.model.TgChatBetBotRelation;
import com.baisha.model.vo.*;
import com.baisha.modulecommon.Constants;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import com.baisha.service.TgBetBotService;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatBetBotRelationService;
import com.baisha.service.TgChatService;
import com.baisha.util.Base64Utils;
import com.baisha.util.constants.CommonConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPermissions;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static com.baisha.util.constants.BotConstant.*;

@Slf4j
@Service
public class CommandBusiness {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TgBetBotBusiness tgBetBotBusiness;

    @Autowired
    private TgBotService tgBotService;

    @Autowired
    private TgBetBotService tgBetBotService;

    @Autowired
    private TgChatService tgChatService;

    @Autowired
    private TgChatBetBotRelationService tgChatBetBotRelationService;

    @Autowired
    private CommonHandler commonHandler;

    public void startNewBureauLoop(StartNewBureauVO vo, URL countdownAddress, TgChat tgChat) {
        // ????????????????????????????????????
        if (!Constants.open.equals(tgChat.getStatus())) {
            return;
        }
        //3.1???  ?????????????????????
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // TG???-????????????
        this.unmuteAllUser(tgChat, myBot);

        //3.3??? ??????????????????????????????
        // ???????????????
        if (null != countdownAddress) {
            InputStream inputStream = null;
            try {
                log.info("[????????????]======[?????????]????????????:{}", countdownAddress);
                String str = countdownAddress.toString();
                inputStream = Base64Utils.videoUrlToStream(countdownAddress);
                myBot.SendAnimation(new InputFile(inputStream, str.substring(str.lastIndexOf("/") + 1)), tgChat.getChatId()+"");
            } catch (Exception e) {
                log.error("[????????????]======??????URL??????[?????????]?????????-??????,????????????:{}", countdownAddress);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String message = this.buildStartMessage(vo.getBureauNum(), tgChat.getMinAmount() + "",
                tgChat.getMaxAmount() + "", tgChat.getMaxShoeAmount() + "");
        myBot.sendMessage(message, tgChat.getChatId()+"");
        redisUtil.set(tgChat.getId() + CommonConstant.FORMER, 0);
    }

    @Async
    public void sealingLineLoop(SealingLineVO vo, ConfigInfo configInfo, Long chatId, BetUserAmountVO betUserAmountVO) {
        TgChat tgChat = tgChatService.findByChatId(chatId);
        // ??????????????????????????????
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // TG???-????????????
        this.muteAllUser(chatId, myBot);
        // ??????TG??????
        String sealingLineMessage = this.buildSealingLineMessage(configInfo, vo, betUserAmountVO);
        myBot.sendMessage(sealingLineMessage, tgChat.getChatId()+"");
        redisUtil.set(tgChat.getId() + CommonConstant.FORMER, 1);
    }

    @Async
    public void openCardLoop(OpenCardVO vo, URL openCardAddress, URL videoResultAddress, URL picResultAddress, URL picRoadAddress, TgChat tgChat) {
        // ??????????????????????????????
        if(!Constants.open.equals(tgChat.getStatus())){
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }

        if (null != openCardAddress) {
            for (int i = 0; i < 25; i++) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Object object = redisUtil.get(tgChat.getId() + CommonConstant.FORMER);
                if (null != object && (Integer)object == 1) {
                    this.showOpenCardButton(vo, openCardAddress, tgChat, myBot);
                    redisUtil.set(tgChat.getId() + CommonConstant.LATTER, 0);
                    return;
                }
            }
            this.showOpenCardButton(vo, openCardAddress, tgChat, myBot);
            redisUtil.set(tgChat.getId() + CommonConstant.LATTER, 0);
        }
        if (null != videoResultAddress) {
            InputStream inputStream = null;
            try {
                log.info("[??????]======[??????]????????????:{}", videoResultAddress);
                String str = videoResultAddress.toString();
                inputStream = Base64Utils.videoUrlToStream(videoResultAddress);
                myBot.SendAnimation(new InputFile(inputStream, str.substring(str.lastIndexOf("/") + 1)), tgChat.getChatId()+"");
            } catch (Exception e) {
                log.error("[??????]======??????URL??????[??????]?????????-??????,????????????:{}", videoResultAddress);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (null != picResultAddress) {
            InputStream inputStream = null;
            try {
                log.info("[??????]======[??????]????????????:{}", picResultAddress);
                String str = picResultAddress.toString();
                inputStream = Base64Utils.picUrlToStream(picResultAddress);
                myBot.SendPhoto(new InputFile(inputStream, str.substring(str.lastIndexOf("/") + 1)), tgChat.getChatId()+"");
            } catch (Exception e) {
                log.error("[??????]======??????URL??????[??????]?????????-??????,????????????:{}", picResultAddress);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (null != picRoadAddress) {
            InputStream inputStream = null;
            try {
                log.info("[??????]======[??????]????????????:{}", picRoadAddress);
                String str = picRoadAddress.toString();
                inputStream = Base64Utils.picUrlToStream(picRoadAddress);
                myBot.SendPhoto(new InputFile(inputStream, str.substring(str.lastIndexOf("/") + 1)), tgChat.getChatId()+"");
                redisUtil.set(tgChat.getId() + CommonConstant.LATTER, 1);
            } catch (Exception e) {
                log.error("[??????]======??????URL??????[??????]?????????-??????,????????????:{}", picRoadAddress);
                redisUtil.set(tgChat.getId() + CommonConstant.LATTER, 1);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Async
    public void settlementLoop(SettlementVO vo, Long chatId, List<UserWinVO> userWinVOs) {
        TgChat tgChat = tgChatService.findByChatId(chatId);
        // ??????????????????????????????
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        MyTelegramLongPollingBot myBot = ControlBotBusiness.myBotMap.get(tgChat.getBotName());
        if (myBot == null) {
            return;
        }
        // ??????????????????
        String settlementMessage = this.buildSettlementMessage(vo, userWinVOs);
        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Object object = redisUtil.get(tgChat.getId() + CommonConstant.LATTER);
            if (null != object && (Integer)object == 1) {
                myBot.sendMessage(settlementMessage, tgChat.getChatId()+"");
                return;
            }
        }
        myBot.sendMessage(settlementMessage, tgChat.getChatId()+"");
    }

    @Async
    public void botStartBet(TgChat tgChat) {
        // ??????????????????????????????
        if (!commonHandler.parseChat(tgChat)) {
            return;
        }
        // ??????chatId?????? ????????????????????????
        List<TgBetBot> tgBetBots = Lists.newArrayList();
        List<TgChatBetBotRelation> tgChatBetBotRelations = tgChatBetBotRelationService.findByTgChatId(tgChat.getId());
        tgChatBetBotRelations.forEach(tgChatBetBotRelation -> {
            TgBetBot tgBetBot = tgBetBotService.findById(tgChatBetBotRelation.getTgBetBotId());
            if (null != tgBetBot && tgBetBot.getStatus().equals(Constants.open)) {
                tgBetBots.add(tgBetBot);
            }
        });
        // ??????????????? ??????????????????
        List<OddsAndLimitVO> redLimits = commonHandler.getRedLimit(DEFAULT_USER_ID);
        log.info("????????????????????????:{}", redLimits);
        tgBetBots.forEach(tgBetBot -> tgBetBotBusiness.betBotSendMessage(tgChat, tgBetBot, redLimits));
    }

    public void muteAllUser(Long chatId, MyTelegramLongPollingBot myBot) {
        updateChatPermission(chatId, myBot, false);
    }

    public void unmuteAllUser(TgChat tgChat, MyTelegramLongPollingBot myBot) {
        updateChatPermission(tgChat.getChatId(), myBot, true);
    }

    private void updateChatPermission(Long chatId, MyTelegramLongPollingBot myBot, Boolean isOpen) {
        try {
            GetChat getChat = new GetChat(chatId.toString());
            Chat chat = myBot.execute(getChat);

            ChatPermissions permissions = chat.getPermissions();
            permissions.setCanSendMessages(isOpen);
            permissions.setCanSendMediaMessages(isOpen);
            permissions.setCanAddWebPagePreviews(isOpen);
            permissions.setCanSendPolls(isOpen);
            permissions.setCanSendOtherMessages(isOpen);
            permissions.setCanInviteUsers(permissions.getCanInviteUsers());

            SetChatPermissions setChatPermissions = new SetChatPermissions(chatId.toString(), permissions);
            myBot.execute(setChatPermissions);
        } catch (TelegramApiException e) {
            if (isOpen) {
                log.error("TG???{}-??????????????????", chatId);
            } else {
                log.error("TG???{}-??????????????????", chatId);
            }
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
        gameRule.append(GAME_RULE13);
        gameRule.append(GAME_RULE8);
        gameRule.append(maxShoeAmount);
        gameRule.append(GAME_RULE9);
        gameRule.append(GAME_RULE10);
        gameRule.append(GAME_RULE11);
        return gameRule.toString();
    }

    public String buildSealingLineMessage(ConfigInfo configInfo, SealingLineVO vo, BetUserAmountVO betUserAmountVO) {
        // ?????????
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
        InputStream inputStream = null;
        try {
            SendPhoto sp = new SendPhoto();
            sp.setChatId(tgChat.getChatId()+"");

            log.info("[??????]======[??????]????????????:{}", openCardAddress);
            String str = openCardAddress.toString();
            inputStream = Base64Utils.picUrlToStream(openCardAddress);
            sp.setPhoto(new InputFile(inputStream, str.substring(str.lastIndexOf("/") + 1)));

            // ????????????
            List<InlineKeyboardButton> firstRow = Lists.newArrayList();
            // ????????????????????????
            InlineKeyboardButton lookDownAddress = new InlineKeyboardButton();
            lookDownAddress.setText("????????????????????????");
            lookDownAddress.setCallbackData("????????????????????????");
            lookDownAddress.setUrl(vo.getLookDownAddress());
            // ????????????????????????
            InlineKeyboardButton frontAddress = new InlineKeyboardButton();
            frontAddress.setText("????????????????????????");
            frontAddress.setCallbackData("????????????????????????");
            frontAddress.setUrl(vo.getFrontAddress());

            firstRow.add(lookDownAddress);
            firstRow.add(frontAddress);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(firstRow);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);
            sp.setReplyMarkup(inlineKeyboardMarkup);
            // ??????
            myBot.SendPhoto(sp);
        } catch (Exception e) {
            log.error("[??????]======??????URL??????[??????]?????????-??????,????????????:{}", openCardAddress);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String buildSettlementMessage(SettlementVO vo, List<UserWinVO> userWinVOs) {
        // ??????
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
            String winStrAmount = userWinVO.getWinStrAmount();
            settlement.append(SEALING_BET_INFO20);
            settlement.append(username);
            settlement.append(SEALING_BET_INFO21);
            settlement.append(winStrAmount);
            settlement.append(SEALING_BET_INFO17);
        });

        return settlement.toString();
    }
}
