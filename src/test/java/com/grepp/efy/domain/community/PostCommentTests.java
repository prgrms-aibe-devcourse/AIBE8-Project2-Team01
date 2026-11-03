package com.grepp.efy.domain.community;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 쿼리 수를 세려고 PostListTests와 같은 설정을 쓴다 (같은 설정이면 Spring 컨텍스트를 재사용)
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostCommentTests {
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired EntityManagerFactory entityManagerFactory;
    private final List<Long> memberIds = new ArrayList<>();
    private Long writer;
    private Long commenter;

    @BeforeEach
    void setUp() {
        writer = member("USER");
        commenter = member("USER");
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

    @Test
    void 작성하면_201이고_게시글_commentCount가_1_증가한다() throws Exception {
        Long expert = member("EXPERT");
        Long postId = newPost(writer);

        createComment(expert, postId, "{\"content\": \"  나방파리 유충 같네요  \"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").isNumber())
                .andExpect(jsonPath("$.memberId").value(expert.intValue()))
                .andExpect(jsonPath("$.memberNickname").isNotEmpty())
                .andExpect(jsonPath("$.memberProfileImageUrl").isEmpty())
                .andExpect(jsonPath("$.isExpert").value(true))
                .andExpect(jsonPath("$.content").value("나방파리 유충 같네요"))
                .andExpect(jsonPath("$.images").isEmpty())
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.liked").value(false));

        // 본인 글에도 작성 가능
        createComment(writer, postId, "{\"content\": \"감사합니다\"}").andExpect(status().isCreated());

        assertEquals(2, commentCount(postId));
        mockMvc.perform(get("/api/public/posts/" + postId))
                .andExpect(jsonPath("$.commentCount").value(2));
    }

    @Test
    void 이미지는_내가_올린_파일_1개까지_첨부할_수_있다() throws Exception {
        Long postId = newPost(writer);
        Long mine = file(commenter);

        createComment(commenter, postId, "{\"content\": \"사진 첨부\", \"imageIds\": [" + mine + "]}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.images.length()").value(1))
                .andExpect(jsonPath("$.images[0].fileId").value(mine.intValue()))
                .andExpect(jsonPath("$.images[0].url").isNotEmpty());

        createComment(commenter, postId, "{\"content\": \"남의 사진\", \"imageIds\": [" + file(writer) + "]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용할 수 없는 이미지가 포함되어 있습니다."));
        createComment(commenter, postId, "{\"content\": \"두 장\", \"imageIds\": [" + file(commenter) + ", " + file(commenter) + "]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));

        // 실패한 요청은 개수에 반영되지 않는다
        assertEquals(1, commentCount(postId));
    }

    @Test
    void 숨김_삭제_없는_글에는_작성할_수_없다() throws Exception {
        Long admin = member("ADMIN");
        Long hidden = newPost(writer);
        Long deleted = newPost(writer);
        jdbcTemplate.update("update post set is_visible = false where id = ?", hidden);
        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", deleted);

        // 숨김 글은 관리자도 불가
        createComment(admin, hidden, "{\"content\": \"관리자 댓글\"}")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 게시물입니다."));
        createComment(commenter, deleted, "{\"content\": \"댓글입니다\"}").andExpect(status().isNotFound());
        createComment(commenter, 999999999L, "{\"content\": \"댓글입니다\"}").andExpect(status().isNotFound());
    }

    @Test
    void 작성_요청이_잘못되면_400_토큰이_없으면_401이다() throws Exception {
        Long postId = newPost(writer);
        String inputMessage = "입력값이 올바르지 않습니다.";

        createComment(commenter, postId, "{}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        // trim 후 1자. @Positive 경로 변수가 있어 HandlerMethodValidationException으로 처리되므로 errors는 없다 (게시글 수정과 같음)
        createComment(commenter, postId, "{\"content\": \"  a  \"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        createComment(commenter, postId, "{\"content\": \"" + "a".repeat(401) + "\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        createComment(commenter, 0L, "{\"content\": \"댓글입니다\"}")
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));

        mockMvc.perform(post(commentsUrl(postId)).contentType(MediaType.APPLICATION_JSON).content("{\"content\": \"댓글입니다\"}"))
                .andExpect(status().isUnauthorized());

        assertEquals(0, commentCount(postId));
    }

    @Test
    void 목록은_최신순_20개씩이고_같은_시각이면_commentId_내림차순이며_삭제_댓글은_제외한다() throws Exception {
        Long postId = newPost(writer);
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            ids.add(comment(postId, commenter, BASE_TIME.plusMinutes(i)));
        }
        Long sameTime = comment(postId, commenter, BASE_TIME.plusMinutes(20));
        Long deleted = comment(postId, commenter, BASE_TIME.plusDays(1));
        jdbcTemplate.update("update post_comment set deleted_at = CURRENT_TIMESTAMP where id = ?", deleted);

        mockMvc.perform(get(publicCommentsUrl(postId)).param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                // 20분 댓글 두 개 중 나중에 저장된(id가 큰) 댓글이 먼저
                .andExpect(jsonPath("$.content[0].commentId").value(sameTime.intValue()))
                .andExpect(jsonPath("$.content[1].commentId").value(ids.get(20).intValue()))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(22));

        mockMvc.perform(get(publicCommentsUrl(postId)).param("pageNumber", "2"))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[1].commentId").value(ids.get(0).intValue()));

        // 마지막 페이지를 넘어서면 빈 목록과 200
        mockMvc.perform(get(publicCommentsUrl(postId)).param("pageNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void 숨김_글의_댓글은_관리자만_조회하고_삭제_글은_모두_404다() throws Exception {
        Long admin = member("ADMIN");
        Long hidden = newPost(writer);
        comment(hidden, commenter, BASE_TIME);
        jdbcTemplate.update("update post set is_visible = false where id = ?", hidden);

        mockMvc.perform(get(publicCommentsUrl(hidden))).andExpect(status().isNotFound());
        mockMvc.perform(get(publicCommentsUrl(hidden)).header("Authorization", "Bearer " + token(writer)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(publicCommentsUrl(hidden)).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", hidden);
        mockMvc.perform(get(publicCommentsUrl(hidden)).header("Authorization", "Bearer " + token(admin)))
                .andExpect(status().isNotFound());
    }

    @Test
    void 목록_조회는_댓글_수와_관계없이_쿼리_수가_일정하다() throws Exception {
        Long postId = newPost(writer);
        for (int i = 0; i < 5; i++) {
            Long member = member("USER");
            Long commentId = comment(postId, member, BASE_TIME.plusMinutes(i));
            jdbcTemplate.update("insert into post_comment_images(comment_id, file_id, sort_order) values (?, ?, 0)",
                    commentId, file(member));
        }
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        mockMvc.perform(get(publicCommentsUrl(postId)))
                .andExpect(jsonPath("$.content.length()").value(5))
                .andExpect(jsonPath("$.content[0].images.length()").value(1));

        // 게시글 1 + 목록 1 + 개수 1 + 이미지 IN 1 (N+1이면 댓글마다 쿼리가 늘어남)
        long queries = statistics.getPrepareStatementCount();
        assertTrue(queries <= 4, "query count: " + queries);
    }

    @Test
    void 목록_요청이_잘못되면_400이다() throws Exception {
        Long postId = newPost(writer);

        mockMvc.perform(get(publicCommentsUrl(postId)).param("pageNumber", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(get(publicCommentsUrl(postId)).param("pageNumber", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("경로 또는 쿼리 값의 타입이 올바르지 않습니다."));
        mockMvc.perform(get("/api/public/posts/abc/comments"))
                .andExpect(status().isBadRequest());
    }

    private ResultActions createComment(Long memberId, Long postId, String body) throws Exception {
        return mockMvc.perform(post(commentsUrl(postId))
                .header("Authorization", "Bearer " + token(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static String commentsUrl(Long postId) {
        return "/api/posts/" + postId + "/comments";
    }

    private static String publicCommentsUrl(Long postId) {
        return "/api/public/posts/" + postId + "/comments";
    }

    private long commentCount(Long postId) {
        return jdbcTemplate.queryForObject("select comment_count from post where id = ?", Long.class, postId);
    }

    // post()로 지으면 static import한 MockMvc post()가 가려져서 newPost
    private Long newPost(Long memberId) {
        String title = UUID.randomUUID().toString().substring(0, 20);
        Timestamp time = Timestamp.valueOf(BASE_TIME);
        jdbcTemplate.update("""
                insert into post(member_id, board_type, title, content, view_count, created_at, updated_at, is_visible)
                values (?, 'QUESTION', ?, '내용입니다.', 0, ?, ?, true)
                """, memberId, title, time, time);
        return jdbcTemplate.queryForObject("select id from post where title = ?", Long.class, title);
    }

    // commentCount는 바꾸지 않는다 (목록 테스트용)
    private Long comment(Long postId, Long memberId, LocalDateTime createdAt) {
        String content = UUID.randomUUID().toString();
        Timestamp time = Timestamp.valueOf(createdAt);
        jdbcTemplate.update("""
                insert into post_comment(post_id, member_id, content, created_at, updated_at)
                values (?, ?, ?, ?, ?)
                """, postId, memberId, content, time, time);
        return jdbcTemplate.queryForObject("select id from post_comment where content = ?", Long.class, content);
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
