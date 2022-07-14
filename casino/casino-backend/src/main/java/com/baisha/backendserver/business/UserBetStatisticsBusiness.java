package com.baisha.backendserver.business;

import com.baisha.backendserver.model.BetDayStatistics;
import com.baisha.backendserver.model.BetStatistics;
import com.baisha.backendserver.service.BetDayStatisticsService;
import com.baisha.backendserver.service.BetStatisticsService;
import com.baisha.modulecommon.util.DateUtil;
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


    @Async
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
                betDayStatisticsService.updateBetDayStatisticsById(betStatistics.getId(), amount);
            }

        } catch (Exception e) {
            log.error("doUserBetStatistics error : {}", e.toString());
            e.printStackTrace();
        }
    }


}
