package com.baisha.gameserver.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.baisha.gameserver.model.Bet;

/**
 * @author: alvin
 */
public interface BetRepository extends JpaRepository<Bet ,Long>, JpaSpecificationExecutor<Bet> {

	List<Bet> findAllByUserIdAndCreateTimeBetween ( Long userId, Date createBeginTime, Date createEndTime );

	List<Bet> findAllByUserId ( Long userId, Pageable pageable );
}
