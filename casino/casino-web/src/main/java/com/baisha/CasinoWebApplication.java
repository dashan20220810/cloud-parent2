package com.baisha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class CasinoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasinoWebApplication.class, args);
    }

}
