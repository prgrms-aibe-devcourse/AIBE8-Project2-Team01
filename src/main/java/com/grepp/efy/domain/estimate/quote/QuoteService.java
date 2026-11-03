package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.credit.reservation.CreditReservationService;
import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteRequestResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteResponse;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequestService;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import com.grepp.efy.domain.user.expertprofile.ExpertProfileService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.grepp.efy.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final MemberService memberService;
    private final ServiceRequestService serviceRequestService;
    private final ExpertProfileService expertProfileService;
    private final CreditReservationService creditReservationService;
    private final QuoteMapper quoteMapper;

    public List<QuoteResponse> getQuotes(Long memberId) {
        validateExpertAccess(memberId);

        List<Quote> quoteList = quoteRepository
                .findValidQuotesByExpertMemberId(memberId, QuoteStatus.REQUESTED, validBoundary());

        return quoteMapper.toResponseList(quoteList);
    }

    public QuoteResponse getQuote(Long memberId, Long quoteId) {
        validateExpertAccess(memberId);
        Quote quote = getValidQuoteEntity(quoteId);
        Long quoteOwnerId = quote.getExpertProfile().getMember().getId();

        if (!quoteOwnerId.equals(memberId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        return quoteMapper.toResponse(quote);
    }

    public List<QuoteRequestResponse> getRequestQuotes(Long memberId, Long requestId) {
        ServiceRequest serviceRequest = serviceRequestService.getRequestEntity(requestId);

        if (!serviceRequest.getMember().getId().equals(memberId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        List<Quote> quotes = quoteRepository.findValidQuotesByRequestId(requestId, QuoteStatus.REQUESTED, validBoundary());

        return quoteMapper.toRequestResponseList(quotes);
    }

    @Transactional
    public QuoteCreateResponse createQuote(Long memberId, Long requestId, QuoteCreateRequest createRequest) {
        ExpertProfile expertProfile = validateExpertAccess(memberId);
        ServiceRequest serviceRequest = serviceRequestService.getRequestEntity(requestId);

        if (serviceRequest.getMember().getId().equals(memberId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (serviceRequest.getRequestStatus().equals(RequestStatus.CLOSED)) {
            throw new BusinessException(CLOSED_REQUEST_QUOTE_NOT_ALLOWED_EXCEPTION);
        }

        if (serviceRequest.getCreatedAt().plusHours(ServiceRequest.VALID_HOURS).isBefore(LocalDateTime.now())) {
            throw new BusinessException(EXPIRED_REQUEST_QUOTE_NOT_ALLOWED_EXCEPTION);
        }

        if (!serviceRequestService.isMatchedExpert(expertProfile, serviceRequest)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (quoteRepository.existsByExpertProfileAndServiceRequest(expertProfile, serviceRequest)) {
            throw new BusinessException(DUPLICATE_QUOTE_NOT_ALLOWED_EXCEPTION);
        }

        Quote quote = quoteMapper.toEntity(createRequest, expertProfile, serviceRequest);
        try {
            quoteRepository.saveAndFlush(quote);
        } catch (DataIntegrityViolationException e) {
            // uk_quote_expert_request 제약 위반은 중복 견적으로 처리
            throw new BusinessException(DUPLICATE_QUOTE_NOT_ALLOWED_EXCEPTION);
        }

        creditReservationService.reserveForQuote(memberId, quote.getId());

        return quoteMapper.toCreateResponse(quote);
    }

    @Transactional
    public void deleteQuote(Long memberId, Long quoteId) {
        validateExpertAccess(memberId);

        Quote quote = getValidQuoteEntity(quoteId);

        if (!quote.getExpertProfile().getMember().getId().equals(memberId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (quote.getQuoteStatus().equals(QuoteStatus.REQUESTED)) {
            throw new BusinessException(QUOTE_DELETE_NOT_ALLOWED_EXCEPTION);
        }

        creditReservationService.releaseReservation(quoteId);
        quoteRepository.delete(quote);
    }

    private ExpertProfile validateExpertAccess(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);

        if (!member.getRole().equals(MemberRole.EXPERT)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        return expertProfileService.getExpertProfileByMemberId(memberId);
    }

    private LocalDateTime validBoundary() {
        return LocalDateTime.now().minusHours(Quote.VALID_HOURS);
    }

    private Quote getValidQuoteEntity(Long quoteId) {
        Quote quote = getQuoteEntity(quoteId);

        if (quote.getQuoteStatus().equals(QuoteStatus.SENT) && !quote.getSentAt().isAfter(validBoundary())) {
            throw new BusinessException(QUOTE_NOT_FOUND_EXCEPTION);
        }

        return quote;
    }

    public Quote getQuoteEntity(Long quoteId) {
        return quoteRepository.findById(quoteId)
                .orElseThrow(() -> new BusinessException(QUOTE_NOT_FOUND_EXCEPTION));
    }
}
