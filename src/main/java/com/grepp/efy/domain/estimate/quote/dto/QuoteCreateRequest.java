package com.grepp.efy.domain.estimate.quote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class QuoteCreateRequest {

    @NotBlank(message = "견적 설명을 입력해주세요.")
    private String description;

    @NotNull(message = "견적 가격을 입력해주세요.")
    @Positive(message = "견적 금액은 0보다 커야 합니다.")
    private Long price;
}
