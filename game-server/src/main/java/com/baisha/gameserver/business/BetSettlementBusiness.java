package com.baisha.gameserver.business;

import com.alibaba.fastjson.JSONObject;
import com.baisha.gameserver.model.Bet;
import com.baisha.gameserver.model.bo.game.BetAwardBO;
import com.baisha.gameserver.model.bo.game.GameBaccOddsBO;
import com.baisha.gameserver.service.BetService;
import com.baisha.gameserver.service.BetStatisticsService;
import com.baisha.gameserver.util.BaccNoCommissionUtil;
import com.baisha.gameserver.util.GameServerUtil;
import com.baisha.modulecommon.MqConstants;
import com.baisha.modulecommon.enums.BetStatusEnum;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.vo.mq.BetSettleVO;
import com.baisha.modulecommon.vo.mq.gameServer.UserBetStatisticsVO;
import com.baisha.modulecommon.vo.mq.userServer.BetSettleUserVO;
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
 */
@Slf4j
@Service
public class BetSettlementBusiness {

    //@Value("${project.server-url.user-server-domain}")
    private String userServerDomain;
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
                .map(item -> CompletableFuture.supplyAsync(() -> doBetSettlement(item, vo, gameBaccOdds), asyncExecutor)).toList();
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
    private List<Bet> doBetSettlement(List<Bet> item, BetSettleVO vo, GameBaccOddsBO gameBaccOdds) {
        String betAwardOption = vo.getAwardOption().toUpperCase();
        List<Bet> settleList = new ArrayList<>();
        for (Bet bet : item) {
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
                }
                log.info("局{} 中奖选项{} 注单Id{} 中奖结果{}", vo.getNoActive(), awardOption, betAwardBO.getId(), JSONObject.toJSONString(betAwardBO));
                finalAmount = finalAmount.add(betAwardBO.getFinalAmount());
                settleRemarkBuffer.append(StringUtils.isNotEmpty(betAwardBO.getRemark()) ? betAwardBO.getRemark() : "");
            }
            winAmount = finalAmount.subtract(BigDecimal.valueOf(betAmount));
            //修改注单数据
            int flag = betService.settleBet(bet.getId(), winAmount, finalAmount, settleRemarkBuffer.toString());
            if (flag > 0) {
                //用户统计今日数据(输赢结果)
                statisticsWinAmount(bet, winAmount);

                log.info("通知后台更新输赢{}和打码量{}", winAmount, betAmount);
                UserBetStatisticsVO userBetStatisticsVO = UserBetStatisticsVO.builder().userId(bet.getUserId())
                        .betTime(DateUtil.getSimpleDateFormat().format(bet.getCreateTime()))
                        .playMoney(BigDecimal.valueOf(betAmount)).winAmount(winAmount).build();
                String userBetStatisticsJsonStr = JSONObject.toJSONString(userBetStatisticsVO);
                log.info("发送给后台MQ消息：{}", userBetStatisticsJsonStr);
                rabbitTemplate.convertAndSend(MqConstants.BACKEND_BET_SETTLEMENT_STATISTICS, userBetStatisticsJsonStr);

                log.info("=======================================================================================");

                log.info("通知用户中心更新余额{}和打码量{}", finalAmount, betAmount);
                BetSettleUserVO betSettleUserVO = BetSettleUserVO.builder().betId(bet.getId()).noActive(bet.getNoActive())
                        .userId(bet.getUserId()).finalAmount(finalAmount).playMoney(BigDecimal.valueOf(betAmount))
                        .remark(settleRemarkBuffer.toString()).build();
                String betSettleUserJsonStr = JSONObject.toJSONString(betSettleUserVO);
                log.info("发送给用户中心MQ消息：{}", betSettleUserJsonStr);
                rabbitTemplate.convertAndSend(MqConstants.USER_SETTLEMENT_ASSETS, betSettleUserJsonStr);
            }

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

}
