package com.baisha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class FileUploadDownloadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadDownloadApplication.class, args);
    }

}
