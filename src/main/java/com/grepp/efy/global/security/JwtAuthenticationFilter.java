package com.grepp.efy.global.security;

import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;

@RequiredArgsConstructor
@NullMarked
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

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
                Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId).orElse(null);

                LocalDateTime issuedAt = LocalDateTime.ofInstant(
                        claims.getIssuedAt().toInstant(), ZoneId.systemDefault()
                );

                if (member != null
                        && (member.getTokenInvalidBefore() == null || !issuedAt.isBefore(member.getTokenInvalidBefore()))) {
                    UsernamePasswordAuthenticationToken authentication =
                            getUsernamePasswordAuthenticationToken(member);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException | IllegalArgumentException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}
