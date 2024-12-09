package org.example.cursera.controller.analisis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.service.impl.AnalysisServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisServiceImpl analysisService;

    @Operation(
            summary = "Получить подписанные курсы пользователя",
            description = "Этот эндпоинт возвращает список курсов, на которые подписан пользователь, с информацией о прогрессе по каждому курсу."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешное получение подписанных курсов"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос, пользователь не найден"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/subscribed")
    public ResponseEntity<List<CourseStatisticsDto>> analyzeSubscribedCourses(
            @Parameter(description = "Идентификатор пользователя", example = "1") @RequestParam Long userId) {
        List<CourseStatisticsDto> subscribedCourses = analysisService.analyzeSubscribedCourses(userId);
        return ResponseEntity.ok(subscribedCourses);
    }


    @Operation(
            summary = "Проверить завершение всех курсов и скачать сертификат",
            description = "Этот эндпоинт проверяет, завершил ли пользователь все подписанные курсы, и скачивает сертификат при завершении."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сертификат успешно создан и скачан"),
            @ApiResponse(responseCode = "400", description = "Пользователь не найден или ошибка проверки"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/generate-certificate")
    public ResponseEntity<byte[]> generateCertificate(
            @Parameter(description = "Идентификатор пользователя", example = "1") @RequestParam Long userId,
            @Parameter(description = "Идентификатор курса", example = "1") @RequestParam Long courseId) {
        try {
            byte[] pdfBytes = analysisService.processUserCompletion(userId,courseId);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=certificate.pdf");
            headers.add("Content-Type", "application/pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage().getBytes());
        } catch (Exception e) {
            log.error("An unexpected error occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
    }

}
