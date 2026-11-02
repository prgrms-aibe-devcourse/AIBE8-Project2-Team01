package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
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

import java.util.List;
import java.util.Objects;

import static com.grepp.efy.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final MemberService memberService;
    private final ServiceRequestService serviceRequestService;
    private final ExpertProfileService expertProfileService;
    private final QuoteMapper quoteMapper;

    public List<QuoteResponse> getQuotes(Long memberId) {
        validateExpertAccess(memberId);
        List<QuoteStatus> visibleStatuses = List.of(QuoteStatus.SENT, QuoteStatus.REQUESTED);

        List<Quote> quoteList = quoteRepository
                .findAllByExpertProfileMemberIdAndQuoteStatusInOrderBySentAtDesc(memberId, visibleStatuses);
        return quoteMapper.toResponseList(quoteList);
    }

    public QuoteResponse getQuote(Long memberId, Long quoteId) {
        validateExpertAccess(memberId);
        Quote quote = getQuoteEntity(quoteId);
        Long quoteOwnerId = quote.getExpertProfile().getMember().getId();

        if (!quoteOwnerId.equals(memberId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        return quoteMapper.toResponse(quote);
    }

    @Transactional
    public QuoteCreateResponse createQuote(Long memberId, Long requestId, QuoteCreateRequest createRequest) {
        Member member = memberService.getMemberEntity(memberId);

        if (!member.getRole().equals(MemberRole.EXPERT)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        ExpertProfile expertProfile = expertProfileService.getExpertProfileByMemberId(memberId);
        ServiceRequest serviceRequest = serviceRequestService.getRequestEntity(requestId);

        if (serviceRequest.getMember().getId().equals(memberId)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (serviceRequest.getRequestStatus().equals(RequestStatus.CLOSED)) {
            throw new BusinessException(CLOSED_REQUEST_QUOTE_NOT_ALLOWED_EXCEPTION);
        }

        if (!canSendQuote(expertProfile, serviceRequest)) {
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

        return quoteMapper.toCreateResponse(quote);
    }

    private boolean canSendQuote(ExpertProfile expertProfile, ServiceRequest serviceRequest) {
        Long categoryId = serviceRequest.getSecondCategory().getId();
        Long regionId = serviceRequest.getSecondRegion().getId();

        boolean categoryMatched = expertProfile.getServices().stream()
                .anyMatch(category -> Objects.equals(category.getId(), categoryId));

        boolean regionMatched = expertProfile.isNationwide()
                || expertProfile.getRegions().stream()
                .anyMatch(region -> Objects.equals(region.getId(), regionId));

        return categoryMatched && regionMatched;
    }

    private void validateExpertAccess(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);

        if (!member.getRole().equals(MemberRole.EXPERT)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        expertProfileService.getExpertProfileByMemberId(memberId);
    }

    public Quote getQuoteEntity(Long quoteId) {
        return quoteRepository.findById(quoteId)
                .orElseThrow(() -> new BusinessException(QUOTE_NOT_FOUND_EXCEPTION));
    }
}
