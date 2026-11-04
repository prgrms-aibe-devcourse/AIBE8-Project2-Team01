package com.grepp.efy.domain.chat;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 채팅방 나가기(PATCH /api/chat-rooms/{roomId}/leave) 테스트. 각 테스트는 롤백된다
// 나간 뒤 STOMP 구독·전송 거절은 ChatSubscribeTests·ChatMessageSendTests의 나간 채팅방 케이스에서 확인한다
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("NonAsciiCharacters")
class ChatLeaveTests {

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired EntityManager entityManager;

    Long customerId;
    Long expertMemberId;
    Long otherCustomerId;
    Long roomId;

    @BeforeEach
    void setUp() {
        customerId = memberId("customer1@example.com");
        expertMemberId = memberId("expert1@example.com");
        otherCustomerId = memberId("customer2@example.com");
        roomId = room(customerId, expertMemberId);
    }

    @Test
    void 나가면_204이고_나간_시각이_기록되며_참여_기록과_메시지는_남는다() throws Exception {
        message(expertMemberId);

        leave(customerId, roomId).andExpect(status().isNoContent());
        entityManager.flush(); // 테스트 트랜잭션 안이라 변경을 DB에 반영한 뒤 jdbc로 확인

        assertNotNull(leftAt(customerId));
        assertNull(leftAt(expertMemberId)); // 상대방은 그대로
        assertEquals(2, jdbcTemplate.queryForObject(
                "select count(*) from chat_participant where chat_room_id = ?", Integer.class, roomId));
        assertEquals(1, jdbcTemplate.queryForObject(
                "select count(*) from chat_message where chat_room_id = ?", Integer.class, roomId));
    }

    @Test
    void 나간_방은_내_목록에서_빠지고_상대방_목록에는_남는다() throws Exception {
        leave(customerId, roomId).andExpect(status().isNoContent());

        mockMvc.perform(get("/api/chat-rooms").header("Authorization", "Bearer " + token(customerId)))
                .andExpect(jsonPath("$.content").isEmpty());
        mockMvc.perform(get("/api/chat-rooms").header("Authorization", "Bearer " + token(expertMemberId)))
                .andExpect(jsonPath("$.content[*].roomId").value(contains(roomId.intValue())));
    }

    @Test
    void 나간_뒤에는_메시지_조회와_읽음_처리와_다시_나가기가_403() throws Exception {
        message(expertMemberId);
        leave(customerId, roomId).andExpect(status().isNoContent());

        String token = token(customerId);
        mockMvc.perform(get("/api/chat-rooms/" + roomId + "/messages").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/chat-rooms/" + roomId + "/read").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        leave(customerId, roomId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

        // 상대방은 계속 조회할 수 있다
        mockMvc.perform(get("/api/chat-rooms/" + roomId + "/messages").header("Authorization", "Bearer " + token(expertMemberId)))
                .andExpect(status().isOk());
    }

    @Test
    void 참여자가_아니거나_없는_채팅방이면_403() throws Exception {
        leave(otherCustomerId, roomId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));
        leave(customerId, Long.MAX_VALUE)
                .andExpect(status().isForbidden());

        assertNull(leftAt(customerId));
    }

    @Test
    void roomId가_1_미만이거나_정수가_아니면_400() throws Exception {
        String token = token(customerId);

        mockMvc.perform(patch("/api/chat-rooms/0/leave").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(patch("/api/chat-rooms/abc/leave").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
    }

    @Test
    void 토큰이_없으면_401() throws Exception {
        mockMvc.perform(patch("/api/chat-rooms/" + roomId + "/leave"))
                .andExpect(status().isUnauthorized());
    }

    // ── 요청 ──

    private ResultActions leave(Long memberId, Long roomId) throws Exception {
        return mockMvc.perform(patch("/api/chat-rooms/" + roomId + "/leave")
                .header("Authorization", "Bearer " + token(memberId)));
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    // ── 테스트 데이터 ──

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    private Object leftAt(Long memberId) {
        return jdbcTemplate.queryForObject(
                "select left_at from chat_participant where chat_room_id = ? and member_id = ?",
                Object.class, roomId, memberId);
    }

    private void message(Long senderId) {
        jdbcTemplate.update("""
                insert into chat_message (chat_room_id, sender_id, type, content, created_at)
                values (?, ?, 'TEXT', '테스트 메시지', current_timestamp)
                """, roomId, senderId);
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
