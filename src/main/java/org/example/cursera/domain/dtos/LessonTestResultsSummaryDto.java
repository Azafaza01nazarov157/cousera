package org.example.cursera.domain.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonTestResultsSummaryDto {
    private Long lessonId;
    private int totalQuestions;
    private int correctAnswers;
    private int incorrectAnswers;
    private double overallPercentage;
}
