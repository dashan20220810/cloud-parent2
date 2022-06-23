package com.baisha.repository;

import com.baisha.model.TgBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author kimi
 */
public interface TgBotRepository extends JpaRepository<TgBot, Long>, JpaSpecificationExecutor<TgBot> {

    TgBot findByBotName(String botName);

    List<TgBot> findByStatus(Integer status);

    /**
     * 更新
     *
     * @param status
     * @param id
     */
    @Query(value = "update TgBot tg set tg.status = ?1 where tg.id = ?2")
    @Modifying
    int updateTgBotById(Integer status, Long id);
}
