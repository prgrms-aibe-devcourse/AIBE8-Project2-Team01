package com.grepp.efy.domain.estimate.servicerequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AnswerRequest {

    @NotNull(message = "질문 ID를 입력해주세요.")
    @Positive(message = "질문 ID는 1 이상이어야 합니다.")
    private Long questionId;

    @NotBlank(message = "답변 내용을 입력해주세요.")
    @Size(max = 500, message = "답변 내용은 500자 이하로 입력해주세요.")
    private String content;
}
