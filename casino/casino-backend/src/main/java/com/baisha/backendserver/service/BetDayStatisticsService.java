package com.baisha.backendserver.service;

import com.baisha.backendserver.model.BetDayStatistics;
import com.baisha.backendserver.repository.BetDayStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@Service
@Transactional
public class BetDayStatisticsService {

    @Autowired
    private BetDayStatisticsRepository betDayStatisticsRepository;


    public BetDayStatistics findByUserIdAndDay(Long userId, Integer day) {
        return betDayStatisticsRepository.findByUserIdAndDay(userId, day);
    }


    public BetDayStatistics save(BetDayStatistics betDayStatistics) {
        betDayStatisticsRepository.save(betDayStatistics);
        return betDayStatistics;
    }

    public int updateBetDayStatisticsById(Long id, BigDecimal amount) {
        if (Objects.isNull(amount)){
            log.error("updateBetDayStatisticsById amount is null ");
            amount = BigDecimal.ZERO;
        }
        return betDayStatisticsRepository.updateBetDayStatisticsById(id, amount);
    }

    public int updateWinAmountById(Long id, BigDecimal amount) {
        if (Objects.isNull(amount)){
            log.error("updateWinAmountById amount is null ");
            amount = BigDecimal.ZERO;
        }
        return betDayStatisticsRepository.updateWinAmountById(id, amount);
    }
    

}
