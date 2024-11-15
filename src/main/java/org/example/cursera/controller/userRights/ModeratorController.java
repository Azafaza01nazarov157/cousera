package org.example.cursera.controller.userRights;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.CourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.SubscriberDto;
import org.example.cursera.domain.enums.RequestStatus;
import org.example.cursera.domain.repository.SubscriptionRequestRepository;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.user.ModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moderator")
@Slf4j
@RequiredArgsConstructor
public class ModeratorController {
    private final ModeratorService moderatorService;

    @Operation(summary = "Добавить подписчика на курс", description = "Добавляет пользователя как подписчика на указанный курс.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно подписан на курс"),
            @ApiResponse(responseCode = "404", description = "Модератор, курс или пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен: пользователь не является модератором")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/add-subscriber")
    public ResponseEntity<String> addSubscriberToCourse(
            @RequestParam Long moderatorId,
            @RequestParam Long courseId,
            @RequestParam Long userId) {
        try {
            moderatorService.addSubscriberToCourse(moderatorId, courseId, userId);
            return ResponseEntity.ok("Пользователь успешно подписан на курс.");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Найти курс по ID", description = "Получает курс по его уникальному идентификатору.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курс найден"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
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

    @Operation(summary = "Получить всех подписчиков курса", description = "Получает всех подписчиков для указанного курса.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписчики успешно получены"),
            @ApiResponse(responseCode = "404", description = "Курс не найден")
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

    @Operation(summary = "Одобрить запрос на подписку", description = "Позволяет модератору одобрить запрос на подписку.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписка успешно одобрена"),
            @ApiResponse(responseCode = "404", description = "Запрос на подписку не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен или достигнут лимит подписок")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/{courseId}/approve-request/{requestId}")
    public ResponseEntity<String> approveSubscription(
            @PathVariable Long courseId,
            @PathVariable Long requestId) {
        try {
            moderatorService.approveSubscription(courseId, requestId);
            return ResponseEntity.ok("Подписка успешно одобрена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Отклонить запрос на подписку", description = "Позволяет модератору отклонить запрос на подписку.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписка успешно отклонена"),
            @ApiResponse(responseCode = "404", description = "Запрос на подписку не найден")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @PostMapping("/{courseId}/reject-request/{requestId}")
    public ResponseEntity<String> rejectSubscription(
            @PathVariable Long courseId,
            @PathVariable Long requestId) {
        try {
            moderatorService.rejectSubscription(courseId, requestId);
            return ResponseEntity.ok("Подписка успешно отклонена");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Получить все курсы модератора", description = "Получает все курсы, управляемые указанным модератором.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Курсы успешно получены"),
            @ApiResponse(responseCode = "404", description = "Курсы для указанного модератора не найдены")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/moderators/{moderatorId}/courses")
    public ResponseEntity<List<CourseDto>> getCoursesByModeratorId(@PathVariable Long moderatorId) {
        try {
            List<CourseDto> courses = moderatorService.getCoursesByModeratorId(moderatorId);
            if (courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(courses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Удалить подписчика с курса", description = "Позволяет модератору удалить подписчика с указанного курса.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписчик успешно удален"),
            @ApiResponse(responseCode = "404", description = "Запрос на подписку или курс не найден")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @DeleteMapping("/{courseId}/remove-subscriber/{userId}")
    public ResponseEntity<String> removeSubscriberFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long userId) {
        try {
            moderatorService.removeSubscriberFromCourse(courseId, userId);
            return ResponseEntity.ok("Подписчик успешно удален");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Получить всех подписчиков со статусом PENDING", description = "Получает всех подписчиков указанного курса, у которых статус PENDING.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Подписчики со статусом PENDING успешно получены"),
            @ApiResponse(responseCode = "404", description = "Курс не найден или нет подписчиков со статусом PENDING")
    })
    @CrossOrigin(origins = "${application.cors.allowed-origins-base}")
    @GetMapping("/courses/{courseId}/pending-subscribers")
    public ResponseEntity<List<SubscriberDto>> getAllStatusPENDING(@PathVariable Long courseId) {
        try {
            List<SubscriberDto> pendingSubscribers = moderatorService.getAllStatusPENDING(courseId);
            if (pendingSubscribers.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(pendingSubscribers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
