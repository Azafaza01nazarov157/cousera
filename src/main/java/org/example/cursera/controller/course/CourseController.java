package org.example.cursera.controller.course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.example.cursera.domain.dtos.CourseDto;
import org.example.cursera.domain.dtos.CreateCourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.GetModuleDto;
import org.example.cursera.domain.entity.SubscriptionRequest;
import org.example.cursera.domain.entity.User;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.CourseService;
import org.example.cursera.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;

    @Operation(summary = "Get all courses", description = "Retrieve a list of all available courses")
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping
    public ResponseEntity<List<GetCourseDto>> getAllCourses() {
        List<GetCourseDto> courses = courseService.findAll();
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get course by ID", description = "Retrieve a course by its unique ID")
    @Parameter(name = "id", description = "ID of the course to be retrieved", required = true)
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
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
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/search")
    public ResponseEntity<List<GetCourseDto>> getCoursesByName(@RequestParam String name) {
        List<GetCourseDto> courses = courseService.getCourseByName(name);
        return ResponseEntity.ok(courses);
    }



    @Operation(summary = "Get courses by module name", description = "Retrieve a list of courses belonging to a specific module")
    @Parameter(name = "moduleName", description = "Name of the module to filter courses by", required = true)
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
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
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<String> createCourse(
            @Parameter(description = "Course details") @ModelAttribute CreateCourseDto courseDto,
            @Parameter(description = "Image file") @RequestPart("image") MultipartFile image
    ) {
        try {
            courseDto.setImage(image);

            courseService.createCourse(courseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Course created successfully");
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have enough rights");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("Invalid request data");
        }
    }


    @Operation(summary = "Request subscription to a course", description = "Allows a user to request subscription to a specific course")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription request sent successfully"),
            @ApiResponse(responseCode = "404", description = "Course or user not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/{id}/request-subscription")
    public ResponseEntity<String> requestSubscription(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            courseService.requestSubscription(id, userId);
            return ResponseEntity.ok("Subscription request sent successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @Operation(summary = "Get user subscription requests by course", description = "Retrieve all subscription requests for a specific user in a specific course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course or user not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/courses/{courseId}/users/{userId}/subscription-requests")
    public ResponseEntity<List<SubscriptionRequest>> getUserSubscriptionRequests(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        try {
            List<SubscriptionRequest> requests = courseService.getUserSubscriptionRequests(courseId, userId);
            return ResponseEntity.ok(requests);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Get modules by course", description = "Retrieve all modules for a specific course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modules retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<GetModuleDto>> getModulesByCourseId(@PathVariable Long courseId) {
        try {
            List<GetModuleDto> modules = courseService.getModuleByCourseId(courseId);
            return ResponseEntity.ok(modules);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @Operation(summary = "Get subscribed courses", description = "Retrieve all subscribed courses for the current user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscribed courses retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/{userId}/subscribed-courses")
    public ResponseEntity<List<CourseDto>> getSubscribedCourses(@PathVariable Long userId) {
        try {
            val user = userService.findById(userId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            final List<CourseDto> subscribedCourses = courseService.getSubscribedCourses(user);
            return ResponseEntity.ok(subscribedCourses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
