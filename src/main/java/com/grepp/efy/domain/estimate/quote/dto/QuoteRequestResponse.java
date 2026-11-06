package com.grepp.efy.domain.estimate.quote.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.QuoteStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"quoteId", "expertProfileId", "expertNickname", "description",
        "price", "quoteStatus", "sentAt"})
public record QuoteRequestResponse(Long quoteId, Long expertProfileId, String expertNickname,
                                   String description, Long price, QuoteStatus quoteStatus, LocalDateTime sentAt) {
}
