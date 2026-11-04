package com.grepp.efy.domain.credit.wallet.dto;

public record CreditBalanceResponse(
        long balance, // 보유 크레딧
        long reservedCredit, // 발송한 견적에 예약된 크레딧
        long availableCredit, // 사용 가능 크레딧 = 보유 - 예약
        long creditPerQuote, // 견적 1건당 예약•차감 크레딧
        long availableQuoteCount // 추가로 발송 가능한 견적 수
) {
}
