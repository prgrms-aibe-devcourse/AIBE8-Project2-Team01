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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 채팅 메시지 목록 조회 (GET /api/chat-rooms/{roomId}/messages) 테스트. 각 테스트는 롤백된다
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("NonAsciiCharacters")
class ChatMessageListTests {

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;

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
    void 최신순_30개와_다음_커서를_반환하고_커서로_이어서_조회한다() throws Exception {
        List<Long> ids = messages(35); // 오래된 것부터 저장

        String first = getMessages(customerId, roomId, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages.length()").value(30))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andReturn().getResponse().getContentAsString();

        List<Long> firstIds = messageIds(first);
        assertEquals(reversed(ids.subList(5, 35)), firstIds);
        Long nextCursor = ((Number) JsonPath.read(first, "$.nextCursor")).longValue();
        assertEquals(firstIds.get(29), nextCursor);

        String second = getMessages(customerId, roomId, nextCursor)
                .andExpect(jsonPath("$.messages.length()").value(5))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.nextCursor").isEmpty())
                .andReturn().getResponse().getContentAsString();

        assertEquals(reversed(ids.subList(0, 5)), messageIds(second));
    }

    @Test
    void 조회_중_새_메시지가_추가되어도_이어서_조회한_결과가_밀리지_않는다() throws Exception {
        List<Long> ids = messages(35);
        String first = getMessages(customerId, roomId, null).andReturn().getResponse().getContentAsString();
        Long nextCursor = ((Number) JsonPath.read(first, "$.nextCursor")).longValue();

        messages(3); // 상대방이 그 사이에 보낸 새 메시지

        String second = getMessages(customerId, roomId, nextCursor).andReturn().getResponse().getContentAsString();
        assertEquals(reversed(ids.subList(0, 5)), messageIds(second));
    }

    @Test
    void 정확히_30개면_다음이_없다() throws Exception {
        messages(30);

        getMessages(customerId, roomId, null)
                .andExpect(jsonPath("$.messages.length()").value(30))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.nextCursor").isEmpty());
    }

    @Test
    void 메시지_항목은_실시간_전송과_같은_형식이다() throws Exception {
        Long messageId = message(expertMemberId, "견적 관련 문의 주세요", false);

        getMessages(customerId, roomId, null)
                .andExpect(jsonPath("$.messages[0].messageId").value(messageId.intValue()))
                .andExpect(jsonPath("$.messages[0].roomId").value(roomId.intValue()))
                .andExpect(jsonPath("$.messages[0].senderId").value(expertMemberId.intValue()))
                .andExpect(jsonPath("$.messages[0].senderNickname").value("매칭고수"))
                .andExpect(jsonPath("$.messages[0].type").value("TEXT"))
                .andExpect(jsonPath("$.messages[0].content").value("견적 관련 문의 주세요"))
                .andExpect(jsonPath("$.messages[0].createdAt").isNotEmpty());

        // 고수도 같은 방의 메시지를 조회할 수 있다
        getMessages(expertMemberId, roomId, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages[0].messageId").value(messageId.intValue()));
    }

    @Test
    void 삭제된_메시지는_제외한다() throws Exception {
        Long kept = message(customerId, "남는 메시지", false);
        message(customerId, "삭제된 메시지", true);

        getMessages(customerId, roomId, null)
                .andExpect(jsonPath("$.messages.length()").value(1))
                .andExpect(jsonPath("$.messages[0].messageId").value(kept.intValue()));
    }

    @Test
    void 메시지가_없으면_빈_목록() throws Exception {
        getMessages(customerId, roomId, null)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").isEmpty())
                .andExpect(jsonPath("$.nextCursor").isEmpty())
                .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    void 참여자가_아니거나_나갔거나_없는_채팅방이면_403() throws Exception {
        messages(1);

        getMessages(otherCustomerId, roomId, null)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

        getMessages(customerId, Long.MAX_VALUE, null)
                .andExpect(status().isForbidden());

        jdbcTemplate.update("update chat_participant set left_at = current_timestamp where chat_room_id = ? and member_id = ?",
                roomId, customerId);
        getMessages(customerId, roomId, null)
                .andExpect(status().isForbidden());
    }

    @Test
    void roomId나_cursor가_1_미만이거나_정수가_아니면_400() throws Exception {
        String token = token(customerId);

        mockMvc.perform(get("/api/chat-rooms/0/messages").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(get("/api/chat-rooms/" + roomId + "/messages").param("cursor", "0")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(get("/api/chat-rooms/abc/messages").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        mockMvc.perform(get("/api/chat-rooms/" + roomId + "/messages").param("cursor", "abc")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
    }

    @Test
    void 토큰이_없으면_401() throws Exception {
        mockMvc.perform(get("/api/chat-rooms/" + roomId + "/messages"))
                .andExpect(status().isUnauthorized());
    }

    // ── 요청 ──

    private ResultActions getMessages(Long memberId, Long roomId, Long cursor) throws Exception {
        var request = get("/api/chat-rooms/" + roomId + "/messages").header("Authorization", "Bearer " + token(memberId));
        if (cursor != null) {
            request.param("cursor", String.valueOf(cursor));
        }
        return mockMvc.perform(request);
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    private static List<Long> messageIds(String json) {
        List<Number> ids = JsonPath.read(json, "$.messages[*].messageId");
        return ids.stream().map(Number::longValue).toList();
    }

    private static List<Long> reversed(List<Long> ids) {
        List<Long> copy = new ArrayList<>(ids);
        Collections.reverse(copy);
        return copy;
    }

    // ── 테스트 데이터 ──

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    // 고객·고수가 번갈아 보낸 메시지 count개. 저장 순서(id 오름차순)대로 반환
    private List<Long> messages(int count) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(message(i % 2 == 0 ? customerId : expertMemberId, "메시지 " + i, false));
        }
        return ids;
    }

    private Long message(Long senderId, String content, boolean deleted) {
        jdbcTemplate.update("""
                insert into chat_message (chat_room_id, sender_id, type, content, created_at, deleted_at)
                values (?, ?, 'TEXT', ?, current_timestamp, case when ? then current_timestamp end)
                """, roomId, senderId, content, deleted);
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
