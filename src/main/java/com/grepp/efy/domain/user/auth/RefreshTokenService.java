package com.grepp.efy.domain.user.auth;

import com.grepp.efy.domain.user.auth.dto.RefreshResult;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationMillis;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMillis
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationMillis = refreshExpirationMillis;
    }

    @Transactional
    public String issued(Member member) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setMember(member);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiredAt(
                LocalDateTime.now().plus(
                        refreshExpirationMillis,
                        ChronoUnit.MILLIS
                )
        );

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    @Transactional
    public RefreshResult refresh(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION));

        LocalDateTime now = LocalDateTime.now();
        if (!refreshToken.getExpiredAt().isAfter(now)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION);
        }

        Member member = refreshToken.getMember();
        if (member.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION);
        }

        // 같은 토큰이 동시에 사용돼도 삭제에 성공한 요청만 재발급한다.
        if (refreshTokenRepository.deleteByToken(token) != 1) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN_EXCEPTION);
        }

        CustomUserDetails userDetails = new CustomUserDetails(member);
        String newRefreshToken = issued(member);
        return new RefreshResult(userDetails, newRefreshToken);
    }

    @Transactional
    public void deleteByMemberId(Long memberId) {
        refreshTokenRepository.deleteByMemberId(memberId);
    }
}
