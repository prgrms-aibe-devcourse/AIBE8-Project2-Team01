package com.grepp.efy.domain.chat.chatmessage.dto;

import com.grepp.efy.domain.chat.chatmessage.MessageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// 보낸 사람은 세션 사용자로 정하므로 받지 않는다
// TEXT는 content, IMAGE는 fileId(파일 업로드 API로 받은 값)가 필수 (ValidChatMessage)
@ValidChatMessage
public record ChatMessageRequest(
        @NotNull MessageType type,
        @Size(max = 500) String content,
        @Positive Long fileId
) {
}
