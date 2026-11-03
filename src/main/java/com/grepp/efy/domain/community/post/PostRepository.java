package com.grepp.efy.domain.community.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :id")
    void increaseViewCount(@Param("id") Long id);

    @Query(value = """
        select p from Post p
        join fetch p.member
        left join fetch p.secondCategory sc left join fetch sc.firstCategory
        left join fetch p.secondRegion sr left join fetch sr.firstRegion
        where p.boardType = :boardType and p.isVisible = true and p.deletedAt is null
          and (:keyword is null or p.title like concat('%', :keyword, '%')
                                or p.content like concat('%', :keyword, '%'))
          and (:secondCategoryId is null or sc.id = :secondCategoryId)
          and (:firstCategoryId is null or sc.firstCategory.id = :firstCategoryId)
          and (:secondRegionId is null or sr.id = :secondRegionId)
          and (:firstRegionId is null or sr.firstRegion.id = :firstRegionId)
        """,
            countQuery = """
        select count(p) from Post p
        left join p.secondCategory sc left join p.secondRegion sr
        where p.boardType = :boardType and p.isVisible = true and p.deletedAt is null
          and (:keyword is null or p.title like concat('%', :keyword, '%')
                                or p.content like concat('%', :keyword, '%'))
          and (:secondCategoryId is null or sc.id = :secondCategoryId)
          and (:firstCategoryId is null or sc.firstCategory.id = :firstCategoryId)
          and (:secondRegionId is null or sr.id = :secondRegionId)
          and (:firstRegionId is null or sr.firstRegion.id = :firstRegionId)
        """)
    Page<Post> findPublicPosts(@Param("boardType") BoardType boardType,
                               @Param("keyword") String keyword,
                               @Param("firstCategoryId") Long firstCategoryId,
                               @Param("secondCategoryId") Long secondCategoryId,
                               @Param("firstRegionId") Long firstRegionId,
                               @Param("secondRegionId") Long secondRegionId,
                               Pageable pageable);
}
