package com.grepp.efy.domain.credit.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCreateRequest {

    @NotNull(message = "충전 상품을 선택해 주세요.")
    @Positive(message = "충전 상품 ID는 양수여야 합니다.")
    private Long creditProductId;
}
