package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cursera.domain.dtos.*;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.entity.SubscriptionRequest;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.RequestStatus;
import org.example.cursera.domain.enums.Role;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.domain.repository.ModuleRepository;
import org.example.cursera.domain.repository.SubscriptionRequestRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.exeption.ForbiddenException;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;

    @Override
    public List<GetCourseDto> findAll() {
        return courseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetCourseDto findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Course not found")));
        return convertToDto(course);
    }

    @Override
    public List<GetCourseDto> getCourseByName(String name) {
        return courseRepository.findAllByName(name).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetCourseDto> getCourseByModule(String moduleName) {
        return moduleRepository.findAllByName(moduleName).stream()
                .map(Module::getCourse)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void createCourse(CreateCourseDto course) {
        if (course == null) {
            throw new NotFoundException(new ErrorDto("404", "Course not found"));
        }

        final User user = userRepository.findById(course.getModeratorId())
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        if (Role.MODERATOR != user.getRole()) {
            throw new ForbiddenException(new ErrorDto("403", "Forbidden! You don't have enough rights"));
        }

        Course courseBuilder = Course.builder()
                .name(course.getName())
                .moderatorId(course.getModeratorId())
                .companyName(course.getCompanyName())
                .description(course.getDescription())
                .createAt(LocalDate.now())
                .modules(new ArrayList<>())
                .subscribers(new ArrayList<>())
                .build();

        courseRepository.save(courseBuilder);
    }

    private GetCourseDto convertToDto(Course course) {
        List<SubscriberDto> subscribers = course.getSubscribers().stream()
                .map(subscriber -> new SubscriberDto(subscriber.getId(), subscriber.getUsername(), subscriber.getEmail(), subscriber.getRole().name()))
                .collect(Collectors.toList());

        List<ModuleDto> modules = course.getModules().stream()
                .map(module -> {
                    ModelCourseDto modelCourseDto = ModelCourseDto.builder()
                            .id(module.getCourse().getId())
                            .name(module.getCourse().getName())
                            .description(module.getCourse().getDescription())
                            .companyName(module.getCourse().getCompanyName())
                            .createAt(module.getCourse().getCreateAt())
                            .moderatorId(module.getCourse().getModeratorId())
                            .build();

                    return ModuleDto.builder()
                            .id(module.getId())
                            .name(module.getName())
                            .course(List.of(modelCourseDto))
                            .lessons(module.getLessons().size())
                            .build();
                })
                .collect(Collectors.toList());

        return GetCourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .companyName(course.getCompanyName())
                .createAt(course.getCreateAt())
                .subscribers(subscribers)
                .moderatorId(course.getModeratorId())
                .modules(modules)
                .build();
    }

    @Override
    @Transactional
    public void requestSubscription(Long courseId, Long userId) {
        final Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Course not found")));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "User not found")));

        SubscriptionRequest request = SubscriptionRequest.builder()
                .course(course)
                .user(user)
                .status(RequestStatus.PENDING)
                .build();

        subscriptionRequestRepository.save(request);
    }


    @Override
    public List<SubscriptionRequest> getUserSubscriptionRequests(Long courseId, Long userId) {
        if(courseId == null || userId == null) {
            throw new ForbiddenException(new ErrorDto("404", "Course or User not found"));
        }
        final Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Пользователь не найден")));

        return subscriptionRequestRepository.findByCourseIdAndUserId(courseId, userId);
    }

}
