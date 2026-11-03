package com.grepp.efy.domain.community.comment;

import com.grepp.efy.domain.community.post.Post;
import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 400)
    private String content;

    // 명세상 최대 1장이지만 게시글 이미지와 같은 구조로 둔다
    @ManyToMany
    @BatchSize(size = 100)
    @JoinTable(name = "post_comment_images",
            joinColumns = @JoinColumn(name = "comment_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id"))
    @OrderColumn(name = "sort_order")
    private List<UploadFile> images = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt; // 값이 있으면 삭제된 댓글

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
    private PostComment(Post post, Member member, String content, List<UploadFile> images) {
        this.post = post;
        this.member = member;
        this.content = content;
        if (images != null) {
            this.images.addAll(images);
        }
    }

    // 바뀐 게 없으면 false (updatedAt 그대로)
    public boolean update(String content, List<UploadFile> images) {
        boolean changed = !this.content.equals(content) || !this.images.equals(images);

        if (!changed) {
            return false;
        }

        this.content = content;
        this.images.clear();
        this.images.addAll(images);
        // 이미지만 바뀌면 post_comment 행 UPDATE가 없어 @PreUpdate가 안 돌므로 직접 갱신
        this.updatedAt = LocalDateTime.now();
        return true;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
