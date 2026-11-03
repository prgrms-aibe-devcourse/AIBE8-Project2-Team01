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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 다른 데이터와 섞이지 않도록 테스트마다 고유 keyword를 제목에 넣고 그 keyword로 조회한다
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class AdminPostListTests {
    private static final String URL = "/api/admin/posts";
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();
    private String adminAuth;

    @BeforeEach
    void setUp() {
        adminAuth = "Bearer " + token(member("ADMIN"));
    }

    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post where member_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    @Test
    void 숨김글을_포함하고_삭제글은_제외하며_isVisible로_고를_수_있다() throws Exception {
        Long writer = member("EXPERT");
        String keyword = keyword();
        Long visible = post(writer, "QUESTION", keyword + "-visible", BASE_TIME.plusMinutes(2));
        Long hidden = post(writer, "TIP", keyword + "-hidden", BASE_TIME.plusMinutes(1));
        Long deleted = post(writer, "QUESTION", keyword + "-deleted", BASE_TIME);
        jdbcTemplate.update("update post set is_visible = false where id = ?", hidden);
        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", deleted);

        // boardType·isVisible 생략 = 전체
        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].postId").value(visible.intValue()))
                .andExpect(jsonPath("$.content[0].isVisible").value(true))
                .andExpect(jsonPath("$.content[1].postId").value(hidden.intValue()))
                .andExpect(jsonPath("$.content[1].isVisible").value(false));

        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("isVisible", "false"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].postId").value(hidden.intValue()));

        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("boardType", "QUESTION"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].postId").value(visible.intValue()));
    }

    @Test
    void pageSize로_페이지_크기를_정한다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        for (int i = 0; i < 3; i++) {
            post(writer, "QUESTION", keyword + "-" + i, BASE_TIME.plusMinutes(i));
        }

        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("pageSize", "2"))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.pageSize").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));

        // 마지막 페이지를 넘어서면 빈 목록과 200
        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("pageNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void sortBy와_sortOrder로_정렬하고_값이_같으면_postId_내림차순이다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        Long a = post(writer, "QUESTION", keyword + "-a", BASE_TIME);
        Long b = post(writer, "QUESTION", keyword + "-b", BASE_TIME.plusMinutes(1));
        Long c = post(writer, "QUESTION", keyword + "-c", BASE_TIME.plusMinutes(2));
        jdbcTemplate.update("update post set view_count = 5 where id in (?, ?)", a, c);

        // 기본값: createdAt desc
        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword))
                .andExpect(jsonPath("$.content[0].postId").value(c.intValue()))
                .andExpect(jsonPath("$.content[2].postId").value(a.intValue()));

        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("sortOrder", "asc"))
                .andExpect(jsonPath("$.content[0].postId").value(a.intValue()));

        // viewCount 5인 a·c는 postId 내림차순
        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("sortBy", "viewCount"))
                .andExpect(jsonPath("$.content[0].postId").value(c.intValue()))
                .andExpect(jsonPath("$.content[1].postId").value(a.intValue()))
                .andExpect(jsonPath("$.content[2].postId").value(b.intValue()));

        // commentCount는 모두 0이라 postId 내림차순
        mockMvc.perform(get(URL).header("Authorization", adminAuth).param("keyword", keyword).param("sortBy", "commentCount"))
                .andExpect(jsonPath("$.content[0].postId").value(c.intValue()))
                .andExpect(jsonPath("$.content[2].postId").value(a.intValue()));
    }

    @Test
    void 토큰이_없으면_401_관리자가_아니면_403이다() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(member("USER"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void 잘못된_요청은_400이다() throws Exception {
        String typeMessage = "경로 또는 쿼리 값의 타입이 올바르지 않습니다.";
        String inputMessage = "입력값이 올바르지 않습니다.";

        for (String[] param : List.of(
                new String[]{"boardType", "XYZ"}, new String[]{"isVisible", "abc"},
                new String[]{"pageNumber", "abc"}, new String[]{"pageSize", "abc"})) {
            mockMvc.perform(get(URL).header("Authorization", adminAuth).param(param[0], param[1]))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(typeMessage));
        }
        for (String[] param : List.of(
                new String[]{"pageNumber", "0"}, new String[]{"pageSize", "0"}, new String[]{"pageSize", "101"},
                new String[]{"sortBy", "title"}, new String[]{"sortOrder", "DESC"},
                new String[]{"keyword", "   "}, new String[]{"firstCategoryId", "999999"})) {
            mockMvc.perform(get(URL).header("Authorization", adminAuth).param(param[0], param[1]))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        }
    }

    private static String keyword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private Long post(Long memberId, String boardType, String title, LocalDateTime createdAt) {
        Timestamp time = Timestamp.valueOf(createdAt);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, ?, ?, '내용입니다.', 0, ?, ?, true)
                """, memberId, boardType, title, time, time);
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
