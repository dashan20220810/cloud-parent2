package com.baisha.repository;

import com.baisha.model.TgBot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author kimi
 */
public interface TgBotRepository extends JpaRepository<TgBot, Long>, JpaSpecificationExecutor<TgBot> {

    TgBot findByBotName(String botName);
}
