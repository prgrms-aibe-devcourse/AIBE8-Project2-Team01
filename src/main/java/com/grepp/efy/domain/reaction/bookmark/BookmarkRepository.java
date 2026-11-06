package com.grepp.efy.domain.reaction.bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMemberIdAndExpertProfileId(Long memberId, Long expertProfileId);

    // 500개 제한용. 활성 북마크만, 탈퇴한 고수(회원 deletedAt 있음)의 북마크는 빼고 센다
    @Query("""
        select count(b) from Bookmark b
        where b.member.id = :memberId and b.active = true
          and b.expertProfile.member.deletedAt is null
        """)
    long countActive(@Param("memberId") Long memberId);

    // [고수 프로필 ID, 누적 찜 수, since 이후 찜 수] — 공개 고수 목록용. 고수마다 세지 않도록 group by 한 번으로
    // 활성 찜만, 탈퇴한 회원이 남긴 찜은 뺀다. 찜이 없는 고수는 행이 없다
    @Query("""
        select b.expertProfile.id, count(b), sum(case when b.bookmarkedAt >= :since then 1 else 0 end)
        from Bookmark b
        where b.expertProfile.id in :ids and b.active = true and b.member.deletedAt is null
        group by b.expertProfile.id
        """)
    List<Object[]> countByExpertProfiles(@Param("ids") Collection<Long> expertProfileIds,
                                         @Param("since") LocalDateTime since);

    // 활성일 때만 비활성으로 바꾸고 바뀐 행 수를 돌려준다. 동시에 두 번 취소해도 한 번만 1이 나온다
    @Modifying
    @Query("""
        update Bookmark b set b.active = false
        where b.member.id = :memberId and b.expertProfile.id = :expertProfileId and b.active = true
        """)
    int deactivate(@Param("memberId") Long memberId, @Param("expertProfileId") Long expertProfileId);

    // 고수 프로필·고수 회원·프로필 사진·대표 카테고리는 fetch join. 지역(컬렉션)은 아래 두 쿼리로 따로
    @Query(value = """
        select b from Bookmark b
        join fetch b.expertProfile p
        join fetch p.member m
        left join fetch m.image
        join fetch p.primaryCategory c
        join fetch c.firstCategory
        where b.member.id = :memberId and b.active = true and m.deletedAt is null
        """,
            countQuery = """
        select count(b) from Bookmark b
        where b.member.id = :memberId and b.active = true
          and b.expertProfile.member.deletedAt is null
        """)
    Page<Bookmark> findActiveBookmarks(@Param("memberId") Long memberId, Pageable pageable);

    // [고수 프로필 ID, 시/도] — 프로필마다 조회하지 않도록 한 페이지분을 IN 한 번으로
    @Query("select p.id, w from ExpertProfile p join p.wholeRegions w where p.id in :ids")
    List<Object[]> findWholeRegions(@Param("ids") Collection<Long> expertProfileIds);

    // [고수 프로필 ID, 시/군/구] — 상위 시/도까지 함께
    @Query("select p.id, r from ExpertProfile p join p.regions r join fetch r.firstRegion where p.id in :ids")
    List<Object[]> findRegions(@Param("ids") Collection<Long> expertProfileIds);
}
