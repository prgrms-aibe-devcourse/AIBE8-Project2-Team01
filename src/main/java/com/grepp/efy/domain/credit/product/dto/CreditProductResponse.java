package com.grepp.efy.domain.credit.product.dto;

public record CreditProductResponse(
        Long id,
        String name,
        long price,
        long creditAmount
) {
}
