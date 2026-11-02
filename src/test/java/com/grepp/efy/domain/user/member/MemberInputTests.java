package com.grepp.efy.domain.user.member;

import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "app.password-min-length=4")
@AutoConfigureMockMvc
@Transactional
class MemberInputTests {
    @Autowired MockMvc mockMvc;
    @Autowired MemberService memberService;
    @Autowired JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final AtomicLong PHONE = new AtomicLong(99900000000L);

    @Test
    void 가입_공백제거와_이메일소문자변환_후_로그인한다() throws Exception {
        Map<String, String> request = fresh();
        String email = request.get("email");
        String nickname = request.get("nickname");
        String phone = request.get("phone");
        request.put("email", "  " + email.toUpperCase(java.util.Locale.ROOT) + "  ");
        request.put("nickname", "  " + nickname + "  ");
        request.put("phone", "  " + phone + "  ");
        JsonNode response = signup(request, 201);
        assertEquals(email, response.get("email").asString());
        assertEquals(nickname, response.get("nickname").asString());
        assertEquals(phone, response.get("phone").asString());
        mockMvc.perform(post("/api/public/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("email", email, "password", "1234"))))
                .andExpect(status().isOk());
    }

    @Test
    void 로컬_비밀번호_3자는_거절하고_4자는_허용한다() throws Exception {
        Map<String, String> request = fresh();
        request.put("password", "123");
        signup(request, 400);
        request.put("password", "1234");
        signup(request, 201);
    }

    @Test
    void 가입_이메일과_닉네임_길이경계_및_공백을_검증한다() throws Exception {
        Map<String, String> request = fresh();
        request.put("email", "a".repeat(45) + "@a.co");
        request.put("nickname", "가");
        signup(request, 201);
        request = fresh();
        request.put("nickname", "나".repeat(20));
        signup(request, 201);
        for (Map.Entry<String, String> invalid : Map.of(
                "email", "a".repeat(46) + "@a.co", "nickname", "다".repeat(21)).entrySet()) {
            request = fresh();
            request.put(invalid.getKey(), invalid.getValue());
            signup(request, 400);
        }
        for (String field : new String[]{"email", "nickname"}) {
            request = fresh();
            request.put(field, "   ");
            signup(request, 400);
        }
        request = fresh();
        request.put("email", "invalid-email");
        signup(request, 400);
    }

    @Test
    void 가입과_수정에서_잘못된_전화번호를_거절한다() throws Exception {
        Map<String, String> original = fresh();
        JsonNode created = signup(original, 201);
        String token = token(created.get("id").asLong());
        for (String phone : new String[]{"", "   ", "0101234567", "010123456789", "010-1234-5678", "abcdefghijk", "010 1234567"}) {
            Map<String, String> request = fresh();
            request.put("phone", phone);
            signup(request, 400);
            update(token, Map.of("phone", phone), 400);
        }
        Map<String, String> request = fresh();
        request.remove("phone");
        signup(request, 400);
        request.put("phone", null);
        signup(request, 400);
        assertEquals(original.get("phone"), memberService.getMemberEntity(created.get("id").asLong()).getPhone());
    }

    @Test
    void 가입은_정규화후_중복을_거절하고_수정은_본인값을_허용한다() throws Exception {
        Map<String, String> original = fresh();
        JsonNode created = signup(original, 201);
        for (String field : new String[]{"email", "nickname", "phone"}) {
            Map<String, String> request = fresh();
            String value = original.get(field);
            request.put(field, "  " + (field.equals("email") ? value.toUpperCase(java.util.Locale.ROOT) : value) + "  ");
            signup(request, 409);
        }
        String token = token(created.get("id").asLong());
        update(token, Map.of("nickname", " " + original.get("nickname") + " ", "phone", " " + original.get("phone") + " "), 200);
        Map<String, String> other = fresh();
        signup(other, 201);
        for (String field : new String[]{"nickname", "phone"}) {
            update(token, Map.of(field, " " + other.get(field) + " "), 409);
        }
    }

    @Test
    void 수정_생략과_null은_유지하고_닉네임_길이경계를_검증한다() throws Exception {
        Map<String, String> original = fresh();
        long id = signup(original, 201).get("id").asLong();
        String token = token(id);
        update(token, Map.of(), 200);
        Map<String, String> nulls = new HashMap<>();
        nulls.put("nickname", null);
        nulls.put("phone", null);
        update(token, nulls, 200);
        assertEquals(original.get("nickname"), memberService.getMemberEntity(id).getNickname());
        assertEquals(original.get("phone"), memberService.getMemberEntity(id).getPhone());
        for (String nickname : new String[]{"", "   ", "가".repeat(21)}) {
            update(token, Map.of("nickname", nickname), 400);
        }
        update(token, Map.of("nickname", " 가 "), 200);
        assertEquals("가", memberService.getMemberEntity(id).getNickname());
        update(token, Map.of("nickname", "나".repeat(20)), 200);
        String phone = String.valueOf(PHONE.incrementAndGet());
        update(token, Map.of("phone", " " + phone + " "), 200);
        assertEquals(phone, memberService.getMemberEntity(id).getPhone());
    }

    private Map<String, String> fresh() {
        String suffix = UUID.randomUUID().toString();
        return new HashMap<>(Map.of("email", suffix + "@test.local", "password", "1234",
                "nickname", suffix.substring(0, 20), "phone", String.valueOf(PHONE.incrementAndGet())));
    }

    private JsonNode signup(Map<String, String> request, int expectedStatus) throws Exception {
        String response = mockMvc.perform(post("/api/public/auth/signup").contentType(MediaType.APPLICATION_JSON)
                        .content(json(request)))
                .andExpect(status().is(expectedStatus)).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response);
    }

    private void update(String token, Map<String, String> request, int expectedStatus) throws Exception {
        mockMvc.perform(put("/api/members/me").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content(json(request)))
                .andExpect(status().is(expectedStatus));
    }

    private String token(long id) {
        return jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(id)));
    }

    private String json(Object value) {
        return objectMapper.writeValueAsString(value);
    }
}
