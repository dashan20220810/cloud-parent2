package com.baisha.backendserver.repository;

import com.baisha.backendserver.model.BetDayStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface BetDayStatisticsRepository extends JpaRepository<BetDayStatistics, Long>, JpaSpecificationExecutor<BetDayStatistics> {

    BetDayStatistics findByUserIdAndDay(Long userId, Integer day);

    @Query(value = "update BetDayStatistics a set a.betAmount = a.betAmount + ?2,a.betNum = a.betNum +1  where a.id=?1 ")
    @Modifying
    int updateBetDayStatisticsById(Long id, BigDecimal amount);

}
