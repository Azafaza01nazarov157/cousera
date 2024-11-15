package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.entity.Test;
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
    public LessonDto createLesson(Long moduleId, String lessonName, String lessonDescription,String level) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Module with ID " + moduleId + " not found")));

        Lesson lesson = Lesson.builder()
                .name(lessonName)
                .description(lessonDescription)
                .module(module)
                .level(level)
                .topics(Collections.emptyList())
                .build();

        lessonRepository.save(lesson);
        log.info("Lesson '{}' created in module '{}'", lessonName, module.getName());

        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(module.getId())
                .moduleName(module.getName())
                .level(lesson.getLevel())
                .build();
    }

    @Override
    public LessonDto findLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found")));

        Module module = lesson.getModule();
        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(module.getId())
                .moduleName(module.getName())
                .level(lesson.getLevel())
                .build();
    }

    @Override
    public List<LessonDto> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId).stream()
                .map(lesson -> {
                    Module module = lesson.getModule();
                    return LessonDto.builder()
                            .id(lesson.getId())
                            .name(lesson.getName())
                            .description(lesson.getDescription())
                            .moduleId(module.getId())
                            .moduleName(module.getName())
                            .level(lesson.getLevel())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLesson(Long lessonId) {
        if (!lessonRepository.existsById(lessonId)) {
            throw new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found"));
        }
        lessonRepository.deleteById(lessonId);
        log.info("Lesson with ID '{}' has been deleted", lessonId);
    }


    @Override
    public List<TestDto> getTestsByLessonId(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404","Lesson with ID " + lessonId + " not found")));

        return lesson.getTopics().stream()
                .flatMap(topic -> topic.getTests().stream())
                .map(this::mapToTestDto)
                .collect(Collectors.toList());
    }

    private TestDto mapToTestDto(Test test) {
        return TestDto.builder()
                .id(test.getId())
                .question(test.getQuestion())
                .options(List.of(test.getOption1(), test.getOption2(), test.getOption3(), test.getOption4()))
                .correctOption(test.getCorrectOption())
                .topicId(test.getTopic().getId())
                .build();
    }

}
