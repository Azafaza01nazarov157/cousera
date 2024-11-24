package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.MinioFileDto;
import org.example.cursera.domain.dtos.TestDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.*;
import org.example.cursera.domain.entity.Module;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.MinioFileRepository;
import org.example.cursera.domain.repository.ModuleRepository;
import org.example.cursera.domain.repository.TopicRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.LessonService;
import org.example.cursera.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final MinioService minioService;
    private final MinioFileRepository minioFileRepository;
    private final TopicRepository topicRepository;

    @Override
    @Transactional
    public LessonDto createLesson(Long moduleId, String lessonName, String lessonDescription, String level, MultipartFile file) {
        // Проверяем существование модуля
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Module with ID " + moduleId + " not found")));

        log.info("Received file: {}", file != null ? file.getOriginalFilename() : "No file uploaded");

        MinioFile uploadedFile = null;

        // Создаем урок
        Lesson.LessonBuilder lessonBuilder = Lesson.builder()
                .name(lessonName)
                .description(lessonDescription)
                .module(module)
                .level(level)
                .topics(Collections.emptyList()); // Создаем пустой список тем

        // Если файл загружен, загружаем его в MinIO и сохраняем данные
        if (file != null && !file.isEmpty()) {
            MinioFileDto fileDto = minioService.uploadFile(file, "lessons/" + moduleId + "/files");
            uploadedFile = mapToEntity(fileDto);

            // Сохраняем данные о файле для урока
            uploadedFile = minioFileRepository.save(uploadedFile);
            lessonBuilder.file(uploadedFile); // Устанавливаем файл в урок
        }

        Lesson lesson = lessonBuilder.build();
        lessonRepository.save(lesson);

        if (uploadedFile != null) {
            uploadedFile.setLesson(lesson);
            minioFileRepository.save(uploadedFile);
        }

        log.info("Lesson '{}' created in module '{}'", lessonName, module.getName());

        // Возвращаем DTO созданного урока
        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(module.getId())
                .moduleName(module.getName())
                .level(lesson.getLevel())
                .fileUrl(uploadedFile != null ? uploadedFile.getFileUrl() : null)
                .build();
    }





    @Override
    public LessonDto findLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found")));

        Module module = lesson.getModule();
        MinioFile file = lesson.getFile();

        return LessonDto.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(module.getId())
                .moduleName(module.getName())
                .level(lesson.getLevel())
                .fileUrl(file != null ? file.getFileUrl() : null)
                .fileName(file != null ? file.getFileName() : null)
                .contentType(file != null ? file.getContentType() : null)
                .fileSize(file != null ? file.getSize() : null)
                .uploadedAt(file != null ? file.getUploadedAt() : null)
                .build();
    }



    @Override
    public List<LessonDto> getLessonsByModuleId(Long moduleId) {
        return lessonRepository.findByModuleId(moduleId).stream()
                .map(lesson -> {
                    Module module = lesson.getModule();
                    MinioFile file = lesson.getFile();
                    return LessonDto.builder()
                            .id(lesson.getId())
                            .name(lesson.getName())
                            .description(lesson.getDescription())
                            .moduleId(module.getId())
                            .moduleName(module.getName())
                            .level(lesson.getLevel())
                            .fileUrl(file != null ? file.getFileUrl() : "No file available")
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
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found")));

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


    private MinioFile mapToEntity(MinioFileDto fileDto) {
        MinioFile file = new MinioFile();
        file.setFileName(fileDto.getFileName());
        file.setFileUrl(fileDto.getFileUrl());
        file.setContentType(fileDto.getContentType());
        file.setSize(fileDto.getSize());
        file.setUploadedAt(fileDto.getUploadedAt());
        return file;
    }


}
