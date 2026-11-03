package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    boolean existsByExpertProfileAndServiceRequest(ExpertProfile expertProfile, ServiceRequest serviceRequest);

    @Query("""
            SELECT q FROM Quote q
            WHERE q.expertProfile.member.id = :memberId
              AND (q.quoteStatus = :requested OR q.sentAt > :boundary)
            ORDER BY q.sentAt DESC
            """)
    List<Quote> findValidQuotesByExpertMemberId(Long memberId, QuoteStatus requested, LocalDateTime boundary);

    @Query("""
            SELECT q FROM Quote q
            WHERE q.serviceRequest.id = :requestId
              AND (q.quoteStatus = :requested OR q.sentAt > :boundary)
            ORDER BY q.sentAt DESC
            """)
    List<Quote> findValidQuotesByRequestId(Long requestId, QuoteStatus requested, LocalDateTime boundary);

    @Query("""
            SELECT q.id
            FROM Quote q
            WHERE q.serviceRequest.id = :requestId
              AND q.quoteStatus = :status
            """)
    List<Long> findIdsByServiceRequestIdAndQuoteStatus(Long requestId, QuoteStatus status);

    @Query("""
            SELECT q.id
            FROM Quote q
            WHERE q.quoteStatus = :status
              AND q.sentAt <= :boundary
            """)
    List<Long> findExpiredQuoteIds(QuoteStatus status, LocalDateTime boundary);

    @Modifying
    @Query("""
            DELETE FROM Quote q
            WHERE q.id IN :ids
              AND q.quoteStatus = :status
            """)
    int deleteByIdsAndStatus(List<Long> ids, QuoteStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT q FROM Quote q WHERE q.id = :quoteId")
    Optional<Quote> findByIdForUpdate(Long quoteId);

    List<Quote> findByExpertProfileIdAndServiceRequestIdIn(Long expertProfileId, List<Long> serviceRequestIds);
}
