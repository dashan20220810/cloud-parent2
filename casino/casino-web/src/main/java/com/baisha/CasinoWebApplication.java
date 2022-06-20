package com.baisha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CasinoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasinoWebApplication.class, args);
    }

}
