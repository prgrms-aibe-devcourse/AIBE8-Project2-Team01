package com.grepp.efy.domain.credit.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {

    Page<CreditHistory> findByCreditWalletId(Long id, Pageable pageable);

    Page<CreditHistory> findByCreditWalletIdAndHistoryType(Long id, CreditHistoryType type, Pageable pageable);
}
