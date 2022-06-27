package com.baisha.init;

import com.baisha.business.TgBotBusiness;
import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import com.baisha.model.TgBot;
import com.baisha.modulecommon.Constants;
import com.baisha.modulecommon.util.SpringContextUtil;
import com.baisha.service.TgBotService;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: kimi
 */
@Slf4j
@Component
public class ApplicationRunnerInit implements ApplicationRunner {

    @Value("${project.server-url.casino-web-domain}")
    private String casinoWebDomain;

    @Autowired
    private TgBotBusiness tgBotBusiness;

    @Autowired
    TgBotService tgBotService;

    @Override
    public void run(ApplicationArguments args) {
        log.info(casinoWebDomain + " run success");
        TelegramBotUtil.casinoWebDomain = casinoWebDomain;

        // 从上下文获取Bean
        TelegramBotUtil.telegramMyChatMemberHandler =
                SpringContextUtil.getBean(TelegramMyChatMemberHandler.class);
        TelegramBotUtil.telegramMessageHandler =
                SpringContextUtil.getBean(TelegramMessageHandler.class);

        // 初始化-注册机器人到TelegramBotsApi
        // 根据状态status来过滤
        List<TgBot> tgBots = tgBotService.findByStatus(Constants.open);
        tgBots.forEach(tgBot -> {
            try {
                // 实例化机器人
                boolean isSuccess = tgBotBusiness.startupBot(tgBot.getBotName(), tgBot.getBotToken());
                if(!isSuccess){
                    tgBot.setStatus(Constants.close);
                    tgBotService.save(tgBot);
                }

            } catch (Throwable e) {
                log.error("初始化-注册机器人失败", e);
            }
        });
        log.info("初始化-注册机器人到TelegramBotsApi run success");
    }
}