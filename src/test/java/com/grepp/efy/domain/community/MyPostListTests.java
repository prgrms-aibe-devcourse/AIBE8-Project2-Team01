package com.grepp.efy.domain.community;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
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

// 회원을 테스트마다 새로 만들므로 다른 데이터와 섞이지 않는다
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class MyPostListTests {
    private static final String URL = "/api/members/me/posts";
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post where member_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    @Test
    void 본인_글만_최신순으로_20개씩_반환한다() throws Exception {
        Long me = member("USER");
        Long other = member("USER");
        for (int i = 0; i < 21; i++) {
            post(me, "QUESTION", "mine-" + i, BASE_TIME.plusMinutes(i));
        }
        post(other, "QUESTION", "other", BASE_TIME.plusDays(1));

        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(me)).param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.content[0].title").value("mine-20"))
                .andExpect(jsonPath("$.content[19].title").value("mine-1"))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(21))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(me)).param("pageNumber", "2"))
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value("mine-0"));

        // 마지막 페이지를 넘어서면 빈 목록과 200
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(me)).param("pageNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(21));
    }

    @Test
    void 숨김글과_삭제글은_제외하고_boardType을_생략하면_전체_게시판이다() throws Exception {
        Long me = member("EXPERT");
        Long question = post(me, "QUESTION", "question", BASE_TIME.plusMinutes(1));
        Long tip = post(me, "TIP", "tip", BASE_TIME);
        Long hidden = post(me, "QUESTION", "hidden", BASE_TIME);
        Long deleted = post(me, "QUESTION", "deleted", BASE_TIME);
        jdbcTemplate.update("update post set is_visible = false where id = ?", hidden);
        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", deleted);

        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(me)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].postId").value(question.intValue()))
                .andExpect(jsonPath("$.content[1].postId").value(tip.intValue()));

        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(me)).param("boardType", "TIP"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].postId").value(tip.intValue()));
    }

    @Test
    void 토큰이_없으면_401이다() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
    }

    @Test
    void 잘못된_요청은_400이다() throws Exception {
        String auth = "Bearer " + token(member("USER"));

        mockMvc.perform(get(URL).header("Authorization", auth).param("boardType", "XYZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        mockMvc.perform(get(URL).header("Authorization", auth).param("pageNumber", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        mockMvc.perform(get(URL).header("Authorization", auth).param("pageNumber", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
    }

    private Long post(Long memberId, String boardType, String title, LocalDateTime createdAt) {
        Timestamp time = Timestamp.valueOf(createdAt);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, ?, ?, '내용입니다.', 0, ?, ?, true)
                """, memberId, boardType, title, time, time);
        return jdbcTemplate.queryForObject("select id from post where member_id = ? and title = ?", Long.class, memberId, title);
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
