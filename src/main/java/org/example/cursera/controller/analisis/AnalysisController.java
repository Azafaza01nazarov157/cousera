package org.example.cursera.controller.analisis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.service.impl.AnalysisServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}
