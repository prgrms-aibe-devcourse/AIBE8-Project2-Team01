package com.grepp.efy.domain.estimate.question;

import com.grepp.efy.domain.estimate.question.dto.QuestionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    List<QuestionResponse> toResponseList(List<Question> questions);
}
