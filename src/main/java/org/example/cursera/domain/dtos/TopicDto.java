package org.example.cursera.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TopicDto {
    private Long id;
    private String name;
    private Long lessonId;
}
