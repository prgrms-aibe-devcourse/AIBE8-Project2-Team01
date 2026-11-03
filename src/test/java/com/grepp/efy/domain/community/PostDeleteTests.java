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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostDeleteTests {
    private static final String URL = "/api/posts/";
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
    void 작성자가_삭제하면_204이고_이미지_연결은_남는다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        attachImage(postId, file(writer));

        deletePost(writer, postId).andExpect(status().isNoContent());

        assertNotNull(deletedAt(postId));
        assertEquals(1, imageCount(postId));
    }

    @Test
    void 삭제된_글은_조회_수정_삭제_모두_404다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", postId);

        mockMvc.perform(get("/api/public/posts/" + postId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시물입니다."));
        mockMvc.perform(put(URL + postId)
                        .header("Authorization", "Bearer " + token(writer))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "수정된 제목", "content": "수정된 본문"}
                                """))
                .andExpect(status().isNotFound());
        deletePost(writer, postId).andExpect(status().isNotFound());
        deletePost(member("ADMIN"), postId).andExpect(status().isNotFound());
    }

    @Test
    void 관리자는_다른사람의_글과_숨김글을_삭제할수있다() throws Exception {
        Long admin = member("ADMIN");
        Long writer = member("USER");
        Long visibleId = post(writer, "QUESTION");
        Long hiddenId = post(writer, "QUESTION");
        jdbcTemplate.update("update post set is_visible = false where id = ?", hiddenId);

        deletePost(admin, visibleId).andExpect(status().isNoContent());
        deletePost(admin, hiddenId).andExpect(status().isNoContent());

        assertNotNull(deletedAt(visibleId));
        assertNotNull(deletedAt(hiddenId));
    }

    @Test
    void 숨김글은_작성자도_404다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "QUESTION");
        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);

        deletePost(writer, postId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시물입니다."));
        // 숨김 글은 타인에게도 403이 아닌 404
        deletePost(member("USER"), postId).andExpect(status().isNotFound());

        assertNull(deletedAt(postId));
    }

    @Test
    void 작성자가_아니면_403이다() throws Exception {
        Long postId = post(member("USER"), "QUESTION");

        deletePost(member("USER"), postId)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));

        assertNull(deletedAt(postId));
    }

    @Test
    void 고수가_아닌_작성자는_TIP_글을_삭제할수없고_관리자는_가능하다() throws Exception {
        Long writer = member("USER");
        Long postId = post(writer, "TIP");

        deletePost(writer, postId).andExpect(status().isForbidden());
        assertNull(deletedAt(postId));

        deletePost(member("ADMIN"), postId).andExpect(status().isNoContent());
        assertNotNull(deletedAt(postId));
    }

    @Test
    void 잘못된_postId는_400_토큰이_없으면_401이다() throws Exception {
        Long writer = member("USER");

        deletePost(writer, 0L).andExpect(status().isBadRequest());
        mockMvc.perform(delete(URL + post(writer, "QUESTION"))).andExpect(status().isUnauthorized());
    }

    private ResultActions deletePost(Long memberId, Long postId) throws Exception {
        return mockMvc.perform(delete(URL + postId)
                .header("Authorization", "Bearer " + token(memberId)));
    }

    private Long post(Long memberId, String boardType) {
        String title = UUID.randomUUID().toString().substring(0, 20);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, ?, ?, '내용입니다.', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, true)
                """, memberId, boardType, title);
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

    private void attachImage(Long postId, Long fileId) {
        jdbcTemplate.update("insert into post_images(post_id, file_id, sort_order) values (?, ?, 0)", postId, fileId);
    }

    private int imageCount(Long postId) {
        return jdbcTemplate.queryForObject("select count(*) from post_images where post_id = ?", Integer.class, postId);
    }

    private String deletedAt(Long postId) {
        return jdbcTemplate.queryForObject("select deleted_at from post where id = ?", String.class, postId);
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
