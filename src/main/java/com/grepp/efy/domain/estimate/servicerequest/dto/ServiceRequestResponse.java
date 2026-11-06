package com.grepp.efy.domain.estimate.servicerequest.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.grepp.efy.domain.estimate.enums.RequestStatus;
import com.grepp.efy.domain.estimate.requestanswer.dto.AnswerResponse;

import java.time.LocalDateTime;
import java.util.List;

@JsonPropertyOrder({"requestId", "customerNickname", "secondCategoryName", "secondRegionName",
        "requestStatus", "createdAt", "answers"})
public record ServiceRequestResponse(Long requestId,
                                     String customerNickname,
                                     String secondCategoryName,
                                     String secondRegionName,
                                     RequestStatus requestStatus,
                                     LocalDateTime createdAt,
                                     List<AnswerResponse> answers) {
}
