package com.grepp.efy.domain.estimate.quote.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDateTime;

@JsonPropertyOrder({"quoteId", "description", "price", "sentAt"})
public record QuoteCreateResponse(Long quoteId, String description, Long price, LocalDateTime sentAt) {
}