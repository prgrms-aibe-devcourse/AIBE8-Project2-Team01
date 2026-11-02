package com.grepp.efy.domain.credit.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {
    
}
