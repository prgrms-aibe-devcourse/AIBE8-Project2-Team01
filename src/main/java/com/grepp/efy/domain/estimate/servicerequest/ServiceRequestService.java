package com.grepp.efy.domain.estimate.servicerequest;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryService;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionService;
import com.grepp.efy.domain.credit.reservation.CreditReservationService;
import com.grepp.efy.domain.estimate.enums.QuoteStatus;
import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.question.Question;
import com.grepp.efy.domain.estimate.question.QuestionService;
import com.grepp.efy.domain.estimate.quote.QuoteRepository;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswer;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswerMapper;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswerRepository;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswerService;
import com.grepp.efy.domain.estimate.requestanswer.dto.AnswerResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.*;
import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import com.grepp.efy.domain.user.expertprofile.ExpertProfileService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRole;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.grepp.efy.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final RequestAnswerRepository requestAnswerRepository;
    private final RequestAnswerService requestAnswerService;
    private final MemberService memberService;
    private final SecondCategoryService categoryService;
    private final SecondRegionService regionService;
    private final QuestionService questionService;
    private final ServiceRequestMapper serviceRequestMapper;
    private final RequestAnswerMapper requestAnswerMapper;
    private final ExpertProfileService expertProfileService;
    private final QuoteRepository quoteRepository;
    private final CreditReservationService creditReservationService;

    public List<ServiceRequestListResponse> getMatchedRequests(Long memberId) {
        ExpertProfile expertProfile = validateExpertAccess(memberId);
        List<Long> categoryIds = expertProfile.getServices().stream().map(SecondCategory::getId).toList();
        List<Long> regionIds = expertProfile.getRegions().stream().map(SecondRegion::getId).toList();

        List<ServiceRequest> requests = serviceRequestRepository
                .findMatchedRequests(categoryIds, regionIds, expertProfile.isNationwide(),
                        RequestStatus.CLOSED, memberId,
                        LocalDateTime.now().minusHours(ServiceRequest.VALID_HOURS));

        return serviceRequestMapper.toRequestListResponse(requests);
    }

    public List<ServiceRequestListResponse> getMyRequests(Long memberId) {
        Member member = validateUserAccess(memberId);
        List<ServiceRequest> requests = getRequestsList(member);

        return serviceRequestMapper.toRequestListResponse(requests);
    }

    public ServiceRequestResponse getRequest(Long memberId, Long requestId) {
        Member member = memberService.getMemberEntity(memberId);
        ServiceRequest request = getRequestEntity(requestId);
        List<AnswerResponse> requestAnswerResponse = requestAnswerService.getRequestAnswerEntity(requestId);

        if (member.getRole().equals(MemberRole.USER)) {
            if (!request.getMember().getId().equals(member.getId())) {
                throw new BusinessException(FORBIDDEN_EXCEPTION);
            }
        } else if (member.getRole().equals(MemberRole.EXPERT)) {
            ExpertProfile expertProfile = expertProfileService.getExpertProfileByMemberId(memberId);

            if (!isMatchedExpert(expertProfile, request)) {
                throw new BusinessException(FORBIDDEN_EXCEPTION);
            }
        } else if (!member.getRole().equals(MemberRole.ADMIN)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (request.getRequestStatus().equals(RequestStatus.CLOSED)) {
            if (!member.getRole().equals(MemberRole.ADMIN)) {
                throw new BusinessException(REQUEST_NOT_FOUND_EXCEPTION);
            }
        }

        return serviceRequestMapper.toRequestResponse(member, request, requestAnswerResponse);
    }

    @Transactional
    public ServiceRequestCreateResponse createRequest(Long memberId, ServiceRequestCreateRequest request) {
        Member member = validateUserAccess(memberId);
        SecondCategory category = categoryService.getSecondCategoryEntity(request.getSecondCategoryId());
        SecondRegion region = regionService.getSecondRegionEntity(request.getSecondRegionId());

        validateDuplicateRequest(memberId, request.getSecondCategoryId());

        List<Question> questions = questionService.getQuestionEntities();

        validateDuplicateAnswers(request);
        validateRequiredAnswers(request, questions);

        ServiceRequest serviceRequest = serviceRequestMapper.createEntity(member, category, region);

        List<RequestAnswer> answers = request.getAnswers().stream()
                .map(answerRequest -> {
                    Question question = findQuestion(answerRequest.getQuestionId(), questions);
                    return requestAnswerMapper.createEntity(answerRequest, question, serviceRequest);
                }).toList();

        serviceRequestRepository.save(serviceRequest);
        requestAnswerRepository.saveAll(answers);
        return serviceRequestMapper.toServiceRequestCreateResponse(serviceRequest);
    }

    @Transactional
    public void cancelRequest(Long memberId, Long requestId) {
        ServiceRequest request = getRequestEntity(requestId);
        Member member = validateUserAccess(memberId);

        if (!member.getId().equals(request.getMember().getId())) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (!request.getRequestStatus().equals(RequestStatus.OPEN)) {
            throw new BusinessException(INVALID_REQUEST_STATUS_EXCEPTION);
        }

        request.setRequestStatus(RequestStatus.CLOSED);

        List<Long> sentQuoteIds = quoteRepository.findIdsByServiceRequestIdAndQuoteStatus(requestId, QuoteStatus.SENT);
        if (!sentQuoteIds.isEmpty()) {
            creditReservationService.releaseReservations(sentQuoteIds);
            quoteRepository.deleteAllByIdInBatch(sentQuoteIds);
        }
    }

    private void validateDuplicateRequest(Long memberId, Long secondCategoryId) {
        LocalDateTime expirationBoundary = LocalDateTime.now().minusHours(ServiceRequest.VALID_HOURS);

        boolean exists = serviceRequestRepository.existsByMemberIdAndSecondCategoryIdAndRequestStatusAndCreatedAtAfter(
                memberId, secondCategoryId, RequestStatus.OPEN, expirationBoundary);

        if (exists) throw new BusinessException(DUPLICATE_SERVICE_REQUEST_EXCEPTION);
    }

    private void validateDuplicateAnswers(ServiceRequestCreateRequest request) {
        Set<Long> answeredIds = request.getAnswers().stream()
                .map(AnswerRequest::getQuestionId)
                .collect(Collectors.toSet());

        if (answeredIds.size() != request.getAnswers().size())
            throw new BusinessException(DUPLICATE_ANSWER_EXCEPTION);
    }

    private void validateRequiredAnswers(ServiceRequestCreateRequest request, List<Question> questions) {
        Set<Long> requiredQuestionId = questions.stream()
                .filter(Question::getIsRequired)
                .map(Question::getId)
                .collect(Collectors.toSet());

        Set<Long> answeredId = request.getAnswers().stream()
                .map(AnswerRequest::getQuestionId)
                .collect(Collectors.toSet());

        if (!answeredId.containsAll(requiredQuestionId)) {
            throw new BusinessException(REQUIRED_ANSWER_MISSING_EXCEPTION);
        }
    }

    private Question findQuestion(Long questionId, List<Question> questions) {
        return questions.stream()
                .filter(question -> question.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(QUESTION_NOT_FOUND_EXCEPTION));
    }

    public ServiceRequest getRequestEntity(Long requestId) {
        return serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(REQUEST_NOT_FOUND_EXCEPTION));
    }

    private List<ServiceRequest> getRequestsList(Member member) {
        return serviceRequestRepository
                .findByMemberIdAndRequestStatusNotOrderByCreatedAtDesc(member.getId(), RequestStatus.CLOSED);
    }

    public boolean isMatchedExpert(ExpertProfile expertProfile, ServiceRequest request) {
        Long categoryId = request.getSecondCategory().getId();
        Long regionId = request.getSecondRegion().getId();

        boolean categoryMatched = expertProfile.getServices().stream()
                .anyMatch(category -> category.getId().equals(categoryId));

        boolean regionMatched = expertProfile.isNationwide()
                || expertProfile.getRegions().stream()
                .anyMatch(region -> region.getId().equals(regionId));

        return categoryMatched && regionMatched;
    }

    private Member validateUserAccess(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);

        if (!member.getRole().equals(MemberRole.USER)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        return member;
    }

    private ExpertProfile validateExpertAccess(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);

        if (!member.getRole().equals(MemberRole.EXPERT)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        return expertProfileService.getExpertProfileByMemberId(memberId);
    }
}
