package com.baisha.init;

import com.baisha.business.TgBotBusiness;
import com.baisha.handle.TelegramMessageHandler;
import com.baisha.handle.TelegramMyChatMemberHandler;
import com.baisha.modulecommon.util.SpringContextUtil;
import com.baisha.service.TgChatService;
import com.baisha.util.TelegramBotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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

    @Override
    public void run(ApplicationArguments args) {
        log.info(casinoWebDomain + " run success");
        TelegramBotUtil.casinoWebDomain = casinoWebDomain;

        // 从上下文获取Bean
        TelegramBotUtil.telegramMyChatMemberHandler =
                SpringContextUtil.getBean(TelegramMyChatMemberHandler.class);
        TelegramBotUtil.telegramMessageHandler =
                SpringContextUtil.getBean(TelegramMessageHandler.class);
        TelegramBotUtil.tgChatService =
                SpringContextUtil.getBean(TgChatService.class);

        // 初始化-注册机器人到TelegramBotsApi
        tgBotBusiness.registerAllBot();
        log.info("初始化-注册机器人到TelegramBotsApi run success");
    }
}