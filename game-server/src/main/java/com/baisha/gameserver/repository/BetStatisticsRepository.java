package com.baisha.gameserver.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.BetStatistics;

public interface BetStatisticsRepository extends JpaRepository<BetStatistics, Long>, JpaSpecificationExecutor<BetStatistics> {

    BetStatistics findByUserIdAndStatisticsDate(Long userId, Integer statisticsDate);

    @Modifying
    @Query(value = " update BetStatistics b set b.flowAmount = b.flowAmount+ ?3 where b.userId =?1 AND b.statisticsDate = ?2 ")
    int updateFlowAmount(Long userId, Integer statisticsDate, BigDecimal finalAmount);

    @Modifying
    @Query(value = " update BetStatistics b set b.winAmount = b.winAmount + ?3 where b.userId =?1 AND b.statisticsDate = ?2 ")
    int updateWinAmount(Long userId, Integer statisticsDate, BigDecimal winAmount);
}
