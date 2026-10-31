package com.grepp.efy.domain.user.expertprofile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpertProfileRepository extends JpaRepository<ExpertProfile, Long> {
    Optional<ExpertProfile> findByMemberId(Long memberId);

    boolean existsByMemberId(Long memberId);

}
