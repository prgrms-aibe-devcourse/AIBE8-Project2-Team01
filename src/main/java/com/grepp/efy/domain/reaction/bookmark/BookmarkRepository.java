package com.grepp.efy.domain.reaction.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
