package com.grepp.efy.domain.user.member;

import com.grepp.efy.domain.file.UploadFileVisibility;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MemberImageTests {
    private static final String URL = "/api/members/me/image";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired SupabaseStorageClient storageClient;
    @MockitoSpyBean MemberMapper memberMapper;
    private final List<Long> memberIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) jdbcTemplate.update("update member set image_id = null where id = ?", id);
        for (Long id : memberIds) {
            jdbcTemplate.update("delete from expert_profile where member_id = ?", id);
            jdbcTemplate.update("delete from file where uploader_id = ?", id);
            jdbcTemplate.update("delete from member where id = ?", id);
        }
    }

    @Test
    void 모든역할의_본인사진_변경과_연결해제는_고수공개조회에도_반영된다() throws Exception {
        for (String role : List.of("USER", "EXPERT", "ADMIN")) {
            Long memberId = member(role);
            String token = token(memberId);
            jdbcTemplate.update("""
                    insert into expert_profile(member_id, short_intro, description, primary_category_id, is_nationwide)
                    values (?, '소개', '서비스 상세 설명입니다.', 101, true)
                    """, memberId);
            Long profileId = jdbcTemplate.queryForObject("select id from expert_profile where member_id = ?", Long.class, memberId);
            myImage(token, null, null);
            for (int i = 0; i < 2; i++) {
                Long fileId = file(memberId, "PUBLIC", false);
                String path = jdbcTemplate.queryForObject("select storage_path from file where id = ?", String.class, fileId);
                String url = storageClient.createUrl(UploadFileVisibility.PUBLIC, path);
                change(token, "{\"imageId\":" + fileId + "}", 200);
                myImage(token, fileId, url);
                mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                        .andExpect(status().isOk()).andExpect(jsonPath("$.imageId").value(fileId.intValue()))
                        .andExpect(jsonPath("$.imageUrl").value(url));
            }
            for (int i = 0; i < 2; i++) {
                mockMvc.perform(delete(URL).header("Authorization", "Bearer " + token)).andExpect(status().isNoContent());
            }
            myImage(token, null, null);
            mockMvc.perform(get("/api/public/expert-profiles/" + profileId))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.imageId").isEmpty())
                    .andExpect(jsonPath("$.imageUrl").isEmpty());
            assertEquals(2, jdbcTemplate.queryForObject("select count(*) from file where uploader_id = ?", Integer.class, memberId));
        }
    }

    @Test
    void 잘못된파일이나_입력은_거절하고_기존사진을_유지한다() throws Exception {
        Long id = member("USER");
        String token = token(id);
        Long original = file(id, "PUBLIC", false);
        change(token, "{\"imageId\":" + original + "}", 200);
        Long other = member("USER");
        List<Long> files = List.of(file(other, "PUBLIC", false), file(id, "PRIVATE", false),
                file(id, "PUBLIC", true), Long.MAX_VALUE);
        int[] statuses = {403, 400, 404, 404};
        for (int i = 0; i < files.size(); i++) {
            change(token, "{\"imageId\":" + files.get(i) + "}", statuses[i]);
            assertEquals(original, storedImage(id));
        }
        for (String body : List.of("{}", "{\"imageId\":null}", "{\"imageId\":\"invalid\"}")) {
            change(token, body, 400);
            assertEquals(original, storedImage(id));
        }
    }

    @Test
    void 응답변환이_실패하면_사진변경도_롤백한다() throws Exception {
        Long id = member("USER");
        Long original = file(id, "PUBLIC", false);
        Long replacement = file(id, "PUBLIC", false);
        jdbcTemplate.update("update member set image_id = ? where id = ?", original, id);
        doThrow(new BusinessException(ErrorCode.INVALID_INPUT_EXCEPTION))
                .when(memberMapper).toResponse(any(Member.class), nullable(String.class));
        change(token(id), "{\"imageId\":" + replacement + "}", 400);
        assertEquals(original, storedImage(id));
    }

    @Test
    void 미인증과_탈퇴회원은_사진변경과_연결해제를_할수없다() throws Exception {
        mockMvc.perform(put(URL).contentType(MediaType.APPLICATION_JSON).content("{\"imageId\":1}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(delete(URL)).andExpect(status().isUnauthorized());
        Long id = member("USER");
        String token = token(id);
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", id);
        change(token, "{\"imageId\":1}", 401);
        mockMvc.perform(delete(URL).header("Authorization", "Bearer " + token)).andExpect(status().isUnauthorized());
    }

    @Test
    void Swagger에_사진변경과_연결해제_경로가_표시된다() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/members/me/image'].put.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/api/members/me/image'].delete.responses['204']").exists());
    }

    private void change(String token, String body, int expectedStatus) throws Exception {
        mockMvc.perform(put(URL).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().is(expectedStatus));
    }

    private void myImage(String token, Long id, String url) throws Exception {
        ResultActions result = mockMvc.perform(get("/api/members/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        if (id == null) result.andExpect(jsonPath("$.imageId").isEmpty()).andExpect(jsonPath("$.imageUrl").isEmpty());
        else result.andExpect(jsonPath("$.imageId").value(id.intValue())).andExpect(jsonPath("$.imageUrl").value(url));
    }

    private Long storedImage(Long id) {
        return jdbcTemplate.queryForObject("select image_id from member where id = ?", Long.class, id);
    }

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

    private Long file(Long memberId, String visibility, boolean deleted) {
        String path = UUID.randomUUID() + ".png";
        jdbcTemplate.update("insert into file(uploader_id, storage_path, original_name, content_type, size, visibility, created_at) values (?, ?, 'profile.png', 'image/png', 100, ?, CURRENT_TIMESTAMP)", memberId, path, visibility);
        Long id = jdbcTemplate.queryForObject("select id from file where storage_path = ?", Long.class, path);
        if (deleted) jdbcTemplate.update("update file set deleted_at = CURRENT_TIMESTAMP where id = ?", id);
        return id;
    }

    private String token(Long memberId) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }
}
