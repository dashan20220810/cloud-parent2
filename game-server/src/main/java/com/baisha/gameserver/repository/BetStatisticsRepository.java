package com.baisha.gameserver.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.BetStatistics;

public interface BetStatisticsRepository extends JpaRepository<BetStatistics, Long>, JpaSpecificationExecutor<BetStatistics> {

	
	BetStatistics findByUserIdAndStatisticsDate ( Long userId, String statisticsDate );

    @Modifying
    @Query(value = " update BetStatistics b set b.flowAmount = ?3 where b.userId =?1 AND b.statisticsDate = ?2 ")
    int updateFlowAmount(Long userId, String statisticsDate, BigDecimal finalAmount);
}
