package com.baisha.gameserver.business;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.model.bo.game.BetAwardBO;
import com.baisha.gameserver.model.bo.game.GameBaccOddsBO;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.util.BaccNoCommissionUtil;
import com.baisha.gameserver.util.GameServerUtil;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.BalanceChangeEnum;
import com.baisha.modulecommon.enums.BetStatusEnum;
import com.baisha.modulecommon.enums.PlayMoneyChangeEnum;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulecommon.vo.mq.gameServer.UserBetStatisticsVO;
import com.baisha.modulecommon.vo.mq.userServer.BetAmountVO;
import com.baisha.modulecommon.vo.mq.userServer.BetSettleUserVO;
import com.baisha.modulecommon.vo.mq.userServer.PlayMoneyAmountVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;


/**
 * @author yihui
 * 结算
 */
@Slf4j
@Service
public class BetSettlementBusiness {

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private Executor asyncExecutor;
    @Autowired
    private BetService betService;
    @Autowired
    private BetStatisticsService betStatisticsService;
    @Autowired
    private GameBusiness gameBusiness;
    @Autowired
    private BetBusiness betBusiness;
    @Autowired
    private AssetsBusiness assetsBusiness;

    public void betSettlement(BetSettleVO vo) {
        log.info("==============={}开始结算=================", vo.getNoActive());
        //下注成功状态
        int status = BetStatusEnum.BET.getCode();
        Long searchBetStart = System.currentTimeMillis();
        List<Bet> bets = betService.findBetNoSettle(vo.getNoActive(), status);
        log.info("查询局={}注单耗时{}毫秒", vo.getNoActive(), System.currentTimeMillis() - searchBetStart);
        if (CollectionUtils.isEmpty(bets)) {
            //没有注单数据，直接返回true
            log.info("noActive={}没有未结算的注单", vo.getNoActive());
            log.info("==============={}结束结算=================", vo.getNoActive());
            return;
        }

        int size = bets.size();
        log.info("{}====未结算注单===={}条", vo.getNoActive(), size);
        //获取百家乐玩法赔率
        GameBaccOddsBO gameBaccOdds = gameBusiness.getBaccOdds();
        int splitSize = 10;
        List<List<Bet>> lists = GameServerUtil.splitList(bets, splitSize);
        List<CompletableFuture<List<Bet>>> futures = lists.stream()
                .map(item -> CompletableFuture.supplyAsync(() ->
                        doBetSettlement(item, vo, gameBaccOdds, false, false), asyncExecutor)).toList();
        lists = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
        bets = trans(lists);
        int settleSize = bets.size();
        log.info("{}==未结算注单{}==结算注单{}条", vo.getNoActive(), size, settleSize);
        log.info("==============={}结束结算=================", vo.getNoActive());
    }

    /**
     * isReopen
     *
     * @param item
     * @param vo
     * @param gameBaccOdds
     * @param isReopen     是否重新开牌
     * @param isReturn     是否返水
     * @return
     */
    private List<Bet> doBetSettlement(List<Bet> item, BetSettleVO vo, GameBaccOddsBO gameBaccOdds,
                                      boolean isReopen, boolean isReturn) {
        //强制大写
        vo.setAwardOption(vo.getAwardOption().toUpperCase());
        List<Bet> settleList = new ArrayList<>();
        for (Bet bet : item) {
            //发生异常，不能影响其他注单 使用try catch
            try {
                //派奖
                BigDecimal finalAmount = BigDecimal.ZERO;
                BigDecimal winAmount;
                //总下注金额
                Long betAmount = getBetAmount(bet);
                //因为 中奖的奖项 可以 传多个(类似Z,ZD多个中奖) ,所以要循环
                String[] awardOptionArr = vo.getAwardOption().split(",");
                StringBuffer settleRemarkBuffer = new StringBuffer();
                for (String awardOption : awardOptionArr) {
                    BetAwardBO betAwardBO = BaccNoCommissionUtil.getBetAward(bet, awardOption, gameBaccOdds);
                    if (Objects.isNull(betAwardBO)) {
                        log.error("没有对应中奖选项awardOption={}", awardOption);
                        continue;
                    }
                    log.info("局{} 中奖选项{} 注单Id{} 中奖结果{}", vo.getNoActive(), awardOption, betAwardBO.getId(), JSONObject.toJSONString(betAwardBO));
                    finalAmount = finalAmount.add(betAwardBO.getFinalAmount());
                    settleRemarkBuffer.append(StringUtils.isNotEmpty(betAwardBO.getRemark()) ? betAwardBO.getRemark() : "");
                }
                winAmount = finalAmount.subtract(BigDecimal.valueOf(betAmount));
                //修改注单数据
                int flag = betService.settleBet(bet.getId(), winAmount, finalAmount, settleRemarkBuffer.toString());
                if (flag > 0) {
                    //没有输赢金额 打码量为0
                    BigDecimal playMoney = BigDecimal.ZERO;
                    if (winAmount.compareTo(BigDecimal.ZERO) != 0) {
                        playMoney = BigDecimal.valueOf(betAmount);

                        log.info("输赢金额 {}", winAmount);
                        //用户统计今日数据(输赢结果)
                        statisticsWinAmount(bet, winAmount);

                        log.info("通知后台更新输赢{}", winAmount);
                        UserBetStatisticsVO userBetStatisticsVO = UserBetStatisticsVO.builder().userId(bet.getUserId())
                                .betTime(DateUtil.getSimpleDateFormat().format(bet.getCreateTime()))
                                .winAmount(winAmount).build();
                        String userBetStatisticsJsonStr = JSONObject.toJSONString(userBetStatisticsVO);
                        log.info("发送给后台MQ消息：{}", userBetStatisticsJsonStr);
                        rabbitTemplate.convertAndSend(MqConstants.BACKEND_BET_SETTLEMENT_STATISTICS, userBetStatisticsJsonStr);
                    }

                    log.info("=======================================================================================");
                    log.info("通知用户中心更新余额{}和打码量{}", finalAmount, betAmount);
                    BetSettleUserVO betSettleUserVO = BetSettleUserVO.builder().betId(bet.getId()).noActive(bet.getNoActive())
                            .userId(bet.getUserId()).finalAmount(finalAmount)
                            .playMoney(playMoney)
                            .isReopen(isReopen ? Constants.open : Constants.close)
                            .remark(settleRemarkBuffer.toString()).build();
                    String betSettleUserJsonStr = JSONObject.toJSONString(betSettleUserVO);
                    log.info("派奖-发送给用户中心MQ消息：{}", betSettleUserJsonStr);
                    rabbitTemplate.convertAndSend(MqConstants.USER_SETTLEMENT_ASSETS, betSettleUserJsonStr);

                    //如果是返水是true，就需要重新返水
                    if (isReturn) {
                        bet.setWinAmount(winAmount);
                        log.info("重新开牌，重新返水");
                        betBusiness.updateReturnAmountReopen(bet);
                    }
                }
                settleList.add(bet);
            } catch (Exception e) {
                log.error("结算异常：{}", e.getMessage());
                e.printStackTrace();
                continue;
            }
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
     * 转换
     *
     * @param lists
     * @return
     */
    private List<Bet> trans(List<List<Bet>> lists) {
        List<Bet> list = new ArrayList<>();
        lists.forEach(list::addAll);
        return list;
    }

    /**
     * 重新开牌 - 重新结算
     *
     * @param vo
     */
    public void betReopenSettlement(BetSettleVO vo) {
        log.info("==============={}重新开牌 - 重新结算 - 开始=================", vo.getNoActive());
        //查询当前局的注单
        Long searchBetStart = System.currentTimeMillis();
        List<Bet> bets = betService.findByNoActive(vo.getNoActive());
        log.info("查询局={}注单耗时{}毫秒", vo.getNoActive(), System.currentTimeMillis() - searchBetStart);
        if (CollectionUtils.isEmpty(bets)) {
            //没有注单数据，直接返回true
            log.info("noActive={}没有注单", vo.getNoActive());
            log.info("==============={}结束 重新开牌 - 重新结算=================", vo.getNoActive());
            return;
        }

        int size = bets.size();
        log.info("{}====注单===={}条", vo.getNoActive(), size);
        //获取百家乐玩法赔率
        GameBaccOddsBO gameBaccOdds = gameBusiness.getBaccOdds();
        for (Bet bet : bets) {
            doOperate(bet, vo, gameBaccOdds);
        }
        log.info("==============={}重新开牌 - 重新结算 - 结束======耗时{}毫秒=========",
                vo.getNoActive(), System.currentTimeMillis() - searchBetStart);
    }

    /**
     * 单条记录  -- 操作
     *
     * @param bet
     * @param vo
     */
    private void doOperate(Bet bet, BetSettleVO vo, GameBaccOddsBO gameBaccOdds) {
        //结算时间 不为空 就表示 已经结算了
        if (null != bet.getSettleTime()) {

            BigDecimal return_winAmount = BigDecimal.ZERO.subtract(bet.getWinAmount());
            if (return_winAmount.compareTo(BigDecimal.ZERO) != 0) {
                log.info("返回 - 输赢金额 {}", return_winAmount);
                //返回-用户统计今日数据(输赢结果)
                statisticsWinAmount(bet, return_winAmount);

                //返回-后台统计该注单重新开牌
                log.info("重新开牌-通知后台更新输赢{}", return_winAmount);
                UserBetStatisticsVO userBetStatisticsVO = UserBetStatisticsVO.builder().userId(bet.getUserId())
                        .betTime(DateUtil.getSimpleDateFormat().format(bet.getCreateTime()))
                        .winAmount(return_winAmount).build();
                String userBetStatisticsJsonStr = JSONObject.toJSONString(userBetStatisticsVO);
                log.info("重新开牌-发送给后台MQ消息：{}", userBetStatisticsJsonStr);
                rabbitTemplate.convertAndSend(MqConstants.BACKEND_BET_SETTLEMENT_STATISTICS, userBetStatisticsJsonStr);
            }

            if (bet.getWinAmount().compareTo(BigDecimal.ZERO) != 0) {
                //输赢不为空 就要把之前 减的打码量 加回来
                //总下注金额
                Long betAmount = getBetAmount(bet);
                log.info("重新开牌-通知用户中心-加回之前打码量-更新打码量{}", betAmount);
                String remark = bet.getNoActive() + "重新开牌,加回之前打码量";
                PlayMoneyAmountVO playMoneyAmountVO = PlayMoneyAmountVO.builder().betId(bet.getId()).noActive(bet.getNoActive())
                        .changeType(PlayMoneyChangeEnum.BET_REOPEN.getCode())
                        .userId(bet.getUserId()).playMoney(BigDecimal.valueOf(betAmount)).remark(remark).build();
                String playMoneyAmountVOJsonStr = JSONObject.toJSONString(playMoneyAmountVO);
                log.info("重新开牌-加回打码量-发送给用户中心MQ消息：{}", playMoneyAmountVOJsonStr);
                rabbitTemplate.convertAndSend(MqConstants.USER_ADD_PLAYMONEY_ASSETS, playMoneyAmountVOJsonStr);
            }

            //派彩金额
            BigDecimal finalAmount = bet.getFinalAmount();
            //返水金额
            BigDecimal returnAmount = bet.getReturnAmount();

            //查看是否有派彩
            if (null != finalAmount && finalAmount.compareTo(BigDecimal.ZERO) > 0) {
                //要把派彩 把钱 拿回来 扣回来
                log.info("重新开牌-通知用户中心-扣除之前派彩-更新余额{}", finalAmount);
                String remark = bet.getNoActive() + "重新开牌,扣除派彩金额";
                BetAmountVO betAmountVO = BetAmountVO.builder().betId(bet.getId()).noActive(bet.getNoActive())
                        .changeType(BalanceChangeEnum.BET_REOPEN.getCode())
                        .userId(bet.getUserId()).amount(finalAmount).remark(remark).build();
                String betAmountVOJsonStr = JSONObject.toJSONString(betAmountVO);
                log.info("重新开牌-派彩-发送给用户中心MQ消息：{}", betAmountVOJsonStr);
                rabbitTemplate.convertAndSend(MqConstants.USER_SUBTRACT_ASSETS, betAmountVOJsonStr);
            }

            //查看是否有返水
            if (null != returnAmount && returnAmount.compareTo(BigDecimal.ZERO) > 0) {
                //修改 gameServer的返水统计
                log.info("扣除之前的返水统计");
                betBusiness.fixReturnAmount(bet);

                //已经返水 就要 扣除返水
                log.info("重新开牌-通知用户中心-扣除之前返水-更新余额{}", returnAmount);
                String remark = bet.getNoActive() + "重新开牌,扣除返水金额";
                BetAmountVO betAmountVO = BetAmountVO.builder().betId(bet.getId()).noActive(bet.getNoActive())
                        .changeType(BalanceChangeEnum.RETURN_AMOUNT_REOPEN.getCode())
                        .userId(bet.getUserId()).amount(returnAmount).remark(remark).build();
                String betAmountVOJsonStr = JSONObject.toJSONString(betAmountVO);
                log.info("重新开牌-返水-发送给用户中心MQ消息：{}", betAmountVOJsonStr);
                rabbitTemplate.convertAndSend(MqConstants.USER_SUBTRACT_ASSETS, betAmountVOJsonStr);
            }
            // 还原数据 回归到初始化
            int isUpdate = betService.returnBet(bet.getId());
            if (isUpdate > 0) {
                log.info("数据还原成功，重新结算注单id={}", bet.getId());
                //注单新
                bet = betService.findById(bet.getId());
                //就去派彩
                List<Bet> item = new ArrayList<>();
                item.add(bet);
                //之前已经算过打码量了，不需要再次计算
                doBetSettlement(item, vo, gameBaccOdds, true, true);
            } else {
                log.error("数据还原失败，注单id={}", bet.getId());
            }
        } else {
            //没派彩
            //就去派彩
            List<Bet> item = new ArrayList<>();
            item.add(bet);
            doBetSettlement(item, vo, gameBaccOdds, false, true);
        }
    }


}
