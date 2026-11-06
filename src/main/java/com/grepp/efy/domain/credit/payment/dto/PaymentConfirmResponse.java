package com.grepp.efy.domain.credit.payment.dto;

import com.grepp.efy.domain.credit.payment.PaymentStatus;

public record PaymentConfirmResponse(
        String orderId,
        PaymentStatus paymentStatus, // DONE 또는 PENDING
        long chargedCredit,          // 지급한 크레딧. PENDING이면 0
        long balance                 // 승인 후 보유 크레딧
) {
}