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

// 채팅방 생성 (POST /api/chat-rooms) 테스트. 각 테스트는 롤백된다
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
        quoteId = createQuote(createRequest(customerId));
    }

    @Test
    void 고객이_생성하면_201이고_고객과_고수가_참여자로_저장된다() throws Exception {
        createChatRoom(customerId, quoteId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId").isNumber())
                .andExpect(jsonPath("$.quoteId").value(quoteId.intValue()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertEquals(List.of(customerId, expertMemberId), participantIds(quoteId));
    }

    @Test
    void 고수도_생성할_수_있다() throws Exception {
        createChatRoom(expertMemberId, quoteId)
                .andExpect(status().isCreated());

        assertEquals(List.of(customerId, expertMemberId), participantIds(quoteId));
    }

    @Test
    void 견적_당사자가_아니면_403() throws Exception {
        createChatRoom(otherCustomerId, quoteId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("해당 견적서의 당사자가 아닙니다."));

        assertEquals(0, chatRoomCount(quoteId));
    }

    @Test
    void 없는_견적이면_404() throws Exception {
        createChatRoom(customerId, Long.MAX_VALUE)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("견적서를 찾을 수 없습니다."));
    }

    @Test
    void 같은_견적으로_다시_요청하면_409() throws Exception {
        createChatRoom(customerId, quoteId).andExpect(status().isCreated());

        // 상대방(고수)이 요청해도 같은 견적이면 거절
        createChatRoom(expertMemberId, quoteId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 채팅방이 존재합니다."));

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
    private Long createRequest(Long memberId) {
        jdbcTemplate.update("""
                insert into service_request (member_id, second_category_id, second_region_id, request_status, created_at)
                values (?, 101, 39, 'OPEN', current_timestamp)
                """, memberId);
        return jdbcTemplate.queryForObject("select max(id) from service_request", Long.class);
    }

    private Long createQuote(Long requestId) {
        Long expertProfileId = jdbcTemplate.queryForObject(
                "select id from expert_profile where member_id = ?", Long.class, expertMemberId);
        jdbcTemplate.update("""
                insert into quote (expert_id, service_request_id, description, price, quote_status, sent_at)
                values (?, ?, '테스트 견적', 100000, 'SENT', current_timestamp)
                """, expertProfileId, requestId);
        return jdbcTemplate.queryForObject("select max(id) from quote", Long.class);
    }

    // ── 조회 ──

    private int chatRoomCount(Long quoteId) {
        return jdbcTemplate.queryForObject("select count(*) from chat_room where quote_id = ?", Integer.class, quoteId);
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
