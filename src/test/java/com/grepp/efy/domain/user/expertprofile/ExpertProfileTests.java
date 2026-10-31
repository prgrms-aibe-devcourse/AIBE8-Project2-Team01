package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpertProfileTests {
    private static final String URL = "/api/members/me/expert-profile";
    private static final String BODY = """
            {"shortIntro":"  한 줄 소개  ","description":"  서비스 상세 설명입니다.  ",
             "serviceIds":[101,102],"primaryCategoryId":101,"nationwide":true}
            """;

    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @MockitoSpyBean ExpertProfileMapper expertProfileMapper;
    private final List<Long> memberIds = new ArrayList<>();

    // 테스트 자체에는 트랜잭션을 걸지 않아 서비스의 실제 커밋과 롤백을 확인한다.
    @AfterEach
    void cleanUp() {
        for (Long memberId : memberIds) {
            for (String table : List.of("expert_profile_services", "expert_profile_whole_regions", "expert_profile_regions")) {
                jdbcTemplate.update("delete from " + table
                        + " where expert_profile_id in (select id from expert_profile where member_id = ?)", memberId);
            }
            jdbcTemplate.update("delete from expert_profile where member_id = ?", memberId);
            jdbcTemplate.update("delete from member where id = ?", memberId);
        }
    }

    @Test
    void 등록과_고수전환_후_공개조회와_중복차단() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        String response = mockMvc.perform(post(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortIntro").value("한 줄 소개"))
                .andExpect(jsonPath("$.description").value("서비스 상세 설명입니다."))
                .andExpect(jsonPath("$.services.length()").value(2))
                .andExpect(jsonPath("$.primaryCategory.id").value(101))
                .andExpect(jsonPath("$.nationwide").value(true))
                .andReturn().getResponse().getContentAsString();
        JsonNode document = new ObjectMapper().readTree(response);
        long profileId = document.get("id").asLong();
        assertEquals("EXPERT", role(memberId));
        mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk())
                .andExpect(content().json(response));
        // 기존 JWT에도 DB의 변경된 역할이 반영된다.
        mockMvc.perform(get("/api/members/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("EXPERT"));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isConflict());
        assertEquals(1, profileCount(memberId));
    }

    @Test
    void 시도전체와_다른시도의_시군구_및_선택경력_저장조회() throws Exception {
        Long memberId = createMember("USER");
        Long districtId = jdbcTemplate.queryForObject(
                "select min(id) from second_region where first_region_id <> 1", Long.class);
        String body = BODY.replace("\"nationwide\":true", """
                "nationwide":false,"wholeRegionIds":[1],"regionIds":[%d],
                "careerYears":0,"careerDescription":"경력 설명"
                """.formatted(districtId));
        String response = mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.careerYears").value(0))
                .andExpect(jsonPath("$.wholeRegions[0].id").value(1))
                .andExpect(jsonPath("$.regions[0].id").value(districtId.intValue()))
                .andReturn().getResponse().getContentAsString();
        long profileId = new ObjectMapper().readTree(response).get("id").asLong();
        mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andExpect(content().json(response));
    }

    @Test
    void 미인증과_고수_관리자_탈퇴회원은_등록불가() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
        for (String role : List.of("EXPERT", "ADMIN")) {
            Long memberId = createMember(role);
            mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                            .contentType(MediaType.APPLICATION_JSON).content(BODY))
                    .andExpect(status().isForbidden());
            assertEquals(0, profileCount(memberId));
        }
        Long memberId = createMember("USER");
        String token = token(memberId);
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", memberId);
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 필드범위와_서비스_지역_잘못된선택은_저장없이_거절() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long districtId = jdbcTemplate.queryForObject(
                "select min(id) from second_region where first_region_id = 1", Long.class);
        List<String> invalidBodies = List.of(
                BODY.replace("  한 줄 소개  ", "   "),
                BODY.replace("  한 줄 소개  ", "가".repeat(101)),
                BODY.replace("  서비스 상세 설명입니다.  ", "짧음"),
                BODY.replace("  서비스 상세 설명입니다.  ", "가".repeat(2001)),
                BODY.replace("\"serviceIds\":[101,102]", "\"serviceIds\":[]"),
                BODY.replace("[101,102]", "[101,101]"),
                BODY.replace("[101,102]", "[101,null]"),
                BODY.replace("\"primaryCategoryId\":101", "\"primaryCategoryId\":103"),
                BODY.replace("\"primaryCategoryId\":101", "\"primaryCategoryId\":null"),
                BODY.replace("\"nationwide\":true", "\"careerYears\":-1,\"nationwide\":true"),
                BODY.replace("\"nationwide\":true", "\"careerYears\":81,\"nationwide\":true"),
                BODY.replace("\"nationwide\":true", "\"careerYears\":1.5,\"nationwide\":true"),
                BODY.replace("\"nationwide\":true", "\"careerDescription\":\"" + "가".repeat(1001) + "\",\"nationwide\":true"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":true,\"wholeRegionIds\":[1]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"wholeRegionIds\":[1,1]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"regionIds\":[null]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"regionIds\":[" + districtId + "," + districtId + "]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"wholeRegionIds\":[1],\"regionIds\":[" + districtId + "]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"wholeRegionIds\":null")
        );
        for (String body : invalidBodies) {
            mockMvc.perform(post(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
            assertEquals("USER", role(memberId));
            assertEquals(0, profileCount(memberId));
        }
    }

    @Test
    void 없는_카테고리와_지역은_404() throws Exception {
        Long memberId = createMember("USER");
        for (String body : List.of(
                BODY.replace("[101,102]", "[101,999999]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"wholeRegionIds\":[999999]"),
                BODY.replace("\"nationwide\":true", "\"nationwide\":false,\"regionIds\":[999999]"))) {
            mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isNotFound());
            assertEquals("USER", role(memberId));
            assertEquals(0, profileCount(memberId));
        }
    }

    @Test
    void Swagger에_등록경로와_요청본문이_표시된다() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/members/me/expert-profile'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/api/members/me/expert-profile'].post.requestBody").exists())
                .andExpect(jsonPath("$.paths['/api/members/me/expert-profile'].post.security[0].accessToken").exists());
    }

    @Test
    void 저장과_역할변경_이후_실패해도_전체롤백() throws Exception {
        Long memberId = createMember("USER");
        // 응답 변환 단계에서 실패시켜 앞서 실행된 INSERT와 역할 변경의 롤백을 확인한다.
        doThrow(new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION))
                .when(expertProfileMapper).toResponse(any(ExpertProfile.class));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest());
        assertEquals("USER", role(memberId));
        assertEquals(0, profileCount(memberId));
    }

    private Long createMember(String role) {
        String suffix = UUID.randomUUID().toString();
        String email = suffix + "@test.local";
        jdbcTemplate.update("insert into member(email, nickname, role, created_at, token_version) values (?, ?, ?, CURRENT_TIMESTAMP, 0)",
                email, suffix.substring(0, 20), role);
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    private String role(Long memberId) {
        return jdbcTemplate.queryForObject("select role from member where id = ?", String.class, memberId);
    }

    private int profileCount(Long memberId) {
        return jdbcTemplate.queryForObject("select count(*) from expert_profile where member_id = ?", Integer.class, memberId);
    }
}
