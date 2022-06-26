package com.baisha.repository;

import com.baisha.model.TgChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author kimi
 */
public interface TgChatRepository extends JpaRepository<TgChat, Long>, JpaSpecificationExecutor<TgChat> {

    TgChat findByChatIdAndBotName(String chatId, String botName);

    TgChat findByChatIdAndBotNameAndStatus(String chatId, String botName, Integer status);
}
