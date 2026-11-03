package com.grepp.efy.domain.community;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostVisibilityTests {
    private static final LocalDateTime OLD_TIME = LocalDateTime.of(2020, 1, 1, 0, 0);
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();
    private Long admin;
    private Long writer;

    @BeforeEach
    void setUp() {
        admin = member("ADMIN");
        writer = member("USER");
    }

    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post_visibility_history where admin_id = ?", id);
            jdbcTemplate.update("delete from post where member_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    @Test
    void 비공개로_바꾸면_이력을_저장하고_updatedAt은_그대로다() throws Exception {
        Long postId = post();

        changeVisibility(admin, postId, "{\"isVisible\": false, \"reason\": \"  광고성 게시글  \"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId.intValue()))
                .andExpect(jsonPath("$.isVisible").value(false));

        Map<String, Object> row = jdbcTemplate.queryForMap("select is_visible, updated_at from post where id = ?", postId);
        assertFalse((Boolean) row.get("is_visible"));
        assertEquals(Timestamp.valueOf(OLD_TIME), row.get("updated_at"));

        Map<String, Object> history = jdbcTemplate.queryForMap(
                "select admin_id, is_visible, reason from post_visibility_history where post_id = ?", postId);
        assertEquals(admin, ((Number) history.get("admin_id")).longValue());
        assertFalse((Boolean) history.get("is_visible"));
        assertEquals("광고성 게시글", history.get("reason"));
    }

    @Test
    void 같은_상태로_요청하면_200이고_이력을_저장하지_않는다() throws Exception {
        Long postId = post();

        changeVisibility(admin, postId, "{\"isVisible\": true}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isVisible").value(true));

        assertEquals(0, historyCount(postId));
    }

    @Test
    void 이력은_최신순이고_사유가_없으면_null이다() throws Exception {
        Long postId = post();
        changeVisibility(admin, postId, "{\"isVisible\": false, \"reason\": \"광고성 게시글\"}");
        changeVisibility(admin, postId, "{\"isVisible\": true}");

        mockMvc.perform(get(historyUrl(postId)).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId.intValue()))
                .andExpect(jsonPath("$.histories.length()").value(2))
                .andExpect(jsonPath("$.histories[0].isVisible").value(true))
                .andExpect(jsonPath("$.histories[0].reason").isEmpty())
                .andExpect(jsonPath("$.histories[0].adminId").value(admin.intValue()))
                .andExpect(jsonPath("$.histories[0].adminNickname").isNotEmpty())
                .andExpect(jsonPath("$.histories[1].isVisible").value(false))
                .andExpect(jsonPath("$.histories[1].reason").value("광고성 게시글"));
    }

    @Test
    void 이력이_없으면_빈_배열이다() throws Exception {
        mockMvc.perform(get(historyUrl(post())).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories").isEmpty());
    }

    @Test
    void 삭제된_글은_바꿀_수_없지만_이력은_조회된다() throws Exception {
        Long postId = post();
        changeVisibility(admin, postId, "{\"isVisible\": false}");
        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", postId);

        changeVisibility(admin, postId, "{\"isVisible\": true}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시물입니다."));

        mockMvc.perform(get(historyUrl(postId)).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories.length()").value(1));
    }

    @Test
    void 없는_글은_404다() throws Exception {
        changeVisibility(admin, 999999999L, "{\"isVisible\": false}").andExpect(status().isNotFound());
        mockMvc.perform(get(historyUrl(999999999L)).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 토큰이_없으면_401_관리자가_아니면_403이다() throws Exception {
        Long postId = post();

        mockMvc.perform(patch(visibilityUrl(postId)).contentType(MediaType.APPLICATION_JSON).content("{\"isVisible\": false}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get(historyUrl(postId))).andExpect(status().isUnauthorized());

        changeVisibility(writer, postId, "{\"isVisible\": false}").andExpect(status().isForbidden());
        mockMvc.perform(get(historyUrl(postId)).header("Authorization", "Bearer " + token(writer)))
                .andExpect(status().isForbidden());
    }

    @Test
    void 잘못된_요청은_400이다() throws Exception {
        Long postId = post();
        String inputMessage = "입력값이 올바르지 않습니다.";

        changeVisibility(admin, 0L, "{\"isVisible\": false}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        changeVisibility(admin, postId, "{}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        changeVisibility(admin, postId, "{\"isVisible\": false, \"reason\": \"   \"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        changeVisibility(admin, postId, "{\"isVisible\": false, \"reason\": \"" + "a".repeat(201) + "\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        changeVisibility(admin, postId, "{\"isVisible\": \"abc\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."));
        mockMvc.perform(get(historyUrl(0L)).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));

        // 400 요청은 상태도 이력도 바꾸지 않는다
        assertEquals(0, historyCount(postId));
    }

    private ResultActions changeVisibility(Long memberId, Long postId, String body) throws Exception {
        return mockMvc.perform(patch(visibilityUrl(postId))
                .header("Authorization", "Bearer " + token(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static String visibilityUrl(Long postId) {
        return "/api/admin/posts/" + postId + "/visibility";
    }

    private static String historyUrl(Long postId) {
        return "/api/admin/posts/" + postId + "/visibility-history";
    }

    private int historyCount(Long postId) {
        return jdbcTemplate.queryForObject("select count(*) from post_visibility_history where post_id = ?", Integer.class, postId);
    }

    // updated_at을 과거로 넣어 두고 바뀌는지 확인한다
    private Long post() {
        String title = UUID.randomUUID().toString().substring(0, 20);
        Timestamp time = Timestamp.valueOf(OLD_TIME);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, 'QUESTION', ?, '내용입니다.', 0, ?, ?, true)
                """, writer, title, time, time);
        return jdbcTemplate.queryForObject("select id from post where title = ?", Long.class, title);
    }

    // member(), token()은 MemberImageTests에서 그대로 복사
    private Long member(String role) {
        String suffix = UUID.randomUUID().toString();
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String email = suffix + "@test.local";
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)",
                email, suffix.substring(0, 20), String.format("%011d", nextId), role);
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }
}
