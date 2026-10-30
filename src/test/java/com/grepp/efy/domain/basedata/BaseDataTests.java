package com.grepp.efy.domain.basedata;

import com.grepp.efy.domain.basedata.firstcategory.FirstCategoryRepository;
import com.grepp.efy.domain.basedata.secondcategory.SecondCategoryRepository;
import com.grepp.efy.domain.basedata.firstregion.FirstRegionRepository;
import com.grepp.efy.domain.basedata.secondregion.SecondRegionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BaseDataTests {

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired FirstCategoryRepository firstCategoryRepository;
    @Autowired SecondCategoryRepository secondCategoryRepository;
    @Autowired FirstRegionRepository firstRegionRepository;
    @Autowired SecondRegionRepository secondRegionRepository;

    @Test
    void Swagger에도_등록_Path와_등록_수정_Body를_구분해_표시한다() throws Exception {
        String response = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        var document = new tools.jackson.databind.ObjectMapper().readTree(response);
        for (String[] c : new String[][]{
                {"first-categories", "firstCategoryId", "second-categories", "SecondCategoryCreateRequest", "SecondCategoryRequest", "secondCategoryId"},
                {"first-regions", "firstRegionId", "second-regions", "SecondRegionCreateRequest", "SecondRegionRequest", "secondRegionId"}
        }) {
            var operation = document.get("paths").get("/api/admin/" + c[0] + "/{" + c[1] + "}/" + c[2]).get("post");
            assertEquals(c[1], operation.get("parameters").get(0).get("name").asText());
            assertEquals("path", operation.get("parameters").get(0).get("in").asText());
            assertEquals("#/components/schemas/" + c[3], operation.get("requestBody").get("content")
                    .get("application/json").get("schema").get("$ref").asText());
            var schemas = document.get("components").get("schemas");
            assertFalse(schemas.get(c[3]).get("properties").has(c[1]));
            assertTrue(schemas.get(c[4]).get("properties").has(c[1]));
            assertTrue(document.get("paths").has("/api/admin/" + c[2] + "/{" + c[5] + "}"));
            assertFalse(document.get("paths").has("/api/admin/" + c[2]));
            var topList = document.get("paths").get("/api/public/" + c[0]).get("get").get("responses");
            assertFalse(topList.has("400"));
            assertFalse(topList.has("404"));
            var topCreate = document.get("paths").get("/api/admin/" + c[0]).get("post").get("responses");
            assertFalse(topCreate.has("404"));
            assertTrue(topCreate.has("400"));
            assertTrue(topCreate.has("401"));
            assertTrue(topCreate.has("403"));
            assertTrue(operation.get("responses").has("404"));
            var secondDelete = document.get("paths").get("/api/admin/" + c[2] + "/{" + c[5] + "}")
                    .get("delete").get("responses").get("409");
            assertEquals("다른 데이터에서 참조 중", secondDelete.get("description").asText());
            if (c[0].equals("first-categories")) {
                assertEquals("이름 중복", operation.get("responses").get("409").get("description").asText());
            }
        }
    }

    @Test
    void 시드의_개수와_기존_ID_및_코드를_보존한다() {
        assertEquals(11, firstCategoryRepository.count());
        assertEquals(108, secondCategoryRepository.count());
        assertEquals(16, firstRegionRepository.count());
        assertEquals(268, secondRegionRepository.count());
        assertEquals(1L, secondCategoryRepository.findById(101L).orElseThrow().getFirstCategory().getId());
        assertEquals("1100000000", firstRegionRepository.findById(1L).orElseThrow().getCode());
        assertEquals("1111000000", secondRegionRepository.findById(17L).orElseThrow().getCode());
        assertEquals(1L, secondRegionRepository.findById(17L).orElseThrow().getFirstRegion().getId());
    }

    @Test
    void 비로그인으로_계층별_목록을_조회한다() throws Exception {
        mockMvc.perform(get("/api/public/first-categories"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(11))
                .andExpect(jsonPath("$[0].id").value(1));
        mockMvc.perform(get("/api/public/first-categories/1/second-categories"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value(101))
                .andExpect(jsonPath("$[0].firstCategoryId").value(1));
        mockMvc.perform(get("/api/public/first-regions"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(16));
        mockMvc.perform(get("/api/public/first-regions/1/second-regions"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].code").value("1111000000"))
                .andExpect(jsonPath("$[0].firstRegionId").value(1));
        mockMvc.perform(get("/api/public/first-regions/8/second-regions"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void 없는_상위_항목_조회와_ID_형식_오류를_거절한다() throws Exception {
        mockMvc.perform(get("/api/public/first-categories/99999/second-categories"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/public/first-regions/99999/second-regions"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/public/first-regions/abc/second-regions"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 관리자는_네_유형을_등록_수정_삭제할_수_있다() throws Exception {
        String[][] cases = {
                {"first-categories", "{\"name\":\"  테스트대분류  \"}", "{\"name\":\"수정대분류\"}", "12"},
                {"second-categories", "{\"name\":\"테스트소분류\"}", "{\"name\":\"수정소분류\",\"firstCategoryId\":2}", "209"},
                {"first-regions", "{\"name\":\"테스트시도\",\"code\":\"9900000000\"}", "{\"name\":\"수정시도\",\"code\":\"9800000000\"}", "17"},
                {"second-regions", "{\"name\":\"테스트시군구\",\"code\":\"9911000000\"}", "{\"name\":\"수정시군구\",\"code\":\"9811000000\",\"firstRegionId\":3}", "285"}
        };
        for (String[] c : cases) {
            String url = "/api/admin/" + c[0];
            // H2 identity 번호는 테스트 롤백으로 복구되지 않으므로 생성 응답의 실제 ID를 사용한다.
            String createUrl = switch (c[0]) {
                case "second-categories" -> "/api/admin/first-categories/1/second-categories";
                case "second-regions" -> "/api/admin/first-regions/1/second-regions";
                default -> url;
            };
            String response = mockMvc.perform(post(createUrl).with(user("admin").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON).content(c[1]))
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
            long id = new tools.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
            assertTrue(id >= Long.parseLong(c[3]));
            mockMvc.perform(put(url + "/" + id).with(user("admin").roles("ADMIN"))
                            .contentType(MediaType.APPLICATION_JSON).content(c[2]))
                    .andExpect(status().isOk());
            mockMvc.perform(delete(url + "/" + id).with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        }
    }

    @Test
    void 관리자_API는_인증과_관리자_역할이_필요하다() throws Exception {
        for (String resource : new String[]{"first-categories", "first-categories/1/second-categories", "first-regions", "first-regions/1/second-regions"}) {
            mockMvc.perform(post("/api/admin/" + resource).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isUnauthorized());
            mockMvc.perform(post("/api/admin/" + resource).with(user("member").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isForbidden());
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 하위_항목은_경로의_상위_ID가_양수이고_존재해야_한다() throws Exception {
        mockMvc.perform(post("/api/admin/first-categories/0/second-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("입력값이 올바르지 않습니다."));
        mockMvc.perform(post("/api/admin/first-regions/-1/second-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\",\"code\":\"9900000000\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/admin/first-categories/abc/second-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\"}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/admin/first-categories/99999/second-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\"}"))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/admin/first-regions/99999/second-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\",\"code\":\"9900000000\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 하위_등록은_경로의_상위_ID를_사용하고_수정은_Body의_상위_ID가_필수다() throws Exception {
        mockMvc.perform(post("/api/admin/first-categories/2/second-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"경로등록테스트\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.firstCategoryId").value(2));
        mockMvc.perform(post("/api/admin/first-regions/3/second-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"경로등록테스트\",\"code\":\"9912340000\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.firstRegionId").value(3));
        mockMvc.perform(put("/api/admin/second-categories/101").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"이사\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.firstCategoryId").exists());
        mockMvc.perform(put("/api/admin/second-regions/17").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"종로구\",\"code\":\"1111000000\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.firstRegionId").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 같은_상위_항목의_이름과_양쪽_테이블의_지역코드_중복을_거절한다() throws Exception {
        mockMvc.perform(post("/api/admin/first-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  이사/청소  \"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/admin/first-categories/1/second-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"이사\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/admin/first-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\",\"code\":\"1111000000\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/admin/first-regions/1/second-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"테스트\",\"code\":\"1100000000\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 하위_카테고리가_있는_대분류는_삭제할_수_없다() throws Exception {
        mockMvc.perform(delete("/api/admin/first-categories/1"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("데이터 무결성 제약 조건 위반입니다."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void 하위_지역이_있는_시도는_삭제할_수_없다() throws Exception {
        mockMvc.perform(delete("/api/admin/first-regions/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void DB에서도_하위_항목의_필수_FK를_보장한다() {
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "insert into second_category(name, first_category_id) values ('테스트',null)"));
    }

    @Test
    void DB에서도_같은_상위_지역의_동일한_이름_중복을_막는다() {
        jdbcTemplate.update("insert into second_region(code,name,first_region_id) values ('9911000000','Test',1)");
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update(
                "insert into second_region(code,name,first_region_id) values ('9912000000','Test',1)"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 서비스에서_이름의_대소문자를_구분하지_않고_중복을_검사한다() throws Exception {
        jdbcTemplate.update("insert into first_category(name) values ('Test')");
        jdbcTemplate.update("insert into second_category(name,first_category_id) values ('Test',1)");
        jdbcTemplate.update("insert into first_region(code,name) values ('9900000000','Test')");
        jdbcTemplate.update("insert into second_region(code,name,first_region_id) values ('9911000000','Test',1)");

        mockMvc.perform(post("/api/admin/first-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/admin/first-categories/1/second-categories").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/admin/first-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"code\":\"9800000000\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/admin/first-regions/1/second-regions").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\",\"code\":\"9811000000\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void 회원당_고수_프로필은_최대_하나이며_회원_FK는_필수다() {
        jdbcTemplate.update("insert into expert_profile(member_id, short_intro, description) values (1, '테스트 소개', '테스트 상세 설명입니다.')");
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update("insert into expert_profile(member_id, short_intro, description) values (1, '테스트 소개', '테스트 상세 설명입니다.')"));
        assertThrows(DataIntegrityViolationException.class, () -> jdbcTemplate.update("insert into expert_profile(member_id, short_intro, description) values (null, '테스트 소개', '테스트 상세 설명입니다.')"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 기존_이름과_코드를_유지하는_수정은_중복으로_거절하지_않는다() throws Exception {
        mockMvc.perform(put("/api/admin/first-categories/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"이사/청소\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/admin/second-categories/101").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"이사\",\"firstCategoryId\":1}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/admin/first-regions/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"서울특별시\",\"code\":\"1100000000\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/admin/second-regions/17").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"종로구\",\"code\":\"1111000000\",\"firstRegionId\":1}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 이름이_같아도_소속을_옮기면_새_상위_항목에서_중복을_검사한다() throws Exception {
        jdbcTemplate.update("insert into second_category(name,first_category_id) values ('이사',2)");
        jdbcTemplate.update("insert into second_region(code,name,first_region_id) values ('9911000000','종로구',3)");

        mockMvc.perform(put("/api/admin/second-categories/101").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"이사\",\"firstCategoryId\":2}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/admin/second-regions/17").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"종로구\",\"code\":\"1111000000\",\"firstRegionId\":3}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 지역코드를_바꾸면_다른_단계_테이블과의_중복도_거절한다() throws Exception {
        mockMvc.perform(put("/api/admin/first-regions/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"서울특별시\",\"code\":\"1111000000\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/admin/second-regions/17").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"종로구\",\"code\":\"1100000000\",\"firstRegionId\":1}"))
                .andExpect(status().isConflict());
    }
}
