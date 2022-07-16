package com.baisha.service;

import com.baisha.model.TgBetBot;
import com.baisha.repository.TgBetBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "tgBetBot")
@Service
@Transactional
public class TgBetBotService {

    @Autowired
    private TgBetBotRepository tgBetBotRepository;

    public TgBetBot findByBetBotName(String betBotName) {
        return tgBetBotRepository.findByBetBotName(betBotName);
    }

    @CachePut(key = "#tgBetBot.id")
    public TgBetBot save(TgBetBot tgBetBot) {
        return tgBetBotRepository.save(tgBetBot);
    }

    public Page<TgBetBot> getTgBetBotPage(Specification<TgBetBot> spec, Pageable pageable) {
        Page<TgBetBot> page = tgBetBotRepository.findAll(spec, pageable);
        return Optional.of(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @CachePut(key = "#id")
    public TgBetBot updateStatusById(Long id, Integer status) {
        tgBetBotRepository.updateStatusById(status,id);
        return tgBetBotRepository.findById(id).get();
    }

    @CacheEvict(key = "#id")
    public void delBot(Long id) {
        tgBetBotRepository.deleteById(id);
    }

    @Cacheable(key = "#p0", unless = "#result==null")
    public TgBetBot findById(Long botId) {
        Optional<TgBetBot> byId = tgBetBotRepository.findById(botId);
        return byId.orElse(null);
    }

    public List<TgBetBot> findByStatus(Integer status) {
        return tgBetBotRepository.findByStatus(status);
    }
}
