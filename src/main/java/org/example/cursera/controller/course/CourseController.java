package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.CreateCourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Get all courses", description = "Retrieve a list of all available courses")
    @GetMapping
    public ResponseEntity<List<GetCourseDto>> getAllCourses() {
        List<GetCourseDto> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get course by ID", description = "Retrieve a course by its unique ID")
    @Parameter(name = "id", description = "ID of the course to be retrieved", required = true)
    @GetMapping("/{id}")
    public ResponseEntity<GetCourseDto> getCourseById(@PathVariable Long id) {
        try {
            GetCourseDto course = courseService.findById(id);
            return ResponseEntity.ok(course);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Search courses by name", description = "Search and retrieve courses by their name")
    @Parameter(name = "name", description = "Name of the course(s) to search for", required = true)
    @GetMapping("/search")
    public ResponseEntity<List<GetCourseDto>> getCoursesByName(@RequestParam String name) {
        List<GetCourseDto> courses = courseService.getCourseByName(name);
        return ResponseEntity.ok(courses);
    }



    @Operation(summary = "Get courses by module name", description = "Retrieve a list of courses belonging to a specific module")
    @Parameter(name = "moduleName", description = "Name of the module to filter courses by", required = true)
    @GetMapping("/module")
    public ResponseEntity<List<GetCourseDto>> getCoursesByModule(@RequestParam String moduleName) {
        List<GetCourseDto> courses = courseService.getCourseByModule(moduleName);
        return ResponseEntity.ok(courses);
    }



    @Operation(summary = "Create a new course", description = "Create a new course with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseDto courseDto) {
        try {
            courseService.createCourse(courseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully");
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have enough rights");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("Invalid request data");
        }
    }
}
