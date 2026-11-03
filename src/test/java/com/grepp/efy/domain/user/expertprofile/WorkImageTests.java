package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
@AutoConfigureMockMvc
class WorkImageTests {
    private static final String URL = "/api/members/me/expert-profile";
    private static final String BODY = """
            {"shortIntro":"작업 사진 검증","description":"작업 사진 연결 동작을 검증합니다.",
             "serviceIds":[101],"primaryCategoryId":101,"nationwide":true}
            """;
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<Long> members = new ArrayList<>();
    private Long memberId;
    private String token;

    @BeforeEach
    void 준비() {
        memberId = member();
        token = jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
    }

    @AfterEach
    void 정리() {
        for (Long id : members) {
            jdbcTemplate.update("delete from work_image where expert_profile_id in (select id from expert_profile where member_id=?)", id);
            for (String table : List.of("expert_profile_services", "expert_profile_whole_regions", "expert_profile_regions")) {
                jdbcTemplate.update("delete from " + table + " where expert_profile_id in (select id from expert_profile where member_id=?)", id);
            }
            jdbcTemplate.update("delete from expert_profile where member_id=?", id);
            jdbcTemplate.update("delete from file where uploader_id=?", id);
            jdbcTemplate.update("delete from member where id=?", id);
        }
    }

    @Test
    void 등록후_공개조회에_입력순서와설명_URL을_반환한다() throws Exception {
        Long a = file(memberId, "PUBLIC", "image/png", 100, false);
        Long b = file(memberId, "PUBLIC", "image/webp", 100, false);
        long profile = create("[{\"fileId\":" + b + ",\"description\":\"  두번째 파일  \"},{\"fileId\":" + a + "}]");
        mockMvc.perform(get("/api/public/expert-profiles/" + profile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workImages.length()").value(2))
                .andExpect(jsonPath("$.workImages[0].fileId").value(b.intValue()))
                .andExpect(jsonPath("$.workImages[0].description").value("두번째 파일"))
                .andExpect(jsonPath("$.workImages[0].url").isNotEmpty())
                .andExpect(jsonPath("$.workImages[1].fileId").value(a.intValue()))
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.phone").doesNotExist());
    }

    @Test
    void 수정은_추가삭제설명순서를_교체하고_생략과null은유지하며_빈배열은연결해제한다() throws Exception {
        Long a = file(memberId, "PUBLIC", "image/png", 100, false);
        Long b = file(memberId, "PUBLIC", "image/png", 100, false);
        Long c = file(memberId, "PUBLIC", "image/jpeg", 100, false);
        long profile = create("[{\"fileId\":" + a + "},{\"fileId\":" + b + "}]");
        String replacement = "{\"workImages\":[{\"fileId\":" + b + ",\"description\":\"설명 수정\"},{\"fileId\":" + c + "}]}";
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(replacement))
                .andExpect(status().isOk()).andExpect(jsonPath("$.workImages[0].fileId").value(b.intValue()));
        for (String body : List.of("{}", "{\"workImages\":null}")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.workImages.length()").value(2));
        }
        mockMvc.perform(get("/api/public/expert-profiles/" + profile))
                .andExpect(status().isOk()).andExpect(jsonPath("$.workImages[0].description").value("설명 수정"))
                .andExpect(jsonPath("$.workImages[1].fileId").value(c.intValue()));
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON).content("{\"workImages\":[]}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.workImages").isEmpty());
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from work_image where expert_profile_id=?", Integer.class, profile));
        assertEquals(3, jdbcTemplate.queryForObject("select count(*) from file where uploader_id=?", Integer.class, memberId));
    }

    @Test
    void 잘못된사진수정은_거절하고_기존프로필과사진을_유지한다() throws Exception {
        Long original = file(memberId, "PUBLIC", "image/png", 100, false);
        long profile = create("[{\"fileId\":" + original + "}]");
        Long foreign = file(member(), "PUBLIC", "image/png", 100, false);
        Long hidden = file(memberId, "PRIVATE", "image/png", 100, false);
        Long deleted = file(memberId, "PUBLIC", "image/png", 100, true);
        Long pdf = file(memberId, "PUBLIC", "application/pdf", 100, false);
        Long large = file(memberId, "PUBLIC", "image/png", 5 * 1024 * 1024 + 1, false);
        List<Long> ids = List.of(foreign, hidden, deleted, pdf, large, Long.MAX_VALUE);
        int[] statuses = {403, 400, 404, 400, 400, 404};
        for (int i = 0; i < ids.size(); i++) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"shortIntro\":\"변경되면 안됨\",\"workImages\":[{\"fileId\":" + ids.get(i) + "}]}"))
                    .andExpect(status().is(statuses[i]));
            mockMvc.perform(get("/api/public/expert-profiles/" + profile)).andExpect(status().isOk())
                    .andExpect(jsonPath("$.shortIntro").value("작업 사진 검증"))
                    .andExpect(jsonPath("$.workImages[0].fileId").value(original.intValue()));
        }
        for (String images : List.of("[null]", "[{}]", "[{\"fileId\":null}]",
                "[{\"fileId\":" + original + "},{\"fileId\":" + original + "}]",
                "[{\"fileId\":" + original + ",\"description\":\"" + "가".repeat(201) + "\"}]",
                "[" + String.join(",", java.util.Collections.nCopies(11, "{\"fileId\":" + original + "}")) + "]")) {
            mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                            .content("{\"workImages\":" + images + "}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void 잘못된등록은_프로필과역할변경을_롤백한다() throws Exception {
        Long hidden = file(memberId, "PRIVATE", "image/png", 100, false);
        mockMvc.perform(post(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.strip().replace("}", ",\"workImages\":[{\"fileId\":" + hidden + "}]}")))
                .andExpect(status().isBadRequest());
        assertEquals("USER", jdbcTemplate.queryForObject("select role from member where id=?", String.class, memberId));
        assertEquals(0, jdbcTemplate.queryForObject("select count(*) from expert_profile where member_id=?", Integer.class, memberId));
    }

    @Test
    void 사진없는등록은빈배열_이후비공개삭제파일은공개조회에서제외한다() throws Exception {
        long profile = create("[]");
        mockMvc.perform(get("/api/public/expert-profiles/" + profile)).andExpect(status().isOk()).andExpect(jsonPath("$.workImages").isEmpty());
        Long a = file(memberId, "PUBLIC", "image/png", 100, false);
        Long b = file(memberId, "PUBLIC", "image/png", 100, false);
        mockMvc.perform(patch(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"workImages\":[{\"fileId\":" + a + "},{\"fileId\":" + b + "}]}"))
                .andExpect(status().isOk());
        jdbcTemplate.update("update file set visibility='PRIVATE' where id=?", a);
        jdbcTemplate.update("update file set deleted_at=CURRENT_TIMESTAMP where id=?", b);
        mockMvc.perform(get("/api/public/expert-profiles/" + profile)).andExpect(status().isOk()).andExpect(jsonPath("$.workImages").isEmpty());
        mockMvc.perform(patch(URL).contentType(MediaType.APPLICATION_JSON).content("{\"workImages\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 최대열장_설명이백자_파일오메가바이트를_허용한다() throws Exception {
        List<String> images = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long id = file(memberId, "PUBLIC", "image/jpeg; charset=UTF-8", 5 * 1024 * 1024, false);
            images.add("{\"fileId\":" + id + ",\"description\":\"" + "가".repeat(200) + "\"}");
        }
        long profile = create("[" + String.join(",", images) + "]");
        mockMvc.perform(get("/api/public/expert-profiles/" + profile)).andExpect(status().isOk())
                .andExpect(jsonPath("$.workImages.length()").value(10))
                .andExpect(jsonPath("$.workImages[0].description").value("가".repeat(200)));
    }

    private long create(String images) throws Exception {
        String response = mockMvc.perform(post(URL).header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON)
                        .content(BODY.strip().replace("}", ",\"workImages\":" + images + "}")))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return mapper.readTree(response).get("id").asLong();
    }

    private Long member() {
        String suffix = UUID.randomUUID().toString();
        Long next = jdbcTemplate.queryForObject("select coalesce(max(id),0)+1 from member", Long.class);
        jdbcTemplate.update("insert into member(email,nickname,phone,role,created_at,token_version) values(?,?,?,'USER',CURRENT_TIMESTAMP,0)",
                suffix + "@test.local", suffix.substring(0,20), String.format("%011d", next));
        Long id = jdbcTemplate.queryForObject("select id from member where email=?", Long.class, suffix + "@test.local");
        members.add(id);
        return id;
    }

    private Long file(Long uploader, String visibility, String type, long size, boolean deleted) {
        String path = UUID.randomUUID() + ".png";
        jdbcTemplate.update("insert into file(uploader_id,storage_path,original_name,content_type,size,visibility,created_at,deleted_at) values(?,?,'work.png',?,?,?,CURRENT_TIMESTAMP,?)",
                uploader, path, type, size, visibility, deleted ? java.time.LocalDateTime.now() : null);
        return jdbcTemplate.queryForObject("select id from file where storage_path=?", Long.class, path);
    }
}
