package com.baisha.service;

import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.modulecommon.Constants;
import com.baisha.repository.TgBotRepository;
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
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "tgBot::chatId")
@Service
public class TgChatService {

    @Autowired
    private TgChatRepository tgChatRepository;

    @Cacheable
    public TgChat findByChatId(String chatId) {
        return tgChatRepository.findByChatId(chatId);
    }

    @CachePut(key = "#tgChat.chatId")
    public TgChat save(TgChat tgChat) {
        return tgChatRepository.save(tgChat);
    }

//    public Page<TgBot> getTgBotPage(Specification<TgBot> spec, Pageable pageable) {
//        Page<TgBot> page = tgBotRepository.findAll(spec, pageable);
//        return Optional.of(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
//    }
//
//    @CachePut(key = "#id")
//    public TgBot updateStatusById(Long id, Integer status) {
//        Optional<TgBot> optionalTgBot = tgBotRepository.findById(id);
//        optionalTgBot.ifPresent(tgBot -> {
//            tgBot.setStatus(status);
//            tgBotRepository.save(tgBot);
//        });
//        return tgBotRepository.findById(id).get();
//    }
//
//    public List<TgBot> getTgBots() {
//        return tgBotRepository.findByStatus(Constants.open);
//    }
}
