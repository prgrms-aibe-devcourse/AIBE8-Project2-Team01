package com.grepp.efy.domain.credit.history.dto;

import com.grepp.efy.domain.credit.history.CreditHistory;
import com.grepp.efy.domain.credit.history.CreditHistoryType;

import java.time.LocalDateTime;

public record CreditHistoryResponse(
        Long id,
        CreditHistoryType historyType,
        Long amount,
        Long balanceAfter,
        Long paymentId,
        Long quoteId,
        LocalDateTime createAt
) {
    public static CreditHistoryResponse from(CreditHistory history) {
        return new CreditHistoryResponse(history.getId(), history.getHistoryType(), history.getAmount(),
                history.getBalanceAfter(), history.getPaymentId(), history.getQuoteId(), history.getCreatedAt());
    }
}
