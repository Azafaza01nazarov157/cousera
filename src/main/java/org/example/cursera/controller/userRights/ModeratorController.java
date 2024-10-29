package org.example.cursera.controller.userRights;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.ModuleDto;
import org.example.cursera.domain.dtos.SubscriberDto;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.service.user.ModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderator")
@Slf4j
@RequiredArgsConstructor
public class ModeratorController {
    private final ModeratorService moderatorService;

    @Operation(summary = "Create a new module", description = "Creates a new module for a given course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Module created successfully"),
            @ApiResponse(responseCode = "404", description = "Course or user not found"),
            @ApiResponse(responseCode = "403", description = "Access denied: user is not a moderator")
    })
    @PostMapping("/create-module")
    public ResponseEntity<String> createModule(@RequestParam Long userId,
                                               @RequestParam Long courseId,
                                               @RequestParam String moduleName) {
        moderatorService.createModule(userId, courseId, moduleName);
        return ResponseEntity.status(HttpStatus.CREATED).body("Module created successfully.");
    }


    @Operation(summary = "Создание урока", description = "Создаёт новый урок в указанном модуле")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Урок успешно создан"),
            @ApiResponse(responseCode = "404", description = "Модуль не найден")
    })
    @PostMapping("/create")
    public ResponseEntity<LessonDto> createLesson(
            @Parameter(description = "ID модуля") @RequestParam Long moduleId,
            @Parameter(description = "Название урока") @RequestParam String lessonName,
            @Parameter(description = "Описание урока") @RequestParam String lessonDescription) {
        val create = moderatorService.createLesson(moduleId, lessonName, lessonDescription);
        return new ResponseEntity<>(create,HttpStatus.CREATED);
    }

    @Operation(summary = "Add a subscriber to a course", description = "Adds a user as a subscriber to a specified course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User subscribed to course successfully"),
            @ApiResponse(responseCode = "404", description = "Moderator, course, or user not found"),
            @ApiResponse(responseCode = "403", description = "Access denied: user is not a moderator")
    })
    @PostMapping("/add-subscriber")
    public ResponseEntity<String> addSubscriberToCourse(@RequestParam Long moderatorId,
                                                        @RequestParam Long courseId,
                                                        @RequestParam Long userId) {
        moderatorService.addSubscriberToCourse(moderatorId, courseId, userId);
        return ResponseEntity.ok("User subscribed to course successfully.");
    }


    @Operation(summary = "Find a course by ID", description = "Retrieves a course by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<GetCourseDto> findCourseById(@PathVariable Long courseId) {
        GetCourseDto course = moderatorService.findCourseById(courseId);
        return ResponseEntity.ok(course);
    }



    @Operation(summary = "Find a module by ID", description = "Retrieves a module by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module found"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<ModuleDto> findModuleById(@PathVariable Long moduleId) {
        final ModuleDto module = moderatorService.findModuleById(moduleId);
        return ResponseEntity.ok(module);
    }

    @Operation(summary = "Find a module by ID", description = "SubscriberDto a module by its unique getAllSubscribers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Module found"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })


    @GetMapping("/{courseId}/all/subscribers")
    public ResponseEntity<List<SubscriberDto>> getAllSubscribers(@PathVariable Long courseId) {
        List<SubscriberDto> subscribers = moderatorService.getAllSubscribers(courseId);
        return ResponseEntity.ok(subscribers);
    }


    @Operation(summary = "Поиск урока по ID", description = "Возвращает информацию об уроке по указанному ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Урок успешно найден"),
            @ApiResponse(responseCode = "404", description = "Урок не найден")
    })
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDto> findLessonById(
            @Parameter(description = "ID урока") @PathVariable Long lessonId) {

        final LessonDto lessonDto = moderatorService.findLessonById(lessonId);
        return new ResponseEntity<>(lessonDto, HttpStatus.OK);
    }

    @Operation(summary = "Get lessons by module ID", description = "Retrieves a list of lessons for the specified module")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson list retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @GetMapping("/module/{moduleId}/lessons")
    public ResponseEntity<List<LessonDto>> getLessonsByModuleId(
            @Parameter(description = "Module ID") @PathVariable Long moduleId) {
        List<LessonDto> lessons = moderatorService.getLessonsByModuleId(moduleId);
        return ResponseEntity.ok(lessons);
    }

}
