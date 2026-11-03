package com.grepp.efy.domain.estimate.servicerequest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.RequestStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"requestId", "sentQuoteId", "secondCategoryName", "secondRegionName", "requestStatus", "createdAt"})
public record RequestMatchedResponse(Long requestId,
                                     Long sentQuoteId,
                                     String secondCategoryName,
                                     String secondRegionName,
                                     RequestStatus requestStatus,
                                     LocalDateTime createdAt) {
}
