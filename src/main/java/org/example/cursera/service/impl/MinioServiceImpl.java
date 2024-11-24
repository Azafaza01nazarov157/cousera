package org.example.cursera.service.impl;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.config.props.MiniProp;
import org.example.cursera.domain.dtos.MinioFileDto;
import org.example.cursera.domain.entity.MinioFile;
import org.example.cursera.domain.repository.MinioFileRepository;
import org.example.cursera.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioFileRepository minioFileRepository;
    private final MiniProp miniProp;

    @Override
    public MinioFileDto uploadFile(MultipartFile file, String directory) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            String fileName = directory + "/" + file.getOriginalFilename();

            // Загружаем файл в MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(miniProp.getBucketName())
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("File uploaded to MinIO: {}", fileName);

            // Возвращаем данные файла
            return MinioFileDto.builder()
                    .fileName(fileName)
                    .fileUrl(miniProp.getMinioUrl() + "/" + miniProp.getBucketName() + "/" + fileName)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("Failed to upload file");
        }
    }

    // Метод преобразования сущности в DTO
    private MinioFileDto mapToDto(MinioFile minioFile) {
        return MinioFileDto.builder()
                .id(minioFile.getId())
                .fileName(minioFile.getFileName())
                .fileUrl(minioFile.getFileUrl())
                .contentType(minioFile.getContentType())
                .size(minioFile.getSize())
                .uploadedAt(minioFile.getUploadedAt())
                .build();
    }

    @Override
    public String getPresignedDownloadUrl(String filePath) {
        try {
            log.info("Generating presigned URL for file: {}", filePath);
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(miniProp.getBucketName())
                            .object(filePath)
                            .method(Method.GET)
                            .expiry(60 * 60) // Срок действия ссылки 1 час
                            .build()
            );
            log.info("Generated presigned URL: {}", url);
            return url;
        } catch (Exception e) {
            log.error("Error generating presigned URL for file: {}", filePath, e);
            throw new RuntimeException("Failed to generate download URL");
        }
    }
}
