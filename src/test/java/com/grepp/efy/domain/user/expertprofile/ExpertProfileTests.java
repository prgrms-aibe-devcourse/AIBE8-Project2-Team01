package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.domain.file.UploadFileVisibility;
import com.grepp.efy.global.storage.SupabaseStorageClient;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("NonAsciiCharacters")
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
    @Autowired SupabaseStorageClient storageClient;
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
            jdbcTemplate.update("update member set image_id = null where id = ?", memberId);
            jdbcTemplate.update("delete from file where uploader_id = ?", memberId);
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
        assertEquals(true, document.get("imageId").isNull());
        assertEquals(true, document.get("imageUrl").isNull());
    }

    @Test
    void 시도전체와_다른시도의_시군구_및_선택경력_저장조회() throws Exception {
        Long memberId = createMember("USER");
        Long districtId = jdbcTemplate.queryForObject(
                "select min(id) from second_region where first_region_id <> 1", Long.class);
        assertNotNull(districtId);
        String body = """
                {"shortIntro":"  한 줄 소개  ","description":"  서비스 상세 설명입니다.  ",
                 "serviceIds":[101,102],"primaryCategoryId":101,
                 "nationwide":false,"wholeRegionIds":[1],"regionIds":[%d],
                 "careerYears":0,"careerDescription":"경력 설명"}
                """.formatted(districtId);
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
                .when(expertProfileMapper).toResponse(any(ExpertProfile.class), nullable(String.class));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest());
        assertEquals("USER", role(memberId));
        assertEquals(0, profileCount(memberId));
    }

    @Test
    void 본인_공개이미지를_등록하고_공개조회한다() throws Exception {
        Long memberId = createMember("USER");
        Long fileId = createFile(memberId, "PUBLIC", false);
        String path = jdbcTemplate.queryForObject("select storage_path from file where id = ?", String.class, fileId);
        String imageUrl = storageClient.createUrl(UploadFileVisibility.PUBLIC, path);
        String response = mockMvc.perform(post(URL)
                        .header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content(imageBody(fileId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageId").value(fileId.intValue()))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl))
                .andReturn().getResponse().getContentAsString();
        long profileId = new ObjectMapper().readTree(response).get("id").asLong();
        assertEquals(fileId, jdbcTemplate.queryForObject(
                "select image_id from member where id = ?", Long.class, memberId));
        mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imageId").value(fileId.intValue()))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl));
    }

    @Test
    void 닉네임_변경은_공개조회에_반영되고_개인정보는_노출하지_않는다() throws Exception {
        Long memberId = createMember("USER");
        Long fileId = createFile(memberId, "PUBLIC", false);
        String token = token(memberId);
        String nickname = memberService.getMemberEntity(memberId).getNickname();
        String response = mockMvc.perform(post(URL).header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(imageBody(fileId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andReturn().getResponse().getContentAsString();
        JsonNode profile = new ObjectMapper().readTree(response);
        String publicUrl = "/api/public/expert-profiles/" + profile.get("id").asLong();
        mockMvc.perform(get(publicUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.phone").doesNotExist());

        String changedNickname = UUID.randomUUID().toString().substring(0, 20);
        mockMvc.perform(put("/api/members/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nickname\":\"" + changedNickname + "\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get(publicUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(changedNickname))
                .andExpect(jsonPath("$.imageId").value(fileId.intValue()))
                .andExpect(jsonPath("$.imageUrl").value(profile.get("imageUrl").asString()))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.phone").doesNotExist());

        jdbcTemplate.update("update member set role = 'USER' where id = ?", memberId);
        mockMvc.perform(get(publicUrl)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(changedNickname));
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", memberId);
        mockMvc.perform(get(publicUrl)).andExpect(status().isNotFound());
    }

    @Test
    void 다른회원_비공개_삭제_없는파일은_연결없이_거절한다() throws Exception {
        Long memberId = createMember("USER");
        Long otherMemberId = createMember("USER");
        Long original = createFile(memberId, "PUBLIC", false);
        jdbcTemplate.update("update member set image_id = ? where id = ?", original, memberId);
        List<Long> fileIds = List.of(createFile(otherMemberId, "PUBLIC", false),
                createFile(memberId, "PRIVATE", false), createFile(memberId, "PUBLIC", true), Long.MAX_VALUE);
        int[] statuses = {403, 400, 404, 404};
        for (int i = 0; i < fileIds.size(); i++) {
            mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                            .contentType(MediaType.APPLICATION_JSON).content(imageBody(fileIds.get(i))))
                    .andExpect(status().is(statuses[i]));
            assertEquals(original, jdbcTemplate.queryForObject(
                    "select image_id from member where id = ?", Long.class, memberId));
            assertEquals("USER", role(memberId));
            assertEquals(0, profileCount(memberId));
        }
    }

    @Test
    void 이미지연결_후_실패하면_기존사진과_회원역할을_유지한다() throws Exception {
        Long memberId = createMember("USER");
        Long original = createFile(memberId, "PUBLIC", false);
        Long replacement = createFile(memberId, "PUBLIC", false);
        jdbcTemplate.update("update member set image_id = ? where id = ?", original, memberId);
        doThrow(new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION))
                .when(expertProfileMapper).toResponse(any(ExpertProfile.class), nullable(String.class));
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + token(memberId))
                        .contentType(MediaType.APPLICATION_JSON).content(imageBody(replacement)))
                .andExpect(status().isBadRequest());
        assertEquals(original, jdbcTemplate.queryForObject(
                "select image_id from member where id = ?", Long.class, memberId));
        assertEquals("USER", role(memberId));
        assertEquals(0, profileCount(memberId));
    }

    private String imageBody(Long fileId) {
        return BODY.replace("\"nationwide\":true", "\"nationwide\":true,\"imageId\":" + fileId);
    }

    private Long createFile(Long memberId, String visibility, boolean deleted) {
        String path = UUID.randomUUID() + ".png";
        jdbcTemplate.update("""
                insert into file(uploader_id, storage_path, original_name, content_type, size, visibility, created_at)
                values (?, ?, 'profile.png', 'image/png', 100, ?, CURRENT_TIMESTAMP)
                """, memberId, path, visibility);
        Long id = jdbcTemplate.queryForObject("select id from file where storage_path = ?", Long.class, path);
        if (deleted) {
            jdbcTemplate.update("update file set deleted_at = CURRENT_TIMESTAMP where id = ?", id);
        }
        return id;
    }

    private Long createMember(String role) {
        String suffix = UUID.randomUUID().toString();
        String email = suffix + "@test.local";
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String phone = String.format("%011d", nextId);
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)",
                email, suffix.substring(0, 20), phone, role);
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

    private Integer profileCount(Long memberId) {
        return jdbcTemplate.queryForObject("select count(*) from expert_profile where member_id = ?", Integer.class, memberId);
    }
}
