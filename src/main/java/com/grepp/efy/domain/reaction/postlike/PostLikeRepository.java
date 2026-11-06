package com.grepp.efy.domain.reaction.postlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    // 지운 행 수를 돌려준다. 동시에 두 번 취소해도 한 번만 1이 나온다
    @Modifying
    @Query("delete from PostLike l where l.member.id = :memberId and l.post.id = :postId")
    int deleteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);
}
