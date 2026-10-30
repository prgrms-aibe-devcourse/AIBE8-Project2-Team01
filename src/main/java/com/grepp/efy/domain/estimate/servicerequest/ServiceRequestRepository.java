package com.grepp.efy.domain.estimate.servicerequest;

import com.grepp.efy.domain.estimate.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    boolean existsByMemberIdAndSecondCategoryIdAndRequestStatusAndCreatedAtAfter(
            Long memberId, Long secondCategoryId, RequestStatus requestStatus, LocalDateTime expirationBoundary);

    List<ServiceRequest> findByMemberIdOrderByCreatedAtDesc(Long id);
}
