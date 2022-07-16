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


    @Query(value = "update BetResult  u set u.awardOption = ?1 where u.noActive=?2")
    @Modifying
    void UpdateAwardOptionByTableIdAndNoActive(String awardOption, String noActive);

    BetResult findByNoActive(String noActive);

    @Query(value = "update BetResult  u set u.reopen=1 where u.noActive=?1")
    @Modifying
    void updateReopenByNoActive(String noActive);

    @Query(value = "update BetResult  u set u.awardOption = ?2 ,u.reopen=1  where u.noActive=?1")
    @Modifying
    int updateReopenAndAwardOptionByNoActive(String noActive, String awardOption);
}
