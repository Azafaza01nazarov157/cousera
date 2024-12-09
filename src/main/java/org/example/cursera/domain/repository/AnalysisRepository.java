package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Analysis;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.Lesson;
import org.example.cursera.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Integer> {
    @Query("SELECT a FROM Analysis a WHERE a.user = :user AND a.course = :course AND a.lesson = :lesson")
    Analysis findByUserAndCourseAndLesson(@Param("user") User user,
                                          @Param("course") Course course,
                                          @Param("lesson") Lesson lesson);

    Optional<Analysis> findByUserIdAndCourseIdAndEventType(Long id, Long id1, String completed);
}