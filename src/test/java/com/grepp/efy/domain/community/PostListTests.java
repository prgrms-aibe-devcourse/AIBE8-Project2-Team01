package com.grepp.efy.domain.community;

import com.jayway.jsonpath.JsonPath;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
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

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// 다른 데이터와 섞이지 않도록 테스트마다 고유 keyword를 제목에 넣고 그 keyword로 조회한다
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
class PostListTests {
    private static final String URL = "/api/public/posts";
    private static final LocalDateTime BASE_TIME = LocalDateTime.of(2026, 1, 1, 0, 0);
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired EntityManagerFactory entityManagerFactory;
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
    void 최신순으로_20개씩_나누고_pageSize는_무시한다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        for (int i = 0; i < 21; i++) {
            post(writer, "QUESTION", keyword + "-" + i, BASE_TIME.plusMinutes(i));
        }

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword).param("pageSize", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.content[0].title").value(keyword + "-20"))
                .andExpect(jsonPath("$.content[19].title").value(keyword + "-1"))
                .andExpect(jsonPath("$.pageNumber").value(1))
                .andExpect(jsonPath("$.pageSize").value(20))
                .andExpect(jsonPath("$.totalElements").value(21))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.lastPage").value(false));

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword).param("pageNumber", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].title").value(keyword + "-0"))
                .andExpect(jsonPath("$.lastPage").value(true));

        // 마지막 페이지를 넘어서면 빈 목록과 200
        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword).param("pageNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(21));
    }

    @Test
    void 숨김글_삭제글_다른게시판글은_목록과_개수에서_제외한다() throws Exception {
        Long writer = member("EXPERT");
        String keyword = keyword();
        Long visible = post(writer, "QUESTION", keyword + "-visible", BASE_TIME);
        Long hidden = post(writer, "QUESTION", keyword + "-hidden", BASE_TIME);
        Long deleted = post(writer, "QUESTION", keyword + "-deleted", BASE_TIME);
        post(writer, "TIP", keyword + "-tip", BASE_TIME);
        jdbcTemplate.update("update post set is_visible = false where id = ?", hidden);
        jdbcTemplate.update("update post set deleted_at = CURRENT_TIMESTAMP where id = ?", deleted);

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].postId").value(visible.intValue()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void keyword는_제목이나_본문에_포함된_글을_찾는다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        post(writer, "QUESTION", "제목에 " + keyword, BASE_TIME);
        Long inContent = post(writer, "QUESTION", keyword() + "-other", BASE_TIME);
        jdbcTemplate.update("update post set content = ? where id = ?", "본문에 " + keyword + " 포함", inContent);
        post(writer, "QUESTION", keyword() + "-none", BASE_TIME);

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void 대분류는_하위_소분류_전체가_대상이고_소분류가_우선한다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        // 101·102는 대분류 1, 107은 대분류 2 (data.sql)
        setCategory(post(writer, "QUESTION", keyword + "-101", BASE_TIME), 101L);
        setCategory(post(writer, "QUESTION", keyword + "-102", BASE_TIME), 102L);
        setCategory(post(writer, "QUESTION", keyword + "-107", BASE_TIME), 107L);
        post(writer, "QUESTION", keyword + "-none", BASE_TIME);

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword)
                        .param("firstCategoryId", "1"))
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword)
                        .param("firstCategoryId", "2").param("secondCategoryId", "101"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value(keyword + "-101"));
    }

    @Test
    void 시도는_하위_시군구_전체가_대상이고_시군구가_우선한다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        // 17·18은 서울(1), 42는 전남광주(2) (data.sql)
        setRegion(post(writer, "QUESTION", keyword + "-17", BASE_TIME), 17L);
        setRegion(post(writer, "QUESTION", keyword + "-18", BASE_TIME), 18L);
        setRegion(post(writer, "QUESTION", keyword + "-42", BASE_TIME), 42L);
        post(writer, "QUESTION", keyword + "-none", BASE_TIME);

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword)
                        .param("firstRegionId", "1"))
                .andExpect(jsonPath("$.totalElements").value(2));

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword)
                        .param("firstRegionId", "1").param("secondRegionId", "42"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value(keyword + "-42"));
    }

    @Test
    void 썸네일은_첫번째_이미지이고_없으면_null이다() throws Exception {
        Long writer = member("USER");
        String keyword = keyword();
        Long withImage = post(writer, "QUESTION", keyword + "-image", BASE_TIME.plusMinutes(1));
        Long first = file(writer);
        attachImages(withImage, first, file(writer));
        setCategory(withImage, 101L);
        setRegion(withImage, 17L);
        post(writer, "QUESTION", keyword + "-plain", BASE_TIME);
        String firstPath = jdbcTemplate.queryForObject("select storage_path from file where id = ?", String.class, first);

        String body = mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].postId").value(withImage.intValue()))
                .andExpect(jsonPath("$.content[0].thumbnailUrl").value(containsString(firstPath)))
                .andExpect(jsonPath("$.content[0].category.firstCategoryId").value(1))
                .andExpect(jsonPath("$.content[0].category.secondCategoryId").value(101))
                .andExpect(jsonPath("$.content[0].region.firstRegionId").value(1))
                .andExpect(jsonPath("$.content[0].region.secondRegionId").value(17))
                .andExpect(jsonPath("$.content[0].commentCount").value(0))
                .andExpect(jsonPath("$.content[0].likeCount").value(0))
                .andExpect(jsonPath("$.content[1].thumbnailUrl").isEmpty())
                .andExpect(jsonPath("$.content[1].category").isEmpty())
                .andExpect(jsonPath("$.content[1].region").isEmpty())
                .andReturn().getResponse().getContentAsString();

        // 수정되지 않은 글은 updatedAt == createdAt
        assertEquals((String) JsonPath.read(body, "$.content[1].createdAt"), JsonPath.read(body, "$.content[1].updatedAt"));
    }

    @Test
    void 목록_조회는_글_수와_관계없이_쿼리_수가_일정하다() throws Exception {
        String keyword = keyword();
        for (int i = 0; i < 5; i++) {
            Long writer = member("USER");
            Long postId = post(writer, "QUESTION", keyword + "-" + i, BASE_TIME.plusMinutes(i));
            attachImages(postId, file(writer));
            setCategory(postId, 101L + i);
            setRegion(postId, 17L + i);
        }
        Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", keyword))
                .andExpect(jsonPath("$.content.length()").value(5));

        // 목록 1 + 개수 1 + 이미지 IN 1 (N+1이면 글마다 쿼리가 늘어남)
        long queries = statistics.getPrepareStatementCount();
        assertTrue(queries <= 3, "query count: " + queries);
    }

    @Test
    void 잘못된_요청은_400이다() throws Exception {
        String typeMessage = "경로 또는 쿼리 값의 타입이 올바르지 않습니다.";
        String inputMessage = "입력값이 올바르지 않습니다.";

        mockMvc.perform(get(URL)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(inputMessage));
        mockMvc.perform(get(URL).param("boardType", "XYZ")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(typeMessage));
        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("pageNumber", "0"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("pageNumber", "abc"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(typeMessage));
        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", "   "))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        mockMvc.perform(get(URL).param("boardType", "QUESTION").param("keyword", "a".repeat(51)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(inputMessage));
        for (String param : List.of("firstCategoryId", "secondCategoryId", "firstRegionId", "secondRegionId")) {
            mockMvc.perform(get(URL).param("boardType", "QUESTION").param(param, "999999"))
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

    private void setCategory(Long postId, Long secondCategoryId) {
        jdbcTemplate.update("update post set second_category_id = ? where id = ?", secondCategoryId, postId);
    }

    private void setRegion(Long postId, Long secondRegionId) {
        jdbcTemplate.update("update post set second_region_id = ? where id = ?", secondRegionId, postId);
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

    // member()는 MemberImageTests에서 그대로 복사
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
}
