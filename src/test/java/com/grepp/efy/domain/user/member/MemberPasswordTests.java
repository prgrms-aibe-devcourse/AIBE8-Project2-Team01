package com.grepp.efy.domain.user.member;

import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 배포 DB 대신 테스트 H2를 사용하면서 비밀번호 최소 길이만 8자로 검증
@SpringBootTest(properties = "app.password-min-length=8")
@AutoConfigureMockMvc
@Transactional
class MemberPasswordTests {
    @Autowired MockMvc mockMvc;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    @Autowired JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 가입_비밀번호_7자는_거절하고_8자와_20자는_허용한다() throws Exception {
        signup("1234567", 400);
        signup("12345678", 201);
        signup("a".repeat(20), 201);
        signup("a".repeat(21), 400);
    }

    @Test
    void 수정_비밀번호를_검증하고_생략과_null은_기존값을_유지한다() throws Exception {
        String email = signup("12345678", 201);
        Long id = memberService.getMemberEntityByEmail(email).getId();
        String token = jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(id)));
        String hash = jdbcTemplate.queryForObject("select password from member where id = ?", String.class, id);
        for (String body : new String[]{"{}", "{\"password\":null}"}) {
            update(token, body, 200);
            assertEquals(hash, jdbcTemplate.queryForObject("select password from member where id = ?", String.class, id));
        }
        for (String password : new String[]{"1234567", "a".repeat(21), " ".repeat(8)}) {
            update(token, objectMapper.writeValueAsString(Map.of("password", password)), 400);
            assertEquals(hash, jdbcTemplate.queryForObject("select password from member where id = ?", String.class, id));
        }
        update(token, "{\"password\":\"abcdefgh\"}", 200);
        login(email, "abcdefgh", 200);
        login(email, "12345678", 401);
        mockMvc.perform(get("/api/members/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        update(token, objectMapper.writeValueAsString(Map.of("password", "b".repeat(20))), 200);
        login(email, "b".repeat(20), 200);
    }

    @Test
    void 비밀번호의_공백과_대소문자는_변경하지_않는다() throws Exception {
        String email = signup(" Abcdef ", 201);
        login(email, " Abcdef ", 200);
        login(email, "Abcdef", 401);
        login(email, " abcdef ", 401);
    }

    private String signup(String password, int expectedStatus) throws Exception {
        String suffix = UUID.randomUUID().toString();
        String email = suffix + "@test.local";
        long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String body = objectMapper.writeValueAsString(Map.of("email", email, "password", password,
                "nickname", suffix.substring(0, 20), "phone", String.format("%011d", nextId)));
        mockMvc.perform(post("/api/public/auth/signup").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is(expectedStatus));
        return email;
    }

    private void update(String token, String body, int expectedStatus) throws Exception {
        mockMvc.perform(put("/api/members/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().is(expectedStatus));
    }

    private void login(String email, String password, int expectedStatus) throws Exception {
        mockMvc.perform(post("/api/public/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().is(expectedStatus));
    }
}
