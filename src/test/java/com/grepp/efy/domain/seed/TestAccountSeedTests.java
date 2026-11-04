package com.grepp.efy.domain.seed;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import tools.jackson.databind.JsonNode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:testAccountSeed")
@AutoConfigureMockMvc
@Transactional
class TestAccountSeedTests {
    private static final List<Integer> TEAM_NUMBERS = List.of(11, 12, 21, 22, 31, 32, 41, 42, 51, 52);
    private static final List<Integer> WITHDRAW_NUMBERS = List.of(19, 29, 39, 49, 59);
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired ObjectMapper objectMapper;

    @Test
    void 커뮤니티와_찜과_채팅이_기존_팀원_계정에_연결된다() throws Exception {
        assertEquals(20, count("select count(*) from post"));
        assertEquals(40, count("select count(*) from post_comment"));
        assertEquals(15, count("select count(*) from bookmark where is_active=true"));
        assertEquals(6, count("select count(*) from chat_message"));
        assertEquals(0, count("select count(*) from post p where p.comment_count <> (select count(*) from post_comment c where c.post_id=p.id and c.deleted_at is null)"));
        assertEquals(0, count("select count(*) from chat_message cm where not exists (select 1 from chat_participant cp where cp.chat_room_id=cm.chat_room_id and cp.member_id=cm.sender_id)"));
        for (int team = 1; team <= 5; team++) {
            String token = login("customer" + (team * 10 + 1) + "@example.com");
            mockMvc.perform(get("/api/members/me/bookmarks").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2));
        }
        for (String type : new String[]{"QUESTION", "TIP", "RECRUIT", "PROMOTION"}) {
            mockMvc.perform(get("/api/public/posts").param("boardType", type))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(5));
        }
        for (Long postId : jdbcTemplate.queryForList("select id from post", Long.class)) {
            mockMvc.perform(get("/api/public/posts/" + postId)).andExpect(status().isOk());
            mockMvc.perform(get("/api/public/posts/" + postId + "/comments"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2));
        }
        for (Map<String, Object> room : jdbcTemplate.queryForList("select cr.id, m.email from chat_room cr join quote q on cr.quote_id=q.id join service_request sr on q.service_request_id=sr.id join member m on sr.member_id=m.id")) {
            mockMvc.perform(get("/api/chat-rooms/" + room.get("id") + "/messages")
                            .header("Authorization", "Bearer " + login(room.get("email").toString())))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void 신규_크레딧_상품은_기존_시드_ID와_겹치지_않는다() {
        jdbcTemplate.update("insert into credit_product (name, price, credit_amount, bonus_credit, is_active, created_at) values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                "신규 상품 검증", 1000, 1, 0, true);
        assertEquals(5L, jdbcTemplate.queryForObject("select id from credit_product where name = ?", Long.class, "신규 상품 검증"));
    }

    @Test
    void 기존_6개와_팀원별_30개_탈퇴용_5개가_중복없이_생성된다() {
        assertEquals(41, count("select count(*) from member"));
        for (String role : new String[]{"USER", "EXPERT", "ADMIN"}) {
            assertEquals(Map.of("USER", 17, "EXPERT", 13, "ADMIN", 11).get(role),
                    count("select count(*) from member where role = ?", role));
        }
        assertEquals(41, count("select count(distinct nickname) from member"));
        assertEquals(41, count("select count(distinct phone) from member"));
        for (Map<String, Object> row : jdbcTemplate.queryForList("select password, phone from member")) {
            String hash = row.get("password").toString();
            assertTrue(hash.startsWith("$2a$"));
            assertTrue(passwordEncoder.matches("1234", hash));
            assertTrue(row.get("phone").toString().matches("[0-9]{11}"));
        }
    }

    @Test
    void 모든_계정이_로그인하고_역할에_맞는_API에_접근한다() throws Exception {
        for (String prefix : new String[]{"customer", "expert", "admin"}) {
            String role = switch (prefix) { case "customer" -> "USER"; case "expert" -> "EXPERT"; default -> "ADMIN"; };
            List<Integer> numbers = new ArrayList<>(TEAM_NUMBERS);
            numbers.add(1);
            if (!prefix.equals("admin")) numbers.add(2);
            if (prefix.equals("expert")) numbers.add(3);
            if (prefix.equals("customer")) numbers.addAll(WITHDRAW_NUMBERS);
            for (int number : numbers) {
                String token = login(prefix + number + "@example.com");
                mockMvc.perform(get("/api/members/me").header("Authorization", "Bearer " + token))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.role").value(role));
                mockMvc.perform(get("/api/admin/members").header("Authorization", "Bearer " + token))
                        .andExpect(status().is(role.equals("ADMIN") ? 200 : 403));
                mockMvc.perform(get("/api/members/me/requests").header("Authorization", "Bearer " + token))
                        .andExpect(status().is(role.equals("USER") ? 200 : 403));
                int expertStatus = role.equals("EXPERT") ? (number == 3 ? 404 : 200) : 403;
                mockMvc.perform(get("/api/members/me/expert-profile/requests").header("Authorization", "Bearer " + token))
                        .andExpect(status().is(expertStatus));
            }
        }
    }

    @Test
    void 신규_고수의_프로필과_시드예약을_유지하며_추가_견적을_발송한다() throws Exception {
        int[] regionIds = {39, 69, 85, 94, 105};
        for (int team = 1; team <= 5; team++) {
            jdbcTemplate.update("insert into service_request(member_id, second_category_id, second_region_id, request_status, created_at) select id,101,?,'OPEN',CURRENT_TIMESTAMP from member where email=?",
                    regionIds[team - 1], "customer" + (team * 10 + 2) + "@example.com");
        }
        for (int number : TEAM_NUMBERS) {
            String email = "expert" + number + "@example.com";
            String token = login(email);
            Long profileId = jdbcTemplate.queryForObject("select ep.id from expert_profile ep join member m on ep.member_id=m.id where m.email=?", Long.class, email);
            mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.services.length()").value(2))
                    .andExpect(jsonPath("$.primaryCategory.id").value(number % 10 == 1 ? 101 : 139))
                    .andExpect(jsonPath("$.nationwide").value(false))
                    .andExpect(jsonPath("$.wholeRegions.length()").value(number % 10 == 1 ? 0 : 1))
                    .andExpect(jsonPath("$.regions.length()").value(number % 10 == 1 ? 1 : 0));
            long balance = List.of(22,32,52).contains(number) ? 18 : 20;
            long reserved = Map.of(11,0,12,2,21,2,22,0,31,4,32,2,41,2,42,0,51,4,52,0).get(number);
            mockMvc.perform(get("/api/members/me/credit").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(balance))
                    .andExpect(jsonPath("$.reservedCredit").value(reserved));
            Long requestId = jdbcTemplate.queryForObject("select sr.id from service_request sr join member m on sr.member_id=m.id where m.email=? and sr.second_category_id=101",
                    Long.class, "customer" + (number / 10 * 10 + 2) + "@example.com");
            mockMvc.perform(post("/api/requests/" + requestId + "/quotes").header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content("{\"description\":\"시드 계정 견적 검증\",\"price\":10000}"))
                    .andExpect(status().isCreated());
            mockMvc.perform(get("/api/members/me/credit").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.reservedCredit").value(reserved + 2))
                    .andExpect(jsonPath("$.availableCredit").value(balance - reserved - 2));
        }
    }

    @Test
    void 기존_회원_ID와_프로필_요청_견적_지갑_연결을_유지한다() {
        String[] emails = {"customer1", "customer2", "expert1", "expert2", "admin1", "expert3"};
        String[] roles = {"USER", "USER", "EXPERT", "EXPERT", "ADMIN", "EXPERT"};
        for (int i = 0; i < emails.length; i++) {
            assertEquals(emails[i] + "@example.com", jdbcTemplate.queryForObject("select email from member where id=?", String.class, i + 1));
            assertEquals(roles[i], jdbcTemplate.queryForObject("select role from member where id=?", String.class, i + 1));
        }
        assertEquals(3L, jdbcTemplate.queryForObject("select member_id from expert_profile where id=1", Long.class));
        assertEquals(4L, jdbcTemplate.queryForObject("select member_id from expert_profile where id=2", Long.class));
        assertEquals(0, count("select count(*) from expert_profile where member_id=6"));
        assertEquals(10, count("select count(*) from service_request where id<=10"));
        assertEquals(3, count("select count(*) from quote where id<=3"));
        assertEquals(3, count("select count(*) from credit_reservation where quote_id<=3"));
        assertEquals(10L, jdbcTemplate.queryForObject("select balance from credit_wallet where id=1 and member_id=3", Long.class));
        assertEquals(2L, jdbcTemplate.queryForObject("select balance from credit_wallet where id=2 and member_id=4", Long.class));
        assertEquals(1, count("select count(*) from quote where expert_id=1 and service_request_id=1"));
        assertEquals(1, count("select count(*) from quote where expert_id=1 and service_request_id=10"));
        assertEquals(1, count("select count(*) from quote where expert_id=2 and service_request_id=4"));
        assertEquals(0, count("select count(*) from quote q left join expert_profile ep on ep.id=q.expert_id left join service_request sr on sr.id=q.service_request_id where ep.id is null or sr.id is null"));
        assertEquals(3, count("select count(*) from credit_reservation cr join quote q on cr.quote_id=q.id join expert_profile ep on q.expert_id=ep.id join credit_wallet w on cr.credit_wallet_id=w.id and w.member_id=ep.member_id where q.id<=3"));
    }

    @Test
    void 탈퇴_전용_5개는_초기에는_로그인되고_탈퇴하면_인증이_차단된다() throws Exception {
        for (int number : WITHDRAW_NUMBERS) {
            String email = "customer" + number + "@example.com";
            String token = login(email);
            Long memberId = jdbcTemplate.queryForObject("select id from member where email=?", Long.class, email);
            mockMvc.perform(delete("/api/members/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get("/api/members/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
            assertEquals(1, count("select count(*) from member where id=? and deleted_at is not null and phone=?",
                    memberId, "deleted_" + memberId));
        }
    }

    @Test
    void 고객_요청과_고수별_받은목록_견적상태가_다르고_의뢰요청_연결이_일치한다() throws Exception {
        Set<Set<Long>> allLists = new HashSet<>();
        Map<Integer,Integer> quoteCounts = Map.of(11,0,12,1,21,1,22,1,31,2,32,2,41,1,42,0,51,2,52,1);
        for (int number : TEAM_NUMBERS) {
            String token = login("expert" + number + "@example.com");
            JsonNode matched = objectMapper.readTree(mockMvc.perform(get("/api/members/me/expert-profile/requests")
                    .header("Authorization", "Bearer " + token)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            Set<Long> ids = new HashSet<>();
            int sent = 0;
            for (JsonNode item : matched) {
                ids.add(item.get("requestId").asLong());
                if (!item.get("sentQuoteId").isNull()) sent++;
            }
            assertTrue(ids.size() >= 2);
            assertEquals(2, ids.stream().filter(id -> count("select count(*) from service_request sr join member m on sr.member_id=m.id where sr.id=? and m.email in (?, ?)", id, "customer" + (number / 10 * 10 + 1) + "@example.com", "customer" + (number / 10 * 10 + 2) + "@example.com") == 1).count());
            assertTrue(allLists.add(ids), "고수마다 받은 요청 목록이 달라야 합니다.");
            assertEquals(quoteCounts.get(number), sent);
            JsonNode quotes = objectMapper.readTree(mockMvc.perform(get("/api/quotes").header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            assertEquals(quoteCounts.get(number), quotes.size());
            for (JsonNode quote : quotes) {
                assertEquals(List.of(22,32,52).contains(number) && quote.get("secondCategoryName").asString().equals("개발 외주") ? "REQUESTED" : "SENT",
                        quote.get("quoteStatus").asString());
            }
            String customerToken = login("customer" + number + "@example.com");
            JsonNode mine = objectMapper.readTree(mockMvc.perform(get("/api/members/me/requests").header("Authorization", "Bearer " + customerToken))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            assertEquals(number % 10 == 1 ? 1 : 2, mine.size());
            for (JsonNode request : mine) {
                mockMvc.perform(get("/api/requests/" + request.get("requestId").asLong()).header("Authorization", "Bearer " + customerToken))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.answers.length()").value(4));
            }
        }
        assertEquals(3, count("select count(*) from quote q join credit_reservation cr on cr.quote_id=q.id join credit_history h on h.quote_id=q.id join chat_room room on room.quote_id=q.id join service_request sr on q.service_request_id=sr.id where q.quote_status='REQUESTED' and cr.reservation_status='USED' and sr.request_status='MATCHED' and h.history_type='USE' and h.amount=-2"));
        assertEquals(6, count("select count(*) from chat_participant"));
        assertEquals(0, count("select count(*) from quote q join credit_reservation cr on cr.quote_id=q.id where q.id>3 and ((q.quote_status='SENT' and cr.reservation_status<>'RESERVED') or (q.quote_status='REQUESTED' and cr.reservation_status<>'USED'))"));
    }

    private String login(String email) throws Exception {
        String result = mockMvc.perform(post("/api/public/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", "1234"))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(result).get("accessToken").asString();
    }

    private int count(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, Integer.class, args);
    }
}
