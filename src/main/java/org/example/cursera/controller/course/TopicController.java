package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.GetTopicDto;
import org.example.cursera.domain.dtos.TopicDto;
import org.example.cursera.service.course.TopicService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;


    @Operation(summary = "Получить все темы", description = "Возвращает список всех тем")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Темы успешно получены")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @Operation(summary = "Get all topics by lesson ID", description = "Retrieve a list of topics for the specified lesson ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Topics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<List<GetTopicDto>> getAllTopicsByLessonId(@PathVariable Long lessonId) {
        List<GetTopicDto> topics = topicService.getAllTopicsByLessonId(lessonId);
        return ResponseEntity.ok(topics);
    }

    @Operation(summary = "Создание темы", description = "Создает новую тему для указанного урока")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Тема успешно создана"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/create")
    public ResponseEntity<Void> createTopic(
            @Parameter(description = "Название темы") @RequestParam String name,
            @Parameter(description = "Описание темы") @RequestParam(required = false) String description,
            @Parameter(description = "Заголовок темы") @RequestParam(required = false) String title,
            @Parameter(description = "ID урока") @RequestParam Long lessonId) {

        topicService.createTopic(name, description, title, lessonId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    @Operation(summary = "Получить тему по ID", description = "Возвращает информацию о теме по её ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тема успешно найдена"),
        @ApiResponse(responseCode = "404", description = "Тема не найдена")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/{topicId}")
    public ResponseEntity<TopicDto> findTopicById(
            @Parameter(description = "ID темы") @PathVariable Long topicId) {
        
        TopicDto topicDto = topicService.findTopicById(topicId);
        return new ResponseEntity<>(topicDto, HttpStatus.OK);
    }



    @Operation(summary = "Обновление темы", description = "Обновляет название темы по её ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тема успешно обновлена"),
        @ApiResponse(responseCode = "404", description = "Тема не найдена")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PutMapping("/{topicId}/update")
    public ResponseEntity<TopicDto> updateTopic(
            @Parameter(description = "ID темы") @PathVariable Long topicId,
            @Parameter(description = "Новое название темы") @RequestParam String name) {
        
        TopicDto updatedTopic = topicService.updateTopic(topicId, name);
        return new ResponseEntity<>(updatedTopic, HttpStatus.OK);
    }



    @Operation(summary = "Удаление темы", description = "Удаляет тему по её ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Тема успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Тема не найдена")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @DeleteMapping("/{topicId}/delete")
    public ResponseEntity<Void> deleteTopic(
            @Parameter(description = "ID темы") @PathVariable Long topicId) {
        
        topicService.deleteTopic(topicId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
