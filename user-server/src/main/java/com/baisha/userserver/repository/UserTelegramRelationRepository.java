package com.baisha.userserver.repository;

import com.baisha.userserver.model.UserTelegramRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserTelegramRelationRepository extends JpaRepository<UserTelegramRelation, Long>
        , JpaSpecificationExecutor<UserTelegramRelation> {

    UserTelegramRelation findByUserIdAndTgGroupId(Long userId, String tgGroupId);

}
