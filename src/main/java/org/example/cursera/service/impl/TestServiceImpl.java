package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.LessonTestResultsSummaryDto;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.domain.dtos.TestResultDto;
import org.example.cursera.domain.dtos.TestSubmissionDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.*;
import org.example.cursera.domain.repository.*;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final TopicRepository topicRepository;
    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final AnalysisRepository analysisRepository;

    @Override
    @Transactional
    public TestDto createTest(Long topicId, String question, String option1, String option2, String option3, String option4, String correctOption) {
        final Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        Test test = Test.builder()
                .question(question)
                .option1(option1)
                .option2(option2)
                .option3(option3)
                .option4(option4)
                .correctOption(correctOption)
                .topic(topic)
                .build();

        testRepository.save(test);
        log.info("Test '{}' created for topic '{}'", question, topic.getId());

        return TestDto.builder()
                .id(test.getId())
                .question(test.getQuestion())
                .options(List.of(test.getOption1(), test.getOption2(), test.getOption3(), test.getOption4()))
                .correctOption(test.getCorrectOption())
                .topicId(test.getTopic().getId())
                .build();
    }

    @Override
    public List<TestDto> getTestsByTopicId(Long topicId) {
        return testRepository.findByTopicId(topicId).stream()
                .map(test -> TestDto.builder()
                        .id(test.getId())
                        .question(test.getQuestion())
                        .options(List.of(test.getOption1(), test.getOption2(), test.getOption3(), test.getOption4()))
                        .correctOption(test.getCorrectOption())
                        .topicId(test.getTopic().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TestResultDto takeTest(List<TestSubmissionDto> submissions, Long userId) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User with ID " + userId + " not found.")));

        int totalCorrect = 0;
        int totalQuestions = submissions.size();
        List<TestResult> testResults = new ArrayList<>();

        for (TestSubmissionDto submission : submissions) {
            Test test = testRepository.findById(submission.getTestId())
                    .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Test with ID " + submission.getTestId() + " not found.")));

            boolean isCorrect = submission.getSelectedOption().equals(test.getCorrectOption());
            int score = isCorrect ? 1 : 0;
            if (isCorrect) totalCorrect++;

            TestResult testResult = TestResult.builder()
                    .test(test)
                    .user(user)
                    .score(score)
                    .percentage(isCorrect ? 100.0 : 0.0)
                    .isCorrect(isCorrect)
                    .lesson(test.getTopic().getLesson())
                    .build();

            testResults.add(testResult);
        }

        testResultRepository.saveAll(testResults);

        double overallPercentage = (totalCorrect / (double) totalQuestions) * 100;

        Test test = testRepository.findById(submissions.get(0).getTestId())
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Test not found for ID " + submissions.get(0).getTestId())));
        Lesson lesson = test.getTopic().getLesson();

        if (!lesson.getCompletedByUsers().contains(user)) {
            lesson.getCompletedByUsers().add(user);
            lessonRepository.saveAndFlush(lesson);
            log.info("User with ID '{}' has completed lesson '{}'", userId, lesson.getName());
        }

        Topic topic = test.getTopic();
        Lesson lessonTest = topic.getLesson();
        Course course = lessonTest.getModule().getCourse();

        Analysis existingAnalysis = analysisRepository.findByUserAndCourseAndLesson(user, course, lesson);

        if (existingAnalysis != null) {
            existingAnalysis.setEventType("COMPLETED");
            analysisRepository.save(existingAnalysis);
        } else {
            Analysis analysis = new Analysis();
            analysis.setUser(user);
            analysis.setCourse(course);
            analysis.setLesson(lesson);
            analysis.setEventType("COMPLETED");

            analysisRepository.save(analysis);
        }
        log.info("User with ID '{}' took multiple tests and scored '{}' out of '{}'", userId, totalCorrect, totalQuestions);

        return TestResultDto.builder()
                .userId(userId)
                .score(totalCorrect)
                .totalQuestions(totalQuestions)
                .percentage(overallPercentage)
                .build();
    }


    @Override
    public List<TestResultDto> getUserResultsByTopic(Long userId, Long topicId) {
        List<TestResult> results = testResultRepository.findByUserIdAndTestTopicId(userId, topicId);

        return results.stream().map(result -> TestResultDto.builder()
                .testId(result.getTest().getId())
                .userId(userId)
                .score(result.getScore())
                .percentage(result.getPercentage())
                .isCorrect(result.getScore() > 0)
                .build()
        ).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public LessonTestResultsSummaryDto getTestResultsSummaryByLessonAndUserId(Long lessonId, Long userId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found")));

        List<Test> tests = lesson.getTopics().stream()
                .flatMap(topic -> topic.getTests().stream())
                .toList();
        List<TestResult> testResults = tests.stream()
                .flatMap(test -> testResultRepository.findByTestAndUserId(test, userId).stream())
                .toList();

        int totalQuestions = testResults.size();
        int correctAnswers = (int) testResults.stream()
                .filter(TestResult::isCorrect)
                .count();
        int incorrectAnswers = totalQuestions - correctAnswers;
        double overallPercentage = totalQuestions > 0
                ? (correctAnswers * 100.0) / totalQuestions
                : 0.0;

        log.info("Aggregated results for lesson '{}': Total Questions = {}, Correct = {}, Incorrect = {}, Percentage = {}%",
                lessonId, totalQuestions, correctAnswers, incorrectAnswers, overallPercentage);

        return LessonTestResultsSummaryDto.builder()
                .lessonId(lessonId)
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswers)
                .incorrectAnswers(incorrectAnswers)
                .overallPercentage((int) Math.round(overallPercentage))
                .build();
    }

}