package com.grepp.efy.domain.chat.chatroom;

import com.grepp.efy.domain.chat.docs.ChatRoomControllerDocs;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateRequest;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController implements ChatRoomControllerDocs {

    private final ChatRoomService chatRoomService;

    @Override
    @PostMapping("/chat-rooms")
    public ResponseEntity<ChatRoomCreateResponse> createChatRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChatRoomCreateRequest chatRoomCreateRequest) {
        ChatRoomCreateResponse response = chatRoomService.createChatRoom(userDetails.getMemberId(), chatRoomCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
