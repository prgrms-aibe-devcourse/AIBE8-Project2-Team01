package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.credit.reservation.CreditReservationService;
import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.quote.dto.*;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequestService;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import com.grepp.efy.domain.user.expertprofile.ExpertProfileService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.grepp.efy.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService {

    private static final List<String> ADMIN_SORTABLE_FIELDS = List.of("id", "sentAt", "price", "quoteStatus");
    private final QuoteRepository quoteRepository;
    private final MemberService memberService;
    private final ServiceRequestService serviceRequestService;
    private final ExpertProfileService expertProfileService;
    private final CreditReservationService creditReservationService;
    private final QuoteMapper quoteMapper;

    public PageResponse<AdminQuoteResponse> getAdminQuotes(
            QuoteStatus status, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageable = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, ADMIN_SORTABLE_FIELDS);

        Page<Quote> page = (status == null)
                ? quoteRepository.findAllBy(pageable)
                : quoteRepository.findByQuoteStatus(status, pageable);

        return PageResponse.of(page, quoteMapper.toAdminQuoteResponseList(page.getContent()));
    }
    
    public List<QuoteResponse> getQuotes(Long memberId) {
        validateExpertAccess(memberId);

        List<Quote> quoteList = quoteRepository
                .findValidQuotesByExpertMemberId(memberId, QuoteStatus.REQUESTED, validBoundary());

        return quoteMapper.toResponseList(quoteList);
    }

    public QuoteResponse getQuote(Long memberId, Long quoteId) {
        Member member = memberService.getMemberEntity(memberId);
        Quote quote = getValidQuoteEntity(quoteId);

        validateQuoteReadAccess(member, quote);

        return quoteMapper.toResponse(quote);
    }

    public List<QuoteRequestResponse> getRequestQuotes(Long memberId, Long requestId) {
        Member member = memberService.getMemberEntity(memberId);
        ServiceRequest serviceRequest = serviceRequestService.getRequestEntity(requestId);

        boolean isAdmin = member.getRole().equals(MemberRole.ADMIN);
        if (!isAdmin && !serviceRequest.getMember().getId().equals(memberId)) {
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

        if (!serviceRequest.getCreatedAt().isAfter(LocalDateTime.now().minusHours(ServiceRequest.VALID_HOURS))) {
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

    @Transactional
    public int deleteExpiredQuotes() {
        List<Long> expiredIds = quoteRepository.findExpiredQuoteIds(QuoteStatus.SENT, validBoundary());

        if (expiredIds.isEmpty()) {
            return 0;
        }

        creditReservationService.releaseReservations(expiredIds);
        return quoteRepository.deleteByIdsAndStatus(expiredIds, QuoteStatus.SENT);
    }

    @Transactional
    public Quote requestQuote(Long customerId, Long quoteId) {
        Quote quote = quoteRepository.findByIdForUpdate(quoteId)
                .orElseThrow(() -> new BusinessException(QUOTE_NOT_FOUND_EXCEPTION));
        ServiceRequest serviceRequest = quote.getServiceRequest();

        if (!serviceRequest.getMember().getId().equals(customerId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (quote.getQuoteStatus().equals(QuoteStatus.REQUESTED)) {
            throw new BusinessException(ALREADY_REQUESTED_QUOTE_EXCEPTION);
        }

        if (!quote.getSentAt().isAfter(validBoundary())) {
            throw new BusinessException(QUOTE_NOT_FOUND_EXCEPTION);
        }

        if (serviceRequest.getRequestStatus().equals(RequestStatus.CLOSED)) {
            throw new BusinessException(CLOSED_REQUEST_QUOTE_REQUEST_NOT_ALLOWED_EXCEPTION);
        }

        quote.setQuoteStatus(QuoteStatus.REQUESTED);

        if (serviceRequest.getRequestStatus().equals(RequestStatus.OPEN)) {
            serviceRequest.setRequestStatus(RequestStatus.MATCHED);
        }

        return quote;
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

    private void validateQuoteReadAccess(Member member, Quote quote) {
        if (member.getRole().equals(MemberRole.ADMIN)) {
            return;
        }
        boolean isSender = quote.getExpertProfile().getMember().getId().equals(member.getId());
        boolean isRequestOwner = quote.getServiceRequest().getMember().getId().equals(member.getId());

        if (!isSender && !isRequestOwner) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }
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
