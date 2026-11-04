package com.grepp.efy.domain.chat;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 채팅방 생성 (POST /api/chat-rooms, 의뢰 요청과 함께 처리) 테스트. 각 테스트는 롤백된다
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("NonAsciiCharacters")
class ChatRoomCreateTests {
    private static final String URL = "/api/chat-rooms";

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;

    Long customerId;
    Long otherCustomerId;
    Long expertMemberId;
    Long quoteId;

    @BeforeEach
    void setUp() {
        customerId = memberId("customer1@example.com");
        otherCustomerId = memberId("customer2@example.com");
        expertMemberId = memberId("expert1@example.com");
        quoteId = createQuote(createRequest(customerId, "OPEN"), 1);
    }

    @Test
    void 고객이_요청하면_201이고_의뢰_요청되며_참여자_2명이_저장된다() throws Exception {
        Long requestId = requestIdOf(quoteId);

        createChatRoom(customerId, quoteId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").isNumber())
                .andExpect(jsonPath("$.quoteId").value(quoteId.intValue()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertEquals(List.of(customerId, expertMemberId), participantIds(quoteId));
        assertEquals("REQUESTED", quoteStatus(quoteId));
        assertEquals("MATCHED", requestStatus(requestId));
    }

    @Test
    void 이미_MATCHED인_요청서는_상태를_유지하고_채팅방을_만든다() throws Exception {
        Long requestId = createRequest(customerId, "MATCHED");
        Long matchedQuoteId = createQuote(requestId, 1);

        createChatRoom(customerId, matchedQuoteId)
                .andExpect(status().isCreated());

        assertEquals("REQUESTED", quoteStatus(matchedQuoteId));
        assertEquals("MATCHED", requestStatus(requestId));
    }

    @Test
    void 고수가_요청하면_403이고_상태가_바뀌지_않는다() throws Exception {
        createChatRoom(expertMemberId, quoteId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

        assertEquals(0, chatRoomCount(quoteId));
        assertEquals("SENT", quoteStatus(quoteId));
    }

    @Test
    void 요청서_작성자가_아니면_403() throws Exception {
        createChatRoom(otherCustomerId, quoteId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

        assertEquals(0, chatRoomCount(quoteId));
        assertEquals("SENT", quoteStatus(quoteId));
    }

    @Test
    void 없는_견적이면_404() throws Exception {
        createChatRoom(customerId, Long.MAX_VALUE)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("견적서를 찾을 수 없습니다."));
    }

    @Test
    void 발송후_72시간이_지난_견적이면_404() throws Exception {
        Long expiredQuoteId = createQuote(createRequest(customerId, "OPEN"), 73);

        createChatRoom(customerId, expiredQuoteId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("견적서를 찾을 수 없습니다."));

        assertEquals(0, chatRoomCount(expiredQuoteId));
    }

    @Test
    void 취소된_요청서의_견적이면_400() throws Exception {
        Long closedQuoteId = createQuote(createRequest(customerId, "CLOSED"), 1);

        createChatRoom(customerId, closedQuoteId)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("취소된 요청서의 견적서에는 의뢰 요청할 수 없습니다."));

        assertEquals(0, chatRoomCount(closedQuoteId));
    }

    @Test
    void 같은_견적으로_다시_요청하면_409() throws Exception {
        createChatRoom(customerId, quoteId).andExpect(status().isCreated());

        createChatRoom(customerId, quoteId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 의뢰 요청된 견적서입니다."));

        assertEquals(1, chatRoomCount(quoteId));
    }

    @Test
    void quoteId가_없거나_1_미만이면_400() throws Exception {
        String token = token(customerId);
        for (String body : List.of("{}", "{\"quoteId\": 0}", "{\"quoteId\": -1}")) {
            mockMvc.perform(post(URL)
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        }
    }

    @Test
    void 토큰이_없으면_401() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quoteId\": " + quoteId + "}"))
                .andExpect(status().isUnauthorized());
    }

    // ── 요청 ──

    private ResultActions createChatRoom(Long memberId, Long quoteId) throws Exception {
        return mockMvc.perform(post(URL)
                .header("Authorization", "Bearer " + token(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quoteId\": " + quoteId + "}"));
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    // ── 테스트 데이터 ──

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    // 이사(101)·강남구(39) 요청서. expert1과 매칭된다
    private Long createRequest(Long memberId, String status) {
        jdbcTemplate.update("""
                insert into service_request (member_id, second_category_id, second_region_id, request_status, created_at)
                values (?, 101, 39, ?, current_timestamp)
                """, memberId, status);
        return jdbcTemplate.queryForObject("select max(id) from service_request", Long.class);
    }

    private Long createQuote(Long requestId, int sentHoursAgo) {
        Long expertProfileId = jdbcTemplate.queryForObject(
                "select id from expert_profile where member_id = ?", Long.class, expertMemberId);
        jdbcTemplate.update("""
                insert into quote (expert_id, service_request_id, description, price, quote_status, sent_at)
                values (?, ?, '테스트 견적', 100000, 'SENT', dateadd('HOUR', ?, current_timestamp))
                """, expertProfileId, requestId, -sentHoursAgo);
        return jdbcTemplate.queryForObject("select max(id) from quote", Long.class);
    }

    // ── 조회 ──

    private int chatRoomCount(Long quoteId) {
        return jdbcTemplate.queryForObject("select count(*) from chat_room where quote_id = ?", Integer.class, quoteId);
    }

    private Long requestIdOf(Long quoteId) {
        return jdbcTemplate.queryForObject("select service_request_id from quote where id = ?", Long.class, quoteId);
    }

    private String quoteStatus(Long quoteId) {
        return jdbcTemplate.queryForObject("select quote_status from quote where id = ?", String.class, quoteId);
    }

    private String requestStatus(Long requestId) {
        return jdbcTemplate.queryForObject("select request_status from service_request where id = ?", String.class, requestId);
    }

    private List<Long> participantIds(Long quoteId) {
        return jdbcTemplate.queryForList("""
                select cp.member_id from chat_participant cp
                join chat_room cr on cp.chat_room_id = cr.id
                where cr.quote_id = ?
                order by cp.id
                """, Long.class, quoteId);
    }
}
