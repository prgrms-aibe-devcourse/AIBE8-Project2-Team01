package com.grepp.efy.domain.chat.chatmessage;

import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageRequest;
import com.grepp.efy.domain.chat.chatmessage.dto.ChatMessageResponse;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.exception.ErrorResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

// STOMP 메시지 전송. 참여자 검사는 StompAuthInterceptor(SEND)에서 먼저 한다
@Controller
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final String brokerPrefix;

    public ChatMessageController(
            ChatMessageService chatMessageService,
            SimpMessagingTemplate messagingTemplate,
            @Value("${app.websocket.broker-prefix}") String brokerPrefix
    ) {
        this.chatMessageService = chatMessageService;
        this.messagingTemplate = messagingTemplate;
        this.brokerPrefix = brokerPrefix;
    }

    // SEND /pub/chat-rooms/{roomId}/messages → 저장 후 /sub/chat-rooms/{roomId} 구독자에게 전달
    @MessageMapping("/chat-rooms/{roomId}/messages")
    public void send(@DestinationVariable Long roomId, @Valid @Payload ChatMessageRequest request, Principal principal) {
        ChatMessageResponse response = chatMessageService.send(roomId, memberIdOf(principal), request);
        messagingTemplate.convertAndSend(brokerPrefix + "/chat-rooms/" + roomId, response);
    }

    // 입력값 오류는 저장하지 않고 보낸 사람(이 세션)에게만 알린다. 연결은 유지
    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public ErrorResponse handleInvalidInput(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        if (e.getBindingResult() != null) {
            for (FieldError error : e.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
        }
        return new ErrorResponse(ErrorCode.INVALID_INPUT_EXCEPTION.getMessage(), errors);
    }

    // JSON 형식이 아니거나 type 값이 잘못된 경우
    @MessageExceptionHandler(MessageConversionException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public ErrorResponse handleUnreadable() {
        return new ErrorResponse(ErrorCode.INVALID_INPUT_EXCEPTION.getMessage());
    }

    @MessageExceptionHandler(BusinessException.class)
    @SendToUser(destinations = "/queue/errors", broadcast = false)
    public ErrorResponse handleBusiness(BusinessException e) {
        return new ErrorResponse(e.getErrorCode().getMessage());
    }

    private static Long memberIdOf(Principal principal) {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        return ((CustomUserDetails) authentication.getPrincipal()).getMemberId();
    }
}
