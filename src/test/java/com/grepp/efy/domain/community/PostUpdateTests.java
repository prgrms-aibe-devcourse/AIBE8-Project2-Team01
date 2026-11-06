package com.grepp.efy.domain.community;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostUpdateTests {
    private static final String URL = "/api/posts/";
    private static final String OLD_UPDATED_AT = "2020-01-01 00:00:00";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post_images where post_id in (select id from post where member_id = ?)", id);
            jdbcTemplate.update("delete from post where member_id = ?", id);
            jdbcTemplate.update("delete from file where uploader_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    @Test
    void 작성자가_수정하면_요청에서_빠진_필드는_비워진다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        attachImages(postId, file(writer), file(writer));

        update(writer, postId, """
                {"title": "  수정된 제목  ", "content": "수정된 본문"}
                """)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.content").value("수정된 본문"))
                .andExpect(jsonPath("$.boardType").value("QUESTION"))
                .andExpect(jsonPath("$.images").isEmpty());

        assertEquals(0, imageCount(postId));
        assertNotEquals(OLD_UPDATED_AT, updatedAt(postId));
    }

    @Test
    void 변경이_없으면_updatedAt이_그대로다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        String title = jdbcTemplate.queryForObject("select title from post where id = ?", String.class, postId);

        update(writer, postId, """
                {"title": "%s", "content": "내용입니다."}
                """.formatted(title))
                .andExpect(status().isOk());

        assertEquals(OLD_UPDATED_AT, updatedAt(postId));
    }

    @Test
    void 이미지_순서만_바꿔도_수정된다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        Long first = file(writer);
        Long second = file(writer);
        attachImages(postId, first, second);

        update(writer, postId, body(postId, second, first))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images[0].fileId").value(second.intValue()))
                .andExpect(jsonPath("$.images[1].fileId").value(first.intValue()));

        assertNotEquals(OLD_UPDATED_AT, updatedAt(postId));
    }

    @Test
    void 없는글과_숨김글은_404다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);

        update(writer, Long.MAX_VALUE, body(postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시물입니다."));
        // 숨김 글은 작성자 본인도 수정 불가
        update(writer, postId, body(postId)).andExpect(status().isNotFound());
    }

    @Test
    void 작성자가_아니면_관리자도_403이다() throws Exception {
        Long postId = post(member("USER"), "QUESTION");

        update(member("USER"), postId, body(postId)).andExpect(status().isForbidden());
        update(member("ADMIN"), postId, body(postId)).andExpect(status().isForbidden());
    }

    @Test
    void 고수가_아닌_작성자는_TIP_글을_수정할수없다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "TIP");

        update(writer, postId, body(postId, file(writer))).andExpect(status().isForbidden());
    }

    @Test
    void 잘못된_입력은_400이다() throws Exception {
        Long expert = member("EXPERT");
        Long tipId = post(expert, "TIP");
        Long otherFile = file(member("USER"));

        // 제목 1자
        update(expert, tipId, """
                {"title": "a", "content": "내용입니다.", "imageIds": [%d]}
                """.formatted(file(expert)))
                .andExpect(status().isBadRequest());
        // 다른 회원이 올린 이미지
        update(expert, tipId, body(tipId, otherFile))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용할 수 없는 이미지가 포함되어 있습니다."));
        // TIP 글 이미지 0장
        update(expert, tipId, body(tipId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("팁 게시글에는 이미지를 1장 이상 첨부해 주세요."));
        // postId 1 미만
        update(expert, 0L, body(tipId)).andExpect(status().isBadRequest());
    }

    private ResultActions update(Long memberId, Long postId, String body) throws Exception {
        return mockMvc.perform(put(URL + postId)
                .header("Authorization", "Bearer " + token(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    // 기존 제목은 유지하고 imageIds만 지정한 요청 본문
    private String body(Long postId, Long... imageIds) {
        String title = jdbcTemplate.queryForObject("select title from post where id = ?", String.class, postId);
        String ids = String.join(",", Arrays.stream(imageIds).map(String::valueOf).toList());
        return """
                {"title": "%s", "content": "내용입니다.", "imageIds": [%s]}
                """.formatted(title, ids);
    }

    private Long post(Long memberId, String boardType) {
        String title = UUID.randomUUID().toString().substring(0, 20);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, ?, ?, '내용입니다.', 0, ?, ?, true)
                """, memberId, boardType, title, OLD_UPDATED_AT, OLD_UPDATED_AT);
        return jdbcTemplate.queryForObject("select id from post where title = ?", Long.class, title);
    }

    private Long file(Long uploaderId) {
        String path = "test/" + UUID.randomUUID();
        jdbcTemplate.update("""
                insert into file(uploader_id, storage_path, original_name, content_type, size, visibility, created_at)
                values (?, ?, 'a.png', 'image/png', 1, 'PUBLIC', CURRENT_TIMESTAMP)
                """, uploaderId, path);
        return jdbcTemplate.queryForObject("select id from file where storage_path = ?", Long.class, path);
    }

    private void attachImages(Long postId, Long... fileIds) {
        for (int i = 0; i < fileIds.length; i++) {
            jdbcTemplate.update("insert into post_images(post_id, file_id, sort_order) values (?, ?, ?)",
                    postId, fileIds[i], i);
        }
    }

    private int imageCount(Long postId) {
        return jdbcTemplate.queryForObject("select count(*) from post_images where post_id = ?", Integer.class, postId);
    }

    private String updatedAt(Long postId) {
        return jdbcTemplate.queryForObject("select updated_at from post where id = ?", String.class, postId);
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
