package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 관리자가 게시글 공개 여부를 바꾼 기록 (삭제하지 않음)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostVisibilityHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id", nullable = false)
    private Member admin;

    // 변경 후 상태
    @Column(nullable = false)
    private boolean isVisible;

    @Column(length = 200)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    private PostVisibilityHistory(Post post, Member admin, boolean isVisible, String reason) {
        this.post = post;
        this.admin = admin;
        this.isVisible = isVisible;
        this.reason = reason;
    }
}
