package org.example.cursera.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseFullAnalysisDto {
    private Long courseId;
    private String courseName;
    private String description;
    private String companyName;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private Long moderatorId;

    private List<ModuleAnalysisDto> modules;
    private List<SubscriberAnalysisDto> subscribers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleAnalysisDto {
        private Long moduleId;
        private String moduleName;
        private List<LessonAnalysisDto> lessons;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LessonAnalysisDto {
        private Long lessonId;
        private String lessonName;
        private String description;
        private String level;
        private List<TopicAnalysisDto> topics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopicAnalysisDto {
        private Long topicId;
        private String topicName;
        private String title;
        private int testCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriberAnalysisDto {
        private Long userId;
        private String username;
        private String email;
        private String role;
        private boolean active;
    }
}
