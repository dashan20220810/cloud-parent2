package com.baisha.userserver.repository;

import com.baisha.userserver.model.UserTelegramRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserTelegramRelationRepository extends JpaRepository<UserTelegramRelation, Long>
        , JpaSpecificationExecutor<UserTelegramRelation> {

    UserTelegramRelation findByUserIdAndTgGroupId(Long userId, String tgGroupId);

    UserTelegramRelation findByTgUserIdAndTgGroupId(String tgUserId, String tgGroupId);

    @Query(value = "update UserTelegramRelation a set a.status = 0 where a.tgUserId = ?1 AND a.tgGroupId=?2 ")
    @Modifying
    int leaveGroup(String tgUserId, String tgGroupId);

    List<UserTelegramRelation> findByTgGroupId(String tgGroupId);

    List<UserTelegramRelation> findByTgGroupIdAndUserType(String tgGroupId, Integer userType);
}
