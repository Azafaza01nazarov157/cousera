package org.example.cursera.service.user;

import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.LessonDto;
import org.example.cursera.domain.dtos.ModuleDto;
import org.example.cursera.domain.dtos.SubscriberDto;

import java.util.List;

public interface ModeratorService {

    /**
     * Create a new module within a specified course.
     *
     * @param userId    The ID of the moderator creating the module.
     * @param courseId  The ID of the course to which the module will be added.
     * @param moduleName The name of the new module.
     */
    void createModule(Long userId, Long courseId, String moduleName);

    /**
     * Create a new lesson associated with a specific module.
     *
     * @param moduleId          The ID of the module to associate the lesson with.
     * @param lessonName        The name of the lesson.
     * @param lessonDescription The description of the lesson.
     * @return The created LessonDto object with details of the new lesson.
     */
    LessonDto createLesson(Long moduleId, String lessonName, String lessonDescription);

    /**
     * Add a subscriber to a specific course, allowed only for moderators.
     *
     * @param moderatorId The ID of the moderator adding the subscriber.
     * @param courseId    The ID of the course to add the subscriber to.
     * @param userId      The ID of the user to be subscribed.
     */
    void addSubscriberToCourse(Long moderatorId, Long courseId, Long userId);

    /**
     * Retrieve detailed information about a course by its ID.
     *
     * @param courseId The ID of the course to retrieve.
     * @return A GetCourseDto object containing details of the course.
     */
    GetCourseDto findCourseById(Long courseId);

    /**
     * Retrieve details of a specific module by its ID.
     *
     * @param moduleId The ID of the module to retrieve.
     * @return A ModuleDto object with details of the specified module.
     */
    ModuleDto findModuleById(Long moduleId);

    /**
     * Get a list of all subscribers in a specific course.
     *
     * @param courseId The ID of the course to retrieve subscribers for.
     * @return A list of SubscriberDto objects, each representing a subscriber.
     */
    List<SubscriberDto> getAllSubscribers(Long courseId);

    /**
     * Find a specific lesson by its ID.
     *
     * @param lessonId The ID of the lesson to retrieve.
     * @return A LessonDto object with details of the specified lesson.
     */
    LessonDto findLessonById(Long lessonId);

    /**
     * Get a list of lessons associated with a specific module.
     *
     * @param moduleId The ID of the module to retrieve lessons for.
     * @return A list of LessonDto objects, each representing a lesson in the module.
     */
    List<LessonDto> getLessonsByModuleId(Long moduleId);
}
