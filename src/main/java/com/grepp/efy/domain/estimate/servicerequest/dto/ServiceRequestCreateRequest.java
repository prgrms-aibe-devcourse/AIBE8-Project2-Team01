package com.grepp.efy.domain.estimate.servicerequest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
public class ServiceRequestCreateRequest {

    @NotNull(message = "서비스 카테고리를 선택해주세요.")
    @Positive(message = "서비스 카테고리 ID는 1 이상이어야 합니다.")
    private Long secondCategoryId;

    @NotNull(message = "서비스 지역을 선택해주세요.")
    @Positive(message = "서비스 지역 ID는 1 이상이어야 합니다.")
    private Long secondRegionId;

    @NotEmpty(message = "질문 답변을 하나 이상 입력해주세요.")
    @Size(max = 20, message = "답변은 최대 20개까지 입력할 수 있습니다.")
    @Valid
    private List<AnswerRequest> answers;
}
