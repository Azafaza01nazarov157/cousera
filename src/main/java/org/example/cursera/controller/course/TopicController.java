package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
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
    @GetMapping
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }



    @Operation(summary = "Создание темы", description = "Создает новую тему для указанного урока")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Тема успешно создана"),
        @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    @PostMapping("/create")
    public ResponseEntity<TopicDto> createTopic(
            @Parameter(description = "Название темы") @RequestParam String name,
            @Parameter(description = "ID урока") @RequestParam Long lessonId) {
        
        TopicDto topicDto = topicService.createTopic(name, lessonId);
        return new ResponseEntity<>(topicDto, HttpStatus.CREATED);
    }



    @Operation(summary = "Получить тему по ID", description = "Возвращает информацию о теме по её ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Тема успешно найдена"),
        @ApiResponse(responseCode = "404", description = "Тема не найдена")
    })
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
    @DeleteMapping("/{topicId}/delete")
    public ResponseEntity<Void> deleteTopic(
            @Parameter(description = "ID темы") @PathVariable Long topicId) {
        
        topicService.deleteTopic(topicId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
