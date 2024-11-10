package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.ModuleRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.LessonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;

    @Override
    @Transactional
    public LessonDto createLesson(Long moduleId, String lessonName, String lessonDescription) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Модуль не найден")));

        Lesson lesson = Lesson.builder()
                .name(lessonName)
                .description(lessonDescription)
                .module(module)
                .topics(Collections.emptyList())
                .build();

        lessonRepository.save(lesson);
        log.info("Урок '{}' создан в модуле '{}'", lessonName, module.getName());

        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(module.getId())
                .build();
    }

    @Override
    public LessonDto findLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Урок не найден")));

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


    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found."));
        }
        lessonRepository.deleteById(lessonId);
    }
}
