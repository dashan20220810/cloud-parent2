package com.baisha.userserver.repository;

import com.baisha.userserver.model.BalanceChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yihui
 */
public interface BalanceChangeRepository extends JpaRepository<BalanceChange, Long>,
        JpaSpecificationExecutor<BalanceChange> {


    BalanceChange findByUserIdAndChangeTypeAndRelateId(Long userId, Integer changeType, Long relateId);



}
