package com.grepp.efy.global.security;

import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberService;
import com.grepp.efy.global.exception.BusinessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
@NullMarked
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    private static @Nullable String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring("Bearer ".length());
        }
        return token;
    }

    private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(Member member) {
        CustomUserDetails userDetails = new CustomUserDetails(member);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        if (token != null) {
            try {
                Claims claims = jwtProvider.parseToken(token);

                Long memberId = Long.valueOf(claims.getSubject());
                Member member = memberService.getMemberEntity(memberId);

                Long tokenVersion = claims.get("tokenVersion", Long.class);

                if (member != null && Objects.equals(tokenVersion, member.getTokenVersion())) {
                    UsernamePasswordAuthenticationToken authentication =
                            getUsernamePasswordAuthenticationToken(member);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException | BusinessException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
