package org.example.cursera.service.user;

import org.example.cursera.domain.dtos.CourseDto;
import org.example.cursera.domain.dtos.GetCourseDto;
import org.example.cursera.domain.dtos.SubscriberDto;

import java.util.List;

public interface ModeratorService {

    /**
     * Добавить подписчика к курсу (доступно только модераторам).
     *
     * @param moderatorId Идентификатор модератора.
     * @param courseId    Идентификатор курса.
     * @param userId      Идентификатор пользователя для подписки.
     */
    void addSubscriberToCourse(Long moderatorId, Long courseId, Long userId);

    /**
     * Получить детальную информацию о курсе по его идентификатору.
     *
     * @param courseId Идентификатор курса.
     * @return Объект GetCourseDto с деталями курса.
     */
    GetCourseDto findCourseById(Long courseId);

    /**
     * Получить список всех подписчиков указанного курса.
     *
     * @param courseId Идентификатор курса.
     * @return Список объектов SubscriberDto.
     */
    List<SubscriberDto> getAllSubscribers(Long courseId);

    /**
     * Approve a subscription request for a course.
     *
     * @param courseId ID of the course.
     * @param requestId ID of the subscription request to approve.
     */
    void approveSubscription(Long courseId, Long requestId);


    /**
     * Approve a subscription request for a course.
     *
     * @param courseId ID of the course.
     * @param requestId ID of the subscription request to approve.
     */
    void rejectSubscription(Long courseId, Long requestId);

    /**
     * Получить список всех курсов, управляемых указанным модератором.
     *
     * @param moderatorId Идентификатор модератора.
     * @return Список объектов GetCourseDto.
     */
    List<CourseDto> getCoursesByModeratorId(Long moderatorId);
}
