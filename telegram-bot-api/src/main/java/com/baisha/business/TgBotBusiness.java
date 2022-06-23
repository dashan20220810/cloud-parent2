package com.baisha.business;

import cn.hutool.core.util.StrUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.service.TgBotService;
import com.baisha.util.TelegramServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.persistence.criteria.Predicate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class TgBotBusiness {

    private TelegramBotsApi botsApi;
    ConcurrentMap<String, BotSession> botSerssionMap = new ConcurrentHashMap<>();

    @Autowired
    private TgBotService tgBotService;

    public Page<TgBot> getTgBotPage(TgBotPageVO vo) {
        Pageable pageable = TelegramServerUtil.setPageable(vo.getPageNumber() - 1, vo.getPageSize());
        Specification<TgBot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StrUtil.isNotEmpty(vo.getBotName())) {
                predicates.add(cb.equal(root.get("botName"), vo.getBotName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return tgBotService.getTgBotPage(spec, pageable);
    }

    public void registerAllBot() {
        // 根据状态status来过滤
        List<TgBot> tgBots = tgBotService.getTgBots();
        tgBots.forEach(tgBot -> {
            try {
                // 实例化机器人
                TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//                botsApi.registerBot(new MyTelegramLongPollingBot(tgBot.getBotName(), tgBot.getBotToken(), tgBot.getChatId()));
            } catch (Throwable e) {
                log.error("初始化-注册机器人失败", e);
            }
        });
    }

    /**
     * 启动机器人
     * @param username
     * @param token
     * @return
     */
    public boolean startTg(String username, String token) {
        // 实例化机器人
        try {
            BotSession botSession = getBotsApiInstance().registerBot(new MyTelegramLongPollingBot(username, token));
            boolean running = botSession.isRunning();
            if (!running) {
                return false;
            }
            botSerssionMap.put(username, botSession);
        } catch (TelegramApiException e) {
            log.error("机器人启动失败");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private TelegramBotsApi getBotsApiInstance() throws TelegramApiException {
        if (this.botsApi == null) {
            botsApi=new TelegramBotsApi(DefaultBotSession.class);
        }
        return botsApi;
    }

    public BotSession getBotSession(String username) {
        return this.botSerssionMap.get(username);
    }
}
