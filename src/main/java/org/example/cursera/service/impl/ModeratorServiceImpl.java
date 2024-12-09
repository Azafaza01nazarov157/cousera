package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.cursera.domain.dtos.CourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.ModuleDto;
import org.example.cursera.domain.dtos.SubscriberDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.SubscriptionRequest;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.RequestStatus;
import org.example.cursera.domain.enums.Role;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.domain.repository.SubscriptionRequestRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.user.ModeratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorServiceImpl implements ModeratorService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;

    @Override
    @Transactional
    public void addSubscriberToCourse(Long moderatorId, Long courseId, Long userId) {
        val moderator = userRepository.findById(moderatorId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Модератор не найден")));
        if (!Role.MODERATOR.equals(moderator.getRole())) {
            throw new ForbiddenException(new ErrorDto("403", "Пользователь не является модератором"));
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Пользователь не найден")));

        boolean alreadySubscribed = subscriptionRequestRepository.existsByUserAndCourseAndStatus(user, course, RequestStatus.APPROVED);
        if (alreadySubscribed) {
            log.info("Пользователь '{}' уже подписан на курс '{}'", user.getUsername(), course.getName());
            return;
        }

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .course(course)
                .user(user)
                .status(RequestStatus.APPROVED)
                .build();

        subscriptionRequestRepository.save(subscriptionRequest);
        log.info("Пользователь '{}' подписан на курс '{}'", user.getUsername(), course.getName());
    }

    @Override
    public GetCourseDto findCourseById(Long courseId) {
        val course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        List<SubscriberDto> subscribers = course.getSubscribers().stream()
                .map(subscriber -> SubscriberDto.builder()
                        .id(subscriber.getId())
                        .username(subscriber.getUsername())
                        .email(subscriber.getEmail())
                        .role(subscriber.getRole().name())
                        .build())
                .collect(Collectors.toList());

        List<ModuleDto> modules = course.getModules().stream()
                .map(module -> ModuleDto.builder()
                        .id(module.getId())
                        .name(module.getName())
                        .lessons(module.getLessons().size())
                        .build())
                .collect(Collectors.toList());

        return GetCourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .companyName(course.getCompanyName())
                .createAt(formatDateTime(course.getCreateAt()))
                .moderatorId(course.getModeratorId())
                .modules(modules)
                .build();
    }
    public String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }
    @Override
    public List<SubscriberDto> getAllSubscribers(Long courseId) {
        val course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        return course.getSubscribers().stream()
                .map(subscriber -> SubscriberDto.builder()
                        .id(subscriber.getId())
                        .username(subscriber.getUsername())
                        .email(subscriber.getEmail())
                        .role(subscriber.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveSubscription(Long courseId, Long requestId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        SubscriptionRequest request = subscriptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Запрос на подписку не найден")));

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new ForbiddenException(new ErrorDto("403", "Запрос на подписку не в состоянии ожидания"));
        }

        if (course.getSubscribers().size() >= 20) {
            throw new ForbiddenException(new ErrorDto("403", "Достигнут лимит подписок"));
        }

        request.setStatus(RequestStatus.APPROVED);
        course.getSubscribers().add(request.getUser());

        subscriptionRequestRepository.save(request);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public void rejectSubscription(Long courseId, Long requestId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        SubscriptionRequest request = subscriptionRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Запрос на подписку не найден")));

        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new ForbiddenException(new ErrorDto("403", "Запрос на подписку уже обработан"));
        }

        request.setStatus(RequestStatus.REJECTED);
        subscriptionRequestRepository.save(request);

        log.info("Запрос на подписку пользователя '{}' на курс '{}' был отклонен",
                request.getUser().getUsername(), course.getName());
    }


    public List<CourseDto> getCoursesByModeratorId(Long moderatorId) {
        List<Course> courses = courseRepository.findAllByModeratorId(moderatorId);
        return courses.stream()
                .map(this::convertToGetCourseDto)
                .collect(Collectors.toList());
    }

    private CourseDto convertToGetCourseDto(Course course) {

        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .companyName(course.getCompanyName())
                .createAt(course.getCreateAt())
                .moderatorId(course.getModeratorId())
                .build();
    }

    @Override
    @Transactional
    public void removeSubscriberFromCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Course not found")));

        log.info("Searching for SubscriptionRequest(s) with userId={}, courseId={}", userId, courseId);
        List<SubscriptionRequest> subscriptionRequests = subscriptionRequestRepository
                .findByUserIdAndCourseIdAndStatus(userId, courseId, RequestStatus.APPROVED.name());

        if (!subscriptionRequests.isEmpty()) {
            log.info("Found SubscriptionRequest(s): {}", subscriptionRequests);

            subscriptionRequestRepository.deleteAll(subscriptionRequests);

            course.getSubscribers().removeIf(user -> user.getId().equals(userId));
            courseRepository.save(course);

            log.info("Subscriber '{}' removed from course '{}'", userId, course.getName());
        } else {
            log.info("No SubscriptionRequest found for userId={}, courseId={}", userId, courseId);
            throw new NotFoundException(new ErrorDto("404", "Subscription request not found"));
        }
    }

    @Override
    public List<SubscriberDto> getAllStatusPENDING(Long courseId) {
        return subscriptionRequestRepository.findByCourseIdAndStatus(courseId, RequestStatus.PENDING)
                .stream()
                .map(subscriptionRequest -> new SubscriberDto(
                        subscriptionRequest.getUser().getId(),
                        subscriptionRequest.getUser().getUsername(),
                        subscriptionRequest.getUser().getEmail(),
                        subscriptionRequest.getUser().getRole().toString(),
                        subscriptionRequest.getId()
                ))
                .collect(Collectors.toList());
    }


}
