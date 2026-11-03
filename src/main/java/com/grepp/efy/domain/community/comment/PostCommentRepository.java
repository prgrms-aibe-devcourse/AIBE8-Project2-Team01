package com.grepp.efy.domain.community.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
