package com.grepp.efy.domain.reaction.bookmark;

import com.grepp.efy.domain.user.expertprofile.ExpertProfile;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 한 회원은 한 고수 프로필에 기록 하나 (동시 요청도 DB 유니크 제약으로 막는다)
// 취소는 기록을 지우지 않고 active = false. 다시 등록하면 같은 기록을 되살린다
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "expert_profile_id"}))
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expert_profile_id", nullable = false)
    private ExpertProfile expertProfile;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime bookmarkedAt;

    public Bookmark(Member member, ExpertProfile expertProfile) {
        this.member = member;
        this.expertProfile = expertProfile;
        this.bookmarkedAt = LocalDateTime.now();
    }

    // 취소했던 북마크를 다시 등록. 북마크 시각도 지금으로
    public void activate() {
        this.active = true;
        this.bookmarkedAt = LocalDateTime.now();
    }
}
