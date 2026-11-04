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
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// 채팅 메시지 전송(SEND /pub/chat-rooms/{roomId}/messages) 테스트. 실제 서버를 띄워 WebSocket으로 접속한다
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SuppressWarnings({"NonAsciiCharacters", "unchecked"})
class ChatMessageSendTests {

    private static final String ERROR_QUEUE = "/user/queue/errors";

    @LocalServerPort int port;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired SimpMessagingTemplate messagingTemplate;
    @Value("${app.frontend-url}") String frontendUrl;

    private final List<StompSession> sessions = new ArrayList<>();
    private final List<Long> requestIds = new ArrayList<>();
    private final List<Long> fileIds = new ArrayList<>();
    private WebSocketStompClient stompClient;

    private Long customerId;
    private Long expertMemberId;
    private Long otherCustomerId;
    private Long roomId;

    @BeforeEach
    void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

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
            jdbcTemplate.update("delete from chat_message where chat_room_id in " + rooms, requestId);
            jdbcTemplate.update("delete from chat_participant where chat_room_id in " + rooms, requestId);
            jdbcTemplate.update("delete from chat_room where id in " + rooms, requestId);
            jdbcTemplate.update("delete from quote where service_request_id = ?", requestId);
            jdbcTemplate.update("delete from service_request where id = ?", requestId);
        }
        for (Long fileId : fileIds) {
            jdbcTemplate.update("delete from file where id = ?", fileId);
        }
    }

    @Test
    void 참여자가_보내면_저장되고_본인과_상대방_구독자_모두에게_전달된다() throws Exception {
        StompSession customer = connect(customerId);
        StompSession expert = connect(expertMemberId);
        BlockingQueue<Map<String, Object>> customerInbox = subscribe(customer, roomPath(roomId));
        BlockingQueue<Map<String, Object>> expertInbox = subscribe(expert, roomPath(roomId));
        awaitSubscribed(customerInbox, expertInbox);

        customer.send(messagePath(roomId), Map.of("type", "TEXT", "content", "내일 오전 가능할까요?"));

        Map<String, Object> toExpert = expertInbox.poll(5, TimeUnit.SECONDS);
        Map<String, Object> toCustomer = customerInbox.poll(5, TimeUnit.SECONDS);
        assertNotNull(toExpert);
        assertEquals(toExpert, toCustomer);

        assertEquals(roomId.intValue(), toExpert.get("roomId"));
        assertEquals(customerId.intValue(), toExpert.get("senderId"));
        assertEquals("고객하나", toExpert.get("senderNickname"));
        assertEquals("TEXT", toExpert.get("type"));
        assertEquals("내일 오전 가능할까요?", toExpert.get("content"));
        assertNull(toExpert.get("imageUrl"));
        assertNotNull(toExpert.get("messageId"));
        assertNotNull(toExpert.get("createdAt"));

        // 저장되고 채팅방 마지막 메시지가 갱신된다 (목록 조회 정렬에 사용)
        assertEquals(1, messageCount(roomId));
        assertEquals("내일 오전 가능할까요?", jdbcTemplate.queryForObject(
                "select last_message_preview from chat_room where id = ?", String.class, roomId));
        assertNotNull(jdbcTemplate.queryForObject(
                "select last_message_at from chat_room where id = ?", Object.class, roomId));
    }

    @Test
    void _500자_메시지도_저장되고_미리보기는_앞_50자다() throws Exception {
        // 미리보기 컬럼(255자)보다 긴 메시지를 보내면 저장이 실패하던 문제 (#180)
        String content = "가".repeat(300) + "나".repeat(200);
        StompSession customer = connect(customerId);
        BlockingQueue<Map<String, Object>> inbox = subscribe(customer, roomPath(roomId));
        awaitSubscribed(inbox);

        customer.send(messagePath(roomId), Map.of("type", "TEXT", "content", content));

        Map<String, Object> received = inbox.poll(5, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals(content, received.get("content"));
        assertEquals(content, jdbcTemplate.queryForObject(
                "select content from chat_message where chat_room_id = ?", String.class, roomId));
        assertEquals("가".repeat(50), jdbcTemplate.queryForObject(
                "select last_message_preview from chat_room where id = ?", String.class, roomId));
    }

    @Test
    void 보낸_사람은_payload가_아니라_세션_사용자로_정해진다() throws Exception {
        StompSession expert = connect(expertMemberId);
        BlockingQueue<Map<String, Object>> inbox = subscribe(expert, roomPath(roomId));
        awaitSubscribed(inbox);

        expert.send(messagePath(roomId), Map.of("type", "TEXT", "content", "견적 드립니다", "senderId", customerId));

        Map<String, Object> received = inbox.poll(5, TimeUnit.SECONDS);
        assertNotNull(received);
        assertEquals(expertMemberId.intValue(), received.get("senderId"));
        assertEquals("매칭고수", received.get("senderNickname"));
    }

    @Test
    void 참여자가_아니면_전송이_거절되고_저장되지_않는다() throws Exception {
        assertSendRejected(otherCustomerId, messagePath(roomId));
    }

    @Test
    void 나간_채팅방에는_보낼_수_없다() throws Exception {
        jdbcTemplate.update("update chat_participant set left_at = current_timestamp where chat_room_id = ? and member_id = ?",
                roomId, customerId);

        assertSendRejected(customerId, messagePath(roomId));
    }

    @Test
    void 전송_경로_형식이_아니면_거절된다() throws Exception {
        assertSendRejected(customerId, "/pub/chat-rooms/" + roomId);
        assertSendRejected(customerId, "/pub/chat-rooms/abc/messages");
    }

    @Test
    void 내용이_비었거나_500자를_넘으면_보낸_사람에게만_에러가_가고_연결은_유지된다() throws Exception {
        StompSession customer = connect(customerId);
        BlockingQueue<Map<String, Object>> errors = subscribe(customer, ERROR_QUEUE);

        Map<String, Object> blank = sendUntilError(customer, Map.of("type", "TEXT", "content", "   "), errors);
        assertEquals("입력값이 올바르지 않습니다.", blank.get("message"));
        assertTrue(((Map<String, Object>) blank.get("errors")).containsKey("content"));

        Map<String, Object> tooLong = sendUntilError(customer, Map.of("type", "TEXT", "content", "가".repeat(501)), errors);
        assertTrue(((Map<String, Object>) tooLong.get("errors")).containsKey("content"));

        assertTrue(customer.isConnected());
        assertEquals(0, messageCount(roomId));
    }

    @Test
    void type이_없거나_잘못되면_에러가_간다() throws Exception {
        StompSession customer = connect(customerId);
        BlockingQueue<Map<String, Object>> errors = subscribe(customer, ERROR_QUEUE);

        assertEquals("입력값이 올바르지 않습니다.",
                sendUntilError(customer, Map.of("content", "안녕하세요"), errors).get("message"));
        assertEquals("입력값이 올바르지 않습니다.",
                sendUntilError(customer, Map.of("type", "VIDEO", "content", "안녕하세요"), errors).get("message"));

        assertTrue(customer.isConnected());
        assertEquals(0, messageCount(roomId));
    }

    @Test
    void 다른_세션의_에러_큐는_직접_구독할_수_없다() throws Exception {
        StompSession customer = connect(customerId);
        subscribe(customer, "/queue/errors-user1234");

        for (int i = 0; i < 50 && customer.isConnected(); i++) {
            Thread.sleep(100);
        }
        assertFalse(customer.isConnected());
    }

    // ── 이미지 메시지 ──

    @Test
    void 본인이_올린_파일로_이미지_메시지를_보내면_저장되고_imageUrl과_함께_전달된다() throws Exception {
        Long fileId = file(customerId, "chat/room-photo.png", false);
        StompSession customer = connect(customerId);
        StompSession expert = connect(expertMemberId);
        BlockingQueue<Map<String, Object>> customerInbox = subscribe(customer, roomPath(roomId));
        BlockingQueue<Map<String, Object>> expertInbox = subscribe(expert, roomPath(roomId));
        awaitSubscribed(customerInbox, expertInbox);

        customer.send(messagePath(roomId), Map.of("type", "IMAGE", "fileId", fileId));

        Map<String, Object> toExpert = expertInbox.poll(5, TimeUnit.SECONDS);
        assertNotNull(toExpert);
        assertEquals(toExpert, customerInbox.poll(5, TimeUnit.SECONDS));
        assertEquals("IMAGE", toExpert.get("type"));
        assertNull(toExpert.get("content"));
        assertTrue(((String) toExpert.get("imageUrl")).endsWith("/chat/room-photo.png"));

        assertEquals(fileId, jdbcTemplate.queryForObject(
                "select image_id from chat_message where chat_room_id = ?", Long.class, roomId));
        assertEquals("사진", jdbcTemplate.queryForObject(
                "select last_message_preview from chat_room where id = ?", String.class, roomId));
    }

    @Test
    void IMAGE인데_fileId가_없으면_에러가_간다() throws Exception {
        StompSession customer = connect(customerId);
        BlockingQueue<Map<String, Object>> errors = subscribe(customer, ERROR_QUEUE);

        Map<String, Object> error = sendUntilError(customer, Map.of("type", "IMAGE"), errors);
        assertEquals("입력값이 올바르지 않습니다.", error.get("message"));
        assertTrue(((Map<String, Object>) error.get("errors")).containsKey("fileId"));

        assertTrue(customer.isConnected());
        assertEquals(0, messageCount(roomId));
    }

    @Test
    void 없거나_삭제된_파일이면_에러가_간다() throws Exception {
        Long deletedFileId = file(customerId, "chat/deleted.png", true);
        StompSession customer = connect(customerId);
        BlockingQueue<Map<String, Object>> errors = subscribe(customer, ERROR_QUEUE);

        assertEquals("파일을 찾을 수 없습니다.",
                sendUntilError(customer, Map.of("type", "IMAGE", "fileId", Long.MAX_VALUE), errors).get("message"));
        assertEquals("파일을 찾을 수 없습니다.",
                sendUntilError(customer, Map.of("type", "IMAGE", "fileId", deletedFileId), errors).get("message"));

        assertTrue(customer.isConnected());
        assertEquals(0, messageCount(roomId));
    }

    @Test
    void 다른_사람이_올린_파일이면_에러가_간다() throws Exception {
        Long expertFileId = file(expertMemberId, "chat/expert-photo.png", false);
        StompSession customer = connect(customerId);
        BlockingQueue<Map<String, Object>> errors = subscribe(customer, ERROR_QUEUE);

        assertEquals("본인이 업로드한 파일만 사용할 수 있습니다.",
                sendUntilError(customer, Map.of("type", "IMAGE", "fileId", expertFileId), errors).get("message"));

        assertTrue(customer.isConnected());
        assertEquals(0, messageCount(roomId));
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

    private BlockingQueue<Map<String, Object>> subscribe(StompSession session, String destination) {
        BlockingQueue<Map<String, Object>> received = new LinkedBlockingQueue<>();
        session.subscribe(destination, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Map.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.add((Map<String, Object>) payload);
            }
        });
        return received;
    }

    // SUBSCRIBE 처리는 비동기라, 모든 구독자에게 확인용 메시지가 도착할 때까지 보낸 뒤 한꺼번에 비운다
    @SafeVarargs
    private void awaitSubscribed(BlockingQueue<Map<String, Object>>... inboxes) throws Exception {
        for (int i = 0; i < 50; i++) {
            messagingTemplate.convertAndSend(roomPath(roomId), (Object) Map.of("probe", true));
            Thread.sleep(100);
            if (Arrays.stream(inboxes).noneMatch(BlockingQueue::isEmpty)) {
                Thread.sleep(200);
                for (BlockingQueue<Map<String, Object>> inbox : inboxes) {
                    inbox.clear();
                }
                return;
            }
        }
        throw new AssertionError("구독이 등록되지 않았습니다.");
    }

    // 에러 큐 구독도 비동기라, 에러가 올 때까지 같은 잘못된 메시지를 다시 보낸다 (저장되지 않으므로 안전)
    private Map<String, Object> sendUntilError(StompSession session, Map<String, Object> payload,
                                               BlockingQueue<Map<String, Object>> errors) throws Exception {
        for (int i = 0; i < 50; i++) {
            session.send(messagePath(roomId), payload);
            Map<String, Object> error = errors.poll(100, TimeUnit.MILLISECONDS);
            if (error != null) {
                Thread.sleep(200);
                errors.clear();
                return error;
            }
        }
        throw new AssertionError("에러 메시지를 받지 못했습니다.");
    }

    // 서버가 ERROR 프레임을 보내고 연결을 끊는다. 메시지는 저장되지 않는다
    private void assertSendRejected(Long memberId, String destination) throws Exception {
        StompSession session = connect(memberId);
        session.send(destination, Map.of("type", "TEXT", "content", "보내지면 안 되는 메시지"));

        for (int i = 0; i < 50 && session.isConnected(); i++) {
            Thread.sleep(100);
        }
        assertFalse(session.isConnected());
        assertEquals(0, messageCount(roomId));
    }

    // PUBLIC 파일. URL은 Supabase 호출 없이 경로로 만들어진다
    private Long file(Long uploaderId, String storagePath, boolean deleted) {
        jdbcTemplate.update("""
                insert into file (uploader_id, storage_path, original_name, content_type, size, visibility, created_at, deleted_at)
                values (?, ?, 'photo.png', 'image/png', 100, 'PUBLIC', current_timestamp, case when ? then current_timestamp end)
                """, uploaderId, UUID.randomUUID() + "/" + storagePath, deleted); // storage_path는 unique
        Long id = jdbcTemplate.queryForObject("select max(id) from file", Long.class);
        fileIds.add(id);
        return id;
    }

    private int messageCount(Long roomId) {
        return jdbcTemplate.queryForObject("select count(*) from chat_message where chat_room_id = ?", Integer.class, roomId);
    }

    private static String roomPath(Long roomId) {
        return "/sub/chat-rooms/" + roomId;
    }

    private static String messagePath(Long roomId) {
        return "/pub/chat-rooms/" + roomId + "/messages";
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    // 고객 요청서 → 고수 견적(의뢰 요청됨) → 채팅방(참여자 2명)
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
