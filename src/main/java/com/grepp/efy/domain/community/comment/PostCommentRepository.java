package com.grepp.efy.domain.community.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    // 작성자와 프로필 사진은 fetch join, 댓글 이미지는 @BatchSize로 IN 조회
    @Query(value = """
        select c from PostComment c
        join fetch c.member m
        left join fetch m.image
        where c.post.id = :postId and c.deletedAt is null
        """,
            countQuery = """
        select count(c) from PostComment c
        where c.post.id = :postId and c.deletedAt is null
        """)
    Page<PostComment> findComments(@Param("postId") Long postId, Pageable pageable);

    // 동시에 여러 좋아요가 들어와도 어긋나지 않도록 DB에서 직접 증감 (감소는 0보다 클 때만)
    @Modifying
    @Query("update PostComment c set c.likeCount = c.likeCount + 1 where c.id = :id")
    void increaseLikeCount(@Param("id") Long id);

    @Modifying
    @Query("update PostComment c set c.likeCount = c.likeCount - 1 where c.id = :id and c.likeCount > 0")
    void decreaseLikeCount(@Param("id") Long id);

    // 엔티티가 아니라 값만 조회하므로 영속성 컨텍스트가 아닌 DB의 현재 값을 읽는다
    @Query("select c.likeCount from PostComment c where c.id = :id")
    long findLikeCount(@Param("id") Long id);
}
