package com.grepp.efy.domain.reaction.postlike;

import com.grepp.efy.domain.community.comment.PostComment;
import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 한 회원은 한 댓글에 한 번만 (동시 요청도 DB 유니크 제약으로 막는다)
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "comment_id"}))
public class PostCommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private PostComment comment;

    public PostCommentLike(Member member, PostComment comment) {
        this.member = member;
        this.comment = comment;
    }
}
