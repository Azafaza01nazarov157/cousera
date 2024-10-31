package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.ModelCourseDto;
import org.example.cursera.domain.dtos.ModuleDto;
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
    public ModuleDto findModuleById(Long moduleId) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Модуль не найден")));

        ModelCourseDto courseDto = ModelCourseDto.builder()
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
                .course(List.of(courseDto))
                .lessons(module.getLessons().size())
                .build();
    }
}
