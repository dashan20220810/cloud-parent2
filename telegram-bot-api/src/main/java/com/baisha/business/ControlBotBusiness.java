package com.baisha.business;

import com.baisha.bot.MyTelegramLongPollingBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class ControlBotBusiness {

    private TelegramBotsApi botsApi;

    public static ConcurrentMap<String, BotSession> botSessionMap = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, MyTelegramLongPollingBot> myBotMap = new ConcurrentHashMap<>();

    private TelegramBotsApi getBotsApiInstance() throws TelegramApiException {
        if (this.botsApi == null) {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
        }
        return botsApi;
    }

    @Async
    public Boolean shutdownBot(String botName) {
        //1.检测人池中是否有该机器人实例,有则停止
        BotSession botSession = botSessionMap.get(botName);
        if (botSession != null) {
            if (botSession.isRunning()) {
                botSession.stop();
            }
        }
        botSessionMap.remove(botName);
        myBotMap.remove(botName);
        return true;
    }

    /**
     * 启动机器人
     *
     * @param botName
     * @param token
     * @return
     */
    public boolean startupBot(String botName, String token) {
        // 1.检测实例池中是否有该机器人实例，有直接启动
        BotSession botSession = botSessionMap.get(botName);
        if (botSession != null) {
            if (botSession.isRunning()) {
                return true;
            }
            botSession.start();
            // 启动后再次判断
            if (botSession.isRunning()) {
                return true;
            }
        }

        // 2.没有实例创建一个机器人
        MyTelegramLongPollingBot myBot;
        try {
            myBot = new MyTelegramLongPollingBot(botName, token);
            botSession = getBotsApiInstance().registerBot(myBot);
        } catch (TelegramApiException e) {
            log.error("启动机器人失败,username:{},token:{}", botName, token);
            return false;
        }
        if (botSession.isRunning()) {
            // 放入实例池中
            botSessionMap.put(botName, botSession);
            myBotMap.put(botName, myBot);
            return true;
        }
        return false;
    }
}
