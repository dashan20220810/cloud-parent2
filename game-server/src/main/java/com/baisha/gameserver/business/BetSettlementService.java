package com.baisha.gameserver.business;

import com.baisha.gameserver.enums.BetOddsEnum;
import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.util.GameServerUtil;
import com.baisha.gameserver.util.contants.GameServerContants;
import com.baisha.gameserver.util.contants.UserServerContants;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.BetStatusEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.util.HttpClient4Util;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


/**
 * @author yihui
 */
@Slf4j
@Service
public class BetSettlementService {

    @Value("${project.server-url.user-server-domain}")
    private String userServerDomain;

    @Autowired
    private BetService betService;
    @Autowired
    private BetStatisticsService betStatisticsService;

    @Transactional(rollbackFor = Exception.class)
    public void betSettlement(BetSettleVO vo) {
        log.info("==============={}开始结算=================", vo.getNoActive());
        //下注成功状态
        int status = BetStatusEnum.BET.getCode();
        List<Bet> bets = betService.findBetNoSettle(vo.getNoActive(), status);
        if (CollectionUtils.isEmpty(bets)) {
            //没有注单数据，直接返回true
            log.info("noActive={}没有未结算的注单", vo.getNoActive());
            log.info("==============={}结束结算=================", vo.getNoActive());
            return;
        }
        int size = bets.size();
        log.info("{}====未结算注单===={}条", vo.getNoActive(), size);
        int splitSize = 30;
        List<List<Bet>> lists = GameServerUtil.splitList(bets, splitSize);
        List<CompletableFuture<List<Bet>>> futures = lists.stream()
                .map(item -> CompletableFuture.supplyAsync(() -> doBetSettlement(item, vo))).toList();
        lists = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        bets = trans(lists);
        int settleSize = bets.size();
        log.info("{}==未结算注单{}==结算注单{}条", vo.getNoActive(), size, settleSize);
        log.info("==============={}结束结算=================", vo.getNoActive());
    }

    /**
     * 结算
     *
     * @param item
     * @param vo
     * @return
     */
    private List<Bet> doBetSettlement(List<Bet> item, BetSettleVO vo) {
        String awardOption = vo.getAwardOption().toUpperCase();
        List<Bet> settleList = new ArrayList<>();
        for (Bet bet : item) {
            //总下注金额
            Long betAmount = getBetAmount(bet);
            boolean isWinFlag = isWinBet(bet, awardOption);
            BigDecimal finalAmount;
            BigDecimal winAmount;
            if (isWinFlag) {
                //中奖
                BetOddsEnum betOddsEnum = BetOddsEnum.getBetOddsByCode(awardOption);
                BigDecimal odds = betOddsEnum.getOdds();
                //中奖选项的下注金额
                Long winBetAmount = getWinBetAmount(bet, awardOption);
                //赢的钱
                BigDecimal winAwardAmount = odds.multiply(BigDecimal.valueOf(winBetAmount));
                //派彩=赢得钱+ 中奖选项的下注金额
                finalAmount = winAwardAmount.add(BigDecimal.valueOf(winBetAmount));
                //输赢金额
                winAmount = finalAmount.subtract(BigDecimal.valueOf(betAmount));
            } else {
                //未中奖
                finalAmount = BigDecimal.ZERO;
                winAmount = finalAmount.subtract(BigDecimal.valueOf(betAmount));
            }
            //修改注单数据
            int flag = betService.settleBet(bet.getId(), winAmount, finalAmount);
            if (flag > 0) {
                //打码量
                doReducePlayMoney(bet, BigDecimal.valueOf(betAmount));
                if (isWinFlag && finalAmount.compareTo(BigDecimal.ZERO) > 0) {
                    //派奖
                    doAddBalance(bet, bet.getNoActive(), finalAmount);
                }
            }
            //用户统计今日数据(输赢结果)
            statisticsWinAmount(bet, winAmount);
            settleList.add(bet);
        }
        return settleList;
    }

    private void statisticsWinAmount(Bet bet, BigDecimal winAmount) {
        Date createTime = bet.getCreateTime();
        String time = DateUtil.dateToyyyyMMdd(createTime);
        Integer day = Integer.parseInt(time);
        betStatisticsService.statisticsWinAmount(day, bet.getUserId(), bet.getTgChatId(), winAmount);
    }

    /**
     * 结算成功后，减去用户打码量
     *
     * @param bet
     * @param betAmount
     */
    private void doReducePlayMoney(Bet bet, BigDecimal betAmount) {
        Long userId = bet.getUserId();
        String url = userServerDomain + UserServerContants.ASSETS_PLAY_MONEY;
        Map<String, Object> param = new HashMap<>(16);
        param.put("userId", userId);
        param.put("playMoneyType", GameServerContants.EXPENSES);
        param.put("amount", betAmount);
        param.put("remark", "会员结算打码量，注单ID为" + bet.getId());
        param.put("relateId", bet.getId());
        param.put("changeType", PlayMoneyChangeEnum.SETTLEMENT.getCode());
        String result = HttpClient4Util.doPost(url, param);
        if (StringUtils.isEmpty(result)) {
            log.error("增加会员userId=" + userId + "的减少打码量" + betAmount + "失败,注单ID=" + bet.getId());
        }
    }

    /**
     * 派奖
     *
     * @param bet
     * @param noActive
     * @param finalAmount
     */
    private void doAddBalance(Bet bet, String noActive, BigDecimal finalAmount) {
        Long userId = bet.getUserId();
        String url = userServerDomain + UserServerContants.ASSETS_BALANCE;
        Map<String, Object> param = new HashMap<>(16);
        param.put("userId", userId);
        param.put("balanceType", GameServerContants.INCOME);
        param.put("amount", finalAmount);
        param.put("remark", "会员" + "在局号为" + noActive + "中奖");
        param.put("relateId", bet.getId());
        param.put("changeType", BalanceChangeEnum.WIN.getCode());
        String result = HttpClient4Util.doPost(url, param);
        if (StringUtils.isEmpty(result)) {
            log.error("增加会员userId=" + userId + "的彩金" + finalAmount + "失败,注单ID=" + bet.getId());
        }
    }


    /**
     * 下注金额
     *
     * @param bet
     * @return
     */
    private Long getBetAmount(Bet bet) {
        Long amount = bet.getAmountZ() + bet.getAmountX() + bet.getAmountH()
                + bet.getAmountZd() + bet.getAmountXd() + bet.getAmountSs();
        return amount;
    }

    /**
     * 获取中奖选择的下注金额
     *
     * @param bet
     * @return
     */
    private Long getWinBetAmount(Bet bet, String awardOption) {
        if (awardOption.equals(BetOddsEnum.Z.getCode())) {
            if (bet.getAmountZ() > 0) {
                return bet.getAmountZ();
            }
        }
        if (awardOption.equals(BetOddsEnum.X.getCode())) {
            if (bet.getAmountX() > 0) {
                return bet.getAmountX();
            }
        }
        if (awardOption.equals(BetOddsEnum.H.getCode())) {
            if (bet.getAmountH() > 0) {
                return bet.getAmountH();
            }
        }
        if (awardOption.equals(BetOddsEnum.ZD.getCode())) {
            if (bet.getAmountZd() > 0) {
                return bet.getAmountZd();
            }
        }
        if (awardOption.equals(BetOddsEnum.XD.getCode())) {
            if (bet.getAmountXd() > 0) {
                return bet.getAmountXd();
            }
        }
        if (awardOption.equals(BetOddsEnum.SS.getCode())) {
            if (bet.getAmountSs() > 0) {
                return bet.getAmountSs();
            }
        }
        return 0L;
    }


    /**
     * 注单 是否中奖
     *
     * @param bet
     * @param awardOption
     * @return
     */
    private boolean isWinBet(Bet bet, String awardOption) {
        if (awardOption.equals(BetOddsEnum.Z.getCode())) {
            if (bet.getAmountZ() > 0) {
                return true;
            }
        }
        if (awardOption.equals(BetOddsEnum.X.getCode())) {
            if (bet.getAmountX() > 0) {
                return true;
            }
        }
        if (awardOption.equals(BetOddsEnum.H.getCode())) {
            if (bet.getAmountH() > 0) {
                return true;
            }
        }
        if (awardOption.equals(BetOddsEnum.ZD.getCode())) {
            if (bet.getAmountZd() > 0) {
                return true;
            }
        }
        if (awardOption.equals(BetOddsEnum.XD.getCode())) {
            if (bet.getAmountXd() > 0) {
                return true;
            }
        }
        if (awardOption.equals(BetOddsEnum.SS.getCode())) {
            if (bet.getAmountSs() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换
     *
     * @param lists
     * @return
     */
    private List<Bet> trans(List<List<Bet>> lists) {
        List<Bet> list = new ArrayList<>();
        lists.forEach(item -> {
            list.addAll(item);
        });
        return list;
    }

}
