package org.example.cursera.service.course;

import org.example.cursera.domain.dtos.LessonDto;

import java.util.List;

public interface LessonService {

    /**
     * Создать новый урок в указанном модуле.
     *
     * @param moduleId          Идентификатор модуля.
     * @param lessonName        Название урока.
     * @param lessonDescription Описание урока.
     * @return Объект LessonDto с деталями созданного урока.
     */
    LessonDto createLesson(Long moduleId, String lessonName, String lessonDescription);

    /**
     * Получить детали урока по его идентификатору.
     *
     * @param lessonId Идентификатор урока.
     * @return Объект LessonDto с деталями урока.
     */
    LessonDto findLessonById(Long lessonId);

    /**
     * Получить список уроков, связанных с указанным модулем.
     *
     * @param moduleId Идентификатор модуля.
     * @return Список объектов LessonDto.
     */
    List<LessonDto> getLessonsByModuleId(Long moduleId);

    /**
     * Удалить урок по его идентификатору.
     *
     * @param lessonId Идентификатор урока.
     */
    void deleteLesson(Long lessonId);
}
