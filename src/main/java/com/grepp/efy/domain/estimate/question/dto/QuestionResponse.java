package com.grepp.efy.domain.estimate.question.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "content", "isRequired", "displayOrder"})
public record QuestionResponse(Long id, String content, Boolean isRequired, Integer displayOrder) {
}
