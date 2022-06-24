package com.baisha.service;

import com.baisha.model.TgBot;
import com.baisha.model.TgChat;
import com.baisha.model.vo.TgBotVO;
import com.baisha.modulecommon.Constants;
import com.baisha.repository.TgBotRepository;
import com.baisha.repository.TgChatRepository;
import com.google.common.collect.Lists;
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

    @Autowired
    private TgChatRepository tgChatRepository;

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

    public List<TgBotVO> getTgBots() {
        List<TgBotVO> result = Lists.newArrayList();

        List<TgChat> tgChats = tgChatRepository.findAll();

        List<TgBot> tgBots = tgBotRepository.findByStatus(Constants.open);
        for (TgBot tgBot : tgBots) {
            for (TgChat tgChat : tgChats) {
                if (tgBot.getBotName().equals(tgChat.getBotName())) {
                    TgBotVO vo = new TgBotVO()
                            .setBotName(tgBot.getBotName())
                            .setBotToken(tgBot.getBotToken())
                            .setChatId(tgChat.getChatId())
                            .setChatName(tgChat.getChatName());
                    result.add(vo);
                }
            }
        }
        return result;
    }
}
