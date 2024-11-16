package org.example.cursera.service.analysis;

import jakarta.servlet.http.HttpServletResponse;
import org.example.cursera.domain.dtos.CourseFullAnalysisDto;
import org.example.cursera.domain.dtos.CourseStatisticsDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public interface AnalysisExportService {
    /**
     * Получить анализ подписчиков курса.
     *
     * @param courseId идентификатор курса.
     * @return список анализа подписчиков.
     */
    List<CourseStatisticsDto> getSubscribersAnalysis(Long courseId);

    /**
     * Экспорт анализа подписчиков в Excel.
     *
     * @param courseId идентификатор курса.
     * @param response HTTP-ответ для отправки файла.
     * @throws IOException в случае ошибок при создании файла.
     */
    void exportAnalysisToExcel(Long courseId, HttpServletResponse response) throws IOException;

    /**
     * Экспорт анализа подписчиков в Excel как поток.
     *
     * @param courseId идентификатор курса.
     * @return поток данных с анализом подписчиков.
     * @throws IOException в случае ошибок при создании файла.
     */
    ByteArrayInputStream exportSubscribersAnalysisFromDatabase(Long courseId) throws IOException;

}
