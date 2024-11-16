package org.example.cursera.service.analysis;

import org.example.cursera.domain.dtos.CourseStatisticsDto;

import java.util.List;

public interface AnalysisService {
    /**
     * Анализирует курсы, на которые пользователь подписан.
     *
     * @param userId ID пользователя
     * @return список статистики по каждому подписанному курсу
     */
    List<CourseStatisticsDto> analyzeSubscribedCourses(Long userId);
}
