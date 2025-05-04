package com.sonnvt.blog.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String ENDPOINT;

    @Value("${minio.accessKey}")
    private String ACCESS_KEY;

    @Value("${minio.secretKey}")
    private String SECRET_KEY;

    @Bean
    public MinioClient minioClient() {
        log.info("Setting up minio client {} {} {}", ENDPOINT, ACCESS_KEY, SECRET_KEY);
        return MinioClient.builder()
                .endpoint(ENDPOINT)
                .credentials(ACCESS_KEY, SECRET_KEY)
                .build();
    }
}
