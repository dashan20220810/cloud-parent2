package com.baisha.gameserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.baisha.gameserver.model.BetResult;

/**
 * @author: alvin
 */
public interface BetResultRepository extends JpaRepository<BetResult, Long>, JpaSpecificationExecutor<BetResult> {


    @Query(value = "update BetResult  u set u.awardOption = ?1 where u.tableId=?2 and u.noActive=?3")
    @Modifying
    void UpdateAwardOptionByTableIdAndNoActive ( String awardOption, Long tableId, String noActive );
	
}
