package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByName(String name);

    List<Course> findAllByModeratorId(Long moderatorId);

    @Query("SELECT c FROM Course c JOIN c.subscribers s WHERE s.id = :userId")
    List<Course> findCoursesBySubscriberId(Long userId);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.image")
    List<Course> findAllWithImages();
}