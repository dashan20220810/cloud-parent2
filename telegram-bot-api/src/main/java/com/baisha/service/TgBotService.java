package com.baisha.service;

import com.baisha.model.TgBot;
import com.baisha.modulecommon.Constants;
import com.baisha.repository.TgBotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CacheConfig(cacheNames = "tgBot::botId")
@Service
@Transactional
public class TgBotService {

    @Autowired
    private TgBotRepository tgBotRepository;

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

    public List<TgBot> getTgBots() {
        return tgBotRepository.findByStatus(Constants.open);
    }
}
