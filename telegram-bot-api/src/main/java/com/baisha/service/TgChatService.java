package com.baisha.service;

import com.baisha.model.TgChat;
import com.baisha.repository.TgChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "tgBot")
@Service
public class TgChatService {

    @Autowired
    private TgChatRepository tgChatRepository;

    @CachePut(key = "#tgChat.id")
    public TgChat save(TgChat tgChat) {
        return tgChatRepository.save(tgChat);
    }

    public Page<TgChat> getTgChatPage(Specification<TgChat> spec, Pageable pageable) {
        Page<TgChat> page = tgChatRepository.findAll(spec, pageable);
        return Optional.of(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }


    public TgChat findByChatIdAndBotId(Long chatId, Long botId) {
        return tgChatRepository.findByChatIdAndBotId(chatId,botId);
    }

    @Cacheable(key="#p0")
    public TgChat findbyId(Long chatId) {
        Optional<TgChat> byId = tgChatRepository.findById(chatId);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public Page<TgChat> pageByCondition(Pageable pageable,TgChat tgChat) {

        //可扩展简单的动态条件
//        ExampleMatcher matcher=null;
       return tgChatRepository.findAll(pageable);
    }

    public List<TgChat> findByTableId(String tableId) {
        return tgChatRepository.findByTableId(tableId);
    }
}
