package com.grepp.efy.domain.chat;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 채팅방 구독(SUBSCRIBE) 권한 테스트. 실제 서버를 띄워 WebSocket으로 접속한다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings("NonAsciiCharacters")
class ChatSubscribeTests {

    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired SimpMessagingTemplate messagingTemplate;
    @Value("${app.frontend-url}") String frontendUrl;

    private final List<StompSession> sessions = new ArrayList<>();
    private final List<Long> requestIds = new ArrayList<>();
    private WebSocketStompClient stompClient;

    private Long customerId;
    private Long expertMemberId;
    private Long otherCustomerId;
    private Long roomId;

    @BeforeEach
    void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        customerId = memberId("customer1@example.com");
        expertMemberId = memberId("expert1@example.com");
        otherCustomerId = memberId("customer2@example.com");
        roomId = room(customerId, expertMemberId);
    }

    // 서버를 띄운 테스트는 롤백되지 않으므로 만든 데이터를 직접 지운다
    @AfterEach
    void cleanUp() {
        sessions.stream().filter(StompSession::isConnected).forEach(StompSession::disconnect);
        stompClient.stop();
        for (Long requestId : requestIds) {
            String rooms = "(select cr.id from chat_room cr join quote q on cr.quote_id = q.id where q.service_request_id = ?)";
            jdbcTemplate.update("delete from chat_participant where chat_room_id in " + rooms, requestId);
            jdbcTemplate.update("delete from chat_room where id in " + rooms, requestId);
            jdbcTemplate.update("delete from quote where service_request_id = ?", requestId);
            jdbcTemplate.update("delete from service_request where id = ?", requestId);
        }
    }

    @Test
    void 참여자는_구독하고_채팅방_메시지를_받는다() throws Exception {
        StompSession session = connect(customerId);
        BlockingQueue<String> received = subscribe(session, chatRoomPath(roomId));

        assertEquals("안녕하세요", sendUntilReceived(chatRoomPath(roomId), "안녕하세요", received));
        assertTrue(session.isConnected());
    }

    @Test
    void 고수_참여자도_구독할_수_있다() throws Exception {
        StompSession session = connect(expertMemberId);
        BlockingQueue<String> received = subscribe(session, chatRoomPath(roomId));

        assertEquals("견적 문의", sendUntilReceived(chatRoomPath(roomId), "견적 문의", received));
    }

    @Test
    void 참여하지_않은_채팅방은_구독이_거절된다() throws Exception {
        assertRejected(otherCustomerId, chatRoomPath(roomId));
    }

    @Test
    void 존재하지_않는_채팅방은_구독이_거절된다() throws Exception {
        assertRejected(customerId, chatRoomPath(Long.MAX_VALUE));
    }

    @Test
    void 나간_채팅방은_구독이_거절된다() throws Exception {
        jdbcTemplate.update("update chat_participant set left_at = current_timestamp where chat_room_id = ? and member_id = ?",
                roomId, customerId);

        assertRejected(customerId, chatRoomPath(roomId));
    }

    @Test
    void roomId가_숫자가_아니면_구독이_거절된다() throws Exception {
        assertRejected(customerId, "/sub/chat-rooms/abc");
    }

    @Test
    void 채팅방_경로가_아니면_구독이_거절된다() throws Exception {
        assertRejected(customerId, "/sub/chat-rooms");
        assertRejected(customerId, "/sub/notices");
        assertRejected(customerId, "/sub/chat-rooms/" + roomId + "/extra");
    }

    // ── 헬퍼 ──

    private StompSession connect(Long memberId) throws Exception {
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.setOrigin(frontendUrl);
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + token(memberId));

        StompSession session = stompClient.connectAsync("ws://localhost:" + port + "/ws",
                handshakeHeaders, connectHeaders, new StompSessionHandlerAdapter() {
                }).get(5, TimeUnit.SECONDS);
        sessions.add(session);
        return session;
    }

    private BlockingQueue<String> subscribe(StompSession session, String destination) {
        BlockingQueue<String> received = new LinkedBlockingQueue<>();
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.add((String) payload);
            }
        });
        return received;
    }

    // SUBSCRIBE 처리는 비동기라, 구독이 등록될 때까지 몇 번 보내 본다
    private String sendUntilReceived(String destination, String payload, BlockingQueue<String> received) throws Exception {
        for (int i = 0; i < 50; i++) {
            messagingTemplate.convertAndSend(destination, payload);
            String message = received.poll(100, TimeUnit.MILLISECONDS);
            if (message != null) {
                return message;
            }
        }
        return null;
    }

    // 서버가 ERROR 프레임을 보내고 연결을 끊는다. 거절된 구독으로는 메시지가 오지 않는다
    private void assertRejected(Long memberId, String destination) throws Exception {
        StompSession session = connect(memberId);
        BlockingQueue<String> received = subscribe(session, destination);

        for (int i = 0; i < 50 && session.isConnected(); i++) {
            Thread.sleep(100);
        }
        assertFalse(session.isConnected());

        messagingTemplate.convertAndSend(destination, "새면 안 되는 메시지");
        assertNull(received.poll(300, TimeUnit.MILLISECONDS));
    }

    private static String chatRoomPath(Long roomId) {
        return "/sub/chat-rooms/" + roomId;
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    // 고객 요청서 → 고수 견적 → 채팅방(참여자 2명)
    private Long room(Long customer, Long expert) {
        jdbcTemplate.update("""
                insert into service_request (member_id, second_category_id, second_region_id, request_status, created_at)
                values (?, 101, 39, 'MATCHED', current_timestamp)
                """, customer);
        Long requestId = jdbcTemplate.queryForObject("select max(id) from service_request", Long.class);
        requestIds.add(requestId);

        Long expertProfileId = jdbcTemplate.queryForObject(
                "select id from expert_profile where member_id = ?", Long.class, expert);
        jdbcTemplate.update("""
                insert into quote (expert_id, service_request_id, description, price, quote_status, sent_at)
                values (?, ?, '테스트 견적', 100000, 'REQUESTED', current_timestamp)
                """, expertProfileId, requestId);
        Long quoteId = jdbcTemplate.queryForObject("select id from quote where service_request_id = ?", Long.class, requestId);

        jdbcTemplate.update("insert into chat_room (quote_id, created_at) values (?, current_timestamp)", quoteId);
        Long chatRoomId = jdbcTemplate.queryForObject("select id from chat_room where quote_id = ?", Long.class, quoteId);
        jdbcTemplate.update("insert into chat_participant (chat_room_id, member_id) values (?, ?), (?, ?)",
                chatRoomId, customer, chatRoomId, expert);
        return chatRoomId;
    }
}
