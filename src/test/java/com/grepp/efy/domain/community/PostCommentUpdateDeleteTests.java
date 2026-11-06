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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostCommentUpdateDeleteTests {
    private static final LocalDateTime OLD_TIME = LocalDateTime.of(2020, 1, 1, 0, 0);
    private static final String NOT_FOUND_MESSAGE = "존재하지 않는 댓글입니다.";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();
    private Long writer;
    private Long commenter;
    private Long other;
    private Long admin;
    private Long postId;

    @BeforeEach
    void setUp() {
        writer = member("USER");
        commenter = member("USER");
        other = member("USER");
        admin = member("ADMIN");
        postId = newPost(writer);
    }

    // 댓글이 다른 회원의 글을 가리키므로 댓글부터 모두 지운 뒤 글·파일·회원을 지운다
    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post_comment_images where comment_id in (select id from post_comment where member_id = ?)", id);
            jdbcTemplate.update("delete from post_comment where member_id = ?", id);
        }
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post where member_id = ?", id);
            jdbcTemplate.update("delete from file where uploader_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    // ---------- 수정 ----------

    @Test
    void 작성자가_수정하면_내용과_이미지와_updatedAt이_바뀌고_commentCount는_그대로다() throws Exception {
        Long commentId = comment(commenter);
        Long image = file(commenter);

        updateComment(commenter, commentId, "{\"content\": \"  고친 내용입니다  \", \"imageIds\": [" + image + "]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentId.intValue()))
                .andExpect(jsonPath("$.content").value("고친 내용입니다"))
                .andExpect(jsonPath("$.images[0].fileId").value(image.intValue()));

        assertNotEquals(Timestamp.valueOf(OLD_TIME), updatedAt(commentId));
        assertEquals(1, commentCount());
    }

    @Test
    void 바뀐_게_없으면_updatedAt을_갱신하지_않고_imageIds를_빼면_이미지가_비워진다() throws Exception {
        Long commentId = comment(commenter);
        Long image = file(commenter);
        jdbcTemplate.update("insert into post_comment_images(comment_id, file_id, sort_order) values (?, ?, 0)", commentId, image);

        // 공백만 다른 같은 내용 + 같은 이미지
        updateComment(commenter, commentId, "{\"content\": \"  원래 내용입니다 \", \"imageIds\": [" + image + "]}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images.length()").value(1));
        assertEquals(Timestamp.valueOf(OLD_TIME), updatedAt(commentId));

        // 전체 교체: imageIds 생략 = 이미지 삭제
        updateComment(commenter, commentId, "{\"content\": \"원래 내용입니다\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.images").isEmpty());
        assertNotEquals(Timestamp.valueOf(OLD_TIME), updatedAt(commentId));
    }

    @Test
    void 작성자가_아니면_관리자도_수정할_수_없다() throws Exception {
        Long commentId = comment(commenter);

        updateComment(other, commentId, "{\"content\": \"남의 댓글\"}").andExpect(status().isForbidden());
        updateComment(admin, commentId, "{\"content\": \"관리자 수정\"}").andExpect(status().isForbidden());
    }

    @Test
    void 부모_글이_숨김이거나_삭제되었거나_댓글이_없으면_수정은_404다() throws Exception {
        Long commentId = comment(commenter);
        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);

        // 숨김 글은 작성자도 404 (숨김 여부 노출 방지)
        updateComment(commenter, commentId, "{\"content\": \"고친 내용\"}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));

        jdbcTemplate.update("update post set is_visible = true, deleted_at = CURRENT_TIMESTAMP where id = ?", postId);
        updateComment(commenter, commentId, "{\"content\": \"고친 내용\"}").andExpect(status().isNotFound());

        Long deleted = comment(commenter);
        jdbcTemplate.update("update post set deleted_at = null where id = ?", postId);
        jdbcTemplate.update("update post_comment set deleted_at = CURRENT_TIMESTAMP where id = ?", deleted);
        updateComment(commenter, deleted, "{\"content\": \"고친 내용\"}").andExpect(status().isNotFound());

        updateComment(commenter, 999999999L, "{\"content\": \"고친 내용\"}").andExpect(status().isNotFound());
    }

    @Test
    void 수정_요청이_잘못되면_400_토큰이_없으면_401이다() throws Exception {
        Long commentId = comment(commenter);
        String inputMessage = "입력값이 올바르지 않습니다.";

        updateComment(commenter, commentId, "{}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        updateComment(commenter, commentId, "{\"content\": \"" + "a".repeat(401) + "\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        updateComment(commenter, commentId, "{\"content\": \"두 장\", \"imageIds\": [" + file(commenter) + ", " + file(commenter) + "]}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        updateComment(commenter, commentId, "{\"content\": \"남의 사진\", \"imageIds\": [" + file(other) + "]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용할 수 없는 이미지가 포함되어 있습니다."));
        updateComment(commenter, 0L, "{\"content\": \"고친 내용\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));

        mockMvc.perform(put(commentUrl(commentId)).contentType(MediaType.APPLICATION_JSON).content("{\"content\": \"고친 내용\"}"))
                .andExpect(status().isUnauthorized());

        assertEquals(Timestamp.valueOf(OLD_TIME), updatedAt(commentId));
    }

    // ---------- 삭제 ----------

    @Test
    void 작성자가_삭제하면_204이고_commentCount가_줄고_목록에서_빠진다() throws Exception {
        Long commentId = comment(commenter);
        comment(other);

        deleteComment(commenter, commentId).andExpect(status().isNoContent());

        assertNotNull(jdbcTemplate.queryForObject("select deleted_at from post_comment where id = ?", Timestamp.class, commentId));
        assertEquals(1, commentCount());
        mockMvc.perform(get("/api/public/posts/" + postId + "/comments"))
                .andExpect(jsonPath("$.totalElements").value(1));

        // 이미 삭제된 댓글
        deleteComment(commenter, commentId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(NOT_FOUND_MESSAGE));
        assertEquals(1, commentCount());
    }

    @Test
    void 관리자는_숨김_글의_남의_댓글도_삭제할_수_있다() throws Exception {
        Long commentId = comment(commenter);
        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);

        deleteComment(admin, commentId).andExpect(status().isNoContent());
        assertEquals(0, commentCount());
    }

    @Test
    void 남의_댓글은_403_숨김_글은_작성자도_404_부모_글이_삭제되면_관리자도_404다() throws Exception {
        Long commentId = comment(commenter);

        deleteComment(other, commentId).andExpect(status().isForbidden());

        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);
        deleteComment(commenter, commentId).andExpect(status().isNotFound());

        jdbcTemplate.update("update post set is_visible = true, deleted_at = CURRENT_TIMESTAMP where id = ?", postId);
        deleteComment(admin, commentId).andExpect(status().isNotFound());

        // 실패한 삭제는 댓글도 개수도 그대로
        assertNull(jdbcTemplate.queryForObject("select deleted_at from post_comment where id = ?", Timestamp.class, commentId));
        assertEquals(1, commentCount());
    }

    @Test
    void commentCount는_0_미만으로_내려가지_않는다() throws Exception {
        Long commentId = comment(commenter);
        jdbcTemplate.update("update post set comment_count = 0 where id = ?", postId);

        deleteComment(commenter, commentId).andExpect(status().isNoContent());
        assertEquals(0, commentCount());
    }

    @Test
    void 삭제_요청이_잘못되면_400_토큰이_없으면_401이다() throws Exception {
        deleteComment(commenter, 0L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(delete("/api/comments/abc").header("Authorization", "Bearer " + token(commenter)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        mockMvc.perform(delete(commentUrl(comment(commenter)))).andExpect(status().isUnauthorized());
    }

    private ResultActions updateComment(Long memberId, Long commentId, String body) throws Exception {
        return mockMvc.perform(put(commentUrl(commentId))
                .header("Authorization", "Bearer " + token(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private ResultActions deleteComment(Long memberId, Long commentId) throws Exception {
        return mockMvc.perform(delete(commentUrl(commentId)).header("Authorization", "Bearer " + token(memberId)));
    }

    private static String commentUrl(Long commentId) {
        return "/api/comments/" + commentId;
    }

    private long commentCount() {
        return jdbcTemplate.queryForObject("select comment_count from post where id = ?", Long.class, postId);
    }

    private Timestamp updatedAt(Long commentId) {
        return jdbcTemplate.queryForObject("select updated_at from post_comment where id = ?", Timestamp.class, commentId);
    }

    // post()로 지으면 static import한 MockMvc post()와 헷갈리므로 newPost
    private Long newPost(Long memberId) {
        String title = UUID.randomUUID().toString().substring(0, 20);
        Timestamp time = Timestamp.valueOf(OLD_TIME);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, 'QUESTION', ?, '내용입니다.', 0, ?, ?, true)
                """, memberId, title, time, time);
        return jdbcTemplate.queryForObject("select id from post where title = ?", Long.class, title);
    }

    // API로 작성한 것처럼 commentCount도 1 올린다. updated_at은 과거로 넣어 두고 바뀌는지 확인
    // delete()로 지으면 static import한 MockMvc delete()가 가려지므로 comment
    private Long comment(Long memberId) {
        Timestamp time = Timestamp.valueOf(OLD_TIME);
        String marker = UUID.randomUUID().toString();
        jdbcTemplate.update("""
                insert into post_comment(post_id, member_id, content, created_at, updated_at)
                values (?, ?, ?, ?, ?)
                """, postId, memberId, marker, time, time);
        Long id = jdbcTemplate.queryForObject("select id from post_comment where content = ?", Long.class, marker);
        jdbcTemplate.update("update post_comment set content = '원래 내용입니다' where id = ?", id);
        jdbcTemplate.update("update post set comment_count = comment_count + 1 where id = ?", postId);
        return id;
    }

    private Long file(Long uploaderId) {
        String path = "test/" + UUID.randomUUID();
        jdbcTemplate.update("""
                insert into file(uploader_id, storage_path, original_name, content_type, size, visibility, created_at)
                values (?, ?, 'a.png', 'image/png', 1, 'PUBLIC', CURRENT_TIMESTAMP)
                """, uploaderId, path);
        return jdbcTemplate.queryForObject("select id from file where storage_path = ?", Long.class, path);
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
