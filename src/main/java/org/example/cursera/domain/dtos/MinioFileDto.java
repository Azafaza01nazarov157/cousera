package org.example.cursera.domain.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MinioFileDto {
    private Integer id;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private Long size;
    private LocalDateTime uploadedAt;
    private Long uploadedById;
}