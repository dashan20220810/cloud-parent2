package com.baisha.service;

import com.baisha.model.TgChat;
import com.baisha.repository.TgChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "tgChat")
@Service
@Transactional
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
        return tgChatRepository.findByChatIdAndBotId(chatId, botId);
    }

    @Cacheable(key = "#p0",unless = "#result==null")
    public TgChat findbyId(Long chatId) {
        Optional<TgChat> byId = tgChatRepository.findById(chatId);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public Page<TgChat> pageByCondition(Pageable pageable, TgChat tgChat) {

        //可扩展简单的动态条件
        ExampleMatcher matcher=ExampleMatcher.matching()
                .withMatcher("botId", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<TgChat> example = Example.of(tgChat, matcher);
        return tgChatRepository.findAll(example,pageable);
    }

    public List<TgChat> findByTableId(Long tableId) {
        return tgChatRepository.findByTableId(tableId);
    }

    @CacheEvict(key="#p0")
    public void deleteById(Long id) {
        tgChatRepository.deleteById(id);
    }

    public TgChat findByChatId(Long chatId) {
        return tgChatRepository.findByChatId(chatId);
    }
}
