package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.domain.entity.*;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.TestResultRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.service.analysis.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger log = LoggerFactory.getLogger(AnalysisServiceImpl.class);
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository; // новый репозиторий

    @Override
    public List<CourseStatisticsDto> analyzeSubscribedCourses(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
        User user = userOpt.get();

        List<Course> subscribedCourses = courseRepository.findCoursesBySubscriberId(userId);
        log.info("Courses subscribed by user {}: {}", user.getEmail(), subscribedCourses.size());


        return subscribedCourses.stream().map(course -> {
            List<Lesson> allLessons = lessonRepository.findByCourseId(course.getId());
            log.info("Lessons for course {}: {}", course.getName(), allLessons.size());

            int totalLessons = allLessons.size();

            List<Lesson> completedLessons = lessonRepository.findCompletedLessonsByUserAndCourse(userId, course.getId());
            log.info("Completed lessons for user {} in course {}: {}", user.getEmail(), course.getName(), completedLessons.size());

            int completedCount = completedLessons.size();

            boolean isCourseCompleted = totalLessons > 0 && totalLessons == completedCount;

            int remainingLessons = totalLessons - completedCount;

            double completionPercentage = (totalLessons > 0) ? (completedCount * 100.0 / totalLessons) : 0;

            List<TestResult> testResults = testResultRepository.findTestResultsByUserAndCourse(userId, course.getId());
            log.info("Test results for course {}: {}", course.getName(), testResults.size());
            for (TestResult testResult : testResults) {
                log.info("Result: {}", testResult);
            }

            double averageTestScore = testResults.stream()
                    .mapToInt(TestResult::getScore)
                    .average()
                    .orElse(0.0);

            long successfulTests = testResults.stream()
                    .filter(TestResult::isCorrect)
                    .count();
            double testSuccessPercentage = (testResults.isEmpty()) ? 0 : (successfulTests * 100.0 / testResults.size());

            return CourseStatisticsDto.builder()
                    .email(user.getEmail())
                    .courseName(course.getName())
                    .totalLessons(totalLessons)
                    .completedLessons(completedCount)
                    .remainingLessons(remainingLessons)
                    .completionPercentage(completionPercentage)
                    .averageTestScore(averageTestScore)
                    .testSuccessPercentage(testSuccessPercentage)
                    .subscriptionStatus(isCourseCompleted ? "COMPLETED" : "IN_PROGRESS")
                    .build();
        }).collect(Collectors.toList());
    }
}

