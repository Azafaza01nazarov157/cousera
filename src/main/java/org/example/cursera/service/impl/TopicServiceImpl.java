package org.example.cursera.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cursera.domain.dtos.TopicDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.Topic;
import org.example.cursera.domain.repository.LessonRepository;
import org.example.cursera.domain.repository.TopicRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.course.TopicService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;

    @Override
    public List<TopicDto> getAllTopics() {
        return topicRepository.findAll().stream()
                .map(topic -> TopicDto.builder()
                        .id(topic.getId())
                        .name(topic.getName())
                        .lessonId(topic.getLesson().getId())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public TopicDto createTopic(String name, Long lessonId) {
        final Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Lesson with ID " + lessonId + " not found.")));

        if (lesson.getModule() == null) {
            throw new NotFoundException(new ErrorDto("404", "Course not found"));
        }

        Topic topic = Topic.builder()
                .name(name)
                .lesson(lesson)
                .build();

        topicRepository.save(topic);
        log.info("Topic '{}' created for lesson '{}'", name, lesson.getId());

        return TopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonId(topic.getLesson().getId())
                .build();
    }

    @Override
    public TopicDto findTopicById(Long topicId) {
        final Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        if (lessonRepository.findById(topic.getLesson().getId()).isEmpty()) {
            throw new NotFoundException(new ErrorDto("404", "Course not found"));
        }

        return TopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonId(topic.getLesson().getId())
                .build();
    }

    @Override
    public TopicDto updateTopic(Long topicId, String name) {
        final Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        topic.setName(name);
        topicRepository.save(topic);
        log.info("Topic with ID '{}' updated to '{}'", topicId, name);

        return TopicDto.builder()
                .id(topic.getId())
                .name(topic.getName())
                .lessonId(topic.getLesson().getId())
                .build();
    }

    @Override
    public void deleteTopic(Long topicId) {
        final Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Topic with ID " + topicId + " not found.")));

        topicRepository.delete(topic);
        log.info("Topic with ID '{}' has been deleted", topicId);
    }
}
