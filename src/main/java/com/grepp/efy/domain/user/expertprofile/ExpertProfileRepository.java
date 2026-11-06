package com.grepp.efy.domain.user.expertprofile;

import com.grepp.efy.domain.user.member.MemberRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long> {
    @EntityGraph(attributePaths = {"member", "member.image", "primaryCategory"})
    List<ExpertProfile> findByMemberRoleAndMemberDeletedAtIsNullOrderByIdDesc(MemberRole role);

    Optional<ExpertProfile> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

}
