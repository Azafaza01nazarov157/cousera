package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Collection<Test> findByTopicId(Long topicId);

    @Query("SELECT t FROM Test t JOIN FETCH t.topic topic JOIN FETCH topic.lesson WHERE t.id = :testId")
    Optional<Test> findByIdWithLesson(@Param("testId") Long testId);
}