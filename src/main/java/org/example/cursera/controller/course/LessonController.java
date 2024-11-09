package org.example.cursera.controller.course;

import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.service.course.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@CrossOrigin(origins = "${application.cors.allowed-origins-base}")
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @Operation(summary = "Create a new lesson", description = "Create a new lesson in the specified module.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Lesson created successfully"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @PostMapping("/{moduleId}")
    public ResponseEntity<LessonDto> createLesson(
            @PathVariable Long moduleId,
            @RequestParam String lessonName,
            @RequestParam String lessonDescription) {
        try {
            LessonDto createdLesson = lessonService.createLesson(moduleId, lessonName, lessonDescription);
            return ResponseEntity.status(201).body(createdLesson);
        } catch (Exception e) {
            return ResponseEntity.status(404).build(); // Adjust error handling as needed
        }
    }

    @Operation(summary = "Get lesson details", description = "Retrieve details of a lesson by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lesson details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @GetMapping("/{lessonId}")
    public ResponseEntity<LessonDto> findLessonById(@PathVariable Long lessonId) {
        try {
            LessonDto lessonDto = lessonService.findLessonById(lessonId);
            if (lessonDto == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(lessonDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Adjust error handling as needed
        }
    }

    @Operation(summary = "Get lessons by module", description = "Retrieve a list of lessons for a specified module.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<LessonDto>> getLessonsByModuleId(@PathVariable Long moduleId) {
        try {
            List<LessonDto> lessons = lessonService.getLessonsByModuleId(moduleId);
            return ResponseEntity.ok(lessons);
        } catch (Exception e) {
            return ResponseEntity.status(404).build(); // Adjust error handling as needed
        }
    }
}
