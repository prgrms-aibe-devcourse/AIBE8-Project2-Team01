package com.grepp.efy.domain.community.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostVisibilityHistoryRepository extends JpaRepository<PostVisibilityHistory, Long> {

    // 최신순, 같은 시각이면 id 내림차순. 관리자 닉네임을 쓰므로 admin을 함께 조회
    @EntityGraph(attributePaths = "admin")
    List<PostVisibilityHistory> findByPostIdOrderByCreatedAtDescIdDesc(Long postId);
}
