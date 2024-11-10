package org.example.cursera.service.course;

import org.example.cursera.domain.dtos.TopicDto;

import java.util.List;

public interface TopicService {

    /**
     * Retrieve all topics available in the system.
     *
     * @return A list of TopicDto objects representing all topics.
     */
    List<TopicDto> getAllTopics();



    List<TopicDto> getAllTopicsByLessonId(Long lessonId);
    /**
     * Create a new topic associated with a specific lesson.
     *
     * @param name     The name of the new topic.
     * @param lessonId The ID of the lesson to associate the topic with.
     */
    void createTopic(String name, String description, String title, Long lessonId);

    /**
     * Retrieve a topic by its unique ID.
     *
     * @param topicId The ID of the topic to retrieve.
     * @return A TopicDto object with details of the specified topic.
     */
    TopicDto findTopicById(Long topicId);

    /**
     * Update the name of an existing topic.
     *
     * @param topicId The ID of the topic to update.
     * @param name    The new name for the topic.
     * @return The updated TopicDto object with the new topic details.
     */
    TopicDto updateTopic(Long topicId, String name);

    /**
     * Delete a topic by its unique ID.
     *
     * @param topicId The ID of the topic to delete.
     */
    void deleteTopic(Long topicId);
}
