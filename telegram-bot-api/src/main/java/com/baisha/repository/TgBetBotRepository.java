package com.baisha.repository;

import com.baisha.model.TgBetBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author kimi
 */
public interface TgBetBotRepository extends JpaRepository<TgBetBot, Long>, JpaSpecificationExecutor<TgBetBot> {

    TgBetBot findByBetBotName(String betBotName);

    List<TgBetBot> findByStatus(Integer status);

    @Query(value = "update TgBetBot tg set tg.status = ?1 where tg.id = ?2")
    @Modifying
    void updateStatusById(Integer status, Long id);
}
