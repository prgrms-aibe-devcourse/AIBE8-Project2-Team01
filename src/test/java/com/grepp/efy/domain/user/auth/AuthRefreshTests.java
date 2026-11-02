package com.grepp.efy.domain.user.auth;

import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.JwtProvider;
import com.grepp.efy.global.security.RefreshTokenCookie;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.SecretKey;
import java.net.HttpCookie;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthRefreshTests {
    private static final String URL = "/api/public/auth/refresh";
    @Autowired MockMvc mockMvc;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MemberService memberService;
    @Autowired RefreshTokenService refreshTokenService;
    @Autowired JwtProvider jwtProvider;
    @Value("${jwt.secret}") String secret;
    private final List<Long> memberIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (Long memberId : memberIds) {
            jdbcTemplate.update("delete from refresh_token where member_id = ?", memberId);
            jdbcTemplate.update("delete from member where id = ?", memberId);
        }
    }

    @Test
    void 만료된_Access와_유효한_Refresh로_재발급하고_현재권한반영() throws Exception {
        Long memberId = createMember();
        String accessToken = expiredToken(memberId);
        String refreshToken = issued(memberId);
        jdbcTemplate.update("update member set role = 'EXPERT' where id = ?", memberId);
        MvcResult result = mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .cookie(cookie(refreshToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
        String newAccess = new ObjectMapper().readTree(result.getResponse().getContentAsString()).get("accessToken").asString();
        Claims claims = jwtProvider.parseToken(newAccess);
        assertEquals(memberId.toString(), claims.getSubject());
        assertEquals("EXPERT", claims.get("role", String.class));
        assertEquals(0L, claims.get("tokenVersion", Long.class));
        String header = result.getResponse().getHeader(HttpHeaders.SET_COOKIE);
        assertNotNull(header);
        assertTrue(header.contains("HttpOnly"));
        assertTrue(header.contains("Secure"));
        String newRefresh = HttpCookie.parse(header).getFirst().getValue();
        assertNotEquals(refreshToken, newRefresh);
        assertEquals(0, count(refreshToken));
        assertEquals(1, count(newRefresh));
        mockMvc.perform(get("/api/members/me").header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccess))
                .andExpect(status().isOk()).andExpect(jsonPath("$.role").value("EXPERT"));

        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(refreshToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
        assertEquals(1, count(newRefresh));
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(newRefresh)))
                .andExpect(status().isOk());
    }

    @Test
    void 아직_유효한_Access는_400이고_Refresh는_소모하지않음() throws Exception {
        Long memberId = createMember();
        String refreshToken = issued(memberId);
        String accessToken = jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(refreshToken)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Access 토큰이 아직 만료되지 않았습니다."))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
        assertEquals(1, count(refreshToken));
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken(memberId)).cookie(cookie(refreshToken)))
                .andExpect(status().isOk());
    }

    @Test
    void Access_누락과_잘못된형식_위조서명과_서명없는토큰_차단() throws Exception {
        Long memberId = createMember();
        String refreshToken = issued(memberId);
        mockMvc.perform(post(URL).cookie(cookie(refreshToken))).andExpect(status().isUnauthorized());
        String wrongKeyToken = Jwts.builder().subject(memberId.toString()).claim("tokenVersion", 0)
                .expiration(past()).signWith(Keys.hmacShaKeyFor("different-test-key-0123456789012345678901234567890123456789".getBytes(StandardCharsets.UTF_8))).compact();
        String unsignedToken = Jwts.builder().subject(memberId.toString()).expiration(past()).compact();
        for (String authorization : List.of("", "Basic abc", "Bearer ", "Bearer invalid", "Bearer " + wrongKeyToken, "Bearer " + unsignedToken)) {
            mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, authorization).cookie(cookie(refreshToken)))
                    .andExpect(status().isUnauthorized());
            assertEquals(1, count(refreshToken));
        }
    }

    @Test
    void 다른회원의_토큰조합은_차단하고_Refresh교체도_롤백() throws Exception {
        Long memberId = createMember();
        Long otherMemberId = createMember();
        String refreshToken = issued(memberId);
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken(otherMemberId)).cookie(cookie(refreshToken)))
                .andExpect(status().isUnauthorized());
        assertEquals(1, count(refreshToken));
        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from refresh_token where member_id = ?", Integer.class, memberId));
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken(memberId)).cookie(cookie(refreshToken)))
                .andExpect(status().isOk());
    }

    @Test
    void 오래된_토큰버전과_버전누락_잘못된타입_회원누락_차단() throws Exception {
        Long memberId = createMember();
        jdbcTemplate.update("update member set token_version = 1 where id = ?", memberId);
        String refreshToken = issued(memberId);
        List<String> invalidTokens = List.of(
                expiredToken(memberId),
                Jwts.builder().subject(memberId.toString()).expiration(past()).signWith(key()).compact(),
                Jwts.builder().subject(memberId.toString()).claim("tokenVersion", "wrong").expiration(past()).signWith(key()).compact(),
                Jwts.builder().claim("tokenVersion", 1).expiration(past()).signWith(key()).compact(),
                Jwts.builder().subject(memberId.toString()).claim("tokenVersion", 1).notBefore(new Date(System.currentTimeMillis() + 60000)).expiration(past()).signWith(key()).compact());
        for (String accessToken : invalidTokens) {
            mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(refreshToken)))
                    .andExpect(status().isUnauthorized());
            assertEquals(1, count(refreshToken));
        }
    }

    @Test
    void Refresh_누락_잘못된값_만료_탈퇴회원_차단() throws Exception {
        Long memberId = createMember();
        String accessToken = expiredToken(memberId);
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, org.hamcrest.Matchers.containsString("Max-Age=0")));
        for (String refreshToken : List.of("", UUID.randomUUID().toString())) {
            mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(refreshToken)))
                    .andExpect(status().isUnauthorized());
        }
        String expiredRefresh = issued(memberId);
        jdbcTemplate.update("update refresh_token set expired_at = ? where token = ?", LocalDateTime.now().minusMinutes(1), expiredRefresh);
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(expiredRefresh)))
                .andExpect(status().isUnauthorized());
        String refreshToken = issued(memberId);
        jdbcTemplate.update("update member set deleted_at = CURRENT_TIMESTAMP where id = ?", memberId);
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그아웃_이전토큰은_재발급할수없음() throws Exception {
        Long memberId = createMember();
        String accessToken = expiredToken(memberId);
        String refreshToken = issued(memberId);
        String validAccess = jwtProvider.createToken(new CustomUserDetails(memberService.getMemberEntity(memberId)));
        mockMvc.perform(post("/api/auth/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + validAccess))
                .andExpect(status().isNoContent());
        mockMvc.perform(post(URL).header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken).cookie(cookie(refreshToken)))
                .andExpect(status().isUnauthorized());
        assertEquals(0, count(refreshToken));
    }

    @Test
    void Swagger에_재발급_만료조건과_400응답과_Bearer인증표시() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/public/auth/refresh'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/api/public/auth/refresh'].post.security[0].accessToken").exists());
    }

    private Long createMember() {
        String suffix = UUID.randomUUID().toString();
        String email = suffix + "@test.local";
        Long nextId = jdbcTemplate.queryForObject("select coalesce(max(id), 0) + 1 from member", Long.class);
        String phone = String.format("%011d", nextId);
        jdbcTemplate.update("insert into member(email, nickname, phone, role, created_at, token_version) values (?, ?, ?, 'USER', CURRENT_TIMESTAMP, 0)", email, suffix.substring(0, 20), phone);
        Long id = jdbcTemplate.queryForObject("select id from member where email = ?", Long.class, email);
        memberIds.add(id);
        return id;
    }

    private String issued(Long memberId) {
        return refreshTokenService.issued(memberService.getMemberEntity(memberId));
    }

    private String expiredToken(Long memberId) {
        return Jwts.builder().subject(memberId.toString()).claim("role", "USER").claim("tokenVersion", 0)
                .issuedAt(new Date(System.currentTimeMillis() - 120000)).expiration(past()).signWith(key()).compact();
    }

    private Date past() {
        return new Date(System.currentTimeMillis() - 60000);
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private Cookie cookie(String token) {
        return new Cookie(RefreshTokenCookie.NAME, token);
    }

    private Integer count(String token) {
        return jdbcTemplate.queryForObject("select count(*) from refresh_token where token = ?", Integer.class, token);
    }
}
