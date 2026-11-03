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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostDetailTests {
    private static final String URL = "/api/public/posts/";
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
    void 숨김글은_관리자만_조회할수있다() throws Exception {
        Long writer = member("USER");
        Long admin = member("ADMIN");
        Long postId = post(writer);
        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);

        // 비로그인
        mockMvc.perform(get(URL + postId)).andExpect(status().isNotFound());
        // 작성자 본인
        mockMvc.perform(get(URL + postId).header("Authorization", "Bearer " + token(writer)))
                .andExpect(status().isNotFound());
        // 관리자
        mockMvc.perform(get(URL + postId).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isVisible").value(false));
    }

    @Test
    void 공개글은_비로그인도_조회하고_빈값규칙을_지킨다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer);

        mockMvc.perform(get(URL + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId.intValue()))
                .andExpect(jsonPath("$.memberId").value(writer.intValue()))
                .andExpect(jsonPath("$.images").isEmpty())
                .andExpect(jsonPath("$.memberProfileImageUrl").isEmpty())
                .andExpect(jsonPath("$.category").isEmpty())
                .andExpect(jsonPath("$.region").isEmpty())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.isVisible").value(true));
    }

    @Test
    void 조회할때마다_조회수가_오르고_수정시각은_그대로다() throws Exception {
        Long postId = post(member("USER"));
        String updatedAt = jdbcTemplate.queryForObject(
                "select updated_at from post where id = ?", String.class, postId);

        mockMvc.perform(get(URL + postId)).andExpect(jsonPath("$.viewCount").value(1));
        mockMvc.perform(get(URL + postId)).andExpect(jsonPath("$.viewCount").value(2));

        assertEquals(2L, jdbcTemplate.queryForObject(
                "select view_count from post where id = ?", Long.class, postId));
        assertEquals(updatedAt, jdbcTemplate.queryForObject(
                "select updated_at from post where id = ?", String.class, postId));
    }

    @Test
    void 없는글은_404_잘못된postId는_400이다() throws Exception {
        mockMvc.perform(get(URL + Long.MAX_VALUE)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시물입니다."));
        mockMvc.perform(get(URL + "0")).andExpect(status().isBadRequest());
        mockMvc.perform(get(URL + "-1")).andExpect(status().isBadRequest());
        mockMvc.perform(get(URL + "abc")).andExpect(status().isBadRequest());
    }

    private Long post(Long memberId) {
        String title = UUID.randomUUID().toString().substring(0, 20);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, 'QUESTION', ?, '내용입니다.', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true)
                """, memberId, title);
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