package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Test;
import org.example.cursera.domain.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByUserIdAndTestTopicId(Long userId, Long topicId);

    List<TestResult> findByTest(Test test);

    List<TestResult> findByTestAndUserId(Test test, Long userId);


    @Query("SELECT tr FROM TestResult tr WHERE tr.user.id = :userId AND tr.lesson.module.course.id = :courseId")
    List<TestResult> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT tr FROM TestResult tr WHERE tr.user.id = :userId AND tr.lesson.module.course.id = :courseId")
    List<TestResult> findTestResultsByUserAndCourse(Long userId, Long courseId);

    List<TestResult> findByUserIdAndLessonId(Long userId, Long id);
}