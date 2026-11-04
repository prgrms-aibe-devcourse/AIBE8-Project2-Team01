package com.grepp.efy.domain.chat.chatmessage.dto;

import java.util.List;

// nextCursor는 다음 요청의 cursor 값(이번 목록에서 가장 오래된 messageId), 더 없으면 null
public record ChatMessageListResponse(
        List<ChatMessageResponse> messages,
        Long nextCursor,
        boolean hasNext
) {
}
