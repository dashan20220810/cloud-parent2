package com.baisha.service;

import cn.hutool.core.util.StrUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.Constants;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import com.baisha.repository.TgBotRepository;
import com.baisha.util.TelegramServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@CacheConfig(cacheNames = "tgBot::botId")
@Service
public class TgBotService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TgBotRepository tgBotRepository;

    public TgBot findByBotName(String botName) {
        return tgBotRepository.findByBotName(botName);
    }

    @CachePut(key = "#tgBot.id")
    public TgBot save(TgBot tgBot) {
        return tgBotRepository.save(tgBot);
    }

    public Page<TgBot> getTgBotPage(TgBotPageVO vo) {
        Pageable pageable = TelegramServerUtil.setPageable(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<TgBot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StrUtil.isNotEmpty(vo.getBotName())) {
                predicates.add(cb.equal(root.get("botName"), vo.getBotName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<TgBot> page = tgBotRepository.findAll(spec, pageable);
        return Optional.ofNullable(page).orElseGet(() -> new PageImpl<>(new ArrayList<>()));
    }

    @CachePut(key = "#id")
    public TgBot updateStatus(Long id, Integer status) {
        Optional<TgBot> optionalTgBot = tgBotRepository.findById(id);
        optionalTgBot.ifPresent(tgBot -> {
            tgBot.setStatus(status);
            tgBotRepository.save(tgBot);
        });
        return tgBotRepository.findById(id).get();
    }

    public void registerAllBot() {
        // 根据状态status来过滤
        List<TgBot> tgBots = getTgBots();
        tgBots.forEach(tgBot -> {
            try {
                // 实例化机器人
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
                botsApi.registerBot(new MyTelegramLongPollingBot(tgBot.getBotName(), tgBot.getBotToken(), tgBot.getChatId()));
            } catch (Throwable e) {
                log.error("初始化-注册机器人失败", e);
            }
        });
    }

    public List<TgBot> getTgBots() {
        TgBot tgBotExample = new TgBot();
        tgBotExample.setStatus(Constants.open);
        Example<TgBot> example = Example.of(tgBotExample);
        return tgBotRepository.findAll(example);
    }
}
