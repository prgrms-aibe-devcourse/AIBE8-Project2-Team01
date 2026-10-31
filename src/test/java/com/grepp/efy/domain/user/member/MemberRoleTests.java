package com.grepp.efy.domain.user.member;

import com.grepp.efy.domain.user.auth.RefreshTokenService;
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
class MemberRoleTests {
    private static final String URL = "/api/members/me/role";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired RefreshTokenService refreshTokenService;
    private final List<Long> memberIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Long memberId : memberIds) {
            jdbcTemplate.update("delete from request_answer where service_request_id in (select id from service_request where member_id = ?)", memberId);
            jdbcTemplate.update("delete from service_request where member_id = ?", memberId);
            jdbcTemplate.update("delete from refresh_token where member_id = ?", memberId);
            for (String table : List.of("expert_profile_services", "expert_profile_whole_regions", "expert_profile_regions")) {
                jdbcTemplate.update("delete from " + table + " where expert_profile_id in (select id from expert_profile where member_id = ?)", memberId);
            }
            jdbcTemplate.update("delete from expert_profile where member_id = ?", memberId);
            jdbcTemplate.update("delete from member where id = ?", memberId);
        }
    }

    @Test
    void 역할왕복과_동일역할재요청에서_프로필과_요청서_유지() throws Exception {
        Long memberId = createMember("USER");
        String userToken = token(memberId);
        Long profileId = createProfile(userToken);
        Long districtId = jdbcTemplate.queryForObject("select min(id) from second_region", Long.class);
        jdbcTemplate.update("insert into service_request(member_id, second_category_id, second_region_id, request_status, created_at) values (?, 101, ?, 'OPEN', CURRENT_TIMESTAMP)", memberId, districtId);
        String profileBefore = mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String expertToken = token(memberId);
        String refreshToken = refreshTokenService.issued(memberService.getMemberEntity(memberId));

        for (String role : List.of("EXPERT", "USER", "USER", "EXPERT", "EXPERT")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"" + role + "\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.role").value(role));
            assertEquals(role, memberService.getMemberEntity(memberId).getRole().name());
            assertEquals(0L, memberService.getMemberEntity(memberId).getTokenVersion());
            assertEquals(1, count("select count(*) from expert_profile where member_id = ?", memberId));
            assertEquals(1, count("select count(*) from service_request where member_id = ?", memberId));
            assertEquals(1, count("select count(*) from refresh_token where member_id = ?", memberId));
            mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                    .andExpect(status().isOk()).andExpect(content().json(profileBefore));
            mockMvc.perform(get("/api/members/me").header("Authorization", "Bearer " + expertToken))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.role").value(role));
            // JWT의 이전 역할 대신 DB의 현재 역할로 기존 API의 접근 권한을 판단한다.
            mockMvc.perform(get("/api/members/me/requests").header("Authorization", "Bearer " + expertToken))
                    .andExpect(status().is(role.equals("USER") ? 200 : 403));
        }
        assertEquals(MemberRole.EXPERT, refreshTokenService.refresh(refreshToken).userDetails().getRole());
    }

    @Test
    void 프로필없는_회원은_고수전환거절과_USER유지() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"EXPERT\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("고수 프로필을 찾을 수 없습니다."));
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"USER\"}"))
                .andExpect(status().isOk());
        assertEquals(MemberRole.USER, memberService.getMemberEntity(memberId).getRole());
        assertEquals(0, count("select count(*) from expert_profile where member_id = ?", memberId));
    }

    @Test
    void ADMIN의_본인전환과_ADMIN으로의_전환차단() throws Exception {
        Long adminId = createMember("ADMIN");
        for (String role : List.of("USER", "EXPERT", "ADMIN")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token(adminId))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"" + role + "\"}"))
                    .andExpect(status().isForbidden());
        }
        for (String role : List.of("USER", "EXPERT")) {
            Long memberId = createMember(role);
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token(memberId))
                            .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"ADMIN\"}"))
                    .andExpect(status().isForbidden());
            assertEquals(role, memberService.getMemberEntity(memberId).getRole().name());
        }
        assertEquals(MemberRole.ADMIN, memberService.getMemberEntity(adminId).getRole());
    }

    @Test
    void 미인증과_탈퇴회원과_잘못된입력_차단() throws Exception {
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"USER\"}"))
                .andExpect(status().isUnauthorized());
        Long memberId = createMember("USER");
        String token = token(memberId);
        for (String body : List.of("{}", "{\"role\":null}", "{\"role\":\"UNKNOWN\"}", "{\"role\":1}", "{", "")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(MemberRole.USER, memberService.getMemberEntity(memberId).getRole());
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", memberId);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"USER\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 요청본문의_다른회원ID로_타인역할을_변경할수없음() throws Exception {
        Long memberId = createMember("USER");
        String token = token(memberId);
        createProfile(token);
        Long otherMemberId = createMember("USER");
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":\"USER\",\"memberId\":" + otherMemberId + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(memberId.intValue()));
        assertEquals(MemberRole.USER, memberService.getMemberEntity(otherMemberId).getRole());
    }

    @Test
    void 기존_관리자역할변경은_세역할을_계속지원() throws Exception {
        Long adminId = createMember("ADMIN");
        Long memberId = createMember("USER");
        String token = token(adminId);
        for (String role : List.of("EXPERT", "ADMIN", "USER")) {
            mockMvc.perform(patch("/api/admin/members/" + memberId + "/role")
                            .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"role\":\"" + role + "\"}"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.role").value(role));
        }
        mockMvc.perform(patch("/api/admin/members/" + memberId + "/role")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"role\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void Swagger의_본인역할전환과_관리자변경은_별도경로() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/members/me/role'].patch.requestBody").exists())
                .andExpect(jsonPath("$.paths['/api/members/me/role'].patch.responses['404']").exists())
                .andExpect(jsonPath("$.paths['/api/admin/members/{memberId}/role'].patch").exists());
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

    private Long createProfile(String token) throws Exception {
        Long districtId = jdbcTemplate.queryForObject("select min(id) from second_region where first_region_id <> 1", Long.class);
        String body = """
                {"shortIntro":"역할 전환 테스트","description":"역할 전환 후에도 유지되는 프로필입니다.",
                 "careerYears":3,"serviceIds":[101,102],"primaryCategoryId":101,
                 "nationwide":false,"wholeRegionIds":[1],"regionIds":[%d]}
                """.formatted(districtId);
        String response = mockMvc.perform(post("/api/members/me/expert-profile")
                        .header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readTree(response).get("id").asLong();
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    private int count(String sql, Long memberId) {
        return jdbcTemplate.queryForObject(sql, Integer.class, memberId);
    }
}
