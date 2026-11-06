package com.grepp.efy.domain.estimate.quote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuoteExpirationScheduler {

    private final QuoteService quoteService;

    @Scheduled(initialDelay = 60_000, fixedDelay = 600_000)
    public void deleteExpiredQuotes() {
        int deleted = quoteService.deleteExpiredQuotes();

        if (deleted > 0) {
            log.info("만료된 견적서 {}건의 크레딧 예약을 해제하고 삭제했습니다.", deleted);
        }
    }
}
