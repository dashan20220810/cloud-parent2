package com.baisha.service;

import com.baisha.model.BotChatRelation;
import com.baisha.repository.BotChatRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@CacheConfig(cacheNames = "tgBot::relation")
@Service
public class BotChatRelationService {

    @Autowired
    private BotChatRelationRepository botChatRelationRepository;

    @Caching(
            put = {@CachePut(key = "#relation.botId"), @CachePut(key = "#relation.chatId")}
    )
    public BotChatRelation save(BotChatRelation relation) {
        return botChatRelationRepository.save(relation);
    }
}
