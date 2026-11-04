package com.grepp.efy.domain.chat.chatroom;

import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomSummaryResponse;
import com.grepp.efy.domain.chat.docs.ChatRoomControllerDocs;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateRequest;
import com.grepp.efy.domain.chat.chatroom.dto.ChatRoomCreateResponse;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping("/chat-rooms")
    public ResponseEntity<PageResponse<ChatRoomSummaryResponse>> getMyRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber
    ) {
        PageResponse<ChatRoomSummaryResponse> response = chatRoomService.getMyRooms(userDetails.getMemberId(), pageNumber);
        return ResponseEntity.ok(response);
    }

}
