package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.domain.dtos.TestResultDto;
import org.example.cursera.domain.dtos.TestSubmissionDto;
import org.example.cursera.service.course.TestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;




    @Operation(summary = "Создание теста", description = "Создаёт новый тест для указанной темы")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Тест успешно создан"),
            @ApiResponse(responseCode = "404", description = "Тема не найдена")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/create")
    public ResponseEntity<TestDto> createTest(
            @Parameter(description = "ID темы") @RequestParam Long topicId,
            @Parameter(description = "Вопрос для теста") @RequestParam String question,
            @Parameter(description = "Первый вариант ответа") @RequestParam String option1,
            @Parameter(description = "Второй вариант ответа") @RequestParam String option2,
            @Parameter(description = "Третий вариант ответа") @RequestParam String option3,
            @Parameter(description = "Четвёртый вариант ответа") @RequestParam String option4,
            @Parameter(description = "Правильный вариант ответа") @RequestParam String correctOption) {

        TestDto testDto = testService.createTest(topicId, question, option1, option2, option3, option4, correctOption);
        return new ResponseEntity<>(testDto, HttpStatus.CREATED);
    }


    @Operation(summary = "Получение тестов по ID темы", description = "Возвращает список тестов для указанной темы")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список тестов успешно получен"),
            @ApiResponse(responseCode = "404", description = "Тема не найдена")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<TestDto>> getTestsByTopicId(
            @Parameter(description = "ID темы") @PathVariable Long topicId) {

        List<TestDto> tests = testService.getTestsByTopicId(topicId);
        return new ResponseEntity<>(tests, HttpStatus.OK);
    }



    @Operation(summary = "Прохождение теста", description = "Позволяет пользователю пройти тест и получить результат")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результат теста успешно получен"),
            @ApiResponse(responseCode = "404", description = "Тест или пользователь не найден")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/take")
    public ResponseEntity<TestResultDto> takeTest(
            @RequestBody List<TestSubmissionDto> submissions,
            @RequestParam Long userId) {

        TestResultDto testResultDto = testService.takeTest(submissions, userId);
        return new ResponseEntity<>(testResultDto, HttpStatus.OK);
    }

    @Operation(summary = "Получение результатов тестов для пользователя по теме", description = "Возвращает результаты тестов для указанной темы и пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результаты тестов успешно получены"),
            @ApiResponse(responseCode = "404", description = "Тема или пользователь не найдены")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/topic/{topicId}/user/{userId}/results")
    public ResponseEntity<List<TestResultDto>> getUserResultsByTopic(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "ID темы") @PathVariable Long topicId) {

        List<TestResultDto> results = testService.getUserResultsByTopic(userId, topicId);
        return ResponseEntity.ok(results);
    }

}
