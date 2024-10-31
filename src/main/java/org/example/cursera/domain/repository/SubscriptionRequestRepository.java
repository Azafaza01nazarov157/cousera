package org.example.cursera.domain.repository;

import org.example.cursera.domain.entity.SubscriptionRequest;
import org.example.cursera.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequest, Long> {
    List<SubscriptionRequest> findByCourseIdAndStatus(Long courseId, RequestStatus status);

    List<SubscriptionRequest> findByCourseIdAndUserId(Long courseId, Long userId);
}
