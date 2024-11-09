package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.GetUsersModuleDto;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.domain.repository.ModuleRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.ModuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void createModule(Long courseId, String moduleName) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс не найден")));

        Module module = Module.builder()
                .name(moduleName)
                .course(course)
                .lessons(new ArrayList<>())
                .build();

        moduleRepository.save(module);
        log.info("Модуль '{}' создан для курса '{}'", moduleName, course.getName());
    }

    @Override
    public GetUsersModuleDto findModuleById(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Модуль не найден")));

        List<LessonDto> lessonDtos = module.getLessons().stream()
                .map(lesson -> LessonDto.builder()
                        .id(lesson.getId())
                        .name(lesson.getName())
                        .description(lesson.getDescription())
                        .moduleId(module.getId())
                        .build())
                .toList();

        return GetUsersModuleDto.builder()
                .id(module.getId())
                .name(module.getName())
                .lessons(lessonDtos)
                .build();
    }

}
