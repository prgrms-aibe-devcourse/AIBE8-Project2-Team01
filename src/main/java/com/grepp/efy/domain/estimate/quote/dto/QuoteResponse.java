package com.grepp.efy.domain.estimate.quote.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.QuoteStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"quoteId", "serviceRequestId", "secondCategoryName", "secondRegionName",
        "description", "price", "quoteStatus", "sentAt"})
public record QuoteResponse(Long quoteId, Long serviceRequestId, String secondCategoryName, String secondRegionName,
                            String description, Long price, QuoteStatus quoteStatus, LocalDateTime sentAt) {
}