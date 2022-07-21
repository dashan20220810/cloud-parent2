package com.baisha.userserver.repository;

import com.baisha.userserver.model.PlayMoneyChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author yihui
 */
public interface PlayMoneyChangeRepository extends JpaRepository<PlayMoneyChange, Long>,
        JpaSpecificationExecutor<PlayMoneyChange> {

    PlayMoneyChange findByUserIdAndChangeTypeAndRelateId(Long userId, Integer changeType, Long relateId);


}
