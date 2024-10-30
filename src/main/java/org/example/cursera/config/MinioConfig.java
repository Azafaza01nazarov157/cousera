package org.example.cursera.config;

import io.minio.MinioClient;
import org.example.cursera.config.props.MiniProp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    private final MiniProp miniProp;

    public MinioConfig(MiniProp miniProp) {
        this.miniProp = miniProp;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(miniProp.getMinioUrl())
                .credentials(miniProp.getAccessKey(), miniProp.getSecretKey())
                .build();
    }
}