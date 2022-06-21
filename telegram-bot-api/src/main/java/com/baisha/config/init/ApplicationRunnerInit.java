package com.baisha.config.init;

import com.baisha.service.TgBotService;
import com.baisha.util.TelegramBotUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author: yh
 */
@Slf4j
@Component
public class ApplicationRunnerInit implements ApplicationRunner {

    @Value("${project.server-url.casino-web-domain}")
    private String casinoWebDomain;

    @Autowired
    private TgBotService tgBotService;

    @Override
    public void run(ApplicationArguments args) {
        log.info(casinoWebDomain + " run success");
        TelegramBotUtil.casinoWebDomain = casinoWebDomain;

        // 初始化-注册机器人到TelegramBotsApi
        tgBotService.registerAllBot();
        log.info("初始化-注册机器人到TelegramBotsApi run success");
    }
}