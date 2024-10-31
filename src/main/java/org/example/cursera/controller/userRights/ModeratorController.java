package org.example.cursera.controller.userRights;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.SubscriberDto;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
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

    @Operation(summary = "Add a subscriber to a course", description = "Adds a user as a subscriber to a specified course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User subscribed to course successfully"),
            @ApiResponse(responseCode = "404", description = "Moderator, course, or user not found"),
            @ApiResponse(responseCode = "403", description = "Access denied: user is not a moderator")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/add-subscriber")
    public ResponseEntity<String> addSubscriberToCourse(
            @RequestParam Long moderatorId,
            @RequestParam Long courseId,
            @RequestParam Long userId) {
        try {
            moderatorService.addSubscriberToCourse(moderatorId, courseId, userId);
            return ResponseEntity.ok("User subscribed to course successfully.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Find a course by ID", description = "Retrieves a course by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<GetCourseDto> findCourseById(@PathVariable Long courseId) {
        try {
            GetCourseDto course = moderatorService.findCourseById(courseId);
            return ResponseEntity.ok(course);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Get all subscribers of a course", description = "Retrieves all subscribers for a specified course.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscribers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/{courseId}/all/subscribers")
    public ResponseEntity<List<SubscriberDto>> getAllSubscribers(@PathVariable Long courseId) {
        try {
            List<SubscriberDto> subscribers = moderatorService.getAllSubscribers(courseId);
            return ResponseEntity.ok(subscribers);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Approve subscription request", description = "Allows a moderator to approve a subscription request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription approved successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription request not found"),
            @ApiResponse(responseCode = "403", description = "Access denied or subscription limit reached")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/{courseId}/approve-request/{requestId}")
    public ResponseEntity<String> approveSubscription(
            @PathVariable Long courseId,
            @PathVariable Long requestId) {
        try {
            moderatorService.approveSubscription(courseId, requestId);
            return ResponseEntity.ok("Subscription approved successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Reject subscription request", description = "Allows a moderator to reject a subscription request")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subscription rejected successfully"),
            @ApiResponse(responseCode = "404", description = "Subscription request not found")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/{courseId}/reject-request/{requestId}")
    public ResponseEntity<String> rejectSubscription(
            @PathVariable Long courseId,
            @PathVariable Long requestId) {
        try {
            moderatorService.rejectSubscription(courseId, requestId);
            return ResponseEntity.ok("Subscription rejected successfully");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
