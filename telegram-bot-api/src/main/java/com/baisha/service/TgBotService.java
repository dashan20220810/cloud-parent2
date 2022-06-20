package com.baisha.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.constants.BotConstant;
import com.baisha.constants.TgBotRedisConstant;
import com.baisha.model.TgBot;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.reponse.ResponseEntity;
import com.baisha.modulecommon.reponse.ResponseUtil;
import com.baisha.modulespringcacheredis.util.RedisUtil;
import com.baisha.repository.TgBotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service
public class TgBotService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private TgBotRepository tgBotRepository;

    public ResponseEntity saveTgBot(TgBot tgBot) {
        TgBot tgBotDb = tgBotRepository.findByBotName(tgBot.getBotName());
        if (null == tgBotDb) {
            TgBot save = tgBotRepository.save(tgBot);
            return ResponseUtil.success(save);
        }
        return ResponseUtil.custom("当前机器人已经存在！");
    }

    public Page<TgBot> getTgBotPage(TgBotPageVO vo) {
        Pageable pageable = PageRequest.of(vo.getPageNumber() - 1, vo.getPageSize());
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

    public void updateStatus(Long id, Integer status) {
        Optional<TgBot> optionalTgBot = tgBotRepository.findById(id);
        optionalTgBot.ifPresent(tgBot -> {
            tgBot.setStatus(status);
            tgBotRepository.save(tgBot);
        });
        // TODO 在此处 更新redis的key
//        redisUtil
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
        List<Object> tgBots = redisUtil.lGet(TgBotRedisConstant.TG_BOT_ALL_PREFIX, 0, -1);
        if (CollUtil.isNotEmpty(tgBots)) {
//            return tgBots;
        }
        TgBot tgBotExample = new TgBot();
        tgBotExample.setStatus(BotConstant.NORMAL);
        Example<TgBot> example = Example.of(tgBotExample);
        return tgBotRepository.findAll(example);
    }
}
