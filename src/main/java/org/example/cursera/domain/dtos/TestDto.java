package org.example.cursera.domain.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestDto {
    private Long id;
    private String  question;
    private List<String> options;
    private String correctOption;
    private Long topicId;
}
