package com.baisha.backendserver.service;

import com.baisha.backendserver.model.BetStatistics;
import com.baisha.backendserver.repository.BetStatisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@Transactional
public class BetStatisticsService {

    @Autowired
    private BetStatisticsRepository betStatisticsRepository;

    public BetStatistics findByUserId(Long userId) {
        return betStatisticsRepository.findByUserId(userId);
    }


    public BetStatistics save(BetStatistics betStatistics) {
        betStatisticsRepository.save(betStatistics);
        return betStatistics;
    }


    public int updateBetStatisticsById(Long id, BigDecimal amount, Date betDate) {
        if (Objects.isNull(amount)){
            log.error("updateBetStatisticsById amount is null ");
            amount = BigDecimal.ZERO;
        }
        return betStatisticsRepository.updateBetStatisticsById(id, amount, betDate);
    }

    public int updateWinAmountById(Long id, BigDecimal amount) {
        if (Objects.isNull(amount)){
            log.error("updateWinAmountById amount is null ");
            amount = BigDecimal.ZERO;
        }
        return betStatisticsRepository.updateWinAmountById(id, amount);
    }
}
