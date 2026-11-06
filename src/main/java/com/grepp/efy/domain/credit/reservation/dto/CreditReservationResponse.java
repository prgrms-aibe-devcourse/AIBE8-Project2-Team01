package com.grepp.efy.domain.credit.reservation.dto;

import java.time.LocalDateTime;

public record CreditReservationResponse(
        Long quoteId,
        long amount,
        LocalDateTime reservedAt
) {
}