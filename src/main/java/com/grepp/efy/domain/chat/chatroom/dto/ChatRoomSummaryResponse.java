package com.grepp.efy.domain.chat.chatroom.dto;

import java.time.LocalDateTime;

public record ChatRoomSummaryResponse(
        Long roomId,
        Long quoteId,
        Partner partner,
        String lastMessagePreview,
        LocalDateTime lastMessageAt,
        long unreadCount,
        LocalDateTime createdAt
) {
    public record Partner(Long memberId, String nickname, String profileImageUrl){ }
}
