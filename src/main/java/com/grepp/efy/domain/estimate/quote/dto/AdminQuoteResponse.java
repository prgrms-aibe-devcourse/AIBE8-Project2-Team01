package com.grepp.efy.domain.estimate.quote.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.QuoteStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"quoteId", "serviceRequestId", "expertProfileId", "expertNickname",
        "customerId", "customerNickname", "secondCategoryName", "secondRegionName",
        "price", "quoteStatus", "sentAt"})
public record AdminQuoteResponse(Long quoteId,
                                 Long serviceRequestId,
                                 Long expertProfileId,
                                 String expertNickname,
                                 Long customerId,
                                 String customerNickname,
                                 String secondCategoryName,
                                 String secondRegionName,
                                 Long price,
                                 QuoteStatus quoteStatus,
                                 LocalDateTime sentAt) {
}