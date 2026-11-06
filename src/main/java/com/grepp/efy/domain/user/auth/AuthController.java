package com.grepp.efy.domain.user.auth;

import com.grepp.efy.domain.user.auth.docs.AuthControllerDocs;

import com.grepp.efy.domain.user.auth.dto.LoginRequest;
import com.grepp.efy.domain.user.auth.dto.LoginResponse;
import com.grepp.efy.domain.user.auth.dto.LoginResult;
import com.grepp.efy.domain.user.member.dto.MemberCreateRequest;
import com.grepp.efy.domain.user.member.dto.MemberResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import com.grepp.efy.global.security.RefreshTokenCookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController implements AuthControllerDocs {

    private final AuthService authService;
    private final RefreshTokenCookie refreshTokenCookie;

    @PostMapping("/public/auth/signup")
    @Override
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody MemberCreateRequest memberCreateRequest) {
        MemberResponse memberResponse = authService.signup(memberCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @PostMapping("/public/auth/login")
    @Override
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResult loginResult = authService.login(loginRequest);
        LoginResponse loginResponse = new LoginResponse(loginResult.accessToken(), "Bearer");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.create(loginResult.refreshToken()).toString())
                .body(loginResponse);
    }

    @PostMapping("/public/auth/refresh")
    @Override
    public ResponseEntity<LoginResponse> refresh(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @CookieValue(value = RefreshTokenCookie.NAME, required = false) String refreshToken) {
        LoginResult loginResult = authService.refresh(authorization, refreshToken);
        LoginResponse loginResponse = new LoginResponse(loginResult.accessToken(), "Bearer");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.create(loginResult.refreshToken()).toString())
                .body(loginResponse);
    }

    @PostMapping("/auth/logout")
    @Override
    public ResponseEntity<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getMemberId());
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.expire().toString())
                .build();
    }
}
