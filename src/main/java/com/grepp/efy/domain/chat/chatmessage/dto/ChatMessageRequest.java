package com.grepp.efy.domain.chat.chatmessage.dto;

import com.grepp.efy.domain.chat.chatmessage.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// 보낸 사람은 세션 사용자로 정하므로 받지 않는다
public record ChatMessageRequest(
        @NotNull MessageType type,
        @NotBlank @Size(max = 500) String content
) {
}
