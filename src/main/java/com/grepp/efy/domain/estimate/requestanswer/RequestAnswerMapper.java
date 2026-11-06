package com.grepp.efy.domain.estimate.requestanswer;

import com.grepp.efy.domain.estimate.question.Question;
import com.grepp.efy.domain.estimate.requestanswer.dto.AnswerResponse;
import com.grepp.efy.domain.estimate.servicerequest.ServiceRequest;
import com.grepp.efy.domain.estimate.servicerequest.dto.AnswerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestAnswerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "question", source = "question")
    @Mapping(target = "serviceRequest", source = "serviceRequest")
    @Mapping(target = "questionContent", source = "question.content")
    @Mapping(target = "content", source = "answerRequest.content")
    RequestAnswer createEntity(AnswerRequest answerRequest, Question question, ServiceRequest serviceRequest);

    @Mapping(target = "questionId", source = "question.id")
    AnswerResponse toAnswerResponse(RequestAnswer requestAnswer);

    List<AnswerResponse> toAnswerResponseList(List<RequestAnswer> requestAnswers);
}
