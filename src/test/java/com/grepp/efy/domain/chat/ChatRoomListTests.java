package com.grepp.efy.domain.chat;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 채팅방 목록 조회 (GET /api/chat-rooms) 테스트. 각 테스트는 롤백된다
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@SuppressWarnings("NonAsciiCharacters")
class ChatRoomListTests {
    private static final String URL = "/api/chat-rooms";

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;

    Long customerId;
    Long otherCustomerId;
    Long expertMemberId;
    Long otherExpertMemberId;

    @BeforeEach
    void setUp() {
        customerId = memberId("customer1@example.com");
        otherCustomerId = memberId("customer2@example.com");
        expertMemberId = memberId("expert1@example.com");
        otherExpertMemberId = memberId("expert2@example.com");
    }

    @Test
    void 마지막_메시지_최신순이고_메시지가_없는_방은_생성_시각으로_정렬한다() throws Exception {
        Long older = room(customerId, expertMemberId, 120, 180);   // 2시간 전 메시지
        Long noMessage = room(customerId, expertMemberId, null, 30); // 메시지 없음, 30분 전 생성
        Long newer = room(customerId, expertMemberId, 60, 180);    // 1시간 전 메시지

        getChatRooms(customerId, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].roomId").value(contains(
                        noMessage.intValue(), newer.intValue(), older.intValue())))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    void 상대방_정보와_채팅방_정보를_반환한다() throws Exception {
        Long roomId = room(customerId, expertMemberId, 10, 60);

        getChatRooms(customerId, 1)
                .andExpect(jsonPath("$.content[0].roomId").value(roomId.intValue()))
                .andExpect(jsonPath("$.content[0].quoteId").value(quoteIdOf(roomId).intValue()))
                .andExpect(jsonPath("$.content[0].partner.memberId").value(expertMemberId.intValue()))
                .andExpect(jsonPath("$.content[0].partnerLeft").value(false))
                .andExpect(jsonPath("$.content[0].partner.nickname").value("매칭고수"))
                .andExpect(jsonPath("$.content[0].partner.profileImageUrl").isNotEmpty())
                .andExpect(jsonPath("$.content[0].lastMessagePreview").value("테스트 메시지"))
                .andExpect(jsonPath("$.content[0].lastMessageAt").isNotEmpty())
                .andExpect(jsonPath("$.content[0].createdAt").isNotEmpty());

        // 고수 쪽에서 보면 상대방은 고객
        getChatRooms(expertMemberId, 1)
                .andExpect(jsonPath("$.content[0].roomId").value(roomId.intValue()))
                .andExpect(jsonPath("$.content[0].partner.memberId").value(customerId.intValue()))
                .andExpect(jsonPath("$.content[0].partner.nickname").value("고객하나"));
    }

    @Test
    void 메시지가_없는_방은_마지막_메시지_필드가_null이다() throws Exception {
        room(customerId, expertMemberId, null, 10);

        getChatRooms(customerId, 1)
                .andExpect(jsonPath("$.content[0].lastMessagePreview").isEmpty())
                .andExpect(jsonPath("$.content[0].lastMessageAt").isEmpty());
    }

    @Test
    void 내가_참여한_방만_조회한다() throws Exception {
        Long mine = room(customerId, expertMemberId, 10, 60);
        room(otherCustomerId, otherExpertMemberId, 5, 60);

        getChatRooms(customerId, 1)
                .andExpect(jsonPath("$.content[*].roomId").value(contains(mine.intValue())))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void 나간_방은_제외하고_상대방_목록에는_남는다() throws Exception {
        Long roomId = room(customerId, expertMemberId, 10, 60);
        jdbcTemplate.update("update chat_participant set left_at = current_timestamp where chat_room_id = ? and member_id = ?",
                roomId, customerId);

        getChatRooms(customerId, 1)
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        // 상대방 목록에는 남고, 상대방이 나갔다고 표시된다
        getChatRooms(expertMemberId, 1)
                .andExpect(jsonPath("$.content[*].roomId").value(contains(roomId.intValue())))
                .andExpect(jsonPath("$.content[0].partner.memberId").value(customerId.intValue()))
                .andExpect(jsonPath("$.content[0].partnerLeft").value(true));
    }

    @Test
    void 참여한_방이_없으면_빈_목록() throws Exception {
        getChatRooms(customerId, 1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void 페이지당_20개씩_반환한다() throws Exception {
        for (int i = 1; i <= 21; i++) {
            room(customerId, expertMemberId, i, 60 * 24);
        }

        getChatRooms(customerId, 1)
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.totalElements").value(21))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.lastPage").value(false));

        getChatRooms(customerId, 2)
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.lastPage").value(true));
    }

    @Test
    void pageNumber가_정수가_아니거나_1_미만이면_400() throws Exception {
        String token = token(customerId);
        for (String pageNumber : new String[]{"0", "-1"}) {
            mockMvc.perform(get(URL).param("pageNumber", pageNumber).header("Authorization", "Bearer " + token))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        }
        mockMvc.perform(get(URL).param("pageNumber", "abc").header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
    }

    @Test
    void 토큰이_없으면_401() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
    }

    // ── 요청 ──

    private ResultActions getChatRooms(Long memberId, int pageNumber) throws Exception {
        return mockMvc.perform(get(URL)
                .param("pageNumber", String.valueOf(pageNumber))
                .header("Authorization", "Bearer " + token(memberId)));
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    // ── 테스트 데이터 ──

    private Long memberId(String email) {
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
    }

    // 고객의 요청서에 고수가 보낸 견적으로 채팅방을 만든다. messageMinutesAgo가 null이면 메시지가 없는 방
    private Long room(Long customer, Long expert, Integer messageMinutesAgo, int createdMinutesAgo) {
        Long quoteId = quote(customer, expert);
        if (messageMinutesAgo == null) {
            jdbcTemplate.update("""
                    insert into chat_room (quote_id, created_at)
                    values (?, dateadd('MINUTE', ?, current_timestamp))
                    """, quoteId, -createdMinutesAgo);
        } else {
            jdbcTemplate.update("""
                    insert into chat_room (quote_id, last_message_preview, last_message_at, created_at)
                    values (?, '테스트 메시지', dateadd('MINUTE', ?, current_timestamp), dateadd('MINUTE', ?, current_timestamp))
                    """, quoteId, -messageMinutesAgo, -createdMinutesAgo);
        }
        Long roomId = jdbcTemplate.queryForObject("select id from chat_room where quote_id = ?", Long.class, quoteId);
        jdbcTemplate.update("insert into chat_participant (chat_room_id, member_id) values (?, ?), (?, ?)",
                roomId, customer, roomId, expert);
        return roomId;
    }

    // 견적은 (고수, 요청서) 조합이 유일해야 하므로 방마다 요청서를 새로 만든다
    private Long quote(Long customer, Long expert) {
        jdbcTemplate.update("""
                insert into service_request (member_id, second_category_id, second_region_id, request_status, created_at)
                values (?, 101, 39, 'OPEN', current_timestamp)
                """, customer);
        Long requestId = jdbcTemplate.queryForObject("select max(id) from service_request", Long.class);
        Long expertProfileId = jdbcTemplate.queryForObject(
                "select id from expert_profile where member_id = ?", Long.class, expert);
        jdbcTemplate.update("""
                insert into quote (expert_id, service_request_id, description, price, quote_status, sent_at)
                values (?, ?, '테스트 견적', 100000, 'SENT', current_timestamp)
                """, expertProfileId, requestId);
        return jdbcTemplate.queryForObject("select max(id) from quote", Long.class);
    }

    private Long quoteIdOf(Long roomId) {
        return jdbcTemplate.queryForObject("select quote_id from chat_room where id = ?", Long.class, roomId);
    }
}
