package com.grepp.efy.domain.estimate.servicerequest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.RequestStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"requestId", "customerId", "customerNickname", "secondCategoryName",
        "secondRegionName", "requestStatus", "createdAt"})
public record AdminRequestResponse(Long requestId,
                                   Long customerId,
                                   String customerNickname,
                                   String secondCategoryName,
                                   String secondRegionName,
                                   RequestStatus requestStatus,
                                   LocalDateTime createdAt) {
}