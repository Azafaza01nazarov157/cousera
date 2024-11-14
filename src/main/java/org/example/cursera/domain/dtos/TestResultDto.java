package org.example.cursera.domain.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestResultDto {
    private Long testId;
    private Long userId;
    private int score;
    private int totalQuestions;
    private double percentage;
    private boolean isCorrect;
}
