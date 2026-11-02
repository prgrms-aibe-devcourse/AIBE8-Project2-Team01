package com.grepp.efy.domain.credit.wallet.dto;

public record CreditBalanceResponse(
        long balance,
        long creditPreQuote,
        long availableQuoteCount
) {
}
