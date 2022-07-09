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
import com.baisha.gameserver.vo.BetReturnAmountVO;

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

    /**
     * status=2 AND (b.isReturned IS NULL or b.isReturned = false) AND b.winAmount < 0
     * @param userId
     * @param tgChatId
     * @param beginTime
     * @param endTime
     * @return
     */
    @Query(value = " SELECT b FROM Bet b WHERE b.status=2 AND (b.isReturned IS NULL or b.isReturned = false) "
    		+ "		AND b.winAmount < 0 "
    		+ " 	AND b.userId = ?1 AND b.tgChatId=?2 AND b.updateTime BETWEEN ?3 AND ?4 ")
    List<Bet> findAllBy( Long userId, Long tgChatId, Date beginTime, Date endTime );

    @Modifying
    @Query(value = " update Bet b SET b.isReturned=true WHERE b.id = ?1 ")
    int updateIsReturnedById(Long id);
    
    @Query(value = " SELECT new com.baisha.gameserver.vo.BetReturnAmountVO( b.userId, b.tgChatId, "
    		+ " (SUM(b.amountH) +SUM(b.amountSs) +SUM(b.amountX) +SUM(b.amountXd) +SUM(b.amountZ) +SUM(b.amountZd)) ) "
    		+ " FROM Bet b WHERE b.status=2 AND (b.isReturned IS NULL or b.isReturned = false) "
    		+ " 	AND b.updateTime BETWEEN ?1 AND ?2 "
    		+ " 		GROUP BY b.userId, b.tgChatId ")
    List<BetReturnAmountVO> sumFlowAmount ( Date beginTime, Date endTime );

    @Modifying
    @Query(value = " update Bet b SET  b.isReturned=?5 WHERE b.status=2 AND (b.isReturned IS NULL or b.isReturned = false) "
    		+ " 	AND b.userId = ?1 AND b.tgChatId=?2 AND b.updateTime BETWEEN ?3 AND ?4 ")
    int updateReturnAmount ( Long userId, Long tgChatId, Date beginTime, Date endTime, boolean isReturned );
}
