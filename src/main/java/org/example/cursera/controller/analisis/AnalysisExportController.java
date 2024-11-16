package org.example.cursera.controller.analisis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.service.analysis.AnalysisExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisExportController {

    private final AnalysisExportService analysisExportService;

    @Operation(
            summary = "Получить анализ подписчиков по курсу",
            description = "Возвращает список статистики подписчиков на основе ID курса"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Анализ подписчиков успешно получен"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
    })
    @GetMapping("/subscribers/{courseId}")
    public ResponseEntity<List<CourseStatisticsDto>> getSubscribersAnalysis(@PathVariable Long courseId) {
        List<CourseStatisticsDto> analysis = analysisExportService.getSubscribersAnalysis(courseId);
        return ResponseEntity.ok(analysis);
    }


    @Operation(
            summary = "Экспортировать анализ подписчиков в Excel",
            description = "Экспортирует данные подписчиков в Excel файл и отправляет его на скачивание"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно экспортирован и отправлен на скачивание"),
            @ApiResponse(responseCode = "500", description = "Ошибка при создании файла")
    })
    @GetMapping("/export/{courseId}")
    public void exportSubscribersAnalysisToExcel(@PathVariable Long courseId, HttpServletResponse response) {
        try {
            analysisExportService.exportAnalysisToExcel(courseId, response);
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @Operation(
            summary = "Скачать анализ подписчиков в Excel",
            description = "Возвращает Excel файл с анализом подписчиков по курсу"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Файл успешно скачан"),
            @ApiResponse(responseCode = "500", description = "Ошибка при создании файла")
    })
    @GetMapping("/download/{courseId}")
    public ResponseEntity<byte[]> downloadSubscribersAnalysis(@PathVariable Long courseId) {
        try {
            ByteArrayInputStream inputStream = analysisExportService.exportSubscribersAnalysisFromDatabase(courseId);
            byte[] bytes = inputStream.readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=subscribers_analysis.xlsx");
            headers.add("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
