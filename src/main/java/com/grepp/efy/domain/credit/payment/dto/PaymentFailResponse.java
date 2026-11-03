package com.grepp.efy.domain.credit.payment.dto;

import com.grepp.efy.domain.credit.payment.PaymentStatus;

public record PaymentFailResponse(
        String orderId,
        PaymentStatus paymentStatus // CANCELED 또는 FAILED
) {
}