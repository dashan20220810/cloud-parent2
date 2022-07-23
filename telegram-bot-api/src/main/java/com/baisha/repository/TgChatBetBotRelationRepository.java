package com.baisha.repository;

import com.baisha.model.TgChatBetBotRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author kimi
 */
public interface TgChatBetBotRelationRepository extends JpaRepository<TgChatBetBotRelation, Long>, JpaSpecificationExecutor<TgChatBetBotRelation> {

    List<TgChatBetBotRelation> findByTgChatId(Long tgChatId);

    void deleteByTgChatId(Long tgChatId);
}
