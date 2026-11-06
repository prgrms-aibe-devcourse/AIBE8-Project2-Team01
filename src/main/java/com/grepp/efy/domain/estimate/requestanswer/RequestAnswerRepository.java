package com.grepp.efy.domain.estimate.requestanswer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestAnswerRepository extends JpaRepository<RequestAnswer, Long> {
    List<RequestAnswer> findByServiceRequestId(Long requestId);
}
