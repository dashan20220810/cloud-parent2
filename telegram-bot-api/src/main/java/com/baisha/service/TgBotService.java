package com.baisha.service;

import com.baisha.model.TgBot;
import com.baisha.modulecommon.Constants;
import com.baisha.repository.TgBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "tgBot")
@Service
@Transactional
public class TgBotService {

    @Autowired
    private TgBotRepository tgBotRepository;

    @Cacheable(key="#p0")
    public TgBot findByBotName(String botName) {
        return tgBotRepository.findByBotName(botName);
    }

    @CachePut(key = "#tgBot.id")
    public TgBot save(TgBot tgBot) {
        return tgBotRepository.save(tgBot);
    }

    @Caching(put = {@CachePut(key = "#id")})
    public TgBot updateTgBotById(Integer status, Long id) {
        int i = tgBotRepository.updateTgBotById(status, id);
        if (i > 0) {
            return tgBotRepository.findById(id).get();
        }
        return null;
    }

    public Page<TgBot> getTgBotPage(Specification<TgBot> spec, Pageable pageable) {
        Page<TgBot> page = tgBotRepository.findAll(spec, pageable);
        return Optional.of(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @CachePut(key = "#id")
    public TgBot updateStatusById(Long id, Integer status) {
        Optional<TgBot> optionalTgBot = tgBotRepository.findById(id);
        optionalTgBot.ifPresent(tgBot -> {
            tgBot.setStatus(status);
            tgBotRepository.save(tgBot);
        });
        return tgBotRepository.findById(id).get();
    }

    @CacheEvict(key = "#id")
    public void delBot (Long id) {
        tgBotRepository.deleteById(id);
    }

    @Cacheable
    public TgBot getById (Long id) {
        return tgBotRepository.getById(id);
    }

    @Cacheable
    public TgBot findById(Long botId) {
        Optional<TgBot> byId = tgBotRepository.findById(botId);
        if (byId.isPresent()) {
            return byId.get();
        }
        return null;
    }

    public List<TgBot> findByStatus(Integer open) {
        return tgBotRepository.findByStatus(open);
    }
}
