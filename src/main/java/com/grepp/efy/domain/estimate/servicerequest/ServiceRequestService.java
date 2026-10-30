package com.grepp.efy.domain.estimate.servicerequest;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryService;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionService;
import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.question.Question;
import com.grepp.efy.domain.estimate.question.QuestionService;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswer;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswerMapper;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswerRepository;
import com.grepp.efy.domain.estimate.requestanswer.RequestAnswerService;
import com.grepp.efy.domain.estimate.requestanswer.dto.AnswerResponse;
import com.grepp.efy.domain.estimate.servicerequest.dto.*;
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

    public List<ServiceRequestListResponse> getMyRequests(Long memberId) {
        Member member = memberService.getMemberEntity(memberId);

        if (!member.getRole().equals(MemberRole.USER)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        List<ServiceRequest> requests = getRequestsList(member);

        return serviceRequestMapper.toRequestListResponse(requests);
    }

    public ServiceRequestResponse getRequest(Long memberId, Long requestId) {
        Member member = memberService.getMemberEntity(memberId);
        ServiceRequest request = getRequestEntity(requestId);
        List<AnswerResponse> requestAnswerResponse = requestAnswerService.getRequestAnswerEntity(requestId);

        if (!request.getMember().getId().equals(member.getId())) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (!member.getRole().equals(MemberRole.USER)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        return serviceRequestMapper.toRequestResponse(member, request, requestAnswerResponse);
    }

    @Transactional
    public ServiceRequestCreateResponse createRequest(Long memberId, ServiceRequestCreateRequest request) {
        Member member = memberService.getMemberEntity(memberId);
        if (!member.getRole().equals(MemberRole.USER)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

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
        Member member = memberService.getMemberEntity(memberId);
        ServiceRequest request = getRequestEntity(requestId);

        if (!member.getRole().equals(MemberRole.USER)) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (!member.getId().equals(request.getMember().getId())) {
            throw new BusinessException(FORBIDDEN_EXCEPTION);
        }

        if (!request.getRequestStatus().equals(RequestStatus.OPEN)) {
            throw new BusinessException(INVALID_REQUEST_STATUS_EXCEPTION);
        }

        request.setRequestStatus(RequestStatus.CLOSED);
    }

    private void validateDuplicateRequest(Long memberId, Long secondCategoryId) {
        LocalDateTime expirationBoundary = LocalDateTime.now().minusHours(24);

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
        return serviceRequestRepository.findByMemberIdOrderByCreatedAtDesc(member.getId());
    }
}
