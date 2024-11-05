package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByName(String name);

    List<Course> findAllByModeratorId(Long moderatorId);
}