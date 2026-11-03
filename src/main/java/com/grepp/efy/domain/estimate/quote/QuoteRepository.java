package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

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
}
