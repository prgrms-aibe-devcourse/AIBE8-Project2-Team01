package com.grepp.efy.domain.credit.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentFailRequest {

    @NotBlank(message = "실패 코드를 입력해주세요.")
    private String code; // 토스 실패 리다이렉트의 code

    private String message; // 토스 실패 리다이렉트의 message
}
