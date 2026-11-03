package com.grepp.efy.domain.chat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChatRoomCreateRequest(
        @NotNull @Positive Long quoteId
) {
}
