package com.baisha.gameserver.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.Bet;

/**
 * @author: alvin
 */
public interface BetRepository extends JpaRepository<Bet, Long>, JpaSpecificationExecutor<Bet> {

    List<Bet> findAllByUserIdAndCreateTimeBetween(Long userId, Date createBeginTime, Date createEndTime);

    List<Bet> findAllByUserIdAndTgChatId(Long userId, Long tgChatId, Pageable pageable);

    List<Bet> findByNoActiveAndStatus(String noActive, Integer status);

    @Modifying
    @Query(value = " update Bet b set b.settleTime = ?4,b.winAmount=?2,b.finalAmount=?3,b.status = 2  where b.id =?1 ")
    int updateSettleBetById(Long id, BigDecimal winAmount, BigDecimal finalAmount, Date settleTime);
    
    @Query(value = " SELECT SUM(b.winAmount) FROM Bet b WHERE b.userId = ?1 AND b.updateTime BETWEEN ?2 AND ?3 ")
    BigDecimal todayTotalProfit ( Long userId, Date beginTime, Date endTime );

    @Query(value = " SELECT SUM(b.amountH) +SUM(b.amountSs) +SUM(b.amountX) +SUM(b.amountXd) +SUM(b.amountZ) +SUM(b.amountZd) FROM Bet b WHERE b.userId = ?1 AND b.updateTime BETWEEN ?2 AND ?3 ")
    BigDecimal todayTotalWater ( Long userId, Date beginTime, Date endTime );
    
}
