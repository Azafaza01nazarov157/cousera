package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Collection<Test> findByTopicId(Long topicId);
}