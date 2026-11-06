package com.grepp.efy.domain.reaction.postlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {

    boolean existsByMemberIdAndCommentId(Long memberId, Long commentId);

    // 지운 행 수를 돌려준다. 동시에 두 번 취소해도 한 번만 1이 나온다
    @Modifying
    @Query("delete from PostCommentLike l where l.member.id = :memberId and l.comment.id = :commentId")
    int deleteByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("commentId") Long commentId);

    // 댓글 목록 한 페이지에서 내가 좋아요한 댓글 ID (댓글마다 조회하지 않도록 IN 한 번)
    @Query("select l.comment.id from PostCommentLike l where l.member.id = :memberId and l.comment.id in :commentIds")
    List<Long> findLikedCommentIds(@Param("memberId") Long memberId, @Param("commentIds") Collection<Long> commentIds);
}
