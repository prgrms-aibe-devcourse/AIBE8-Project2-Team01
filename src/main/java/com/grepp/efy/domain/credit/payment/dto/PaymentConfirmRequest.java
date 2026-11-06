package com.grepp.efy.domain.credit.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentConfirmRequest {

    @NotBlank(message = "paymentKey를 입력해 주세요.")
    private String paymentKey;

    @NotBlank(message = "orderId를 입력해 주세요.")
    private String orderId;

    @NotNull(message = "결제 금액을 입력해 주세요.")
    @Positive(message = "결제 금액은 양수여야 합니다.")
    private Long amount;
}