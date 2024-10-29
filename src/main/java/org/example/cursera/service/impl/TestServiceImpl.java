package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.domain.dtos.TestResultDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Test;
import org.example.cursera.domain.entity.Topic;
import org.example.cursera.domain.entity.TestResult;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.repository.TestRepository;
import org.example.cursera.domain.repository.TopicRepository;
import org.example.cursera.domain.repository.TestResultRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.TestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TestResultDto takeTest(Long testId, String selectedOption, Long userId) {
        final Test test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Test with ID " + testId + " not found.")));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User with ID " + userId + " not found.")));

        boolean isCorrect = selectedOption.equals(test.getCorrectOption());
        int score = isCorrect ? 1 : 0;

        double percentage = isCorrect ? 100.0 : 0.0;

        TestResult testResult = TestResult.builder()
                .test(test)
                .user(user)
                .score(score)
                .percentage(percentage)
                .build();

        testResultRepository.save(testResult);

        log.info("User with ID '{}' took the test with ID '{}' and scored '{}'", userId, testId, score);

        return TestResultDto.builder()
                .testId(test.getId())
                .userId(userId)
                .score(score)
                .percentage(percentage)
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
                .isCorrect(result.getScore() > 0) // Assuming score > 0 indicates a correct answer
                .build()
        ).collect(Collectors.toList());
    }

}
