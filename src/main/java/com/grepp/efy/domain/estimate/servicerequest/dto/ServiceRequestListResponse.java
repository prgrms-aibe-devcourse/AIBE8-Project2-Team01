package com.grepp.efy.domain.estimate.servicerequest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.RequestStatus;

import java.time.LocalDateTime;

@JsonPropertyOrder({"requestId", "secondCategoryName", "secondRegionName", "requestStatus", "createdAt"})
public record ServiceRequestListResponse(Long requestId,
                                         String secondCategoryName,
                                         String secondRegionName,
                                         RequestStatus requestStatus,
                                         LocalDateTime createdAt) {
}
