package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_category_id")
    private SecondCategory secondCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "second_region_id")
    private SecondRegion secondRegion;

    @Column(nullable = false)
    @Size(min = 2, max = 30, message = "제목은 2자 이상 30자 이하로 입력해주세요.")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(max = 5000, message = "본문은 5000자 이하로 입력해주세요.")
    private String content;

    @Column(nullable = false)
    private long viewCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private boolean isVisible = true;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    private Post(Member member, SecondCategory secondCategory, SecondRegion secondRegion, String title, String content) {
        this.member = member;
        this.secondCategory = secondCategory;
        this.secondRegion = secondRegion;
        this.title = title;
        this.content = content;
    }
}

