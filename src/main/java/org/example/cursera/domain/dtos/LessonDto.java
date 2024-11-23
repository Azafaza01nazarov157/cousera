package org.example.cursera.domain.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDto {
    private Long id;
    private String name;
    private String description;
    private Long moduleId;
    private String moduleName;
    private String level;
    private String fileUrl;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
