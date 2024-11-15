package org.example.cursera.service.course;

import org.example.cursera.domain.dtos.LessonTestResultsSummaryDto;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.domain.dtos.TestResultDto;
import org.example.cursera.domain.dtos.TestSubmissionDto;

import java.util.List;

public interface TestService {
    TestDto createTest(Long topicId, String question, String option1, String option2, String option3, String option4, String correctOption);

    List<TestDto> getTestsByTopicId(Long topicId);

    TestResultDto takeTest(List<TestSubmissionDto> submissions, Long userId);

    List<TestResultDto> getUserResultsByTopic(Long userId, Long topicId);

    LessonTestResultsSummaryDto getTestResultsSummaryByLessonAndUserId(Long lessonId, Long userId);
}
