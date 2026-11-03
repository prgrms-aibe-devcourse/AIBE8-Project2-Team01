package com.grepp.efy.domain.credit.payment.dto;

public record PaymentCreateResponse(
        Long paymentId,
        String orderId, // 토스 결제창 orderId
        String orderName, // 토스 결제창 orderName
        long amount // 토스 결제창 amount
) {
}
