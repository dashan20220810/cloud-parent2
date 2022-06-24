package com.baisha.repository;

import com.baisha.model.TgChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author kimi
 */
public interface TgChatRepository extends JpaRepository<TgChat, Long>, JpaSpecificationExecutor<TgChat> {

    TgChat findByChatId(String chatId);
}
