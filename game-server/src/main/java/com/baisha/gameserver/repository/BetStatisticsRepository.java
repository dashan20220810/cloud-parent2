package com.baisha.gameserver.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.BetStatistics;

public interface BetStatisticsRepository extends JpaRepository<BetStatistics, Long>, JpaSpecificationExecutor<BetStatistics> {

    BetStatistics findByUserIdAndTgChatIdAndStatisticsDate(Long userId, Long tgChatId, Integer statisticsDate);

    @Modifying
    @Query(value = " update BetStatistics b set b.flowAmount = b.flowAmount+ ?4 where b.userId =?1 AND b.tgChatId =?2 AND b.statisticsDate = ?3 ")
    int updateFlowAmount(Long userId, Long tgChatId, Integer statisticsDate, BigDecimal finalAmount);

    @Modifying
    @Query(value = " update BetStatistics b set b.winAmount = b.winAmount + ?4 where b.userId =?1 AND b.tgChatId =?2 AND b.statisticsDate = ?3 ")
    int updateWinAmount(Long userId, Long tgChatId, Integer statisticsDate, BigDecimal winAmount);

    @Modifying
    @Query(value = " update BetStatistics b set b.returnAmount = b.returnAmount + ?4 "
    		+ " where b.userId =?1 AND b.tgChatId =?2 AND b.statisticsDate = ?3 ")
    int updateReturnAmount(Long userId, Long tgChatId, Integer statisticsDate, BigDecimal returnAmount);
}
