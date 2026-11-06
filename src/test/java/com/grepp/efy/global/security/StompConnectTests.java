package com.grepp.efy.global.security;

import com.grepp.efy.domain.user.member.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// STOMP 연결(/ws)과 CONNECT 인증 테스트. 실제 서버를 띄워 WebSocket으로 접속한다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("NonAsciiCharacters")
class StompConnectTests {

    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired StompAuthInterceptor stompAuthInterceptor;
    @Value("${jwt.secret}") String jwtSecret;
    @Value("${app.frontend-url}") String frontendUrl;

    private final List<Long> memberIds = new ArrayList<>();
    private final List<StompSession> sessions = new ArrayList<>();
    private WebSocketStompClient stompClient;
    private Long memberId;

    @BeforeEach
    void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        memberId = member();
    }

    @AfterEach
    void cleanUp() {
        sessions.stream().filter(StompSession::isConnected).forEach(StompSession::disconnect);
        stompClient.stop();
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    // ── 실제 연결 ──

    @Test
    void 유효한_토큰이면_연결된다() throws Exception {
        StompSession session = connect("Bearer " + token(memberId)).get(5, TimeUnit.SECONDS);

        assertTrue(session.isConnected());
    }

    @Test
    void Authorization_헤더가_없으면_연결이_거절된다() {
        assertRejected(connect(null));
    }

    @Test
    void Bearer_형식이_아니면_연결이_거절된다() {
        assertRejected(connect(token(memberId)));
    }

    @Test
    void 서명이_다른_토큰이면_연결이_거절된다() {
        JwtProvider otherKeyProvider = new JwtProvider("another-secret-key-for-test-at-least-32-bytes", 3_600_000);
        String forged = otherKeyProvider.createToken(userDetails(memberId));

        assertRejected(connect("Bearer " + forged));
    }

    @Test
    void 만료된_토큰이면_연결이_거절된다() {
        JwtProvider expiredProvider = new JwtProvider(jwtSecret, -1_000);
        String expired = expiredProvider.createToken(userDetails(memberId));

        assertRejected(connect("Bearer " + expired));
    }

    @Test
    void 로그아웃_등으로_tokenVersion이_바뀐_토큰이면_연결이_거절된다() {
        String oldToken = token(memberId);
        jdbcTemplate.update("update member set token_version = token_version + 1 where id = ?", memberId);

        assertRejected(connect("Bearer " + oldToken));
    }

    // ── 인터셉터: 세션 사용자 저장 ──

    @Test
    void CONNECT가_성공하면_세션_사용자로_회원_정보가_저장된다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setNativeHeader("Authorization", "Bearer " + token(memberId));
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        stompAuthInterceptor.preSend(message, (msg, timeout) -> true);

        StompHeaderAccessor result = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        UsernamePasswordAuthenticationToken authentication =
                assertInstanceOf(UsernamePasswordAuthenticationToken.class, result.getUser());
        CustomUserDetails userDetails = assertInstanceOf(CustomUserDetails.class, authentication.getPrincipal());
        assertEquals(memberId, userDetails.getMemberId());
    }

    @Test
    void CONNECT_SUBSCRIBE_SEND가_아닌_프레임은_검사_없이_통과한다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
        accessor.setSubscriptionId("sub-0");
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertSame(message, stompAuthInterceptor.preSend(message, (msg, timeout) -> true));
        assertNull(MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class).getUser());
    }

    @Test
    void CONNECT_인증에_실패하면_예외를_던진다() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setLeaveMutable(true);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());

        assertThrows(MessageDeliveryException.class, () -> stompAuthInterceptor.preSend(message, (msg, timeout) -> true));
    }

    // ── 헬퍼 ──

    private CompletableFuture<StompSession> connect(String authorization) {
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.setOrigin(frontendUrl);
        StompHeaders connectHeaders = new StompHeaders();
        if (authorization != null) {
            connectHeaders.add("Authorization", authorization);
        }
        CompletableFuture<StompSession> future = stompClient.connectAsync(
                "ws://localhost:" + port + "/ws", handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {
                });
        future.thenAccept(sessions::add);
        return future;
    }

    // 서버가 ERROR 프레임을 보내고 연결을 끊으므로 세션이 만들어지지 않는다
    private void assertRejected(CompletableFuture<StompSession> future) {
        assertThrows(ExecutionException.class, () -> future.get(5, TimeUnit.SECONDS));
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(userDetails(memberId));
    }

    private CustomUserDetails userDetails(Long memberId) {
        return new CustomUserDetails(memberService.getMemberEntity(memberId));
    }

    private Long member() {
        String suffix = UUID.randomUUID().toString();
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String email = suffix + "@test.local";
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, 'USER', CURRENT_TIMESTAMP, 0)",
                email, suffix.substring(0, 20), String.format("%011d", nextId));
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }
}
