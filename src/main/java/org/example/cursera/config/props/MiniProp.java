package org.example.cursera.config.props;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class MiniProp {
    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.secret.key}")
    private String secretKey;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Value("${minio.file-size}")
    private long fileSize;

}
