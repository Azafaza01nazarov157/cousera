package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.*;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.MinioFile;
import org.example.cursera.domain.entity.Test;
import org.example.cursera.domain.entity.Topic;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.MinioFileRepository;
import org.example.cursera.domain.repository.TopicRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.TopicService;
import org.example.cursera.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final MinioService minioService;
    private final MinioFileRepository minioFileRepository;

    private List<TestDto> mapTestsToTestDtos(List<Test> tests) {
        return tests.stream().map(test -> TestDto.builder()
                .id(test.getId())
                .question(test.getQuestion())
                .options(List.of(test.getOption1(), test.getOption2(), test.getOption3(), test.getOption4()))
                .correctOption(test.getCorrectOption())
                .topicId(test.getTopic().getId())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<GetTopicDto> getAllTopicsByLessonId(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found.")));

        return lesson.getTopics().stream()
                .map(topic -> GetTopicDto.builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .lessonId(topic.getLesson().getId())
                        .description(topic.getDescription())
                        .title(topic.getTitle())
                        .lessonName(lesson.getName())
                        .tests(mapTestsToTestDtos(topic.getTests()))
                        .files(topic.getFiles().stream()
                                .map(file -> FileDto.builder()
                                        .fileUrl(file.getFileUrl())
                                        .contentType(file.getContentType())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }



    @Override
    public List<TopicDto> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(topic -> TopicDto.builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .lessonId(topic.getLesson().getId())
                        .tests(mapTestsToTestDtos(topic.getTests()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void createTopicWithFiles(String name, String description, String title, Long lessonId, List<MultipartFile> files) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found.")));

        if (lesson.getModule() == null) {
            throw new NotFoundException(new ErrorDto("404", "Course not found"));
        }

        Topic topic = Topic.builder()
                .name(name)
                .description(description)
                .title(title)
                .lesson(lesson)
                .build();

        Topic savedTopic = topicRepository.save(topic);

        if (files != null && !files.isEmpty()) {
            files.forEach(file -> {
                MinioFileDto uploadedFile = minioService.uploadFile(file, "topics/" + savedTopic.getId());

                MinioFile minioFile = mapToEntity(uploadedFile);
                minioFile.setTopic(savedTopic);
                minioFileRepository.save(minioFile);
            });
        }

        log.info("Topic '{}' with files created for lesson '{}'", name, lesson.getId());
    }


    @Override
    public TopicDto findTopicById(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        return TopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonId(topic.getLesson().getId())
                .tests(mapTestsToTestDtos(topic.getTests()))
                .build();
    }

    @Override
    public TopicDto updateTopic(Long topicId, String name) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        topic.setName(name);
        topicRepository.save(topic);
        log.info("Topic with ID '{}' updated to '{}'", topicId, name);

        return TopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonId(topic.getLesson().getId())
                .tests(mapTestsToTestDtos(topic.getTests()))
                .build();
    }

    @Override
    public void deleteTopic(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        topicRepository.delete(topic);
        log.info("Topic with ID '{}' has been deleted", topicId);
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
