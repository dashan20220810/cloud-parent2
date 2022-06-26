package com.baisha.service;

import com.baisha.model.TgChat;
import com.baisha.repository.TgChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@CacheConfig(cacheNames = "tgBot::chatId")
@Service
public class TgChatService {

    @Autowired
    private TgChatRepository tgChatRepository;

    @Cacheable(key = "#botName+':'+#chatId", unless="#result == null")
    public TgChat findByChatIdAndBotName(String chatId, String botName) {
        return tgChatRepository.findByChatIdAndBotName(chatId, botName);
    }

    @Cacheable(key = "#botName+':'+#chatId+':'+#status", unless="#result == null")
    public TgChat findByChatIdAndBotNameAndStatus(String chatId, String botName, Integer status) {
        return tgChatRepository.findByChatIdAndBotNameAndStatus(chatId, botName, status);
    }

    @CachePut(key = "#tgChat.chatId")
    public TgChat save(TgChat tgChat) {
        return tgChatRepository.save(tgChat);
    }

    public Page<TgChat> getTgChatPage(Specification<TgChat> spec, Pageable pageable) {
        Page<TgChat> page = tgChatRepository.findAll(spec, pageable);
        return Optional.of(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @CachePut(key = "#id")
    public TgChat auditStatus(Long id, Integer status) {
        Optional<TgChat> optionalTgBot = tgChatRepository.findById(id);
        optionalTgBot.ifPresent(tgBot -> {
            tgBot.setStatus(status);
            tgChatRepository.save(tgBot);
        });
        return tgChatRepository.findById(id).get();
    }
//
//    public List<TgBot> getTgBots() {
//        return tgBotRepository.findByStatus(Constants.open);
//    }
}
