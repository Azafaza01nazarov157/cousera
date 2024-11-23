package org.example.cursera.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
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
        MinioClient minioClient = MinioClient.builder()
                .endpoint(miniProp.getMinioUrl())
                .credentials(miniProp.getAccessKey(), miniProp.getSecretKey())
                .build();

        createBucketIfNotExists(minioClient, "cousera");
        return minioClient;
    }

    private void createBucketIfNotExists(MinioClient minioClient, String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                System.out.println("Bucket '" + bucketName + "' created successfully.");
            } else {
                System.out.println("Bucket '" + bucketName + "' already exists.");
            }
        } catch (MinioException e) {
            System.err.println("Error occurred while checking/creating bucket: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
