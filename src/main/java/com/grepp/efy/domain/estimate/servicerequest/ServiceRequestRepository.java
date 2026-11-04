package com.grepp.efy.domain.estimate.servicerequest;

import com.grepp.efy.domain.estimate.enums.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    boolean existsByMemberIdAndSecondCategoryIdAndRequestStatusNotAndCreatedAtAfter
            (Long memberId, Long secondCategoryId, RequestStatus requestStatus, LocalDateTime expirationBoundary);

    @EntityGraph(attributePaths = {"secondCategory", "secondRegion"})
    List<ServiceRequest> findByMemberIdAndRequestStatusNotOrderByCreatedAtDesc(Long member_id, RequestStatus requestStatus);

    @EntityGraph(attributePaths = {"secondCategory", "secondRegion"})
    @Query("""
            SELECT r
            FROM ServiceRequest r
            WHERE r.secondCategory.id IN :categoryIds
              AND (:nationwide = true
                   OR r.secondRegion.id IN :secondRegionIds
                   OR r.secondRegion.firstRegion.id IN :firstRegionIds)
              AND r.requestStatus <> :requestStatus
              AND r.member.id <> :id
              AND r.member.deletedAt IS NULL
              AND r.createdAt > :boundary
            ORDER BY r.createdAt DESC
            """)
    List<ServiceRequest> findMatchedRequests(
            List<Long> categoryIds, List<Long> secondRegionIds, List<Long> firstRegionIds,
            boolean nationwide, RequestStatus requestStatus, Long id, LocalDateTime boundary);

    @EntityGraph(attributePaths = {"member", "secondCategory", "secondRegion"})
    Page<ServiceRequest> findAllBy(Pageable pageable);

    @EntityGraph(attributePaths = {"member", "secondCategory", "secondRegion"})
    Page<ServiceRequest> findByRequestStatus(RequestStatus requestStatus, Pageable pageable);
}
