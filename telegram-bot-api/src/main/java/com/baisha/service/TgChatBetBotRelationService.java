package com.baisha.service;

import com.baisha.model.TgChatBetBotRelation;
import com.baisha.repository.TgChatBetBotRelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@CacheConfig(cacheNames = "tgChatBetBotRelation")
@Service
@Transactional
public class TgChatBetBotRelationService {

    @Autowired
    private TgChatBetBotRelationRepository tgChatBetBotRelationRepository;

    public List<TgChatBetBotRelation> findByTgChatId(Long tgChatId) {
        return tgChatBetBotRelationRepository.findByTgChatId(tgChatId);
    }

    @CachePut(key = "#relation.tgChatId")
    public TgChatBetBotRelation save(TgChatBetBotRelation relation) {
        return tgChatBetBotRelationRepository.save(relation);
    }

    @CacheEvict(key = "#tgChatId")
    public void deleteByTgChatId(Long tgChatId) {
        tgChatBetBotRelationRepository.deleteByTgChatId(tgChatId);
    }
}
