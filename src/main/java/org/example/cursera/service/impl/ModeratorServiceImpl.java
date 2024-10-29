package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.cursera.domain.dtos.*;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.Role;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.ModuleRepository;
import org.example.cursera.domain.repository.UserRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.user.ModeratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModeratorServiceImpl implements ModeratorService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public void createModule(Long userId, Long courseId, String moduleName) {
        final User manager = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "userId not found")));
        if (!Role.MODERATOR.equals(manager.getRole())) {
            throw new NotFoundException(new ErrorDto("404", "role not found"));
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "moduleId not found")));
        Module module = Module.builder()
                .name(moduleName)
                .course(course)
                .lessons(new ArrayList<>())
                .build();
        moduleRepository.save(module);
        log.info("Module '{}' created for course '{}'", moduleName, course.getName());
    }

    @Override
    @Transactional
    public LessonDto createLesson(Long moduleId, String lessonName, String lessonDescription) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Module with ID " + moduleId + " not found")));

        Lesson lesson = Lesson.builder()
                .name(lessonName)
                .description(lessonDescription)
                .module(module)
                .topics(Collections.emptyList())
                .build();

        lessonRepository.save(lesson);
        log.info("Lesson '{}' created in module '{}'", lessonName, module.getName());

        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(module.getId())
                .build();
    }


    @Override
    @Transactional
    public void addSubscriberToCourse(Long moderatorId, Long courseId, Long userId) {
        val moderator = userRepository.findById(moderatorId).orElseThrow(() -> new NotFoundException(new ErrorDto("404", "moderatorId not found")));
        if (!Role.MODERATOR.equals(moderator.getRole())) {
            throw new NotFoundException(new ErrorDto("404", "moderatorId not found"));
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "courseId not found")));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "userId not found")));

        if (!course.getSubscribers().contains(user)) {
            course.getSubscribers().add(user);
            courseRepository.save(course);
            log.info("User '{}' subscribed to course '{}'", user.getUsername(), course.getName());
        } else {
            log.info("User '{}' is already subscribed to course '{}'", user.getUsername(), course.getName());
        }
    }

    @Override
    public GetCourseDto findCourseById(Long courseId) {
        val course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "courseId not found")));

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
                        .course(List.of(
                                ModelCourseDto.builder()
                                        .id(course.getId())
                                        .name(course.getName())
                                        .description(course.getDescription())
                                        .companyName(course.getCompanyName())
                                        .createAt(course.getCreateAt())
                                        .moderatorId(course.getModeratorId())
                                        .build()
                        ))
                        .build())
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
    public ModuleDto findModuleById(Long moduleId) {
        final Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Module not found")));

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
    }



    public List<SubscriberDto> getAllSubscribers(Long courseId) {
        val course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Course not found")));

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
    public LessonDto findLessonById(Long lessonId) {
        final Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "lessonId not found")));

        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(lesson.getModule().getId())
                .build();
    }

    @Override
    public List<LessonDto> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId).stream()
                .map(lesson -> LessonDto.builder()
                        .id(lesson.getId())
                        .name(lesson.getName())
                        .description(lesson.getDescription())
                        .moduleId(lesson.getModule().getId())
                        .build())
                .collect(Collectors.toList());
    }
}