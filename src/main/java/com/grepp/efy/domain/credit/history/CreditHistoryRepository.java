package com.grepp.efy.domain.credit.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditHistoryRepository extends JpaRepository<CreditHistory, Long> {

    Page<CreditHistory> findByCreditWalletId(Long id, Pageable pageable);

    Page<CreditHistory> findByCreditWalletIdAndHistoryType(Long id, CreditHistoryType type, Pageable pageable);
    interface QuoteCustomer {
        Long getQuoteId();
        String getCustomerNickname();
    }

    @Query("""
            select q.id as quoteId, customer.nickname as customerNickname
            from Quote q join q.serviceRequest r join r.member customer
            where q.id in :quoteIds
            """)
    List<QuoteCustomer> findQuoteCustomers(@Param("quoteIds") List<Long> quoteIds);
}
