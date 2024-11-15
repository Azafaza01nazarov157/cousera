package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.Test;
import org.example.cursera.domain.entity.TestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {
    List<TestResult> findByUserIdAndTestTopicId(Long userId, Long topicId);

    List<TestResult> findByTest(Test test);

    List<TestResult> findByTestAndUserId(Test test, Long userId);
}