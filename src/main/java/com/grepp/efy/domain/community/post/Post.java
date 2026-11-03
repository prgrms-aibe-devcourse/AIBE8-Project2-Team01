package com.grepp.efy.domain.community.post;

import com.grepp.efy.domain.basedata.secondcategory.SecondCategory;
import com.grepp.efy.domain.basedata.secondregion.SecondRegion;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BoardType boardType;

    @Column(nullable = false)
    @Size(min = 2, max = 30, message = "제목은 2자 이상 30자 이하로 입력해 주세요.")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(min = 2, max = 5000, message = "본문은 2자 이상 5000자 이하로 입력해 주세요.")
    private String content;

    @ManyToMany
    @BatchSize(size = 100)
    @JoinTable(name = "post_images",
            joinColumns =  @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    @OrderColumn(name = "sort_order")
    private List<UploadFile> images = new ArrayList<>();

    @Column(nullable = false)
    private long viewCount;

    // 삭제되지 않은 댓글 수. 댓글 작성·삭제 때 DB에서 직접 증감
    // 기존 행이 있는 테이블에 컬럼을 추가해도 실패하지 않도록 DB 기본값 0
    @ColumnDefault("0")
    @Column(nullable = false)
    private long commentCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt; // 값이 있으면 삭제된 글

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
    private Post(Member member,
                 SecondCategory secondCategory,
                 SecondRegion secondRegion,
                 BoardType boardType,
                 String title,
                 String content,
                 List<UploadFile> images) {
        this.member = member;
        this.secondCategory = secondCategory;
        this.secondRegion = secondRegion;
        this.boardType = boardType;
        this.title = title;
        this.content = content;
        if (images != null) {
            this.images.addAll(images);
        }
    }

    public boolean update(SecondCategory secondCategory,
                          SecondRegion secondRegion,
                          String title,
                          String content,
                          List<UploadFile> images) {
        boolean changed = this.secondCategory != secondCategory
                || this.secondRegion != secondRegion
                || !this.title.equals(title)
                || !this.content.equals(content)
                || !this.images.equals(images);

        if (!changed) {
            return false;
        }

        this.secondCategory = secondCategory;
        this.secondRegion = secondRegion;
        this.title = title;
        this.content = content;
        this.images.clear();
        this.images.addAll(images);
        // 이미지만 바뀌면 post 행 UPDATE가 없어 @PreUpdate가 안 돌므로 직접 갱신
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}

