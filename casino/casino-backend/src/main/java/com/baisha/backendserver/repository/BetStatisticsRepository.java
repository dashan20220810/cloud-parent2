package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.BetStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Date;

public interface BetStatisticsRepository extends JpaRepository<BetStatistics, Long>, JpaSpecificationExecutor<BetStatistics> {

    BetStatistics findByUserId(Long userId);

    @Query(value = "update BetStatistics a set a.betAmount = a.betAmount + ?2,a.lastBetTime = ?3,a.betNum = a.betNum +1 where a.id=?1 ")
    @Modifying
    int updateBetStatisticsById(Long id, BigDecimal amount, Date betDate);


}
