package org.example.cursera.domain.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.cursera.domain.entity.Test;

import java.util.List;

@Getter
@Setter
@Builder
public class TopicDto {
    private Long id;
    private String name;
    private Long lessonId;
    private String description;
    private String title;
    private List<TestDto> tests;
}
