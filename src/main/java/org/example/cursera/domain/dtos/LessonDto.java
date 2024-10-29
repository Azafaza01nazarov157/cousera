package org.example.cursera.domain.dtos;

import lombok.*;

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
}
