package org.example.cursera.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.example.cursera.config.props.MiniProp;
import org.example.cursera.domain.entity.MinioFile;
import org.example.cursera.domain.repository.MinioFileRepository;
import org.example.cursera.exeption.MinioNotSaveException;
import org.example.cursera.service.minio.FileService;
import org.example.cursera.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final MinioFileRepository minioFileRepository;
    private final MiniProp minioProp;
    private final MinioService minioService;


    public void save(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String extFile = FileNameUtils.getExtension(fileName);
        UUID uuid = UUID.randomUUID();
        String minioFileName = uuid.toString();
        String hashObjectName = minioFileName + "." + extFile;

        Boolean isSave = minioService.saveObject(minioProp.getBucketName(), multipartFile, hashObjectName);

//        if (isSave) {
//            File file = newFile(extFile, uuid, hashObjectName, fileType, fileName);
//            File savedFile = fileRepository.save(file);
//            return fileMapper.toDto(savedFile);
//        } else {
//            throw new MinioNotSaveException("Minio", "Minio doesn't save file");
//        }
    }


    private MinioFile newFile(String extFile, UUID uuid, String hashObjectName, String filename) {
        return MinioFile.builder()
                .build();
    }

}
