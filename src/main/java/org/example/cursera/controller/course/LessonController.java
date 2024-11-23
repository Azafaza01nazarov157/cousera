package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lessons")
@CrossOrigin(origins = "${application.cors.allowed-origins-base}")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @PostMapping(value = "/{moduleId}", consumes = {"multipart/form-data"})
    public ResponseEntity<LessonDto> createLesson(
            @PathVariable Long moduleId,
            @RequestParam String lessonName,
            @RequestParam String lessonDescription,
            @RequestParam String level,
            @RequestPart(required = false) MultipartFile file
    ) {
        try {
            LessonDto createdLesson = lessonService.createLesson(moduleId, lessonName, lessonDescription, level, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLesson);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
            return ResponseEntity.ok(lessonDto);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Delete a lesson", description = "Delete a lesson by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lesson deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        try {
            lessonService.deleteLesson(lessonId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Get all tests for a lesson", description = "Retrieve all tests associated with a specific lesson ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tests retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @GetMapping("/{lessonId}/tests")
    public ResponseEntity<List<TestDto>> getTestsByLessonId(@PathVariable Long lessonId) {
        try {
            List<TestDto> tests = lessonService.getTestsByLessonId(lessonId);
            return ResponseEntity.ok(tests);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
