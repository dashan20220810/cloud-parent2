package com.baisha.business;

import cn.hutool.core.util.StrUtil;
import com.baisha.bot.MyTelegramLongPollingBot;
import com.baisha.model.TgBot;
import com.baisha.model.vo.TgBotPageVO;
import com.baisha.modulecommon.Constants;
import com.baisha.service.TgBotService;
import com.baisha.util.TelegramServerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public static ConcurrentMap<String, BotSession> botSerssionMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<String, MyTelegramLongPollingBot> myBotMap = new ConcurrentHashMap<>();

    @Autowired
    private TgBotService tgBotService;

    public Page<TgBot> getTgBotPage(TgBotPageVO vo) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = TelegramServerUtil.setPageable(vo.getPageNumber(), vo.getPageSize(), sort);
        Specification<TgBot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new LinkedList<>();
            if (StrUtil.isNotEmpty(vo.getBotName())) {
                predicates.add(cb.equal(root.get("botName"), vo.getBotName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return tgBotService.getTgBotPage(spec, pageable);
    }

    private TelegramBotsApi getBotsApiInstance() throws TelegramApiException {
        if (this.botsApi == null) {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
        }
        return botsApi;
    }

    public BotSession getBotSession(String username) {
        return this.botSerssionMap.get(username);
    }

    public boolean updateBotSession(Long id, Integer status) {
        TgBot tgBot = tgBotService.findById(id);
        if (tgBot == null) {
            return false;
        }
        if (Constants.open.equals(status)) {
            return startupBot(tgBot.getBotName(), tgBot.getBotToken());
        }
        return shutdownBot(tgBot.getBotName(),tgBot.getBotToken());

    }

    private boolean shutdownBot(String botName,String token) {
        //1.检测人池中是否有该机器人实例,有则停止
        BotSession botSession = botSerssionMap.get(botName);
        if (botSession != null) {
            if (botSession.isRunning()) {
                botSession.stop();
            }
        }

        return true;
    }

    /**
     * 啟動機器人
     *
     * @param botName
     * @param token
     * @return
     */
    public boolean startupBot(String botName, String token) {
        //1.检测人池中是否有该机器人实例,有直接启动
        BotSession botSession = botSerssionMap.get(botName);
        if (botSession != null) {
            if (botSession.isRunning()) {
                return true;
            }
            //有实例被停止启动机器人
            botSession.start();
            //启动后再次判断
            if (botSession.isRunning()) {
                return false;
            }
        }

        //2. 没有实例创建一个机器人
        MyTelegramLongPollingBot myBot = null;
        try {
            myBot = new MyTelegramLongPollingBot(botName, token);
            botSession = getBotsApiInstance().registerBot(myBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("启动机器人失败,username:{},token:{}", botName, token);
            return false;
        }
        if (botSession.isRunning()) {
            //新加機器人放入实例池中
            botSerssionMap.put(botName, botSession);
            myBotMap.put(botName, myBot);
            return true;
        }
        return false;
    }
}
