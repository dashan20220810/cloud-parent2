package com.baisha.fileuploaddownload.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author yihui
 */
@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;


    @Bean
    public MinioClient initMinioClient() {
        log.info("MinioConfig初始化endpoint={},accessKey={},secretKey={}", endpoint, accessKey, secretKey);
        MinioClient minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        if (Objects.nonNull(minioClient)) {
            log.info("Minio 初始化成功 ");
            return minioClient;
        } else {
            log.info("Minio 初始化失败 ");
        }
        return null;
    }


}
