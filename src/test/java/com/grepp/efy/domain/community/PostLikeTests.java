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
import org.springframework.test.web.servlet.ResultActions;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostLikeTests {
    private static final String POST_NOT_FOUND = "존재하지 않는 게시물입니다.";
    private static final String COMMENT_NOT_FOUND = "존재하지 않는 댓글입니다.";
    private static final String LIKE_NOT_FOUND = "좋아요한 기록이 없습니다.";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();
    private Long writer;
    private Long liker;
    private Long other;
    private Long admin;
    private Long postId;

    @BeforeEach
    void setUp() {
        writer = member("USER");
        liker = member("USER");
        other = member("USER");
        admin = member("ADMIN");
        postId = newPost(writer);
    }

    // 좋아요 → 댓글 → 글·회원 순으로 지운다 (외래 키)
    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post_like where member_id = ?", id);
            jdbcTemplate.update("delete from post_comment_like where member_id = ?", id);
        }
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post_comment where member_id = ?", id);
        }
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from post where member_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    // ---------- 게시글 ----------

    @Test
    void 게시글_좋아요하면_201이고_likeCount가_늘고_상세에_liked가_반영된다() throws Exception {
        likePost(liker, postId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(postId.intValue()))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));

        // 본인 글에도 좋아요 가능
        likePost(writer, postId).andExpect(status().isCreated()).andExpect(jsonPath("$.likeCount").value(2));

        mockMvc.perform(get("/api/public/posts/" + postId).header("Authorization", "Bearer " + token(liker)))
                .andExpect(jsonPath("$.likeCount").value(2))
                .andExpect(jsonPath("$.liked").value(true));
        mockMvc.perform(get("/api/public/posts/" + postId).header("Authorization", "Bearer " + token(other)))
                .andExpect(jsonPath("$.liked").value(false));
        mockMvc.perform(get("/api/public/posts/" + postId))
                .andExpect(jsonPath("$.liked").value(false));
        mockMvc.perform(get("/api/public/posts").param("boardType", "QUESTION"))
                .andExpect(jsonPath("$.content[?(@.postId == " + postId + ")].likeCount").value(2));
    }

    @Test
    void 이미_좋아요한_게시글은_409이고_likeCount는_그대로다() throws Exception {
        likePost(liker, postId).andExpect(status().isCreated());

        likePost(liker, postId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 좋아요한 게시글입니다."));
        assertEquals(1, postLikeCount());
    }

    @Test
    void 게시글_좋아요를_취소하면_200이고_likeCount가_줄며_다시_좋아요할_수_있다() throws Exception {
        likePost(liker, postId);
        likePost(other, postId);

        unlikePost(liker, postId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(1));

        // 이미 취소함 / 좋아요한 적 없음
        unlikePost(liker, postId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(LIKE_NOT_FOUND));
        unlikePost(writer, postId).andExpect(status().isNotFound());
        assertEquals(1, postLikeCount());

        likePost(liker, postId).andExpect(status().isCreated()).andExpect(jsonPath("$.likeCount").value(2));
    }

    @Test
    void 게시글_likeCount는_0_미만으로_내려가지_않는다() throws Exception {
        likePost(liker, postId);
        jdbcTemplate.update("update post set like_count = 0 where id = ?", postId);

        unlikePost(liker, postId).andExpect(status().isOk()).andExpect(jsonPath("$.likeCount").value(0));
    }

    @Test
    void 숨김_삭제_없는_게시글은_관리자도_404다() throws Exception {
        likePost(liker, postId);
        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);

        likePost(admin, postId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(POST_NOT_FOUND));
        unlikePost(liker, postId).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value(POST_NOT_FOUND));

        jdbcTemplate.update("update post set is_visible = true, deleted_at = CURRENT_TIMESTAMP where id = ?", postId);
        likePost(other, postId).andExpect(status().isNotFound());

        likePost(liker, 999999999L).andExpect(status().isNotFound());
        assertEquals(1, postLikeCount());
    }

    @Test
    void 관리자_목록은_likeCount로_정렬할_수_있다() throws Exception {
        Long popular = newPost(writer);
        likePost(liker, popular);
        likePost(other, popular);
        likePost(liker, postId);

        mockMvc.perform(get("/api/admin/posts")
                        .header("Authorization", "Bearer " + token(admin))
                        .param("sortBy", "likeCount")
                        .param("pageSize", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].postId").value(popular.intValue()))
                .andExpect(jsonPath("$.content[0].likeCount").value(2))
                .andExpect(jsonPath("$.content[1].postId").value(postId.intValue()));
    }

    // ---------- 댓글 ----------

    @Test
    void 댓글_좋아요하면_201이고_댓글_목록에_likeCount와_liked가_반영된다() throws Exception {
        Long commentId = comment(writer);
        Long untouched = comment(other);

        likeComment(liker, commentId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(commentId.intValue()))
                .andExpect(jsonPath("$.liked").value(true))
                .andExpect(jsonPath("$.likeCount").value(1));
        likeComment(writer, commentId).andExpect(jsonPath("$.likeCount").value(2));

        String url = "/api/public/posts/" + postId + "/comments";
        mockMvc.perform(get(url).header("Authorization", "Bearer " + token(liker)))
                .andExpect(jsonPath("$.content[?(@.commentId == " + commentId + ")].likeCount").value(2))
                .andExpect(jsonPath("$.content[?(@.commentId == " + commentId + ")].liked").value(true))
                .andExpect(jsonPath("$.content[?(@.commentId == " + untouched + ")].liked").value(false));
        mockMvc.perform(get(url))
                .andExpect(jsonPath("$.content[?(@.commentId == " + commentId + ")].liked").value(false));
    }

    @Test
    void 댓글_좋아요_중복은_409_취소는_200_기록이_없으면_404다() throws Exception {
        Long commentId = comment(writer);
        likeComment(liker, commentId);

        likeComment(liker, commentId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("이미 좋아요한 댓글입니다."));

        unlikeComment(liker, commentId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.likeCount").value(0));
        unlikeComment(liker, commentId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(LIKE_NOT_FOUND));
        assertEquals(0, commentLikeCount(commentId));
    }

    @Test
    void 삭제된_댓글이나_부모_글이_숨김_삭제된_댓글은_관리자도_404다() throws Exception {
        Long commentId = comment(writer);
        likeComment(liker, commentId);

        jdbcTemplate.update("update post set is_visible = false where id = ?", postId);
        likeComment(admin, commentId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(COMMENT_NOT_FOUND));
        unlikeComment(liker, commentId).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value(COMMENT_NOT_FOUND));

        jdbcTemplate.update("update post set is_visible = true, deleted_at = CURRENT_TIMESTAMP where id = ?", postId);
        likeComment(other, commentId).andExpect(status().isNotFound());

        jdbcTemplate.update("update post set deleted_at = null where id = ?", postId);
        jdbcTemplate.update("update post_comment set deleted_at = CURRENT_TIMESTAMP where id = ?", commentId);
        likeComment(other, commentId).andExpect(status().isNotFound());

        assertEquals(1, commentLikeCount(commentId));
    }

    // ---------- 공통 ----------

    @Test
    void 잘못된_id는_400_토큰이_없으면_401이다() throws Exception {
        likePost(liker, 0L)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(post("/api/comments/abc/likes").header("Authorization", "Bearer " + token(liker)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        unlikeComment(liker, 0L).andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/posts/" + postId + "/likes")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete("/api/comments/" + comment(writer) + "/likes")).andExpect(status().isUnauthorized());
    }

    private ResultActions likePost(Long memberId, Long id) throws Exception {
        return mockMvc.perform(post("/api/posts/" + id + "/likes").header("Authorization", "Bearer " + token(memberId)));
    }

    private ResultActions unlikePost(Long memberId, Long id) throws Exception {
        return mockMvc.perform(delete("/api/posts/" + id + "/likes").header("Authorization", "Bearer " + token(memberId)));
    }

    private ResultActions likeComment(Long memberId, Long id) throws Exception {
        return mockMvc.perform(post("/api/comments/" + id + "/likes").header("Authorization", "Bearer " + token(memberId)));
    }

    private ResultActions unlikeComment(Long memberId, Long id) throws Exception {
        return mockMvc.perform(delete("/api/comments/" + id + "/likes").header("Authorization", "Bearer " + token(memberId)));
    }

    private long postLikeCount() {
        return jdbcTemplate.queryForObject("select like_count from post where id = ?", Long.class, postId);
    }

    private long commentLikeCount(Long commentId) {
        return jdbcTemplate.queryForObject("select like_count from post_comment where id = ?", Long.class, commentId);
    }

    // post()로 지으면 static import한 MockMvc post()와 헷갈리므로 newPost
    private Long newPost(Long memberId) {
        String title = UUID.randomUUID().toString().substring(0, 20);
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, 'QUESTION', ?, '내용입니다.', 0, ?, ?, true)
                """, memberId, title, time, time);
        return jdbcTemplate.queryForObject("select id from post where title = ?", Long.class, title);
    }

    private Long comment(Long memberId) {
        String marker = UUID.randomUUID().toString();
        jdbcTemplate.update("""
                insert into post_comment(post_id, member_id, content, created_at, updated_at)
                values (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """, postId, memberId, marker);
        return jdbcTemplate.queryForObject("select id from post_comment where content = ?", Long.class, marker);
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
