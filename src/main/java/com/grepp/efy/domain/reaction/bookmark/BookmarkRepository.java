package com.grepp.efy.domain.reaction.bookmark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 활성일 때만 비활성으로 바꾸고 바뀐 행 수를 돌려준다. 동시에 두 번 취소해도 한 번만 1이 나온다
    @Modifying
    @Query("""
        update Bookmark b set b.active = false
        where b.member.id = :memberId and b.expertProfile.id = :expertProfileId and b.active = true
        """)
    int deactivate(@Param("memberId") Long memberId, @Param("expertProfileId") Long expertProfileId);
}
