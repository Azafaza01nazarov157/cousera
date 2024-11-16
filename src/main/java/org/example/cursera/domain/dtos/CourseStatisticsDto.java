package org.example.cursera.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatisticsDto {
    @JsonProperty("email")
    private String email;

    @JsonProperty("course_name")
    private String courseName;

    @JsonProperty("total_lessons")
    private int totalLessons;

    @JsonProperty("completed_lessons")
    private int completedLessons;

    @JsonProperty("remaining_lessons")
    private int remainingLessons;

    @JsonProperty("completion_percentage")
    private double completionPercentage;

    @JsonProperty("average_test_score")
    private double averageTestScore;

    @JsonProperty("test_success_percentage")
    private double testSuccessPercentage;

    @JsonProperty("subscription_status")
    private String subscriptionStatus;
}
