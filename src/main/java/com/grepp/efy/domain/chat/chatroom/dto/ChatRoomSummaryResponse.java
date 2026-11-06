package com.grepp.efy.domain.chat.chatroom.dto;

import java.time.LocalDateTime;

public record ChatRoomSummaryResponse(
        Long roomId,
        Long quoteId,
        Partner partner,
        boolean partnerLeft, // true면 상대방이 나간 방 (메시지 전송 불가)
        String lastMessagePreview,
        LocalDateTime lastMessageAt,
        long unreadCount,
        LocalDateTime createdAt
) {
    public record Partner(Long memberId, String nickname, String profileImageUrl){ }
}
