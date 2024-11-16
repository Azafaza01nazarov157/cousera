package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.domain.entity.*;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.service.analysis.AnalysisService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public List<CourseStatisticsDto> analyzeSubscribedCourses(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с ID " + userId + " не найден");
        }
        User user = userOpt.get();

        List<Course> subscribedCourses = courseRepository.findCoursesBySubscriberId(userId);

        return subscribedCourses.stream().map(course -> {
            List<Lesson> allLessons = lessonRepository.findByCourseId(course.getId());
            int totalLessons = allLessons.size();

            List<Lesson> completedLessons = lessonRepository.findCompletedLessonsByUserAndCourse(userId, course.getId());
            int completedCount = completedLessons.size();

            boolean isCourseCompleted = totalLessons > 0 && totalLessons == completedCount;

            int remainingLessons = totalLessons - completedCount;

            double completionPercentage = (totalLessons > 0) ? (completedCount * 100.0 / totalLessons) : 0;

            return CourseStatisticsDto.builder()
                    .email(user.getEmail())
                    .courseName(course.getName())
                    .totalLessons(totalLessons)
                    .completedLessons(completedCount)
                    .remainingLessons(remainingLessons)
                    .completionPercentage(completionPercentage)
                    .subscriptionStatus(isCourseCompleted ? "COMPLETED" : "IN_PROGRESS")
                    .build();
        }).collect(Collectors.toList());
    }
}
