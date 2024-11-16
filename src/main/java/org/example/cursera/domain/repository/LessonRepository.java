package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Collection<Lesson> findByModuleId(Long moduleId);

    @Query("SELECT l FROM Lesson l WHERE l.module.course.id = :courseId")
    List<Lesson> findByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT l FROM Lesson l JOIN l.completedByUsers u WHERE u.id = :userId AND l.module.course.id = :courseId")
    List<Lesson> findCompletedLessonsByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}