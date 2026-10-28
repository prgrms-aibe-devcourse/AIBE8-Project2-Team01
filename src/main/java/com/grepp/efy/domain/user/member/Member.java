package com.grepp.efy.domain.user.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    private String password;

    @Column(length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(length = 11, unique = true)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role; // USER, EXPERT, ADMIN

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime tokenInvalidBefore; // 토큰 무효화 기준 시간

    private LocalDateTime deletedAt; // 값이 있으면 탈퇴한 회원

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
