package com.grepp.efy.global.security;

import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;

// 브라우저 WebSocket은 핸드셰이크에 헤더를 넣을 수 없어서, STOMP CONNECT 프레임의 Authorization 헤더로 인증한다
// 토큰 검증 규칙은 JwtAuthenticationFilter와 같다 (서명·만료, 회원 존재, tokenVersion)
@Component
@RequiredArgsConstructor
@NullMarked
public class StompAuthInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));
        UsernamePasswordAuthenticationToken authentication = token == null ? null : authenticate(token);

        if (authentication == null) {
            // 예외를 던지면 Spring이 ERROR 프레임을 보내고 연결을 끊는다
            throw new MessageDeliveryException("인증에 실패했습니다.");
        }

        // 세션 사용자로 저장하면 이후 SUBSCRIBE·SEND에서 Principal로 꺼낼 수 있다
        accessor.setUser(authentication);
        return message;
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
