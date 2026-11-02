package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.MemberRole;
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
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ExpertProfileUpdateTests {
    private static final String URL = "/api/members/me/expert-profile";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final List<Long> memberIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Long memberId : memberIds) {
            for (String table : List.of("expert_profile_services", "expert_profile_whole_regions", "expert_profile_regions")) {
                jdbcTemplate.update("delete from " + table + " where expert_profile_id in (select id from expert_profile where member_id = ?)", memberId);
            }
            jdbcTemplate.update("delete from expert_profile where member_id = ?", memberId);
            jdbcTemplate.update("delete from member where id = ?", memberId);
        }
    }

    @Test
    void 고수와_일반회원이_본인소개를_수정하고_공개조회() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        for (String role : List.of("EXPERT", "USER")) {
            mockMvc.perform(patch("/api/members/me/role").header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"" + role + "\"}"))
                    .andExpect(status().isOk());
            String response = mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"shortIntro":"  수정 소개 %s  ","description":"  수정된 서비스 상세 설명입니다.  ",
                                     "careerYears":0,"careerDescription":"변경 경력"}
                                    """.formatted(role)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(profileId.intValue()))
                    .andExpect(jsonPath("$.shortIntro").value("수정 소개 " + role))
                    .andExpect(jsonPath("$.description").value("수정된 서비스 상세 설명입니다."))
                    .andExpect(jsonPath("$.careerYears").value(0))
                    .andExpect(jsonPath("$.services.length()").value(2))
                    .andExpect(jsonPath("$.wholeRegions[0].id").value(1))
                    .andReturn().getResponse().getContentAsString();
            assertPublic(profileId, response);
            assertEquals(role, memberService.getMemberEntity(memberId).getRole().name());
            assertEquals(0L, memberService.getMemberEntity(memberId).getTokenVersion());
        }
    }

    @Test
    void 생략과_null유지_및_선택경력_null삭제를_구분() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        String before = getProfile(profileId);
        for (String body : List.of("{}", "{\"shortIntro\":null,\"description\":null,\"serviceIds\":null,\"primaryCategoryId\":null,\"nationwide\":null,\"wholeRegionIds\":null,\"regionIds\":null}",
                "{\"careerYearsSet\":true,\"careerDescriptionSet\":true}")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk()).andExpect(content().json(before));
        }
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"careerYears\":null,\"careerDescription\":null}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.careerYears").isEmpty())
                .andExpect(jsonPath("$.careerDescription").isEmpty());
        mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.careerYears").isEmpty())
                .andExpect(jsonPath("$.careerDescription").isEmpty());
    }

    @Test
    void 서비스와_대표서비스는_최종조합으로_검사() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        String before = getProfile(profileId);
        for (String body : List.of("{\"serviceIds\":[102]}", "{\"primaryCategoryId\":103}", "{\"serviceIds\":[]}",
                "{\"serviceIds\":[101,101]}", "{\"serviceIds\":[101,null]}")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
            assertPublic(profileId, before);
        }
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"serviceIds\":[102],\"primaryCategoryId\":102}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.services.length()").value(1))
                .andExpect(jsonPath("$.services[0].id").value(102))
                .andExpect(jsonPath("$.primaryCategory.id").value(102));
        mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andExpect(jsonPath("$.services[0].id").value(102));
    }

    @Test
    void 대표서비스만_또는_제공서비스만_수정해도_나머지는_유지() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"primaryCategoryId\":102}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.services.length()").value(2))
                .andExpect(jsonPath("$.primaryCategory.id").value(102));
        String response = mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"serviceIds\":[102,103]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.services.length()").value(2))
                .andExpect(jsonPath("$.primaryCategory.id").value(102))
                .andReturn().getResponse().getContentAsString();
        assertPublic(profileId, response);
    }

    @Test
    void 전국_시도전체_개별시군구_수정과_기존지역삭제() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        Long districtId = districtId();
        String before = getProfile(profileId);
        for (String body : List.of("{\"nationwide\":true}", "{\"wholeRegionIds\":[],\"regionIds\":[]}",
                "{\"wholeRegionIds\":[1,1]}", "{\"regionIds\":[" + districtId + "," + districtId + "]}",
                "{\"wholeRegionIds\":[null]}", "{\"regionIds\":[null]}")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
            assertPublic(profileId, before);
        }
        // 시도 전체와 그 아래 시군구를 함께 선택하면 변경 전체가 취소된다.
        Long seoulDistrict = jdbcTemplate.queryForObject("select min(id) from second_region where first_region_id = 1", Long.class);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"regionIds\":[" + seoulDistrict + "]}"))
                .andExpect(status().isBadRequest());
        assertPublic(profileId, before);
        String nationwide = update(token, "{\"nationwide\":true,\"wholeRegionIds\":[],\"regionIds\":[]}");
        assertPublic(profileId, nationwide);
        String wholeRegion = update(token, "{\"nationwide\":false,\"wholeRegionIds\":[1]}");
        assertPublic(profileId, wholeRegion);
        String district = update(token, "{\"wholeRegionIds\":[],\"regionIds\":[" + districtId + "]}");
        assertPublic(profileId, district);
    }

    @Test
    void 잘못된텍스트와_경력범위를_거절하고_기존값유지() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        String before = getProfile(profileId);
        for (String body : List.of("{\"shortIntro\":\"   \"}", "{\"shortIntro\":\"" + "가".repeat(101) + "\"}",
                "{\"shortIntro\":\"" + "\u2003" + "\"}",
                "{\"description\":\"짧음\"}", "{\"description\":\"" + "가".repeat(2001) + "\"}",
                "{\"description\":\"" + "\u2003".repeat(10) + "\"}",
                "{\"careerYears\":-1}", "{\"careerYears\":81}", "{\"careerYears\":1.5}",
                "{\"careerDescription\":\"" + "가".repeat(1001) + "\"}")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
            assertPublic(profileId, before);
        }
    }

    @Test
    void 없는카테고리와_지역_조회실패시_소개와_연결도_전체롤백() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        String before = getProfile(profileId);
        for (String body : List.of(
                "{\"shortIntro\":\"롤백 확인\",\"serviceIds\":[101,999999]}",
                "{\"shortIntro\":\"롤백 확인\",\"wholeRegionIds\":[999999]}",
                "{\"shortIntro\":\"롤백 확인\",\"serviceIds\":[102],\"primaryCategoryId\":102,\"regionIds\":[999999]}")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isNotFound());
            assertPublic(profileId, before);
        }
    }

    @Test
    void 미인증_프로필없음_ADMIN_탈퇴회원_차단() throws Exception {
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        Long memberId = createMember("USER");
        String token = token(memberId);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNotFound());
        createProfile(token);
        jdbcTemplate.update("update member set role = 'ADMIN' where id = ?", memberId);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", memberId);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 타인ID와_필드표시를_보내도_타인프로필이나_경력을_바꿀수없음() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        Long profileId = createProfile(token);
        Long otherMemberId = createMember("USER");
        Long otherProfileId = createProfile(token(otherMemberId));
        String otherBefore = getProfile(otherProfileId);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":" + otherProfileId + ",\"memberId\":" + otherMemberId + ",\"shortIntro\":\"내 소개 변경\",\"careerYearsSet\":true}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(profileId.intValue()))
                .andExpect(jsonPath("$.careerYears").value(3));
        assertPublic(otherProfileId, otherBefore);
        assertEquals(MemberRole.EXPERT, memberService.getMemberEntity(memberId).getRole());
    }

    @Test
    void Swagger에_수정경로와_수정DTO를_표시() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/members/me/expert-profile'].patch.requestBody.content['application/json'].schema['$ref']")
                        .value("#/components/schemas/ExpertProfileUpdateRequest"))
                .andExpect(jsonPath("$.components.schemas.ExpertProfileUpdateRequest.properties.careerYearsSet").doesNotExist());
    }

    private Long createMember(String role) {
        String suffix = UUID.randomUUID().toString();
        String email = suffix + "@test.local";
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String phone = String.format("%011d", nextId);
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)", email, suffix.substring(0, 20), phone, role);
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }

    private Long createProfile(String token) throws Exception {
        String body = """
                {"shortIntro":"기존 소개","description":"기존 서비스 상세 설명입니다.","careerYears":3,"careerDescription":"기존 경력",
                 "serviceIds":[101,102],"primaryCategoryId":101,"nationwide":false,"wholeRegionIds":[1],"regionIds":[%d]}
                """.formatted(districtId());
        String response = mockMvc.perform(post(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readTree(response).get("id").asLong();
    }

    private Long districtId() {
        return jdbcTemplate.queryForObject("select min(id) from second_region where first_region_id <> 1", Long.class);
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    private String update(String token, String body) throws Exception {
        return mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private String getProfile(Long profileId) throws Exception {
        return mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
    }

    private void assertPublic(Long profileId, String response) throws Exception {
        mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andExpect(content().json(response));
    }
}
