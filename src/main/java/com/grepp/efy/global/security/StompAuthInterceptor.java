package com.grepp.efy.global.security;

import com.grepp.efy.domain.chat.chatroom.ChatRoomService;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 브라우저 WebSocket은 핸드셰이크에 헤더를 넣을 수 없어서, STOMP CONNECT 프레임의 Authorization 헤더로 인증한다
// 토큰 검증 규칙은 JwtAuthenticationFilter와 같다 (서명·만료, 회원 존재, tokenVersion)
// SUBSCRIBE는 채팅방 참여자만 /sub/chat-rooms/{roomId}를 구독할 수 있다 (본인 에러 큐 /user/queue/errors는 허용)
// SEND는 채팅방 참여자만 /pub/chat-rooms/{roomId}/messages로 보낼 수 있다
@Component
@NullMarked
public class StompAuthInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";
    // 입력값 오류를 보낸 사람에게만 돌려주는 큐 (ChatMessageController의 @SendToUser)
    private static final String USER_ERROR_DESTINATION = "/user/queue/errors";

    private final JwtProvider jwtProvider;
    private final MemberService memberService;
    private final ChatRoomService chatRoomService;
    private final Pattern chatRoomDestination;
    private final Pattern chatMessageDestination;

    public StompAuthInterceptor(
            JwtProvider jwtProvider,
            MemberService memberService,
            ChatRoomService chatRoomService,
            @Value("${app.websocket.broker-prefix}") String brokerPrefix,
            @Value("${app.websocket.application-prefix}") String applicationPrefix
    ) {
        this.jwtProvider = jwtProvider;
        this.memberService = memberService;
        this.chatRoomService = chatRoomService;
        this.chatRoomDestination = Pattern.compile("^" + Pattern.quote(brokerPrefix) + "/chat-rooms/(\\d+)$");
        this.chatMessageDestination = Pattern.compile("^" + Pattern.quote(applicationPrefix) + "/chat-rooms/(\\d+)/messages$");
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            connect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            subscribe(accessor);
        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            send(accessor);
        }
        return message;
    }

    private void connect(StompHeaderAccessor accessor) {
        String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));
        UsernamePasswordAuthenticationToken authentication = token == null ? null : authenticate(token);

        if (authentication == null) {
            // 예외를 던지면 Spring이 ERROR 프레임을 보내고 연결을 끊는다
            throw new MessageDeliveryException("인증에 실패했습니다.");
        }

        // 세션 사용자로 저장하면 이후 SUBSCRIBE·SEND에서 Principal로 꺼낼 수 있다
        accessor.setUser(authentication);
    }

    // 형식이 다른 경로, 참여하지 않은(없는 방 포함)·나간 채팅방은 거절한다
    private void subscribe(StompHeaderAccessor accessor) {
        if (USER_ERROR_DESTINATION.equals(accessor.getDestination())) {
            return;
        }
        checkParticipant(accessor, chatRoomDestination, "구독할 수 없는 경로입니다.");
    }

    private void send(StompHeaderAccessor accessor) {
        checkParticipant(accessor, chatMessageDestination, "보낼 수 없는 경로입니다.");
    }

    // 경로에서 roomId를 꺼내 세션 사용자가 그 채팅방에 참여 중인지 확인
    private void checkParticipant(StompHeaderAccessor accessor, Pattern pattern, String invalidPathMessage) {
        String destination = accessor.getDestination();
        Matcher matcher = destination == null ? null : pattern.matcher(destination);

        if (matcher == null || !matcher.matches()) {
            throw new MessageDeliveryException(invalidPathMessage);
        }

        Long memberId = memberIdOf(accessor.getUser());
        Long roomId = parseRoomId(matcher.group(1));

        if (memberId == null || roomId == null || !chatRoomService.isActiveParticipant(roomId, memberId)) {
            throw new MessageDeliveryException("채팅방 참여자가 아닙니다.");
        }
    }

    private static @Nullable Long memberIdOf(@Nullable Principal user) {
        if (user instanceof UsernamePasswordAuthenticationToken authentication
                && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getMemberId();
        }
        return null;
    }

    // Long 범위를 넘는 숫자는 없는 방으로 본다
    private static @Nullable Long parseRoomId(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static @Nullable String resolveToken(@Nullable String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 유효하지 않으면 null
    private @Nullable UsernamePasswordAuthenticationToken authenticate(String token) {
        try {
            Claims claims = jwtProvider.parseToken(token);

            Long memberId = Long.valueOf(claims.getSubject());
            Member member = memberService.getMemberEntity(memberId);

            Long tokenVersion = claims.get("tokenVersion", Long.class);

            if (member == null || !Objects.equals(tokenVersion, member.getTokenVersion())) {
                return null;
            }

            CustomUserDetails userDetails = new CustomUserDetails(member);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException | BusinessException e) {
            return null;
        }
    }
}
