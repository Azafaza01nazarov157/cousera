package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.exeption.MinioNotSaveException;
import org.example.cursera.service.minio.MinioService;
import org.example.cursera.util.MinioUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioUtil minioUtil;

    @Override
    @SneakyThrows
    public Boolean saveObject(String bucketName, MultipartFile object, String objectName) {
        try {
            return minioUtil.putObject(bucketName, objectName, object.getInputStream(), object.getContentType());
        } catch (IOException e) {
            throw new MinioNotSaveException(new ErrorDto("400", e.getMessage()));
        }
    }

    @Override
    public Resource getObjectResource(String bucketName, String fileName) {
        return new InputStreamResource(minioUtil.getObject(bucketName, fileName));
    }

    @Override
    public InputStream getInputStreamFromMinio(String bucketName, String fileName) {
        return minioUtil.getObject(bucketName, fileName);
    }

    @Override
    public Boolean deleteObject(String bucketName, String objectName) {
        return minioUtil.removeObject(bucketName, objectName);
    }

    @Override
    public Boolean deleteMultipleObject(String bucketName, List<String> objectNames) {
        return minioUtil.removeObject(bucketName, objectNames);
    }
}
