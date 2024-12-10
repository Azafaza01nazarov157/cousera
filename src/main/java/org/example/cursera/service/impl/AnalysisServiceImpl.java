package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.*;
import org.example.cursera.domain.repository.*;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.analysis.AnalysisService;
import org.example.cursera.util.CertificateGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TestResultRepository testResultRepository;
    private final AnalysisRepository analysisRepository;
    @Autowired
    private final CertificateGenerator certificateGenerator;

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
            int totalLessons = allLessons.size();

            List<Lesson> completedLessons = lessonRepository.findCompletedLessonsByUserAndCourse(userId, course.getId());
            int completedCount = completedLessons.size();
            log.debug("Completed lessons for user '{}' in course '{}': {}", user.getEmail(), course.getName(), completedCount);

            boolean isCourseCompleted = false;

            if (totalLessons == 0) {
                log.warn("Course '{}' has no lessons. Please check the data for consistency.", course.getName());
            } else if (totalLessons == completedCount) {
                isCourseCompleted = true;
                log.info("Course '{}' is marked as completed. Total lessons: {}, Completed lessons: {}.", course.getName(), totalLessons, completedCount);
            } else {
                log.info("Course '{}' is in progress. Total lessons: {}, Completed lessons: {}.", course.getName(), totalLessons, completedCount);
            }

            int remainingLessons = totalLessons - completedCount;

            double completionPercentage = (totalLessons > 0)
                    ? Math.round((completedCount * 100.0 / totalLessons) * 100.0) / 100.0
                    : 0.0;

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
                    .courseId(course.getId())
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


    @Transactional
    protected byte[] checkUserCourseCompletion(Long userId,Long courseId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User with ID " + userId + " not found");
        }
        User user = userOpt.get();

        List<Course> subscribedCourses = courseRepository.findCoursesBySubscriberId(userId);
        log.info("Found {} courses subscribed by user {}", subscribedCourses.size(), user.getEmail());

        for (Course course : subscribedCourses) {
            if (courseId == course.getId()) {
                List<Lesson> allLessons = lessonRepository.findByCourseId(course.getId());
                int totalLessons = allLessons.size();
                log.info("Total lessons for course '{}': {}", course.getName(), totalLessons);

                List<Lesson> completedLessons = lessonRepository.findCompletedLessonsByUserAndCourse(userId, course.getId());
                int completedCount = completedLessons.size();
                log.info("Completed lessons for user '{}' in course '{}': {}", user.getEmail(), course.getName(), completedCount);

                if (totalLessons == 0 || totalLessons != completedCount) {
                    log.info("Course '{}' is NOT completed by user '{}'", course.getName(), user.getEmail());
                } else {
                    log.info("Course '{}' is completed by user '{}'", course.getName(), user.getEmail());

                    double finalScore = calculateFinalScore(userId, course.getId());
                    log.info("Final score for user '{}' in course '{}' is {}%", user.getEmail(), course.getName(), finalScore);

                    if (finalScore < 70) {
                        log.info("User '{}' did not achieve the required score (70%) for course '{}'. Score: {}%", user.getEmail(), course.getName(), finalScore);
                        throw new IllegalStateException("User " + user.getEmail() + " did not achieve the required score (70%) for course " + course.getName());
                    }
                    try {
                        String name = user.getUsername();
                        String email = user.getEmail();

                        User moderator = userRepository.findById(course.getModeratorId())
                                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Moderator not found for course: " + course.getName())));
                        String moderatorName = moderator.getUsername();

                        byte[] pdfBytes = certificateGenerator.generateHorizontalCertificate(name, email, course.getName(), moderatorName);
                        log.info("Certificate generated for user '{}' for course '{}'", user.getEmail(), course.getName());
                        return pdfBytes;
                    } catch (Exception e) {
                        log.error("Error generating certificate for user '{}': {}", user.getEmail(), e.getMessage());
                        throw new RuntimeException("Failed to generate certificate for user " + user.getEmail());
                    }
                }
            }
        }

        throw new IllegalStateException("No courses were fully completed by user " + userId);
    }


    @Transactional
    public byte[] processUserCompletion(Long userId ,Long courseId) {
        byte[] pdfBytes = checkUserCourseCompletion(userId,courseId);
        if (pdfBytes != null && pdfBytes.length > 0) {
            log.info("User {} has completed one of their subscribed courses.", userId);
            return pdfBytes;
        } else {
            log.info("User {} has not completed any subscribed courses.", userId);
            throw new IllegalStateException("User " + userId + " has not completed any subscribed courses.");
        }
    }


    /**
     * Calculate the final score for a user's progress in a specific course.
     * The final score is the average of the test scores for all lessons within the course.
     *
     * @param userId   The ID of the user.
     * @param courseId The ID of the course.
     * @return The final score as a percentage (0-100).
     */
    private double calculateFinalScore(Long userId, Long courseId) {
        List<Lesson> allLessons = lessonRepository.findByCourseId(courseId);
        int totalTests = 0;
        int totalScore = 0;

        for (Lesson lesson : allLessons) {
            List<TestResult> testResults = testResultRepository.findByUserIdAndLessonId(userId, lesson.getId());
            for (TestResult result : testResults) {
                totalTests++;
                totalScore += result.getScore();
            }
        }

        if (totalTests == 0) {
            return 0.0;
        }

        double finalScore = (totalScore * 100.0) / totalTests;
        return Math.round(finalScore * 100.0) / 100.0;
    }


}

