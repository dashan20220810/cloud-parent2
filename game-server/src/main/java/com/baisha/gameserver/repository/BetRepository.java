package com.baisha.gameserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.baisha.gameserver.model.Bet;

/**
 * @author: alvin
 */
public interface BetRepository extends JpaRepository<Bet ,Long>, JpaSpecificationExecutor<Bet> {

}
