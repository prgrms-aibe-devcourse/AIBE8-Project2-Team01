package com.grepp.efy.domain.chat.chatmessage;

import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageListResponse;
import com.grepp.efy.domain.chat.docs.ChatMessageControllerDocs;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// 메시지 REST API. STOMP 전송은 ChatMessageController
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatMessageRestController implements ChatMessageControllerDocs {

    private final ChatMessageService chatMessageService;

    @Override
    @GetMapping("/chat-rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageListResponse> getMessages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @Positive Long roomId,
            @RequestParam(required = false) @Positive Long cursor
    ) {
        ChatMessageListResponse response = chatMessageService.getMessages(roomId, userDetails.getMemberId(), cursor);
        return ResponseEntity.ok(response);
    }
}
