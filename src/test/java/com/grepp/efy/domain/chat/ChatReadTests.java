package com.grepp.efy.domain.chat;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 채팅 읽음 처리(PATCH /api/chat-rooms/{roomId}/read)와 목록의 안 읽은 메시지 수 테스트. 각 테스트는 롤백된다
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("NonAsciiCharacters")
class ChatReadTests {

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;

    Long customerId;
    Long expertMemberId;
    Long otherExpertMemberId;
    Long otherCustomerId;
    Long roomId;

    @BeforeEach
    void setUp() {
        customerId = memberId("customer1@example.com");
        expertMemberId = memberId("expert1@example.com");
        otherExpertMemberId = memberId("expert2@example.com");
        otherCustomerId = memberId("customer2@example.com");
        roomId = room(customerId, expertMemberId);
    }

    // ── 안 읽은 메시지 수 ──

    @Test
    void 상대방이_보낸_메시지만_안_읽은_수로_센다() throws Exception {
        message(roomId, expertMemberId, false);
        message(roomId, expertMemberId, false);
        message(roomId, customerId, false);   // 내가 보낸 메시지
        message(roomId, expertMemberId, true); // 삭제된 메시지

        assertEquals(2, unreadCount(customerId, roomId));
        assertEquals(1, unreadCount(expertMemberId, roomId));
    }

    @Test
    void 메시지가_없으면_안_읽은_수는_0() throws Exception {
        assertEquals(0, unreadCount(customerId, roomId));
    }

    @Test
    void 여러_채팅방의_안_읽은_수를_방마다_따로_센다() throws Exception {
        Long otherRoomId = room(customerId, otherExpertMemberId);
        message(roomId, expertMemberId, false);
        message(otherRoomId, otherExpertMemberId, false);
        message(otherRoomId, otherExpertMemberId, false);
        message(otherRoomId, otherExpertMemberId, false);

        assertEquals(1, unreadCount(customerId, roomId));
        assertEquals(3, unreadCount(customerId, otherRoomId));
    }

    // ── 읽음 처리 ──

    @Test
    void 읽음_처리하면_204이고_최신_메시지까지_읽은_것으로_바뀐다() throws Exception {
        message(roomId, expertMemberId, false);
        Long latest = message(roomId, customerId, false);

        read(customerId, roomId).andExpect(status().isNoContent());

        assertEquals(latest, lastReadMessageId(customerId));
        assertEquals(0, unreadCount(customerId, roomId));
        assertNull(lastReadMessageId(expertMemberId)); // 상대방 읽음 위치는 그대로
    }

    @Test
    void 읽음_처리_후_상대방이_새로_보낸_메시지만_센다() throws Exception {
        message(roomId, expertMemberId, false);
        read(customerId, roomId).andExpect(status().isNoContent());

        message(roomId, expertMemberId, false);
        message(roomId, expertMemberId, false);
        message(roomId, customerId, false);

        assertEquals(2, unreadCount(customerId, roomId));
    }

    @Test
    void 메시지가_없는_방은_읽음_위치가_바뀌지_않는다() throws Exception {
        read(customerId, roomId).andExpect(status().isNoContent());

        assertNull(lastReadMessageId(customerId));
    }

    @Test
    void 읽음_위치는_뒤로_가지_않는다() throws Exception {
        Long latest = message(roomId, expertMemberId, false);
        // 다른 요청이 먼저 더 앞선 위치까지 읽음 처리한 상황
        jdbcTemplate.update("update chat_participant set last_read_message_id = ? where chat_room_id = ? and member_id = ?",
                latest + 1000, roomId, customerId);

        read(customerId, roomId).andExpect(status().isNoContent());

        assertEquals(latest + 1000, lastReadMessageId(customerId));
    }

    @Test
    void 참여자가_아니거나_나갔거나_없는_채팅방이면_403() throws Exception {
        message(roomId, expertMemberId, false);

        read(otherCustomerId, roomId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));
        read(customerId, Long.MAX_VALUE)
                .andExpect(status().isForbidden());

        jdbcTemplate.update("update chat_participant set left_at = current_timestamp where chat_room_id = ? and member_id = ?",
                roomId, customerId);
        read(customerId, roomId)
                .andExpect(status().isForbidden());
        assertNull(lastReadMessageId(customerId));
    }

    @Test
    void roomId가_1_미만이거나_정수가_아니면_400() throws Exception {
        String token = token(customerId);

        mockMvc.perform(patch("/api/chat-rooms/0/read").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(patch("/api/chat-rooms/abc/read").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
    }

    @Test
    void 토큰이_없으면_401() throws Exception {
        mockMvc.perform(patch("/api/chat-rooms/" + roomId + "/read"))
                .andExpect(status().isUnauthorized());
    }

    // ── 요청 ──

    private ResultActions read(Long memberId, Long roomId) throws Exception {
        return mockMvc.perform(patch("/api/chat-rooms/" + roomId + "/read")
                .header("Authorization", "Bearer " + token(memberId)));
    }

    // 채팅방 목록 API에서 해당 방의 unreadCount를 꺼낸다
    private long unreadCount(Long memberId, Long roomId) throws Exception {
        String json = mockMvc.perform(get("/api/chat-rooms").header("Authorization", "Bearer " + token(memberId)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Number> counts = JsonPath.read(
                json, "$.content[?(@.roomId == " + roomId + ")].unreadCount");
        assertEquals(1, counts.size());
        return counts.get(0).longValue();
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    // ── 테스트 데이터 ──

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    private Long lastReadMessageId(Long memberId) {
        return jdbcTemplate.queryForObject(
                "select last_read_message_id from chat_participant where chat_room_id = ? and member_id = ?",
                Long.class, roomId, memberId);
    }

    private Long message(Long roomId, Long senderId, boolean deleted) {
        jdbcTemplate.update("""
                insert into chat_message (chat_room_id, sender_id, type, content, created_at, deleted_at)
                values (?, ?, 'TEXT', '테스트 메시지', current_timestamp, case when ? then current_timestamp end)
                """, roomId, senderId, deleted);
        return jdbcTemplate.queryForObject("select max(id) from chat_message where chat_room_id = ?", Long.class, roomId);
    }

    // 고객 요청서 → 고수 견적(의뢰 요청됨) → 채팅방(참여자 2명)
    private Long room(Long customer, Long expert) {
        jdbcTemplate.update("""
                insert into service_request (member_id, second_category_id, second_region_id, request_status, created_at)
                values (?, 101, 39, 'MATCHED', current_timestamp)
                """, customer);
        Long requestId = jdbcTemplate.queryForObject("select max(id) from service_request", Long.class);

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
