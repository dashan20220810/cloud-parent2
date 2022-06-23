package com.baisha.repository;

import com.baisha.model.BotChatRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author kimi
 */
public interface BotChatRelationRepository extends JpaRepository<BotChatRelation, Long>, JpaSpecificationExecutor<BotChatRelation> {

}
