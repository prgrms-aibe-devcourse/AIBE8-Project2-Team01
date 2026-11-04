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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostCreateTests {
    private static final String URL = "/api/posts";
    private static final String INPUT_MESSAGE = "입력값이 올바르지 않습니다.";
    private static final String IMAGE_MESSAGE = "사용할 수 없는 이미지가 포함되어 있습니다.";
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
    void 작성하면_201이고_입력값과_이미지_순서대로_저장된다() throws Exception {
        Long writer = member("USER");
        Long first = file(writer);
        Long second = file(writer);

        // 101은 대분류 1, 17은 서울(1) (data.sql)
        create(writer, """
                {"boardType": "QUESTION", "title": "  제목입니다  ", "content": "  본문입니다  ",
                 "secondCategoryId": 101, "secondRegionId": 17, "imageIds": [%d, %d]}
                """.formatted(second, first))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").isNumber())
                .andExpect(jsonPath("$.boardType").value("QUESTION"))
                .andExpect(jsonPath("$.memberId").value(writer.intValue()))
                .andExpect(jsonPath("$.title").value("제목입니다"))
                .andExpect(jsonPath("$.content").value("본문입니다"))
                .andExpect(jsonPath("$.category.firstCategoryId").value(1))
                .andExpect(jsonPath("$.category.secondCategoryId").value(101))
                .andExpect(jsonPath("$.region.firstRegionId").value(1))
                .andExpect(jsonPath("$.region.secondRegionId").value(17))
                .andExpect(jsonPath("$.images[0].fileId").value(second.intValue()))
                .andExpect(jsonPath("$.images[1].fileId").value(first.intValue()))
                .andExpect(jsonPath("$.images[0].url").isNotEmpty())
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andExpect(jsonPath("$.liked").value(false))
                .andExpect(jsonPath("$.isVisible").value(true));

        Long postId = jdbcTemplate.queryForObject("select id from post where member_id = ?", Long.class, writer);
        assertEquals(List.of(second, first), jdbcTemplate.queryForList(
                "select file_id from post_images where post_id = ? order by sort_order", Long.class, postId));
    }

    @Test
    void 카테고리_지역_이미지는_생략할_수_있다() throws Exception {
        Long writer = member("USER");

        create(writer, """
                {"boardType": "RECRUIT", "title": "제목입니다", "content": "본문입니다"}
                """)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").isEmpty())
                .andExpect(jsonPath("$.region").isEmpty())
                .andExpect(jsonPath("$.images").isEmpty());
    }

    @Test
    void 고수가_아니면_TIP_PROMOTION_글을_쓸_수_없다() throws Exception {
        Long writer = member("USER");

        create(writer, body("TIP", file(writer)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("접근 권한이 없습니다."));
        create(writer, body("PROMOTION")).andExpect(status().isForbidden());

        assertEquals(0, postCount(writer));
    }

    @Test
    void 고수는_TIP_글에_이미지가_있어야_하고_PROMOTION은_이미지_없이_쓸_수_있다() throws Exception {
        Long expert = member("EXPERT");

        create(expert, body("TIP"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("팁 게시글에는 이미지를 1장 이상 첨부해 주세요."));
        create(expert, body("TIP", file(expert))).andExpect(status().isCreated());
        create(expert, body("PROMOTION")).andExpect(status().isCreated());

        assertEquals(2, postCount(expert));
    }

    @Test
    void 남의_파일_비공개_파일_삭제된_파일_없는_파일은_첨부할_수_없다() throws Exception {
        Long writer = member("USER");
        Long privateFile = file(writer);
        jdbcTemplate.update("update file set visibility = 'PRIVATE' where id = ?", privateFile);
        Long deletedFile = file(writer);
        jdbcTemplate.update("update file set deleted_at = CURRENT_TIMESTAMP where id = ?", deletedFile);

        for (Long fileId : List.of(file(member("USER")), privateFile, deletedFile, 999999999L)) {
            create(writer, body("QUESTION", fileId))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(IMAGE_MESSAGE));
        }
        assertEquals(0, postCount(writer));
    }

    @Test
    void 없는_카테고리와_지역은_404다() throws Exception {
        Long writer = member("USER");

        create(writer, """
                {"boardType": "QUESTION", "title": "제목입니다", "content": "본문입니다", "secondCategoryId": 999999}
                """)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("카테고리를 찾을 수 없습니다."));
        create(writer, """
                {"boardType": "QUESTION", "title": "제목입니다", "content": "본문입니다", "secondRegionId": 999999}
                """)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("지역을 찾을 수 없습니다."));

        assertEquals(0, postCount(writer));
    }

    @Test
    void 잘못된_입력은_400_토큰이_없으면_401이다() throws Exception {
        Long writer = member("USER");

        create(writer, """
                {"title": "제목입니다", "content": "본문입니다"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(INPUT_MESSAGE))
                .andExpect(jsonPath("$.errors.boardType").value("게시판 종류를 선택해 주세요."));
        // trim 후 1자
        create(writer, """
                {"boardType": "QUESTION", "title": "  a  ", "content": "본문입니다"}
                """)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").value("제목은 2자 이상 30자 이하로 입력해 주세요."));
        create(writer, """
                {"boardType": "QUESTION", "title": "제목입니다", "content": "%s"}
                """.formatted("a".repeat(5001)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.content").value("본문은 2자 이상 5000자 이하로 입력해 주세요."));
        Long[] elevenFiles = new Long[11];
        for (int i = 0; i < elevenFiles.length; i++) {
            elevenFiles[i] = file(writer);
        }
        create(writer, body("QUESTION", elevenFiles))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.imageIds").value("이미지는 최대 10개까지 첨부할 수 있습니다."));
        create(writer, body("XYZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요."));
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body("QUESTION")))
                .andExpect(status().isUnauthorized());

        assertEquals(0, postCount(writer));
    }

    private ResultActions create(Long memberId, String body) throws Exception {
        return mockMvc.perform(post(URL)
                .header("Authorization", "Bearer " + token(memberId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private String body(String boardType, Long... imageIds) {
        String ids = String.join(",", Arrays.stream(imageIds).map(String::valueOf).toList());
        return """
                {"boardType": "%s", "title": "제목입니다", "content": "본문입니다", "imageIds": [%s]}
                """.formatted(boardType, ids);
    }

    private int postCount(Long memberId) {
        return jdbcTemplate.queryForObject("select count(*) from post where member_id = ?", Integer.class, memberId);
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
