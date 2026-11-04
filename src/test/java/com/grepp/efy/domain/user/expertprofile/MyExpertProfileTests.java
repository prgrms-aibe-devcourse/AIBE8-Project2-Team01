package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import com.grepp.efy.domain.file.UploadFileVisibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MyExpertProfileTests {
    private static final String URL = "/api/members/me/expert-profile";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired SupabaseStorageClient storageClient;

    @Test
    void 프로필을_보유한_USER와_EXPERT는_본인_등록정보를_조회한다() throws Exception {
        for (String role : new String[]{"USER", "EXPERT"}) {
            Long memberId = member(role);
            Long profileId = profile(memberId);
            String token = token(memberId);
            String publicResponse = mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
            myProfile(token).andExpect(status().isOk())
                    .andExpect(content().json(publicResponse))
                    .andExpect(jsonPath("$.id").value(profileId))
                    .andExpect(jsonPath("$.shortIntro").value("본인 프로필 소개"))
                    .andExpect(jsonPath("$.description").value("본인 프로필 상세 설명입니다."))
                    .andExpect(jsonPath("$.careerYears").value(3))
                    .andExpect(jsonPath("$.careerDescription").value("경력 설명"))
                    .andExpect(jsonPath("$.services[0].id").value(101))
                    .andExpect(jsonPath("$.primaryCategory.id").value(101))
                    .andExpect(jsonPath("$.nationwide").value(true))
                    .andExpect(jsonPath("$.imageId").value(org.hamcrest.Matchers.nullValue()))
                    .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.nullValue()))
                    .andExpect(jsonPath("$.workImages").isEmpty())
                    .andExpect(jsonPath("$.email").doesNotExist())
                    .andExpect(jsonPath("$.phone").doesNotExist());
        }
    }

    @Test
    void 최신_닉네임과_회원사진_작업사진을_공개조회와_같이_반환한다() throws Exception {
        Long memberId = member("EXPERT");
        Long profileId = profile(memberId);
        Long imageId = file(memberId, "PUBLIC");
        Long workImageId = file(memberId, "PUBLIC");
        jdbcTemplate.update("update member set image_id = ? where id = ?", imageId, memberId);
        jdbcTemplate.update("insert into work_image(expert_profile_id, file_id, description, display_order) values (?, ?, '작업 설명', 0)",
                profileId, workImageId);
        String token = token(memberId);
        String nickname = "수정닉네임" + UUID.randomUUID().toString().substring(0, 8);
        mockMvc.perform(put("/api/members/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"nickname\":\"" + nickname + "\"}"))
                .andExpect(status().isOk());
        myProfile(token).andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname))
                .andExpect(jsonPath("$.imageId").value(imageId))
                .andExpect(jsonPath("$.imageUrl").value(imageUrl(imageId)))
                .andExpect(jsonPath("$.workImages[0].fileId").value(workImageId))
                .andExpect(jsonPath("$.workImages[0].url").value(imageUrl(workImageId)))
                .andExpect(jsonPath("$.workImages[0].description").value("작업 설명"));
        String publicResponse = mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        myProfile(token).andExpect(content().json(publicResponse));
    }

    @Test
    void 다른_회원_ID를_쿼리로_보내도_인증된_본인_프로필만_반환한다() throws Exception {
        Long me = member("USER");
        Long myProfileId = profile(me);
        Long other = member("EXPERT");
        Long otherProfileId = profile(other);
        mockMvc.perform(get(URL).header("Authorization", "Bearer " + token(me))
                        .param("memberId", other.toString()).param("expertProfileId", otherProfileId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(myProfileId));
        myProfile(token(other)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(otherProfileId));
    }

    @Test
    void 본인_프로필이_없으면_다른_프로필이_있어도_404다() throws Exception {
        Long me = member("USER");
        profile(member("EXPERT"));
        myProfile(token(me)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("고수 프로필을 찾을 수 없습니다."));
    }

    @Test
    void 비로그인과_탈퇴회원은_401이다() throws Exception {
        mockMvc.perform(get(URL)).andExpect(status().isUnauthorized());
        Long me = member("EXPERT");
        profile(me);
        String token = token(me);
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", me);
        myProfile(token).andExpect(status().isUnauthorized());
    }

    @Test
    void 비공개나_삭제된_회원사진_URL과_작업사진은_노출하지_않는다() throws Exception {
        for (String visibility : new String[]{"PRIVATE", "PUBLIC"}) {
            Long me = member("EXPERT");
            Long profileId = profile(me);
            Long imageId = file(me, visibility);
            if (visibility.equals("PUBLIC")) {
                jdbcTemplate.update("update file set deleted_at = CURRENT_TIMESTAMP where id = ?", imageId);
            }
            jdbcTemplate.update("update member set image_id = ? where id = ?", imageId, me);
            jdbcTemplate.update("insert into work_image(expert_profile_id, file_id, description, display_order) values (?, ?, '숨김', 0)",
                    profileId, imageId);
            myProfile(token(me)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageUrl").value(org.hamcrest.Matchers.nullValue()))
                    .andExpect(jsonPath("$.workImages").isEmpty());
        }
    }

    @Test
    void Swagger에_본인조회와_응답_인증_오류조건을_문서화한다() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['" + URL + "'].get.operationId").value("getMyExpertProfile"))
                .andExpect(jsonPath("$.paths['" + URL + "'].get.parameters").doesNotExist())
                .andExpect(jsonPath("$.paths['" + URL + "'].get.security[0].accessToken").exists())
                .andExpect(jsonPath("$.paths['" + URL + "'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['" + URL + "'].get.responses['401']").exists())
                .andExpect(jsonPath("$.paths['" + URL + "'].get.responses['404']").exists());
    }

    private ResultActions myProfile(String token) throws Exception {
        return mockMvc.perform(get(URL).header("Authorization", "Bearer " + token));
    }

    private Long member(String role) {
        String suffix = UUID.randomUUID().toString();
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, ?, CURRENT_TIMESTAMP, 0)",
                suffix + "@test.local", suffix.substring(0, 20), String.format("%011d", nextId), role);
        return jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, suffix + "@test.local");
    }

    private Long profile(Long memberId) {
        jdbcTemplate.update("insert into expert_profile(member_id, short_intro, description, career_years, career_description, primary_category_id, is_nationwide) values (?, '본인 프로필 소개', '본인 프로필 상세 설명입니다.', 3, '경력 설명', 101, true)", memberId);
        Long id = jdbcTemplate.queryForObject("select id from expert_profile where member_id = ?", Long.class, memberId);
        jdbcTemplate.update("insert into expert_profile_services(expert_profile_id, services_id) values (?, 101)", id);
        return id;
    }

    private Long file(Long memberId, String visibility) {
        String path = UUID.randomUUID() + ".png";
        jdbcTemplate.update("insert into file(uploader_id, storage_path, original_name, content_type, size, visibility, created_at) values (?, ?, 'profile.png', 'image/png', 100, ?, CURRENT_TIMESTAMP)",
                memberId, path, visibility);
        return jdbcTemplate.queryForObject("select id from file where storage_path = ?", Long.class, path);
    }

    private String imageUrl(Long fileId) {
        String path = jdbcTemplate.queryForObject("select storage_path from file where id = ?", String.class, fileId);
        return storageClient.createUrl(UploadFileVisibility.PUBLIC, path);
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }
}
