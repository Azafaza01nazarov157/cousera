package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    Collection<Lesson> findByModuleId(Long moduleId);
}