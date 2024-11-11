package org.example.cursera.domain.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTopicDto {
    private Long id;
    private String name;
    private Long lessonId;
    private String description;
    private String title;
    private String lessonName;
    private List<TestDto> tests;
}
