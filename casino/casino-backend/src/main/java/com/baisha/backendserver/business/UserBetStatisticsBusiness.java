package com.baisha.backendserver.business;

import com.baisha.backendserver.model.BetDayStatistics;
import com.baisha.backendserver.model.BetStatistics;
import com.baisha.backendserver.service.BetDayStatisticsService;
import com.baisha.backendserver.service.BetStatisticsService;
import com.baisha.modulecommon.util.DateUtil;
import com.baisha.modulecommon.vo.mq.gameServer.UserBetStatisticsVO;
import com.baisha.modulecommon.vo.mq.webServer.UserBetVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@Service
public class UserBetStatisticsBusiness {

    @Autowired
    private BetStatisticsService betStatisticsService;

    @Autowired
    private BetDayStatisticsService betDayStatisticsService;


    public void doUserBetStatistics(UserBetVO userBetVO) {
        //下注天 yyyy-MM-dd HH:mm:ss
        String betTime = userBetVO.getBetTime();
        //用户ID
        Long userId = userBetVO.getUserId();
        //用户TgId
        String tgUserId = userBetVO.getTgUserId();
        //金额
        BigDecimal amount = userBetVO.getAmount();
        try {
            //总统计
            Date betDate = (DateUtil.getSimpleDateFormat()).parse(betTime);
            BetStatistics betStatistics = betStatisticsService.findByUserId(userId);
            if (Objects.isNull(betStatistics)) {
                //新增
                betStatistics = new BetStatistics();
                betStatistics.setBetAmount(amount);
                betStatistics.setLastBetTime(betDate);
                betStatistics.setUserId(userId);
                betStatistics.setTgUserId(tgUserId);
                betStatisticsService.save(betStatistics);
            } else {
                //更新
                betStatisticsService.updateBetStatisticsById(betStatistics.getId(), amount, betDate);
            }

            //每天统计
            String time = DateUtil.dateToyyyyMMdd(betDate);
            Integer day = Integer.parseInt(time);
            //String dayStr = betTime.substring(0, 10);
            //Integer day = Integer.parseInt(dayStr.replaceAll("-", ""));
            BetDayStatistics betDayStatistics = betDayStatisticsService.findByUserIdAndDay(userId, day);
            if (Objects.isNull(betDayStatistics)) {
                //新增
                betDayStatistics = new BetDayStatistics();
                betDayStatistics.setDay(day);
                betDayStatistics.setBetAmount(amount);
                betDayStatistics.setUserId(userId);
                betDayStatistics.setTgUserId(tgUserId);
                betDayStatisticsService.save(betDayStatistics);
            } else {
                //更新
                betDayStatisticsService.updateBetDayStatisticsById(betDayStatistics.getId(), amount);
            }
        } catch (Exception e) {
            log.error("doUserBetStatistics error : {}", e.toString());
            e.printStackTrace();
        }
    }

    public void doUserSettleBetStatistics(UserBetStatisticsVO userBetStatisticsVO) {
        //下注天 yyyy-MM-dd HH:mm:ss
        String betTime = userBetStatisticsVO.getBetTime();
        //用户ID
        Long userId = userBetStatisticsVO.getUserId();
        //金额
        BigDecimal winAmount = userBetStatisticsVO.getWinAmount();
        try {
            BetStatistics betStatistics = betStatisticsService.findByUserId(userId);
            if (Objects.nonNull(betStatistics)) {
                log.info("更新总统计的输赢");
                betStatisticsService.updateWinAmountById(betStatistics.getId(), winAmount);
            }

            //每天统计
            Date betDate = (DateUtil.getSimpleDateFormat()).parse(betTime);
            String time = DateUtil.dateToyyyyMMdd(betDate);
            Integer day = Integer.parseInt(time);
            BetDayStatistics betDayStatistics = betDayStatisticsService.findByUserIdAndDay(userId, day);
            if (Objects.nonNull(betDayStatistics)) {
                log.info("更新每日统计的输赢");
                betDayStatisticsService.updateWinAmountById(betDayStatistics.getId(), winAmount);
            }
        } catch (Exception e) {
            log.error("error {}", e.getMessage());
            e.printStackTrace();
        }
    }


}
