package com.grepp.efy.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomCreateResponse(Long roomId, Long quoteId, LocalDateTime createdAt) {
}
