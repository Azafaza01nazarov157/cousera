package org.example.cursera.domain.repository;

import io.swagger.v3.oas.annotations.Operation;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.SubscriptionRequest;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {
    List<SubscriptionRequest> findByCourseIdAndStatus(Long courseId, RequestStatus status);

    List<SubscriptionRequest> findByCourseIdAndUserId(Long courseId, Long userId);

    List<SubscriptionRequest> findByUserAndStatus(User user, RequestStatus requestStatus);

    boolean existsByUserAndCourseAndStatus(User user, Course course, RequestStatus requestStatus);

    @Query(value = "SELECT * FROM subscription_requests WHERE user_id = :userId AND course_id = :courseId AND status = :status", nativeQuery = true)
    List<SubscriptionRequest> findByUserIdAndCourseIdAndStatus(@Param("userId") Long userId, @Param("courseId") Long courseId, @Param("status") String status);

    List<SubscriptionRequest> findByUserIdAndCourseId(Long userId, Long courseId);
}
