package org.example.cursera.service.minio;

import org.example.cursera.domain.dtos.MinioFileDto;
import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    MinioFileDto uploadFile(MultipartFile file, String directory);
    String getPresignedDownloadUrl(String filePath);
}
