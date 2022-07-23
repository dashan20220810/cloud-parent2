package com.baisha;

import com.baisha.modulecommon.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
@EnableJpaAuditing
public class TelegramBotApiApplication {
    public static void main(String[] args) throws UnknownHostException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Bangkok"));
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TelegramBotApiApplication.class, args);
        new SpringContextUtil().setApplicationContext(applicationContext);

        Environment env = applicationContext.getEnvironment();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        log.info("\n----------------------------------------------------------\n\t" +
                "Application Telegram-Bot-Api is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "\n\t" +
                "External: \thttp://" + ip + ":" + port + "\n\t" +
                "Swagger: \thttp://" + ip + ":" + port + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}
