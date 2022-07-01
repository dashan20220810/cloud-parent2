package com.baisha.gameserver.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.baisha.gameserver.model.Bet;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author: alvin
 */
public interface BetRepository extends JpaRepository<Bet, Long>, JpaSpecificationExecutor<Bet> {

    List<Bet> findAllByUserIdAndCreateTimeBetween(Long userId, Date createBeginTime, Date createEndTime);

    List<Bet> findAllByUserId(Long userId, Pageable pageable);

    List<Bet> findByNoActiveAndStatus(String noActive, Integer status);

    @Modifying
    @Query(value = " update Bet b set b.settleTime = ?4,b.winAmount=?2,b.finalAmount=?3,b.status = 2  where b.id =?1 ")
    int updateSettleBetById(Long id, BigDecimal winAmount, BigDecimal finalAmount, Date settleTime);
}
