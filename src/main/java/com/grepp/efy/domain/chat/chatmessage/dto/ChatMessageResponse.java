package com.grepp.efy.domain.chat.chatmessage.dto;

import com.grepp.efy.domain.chat.chatmessage.MessageType;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long messageId,
        Long roomId,
        Long senderId,
        String senderNickname,
        MessageType type,
        String content,
        LocalDateTime createdAt
) {
}
