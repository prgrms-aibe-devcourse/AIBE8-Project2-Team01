package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.quote.docs.QuoteControllerDocs;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteRequestResponse;
import com.grepp.efy.domain.estimate.quote.dto.QuoteResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuoteController implements QuoteControllerDocs {

    private final QuoteService quoteService;

    @Override
    @GetMapping("/quotes")
    public ResponseEntity<List<QuoteResponse>> getQuotes(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<QuoteResponse> response = quoteService.getQuotes(userDetails.getMemberId());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/quotes/{quoteId}")
    public ResponseEntity<QuoteResponse> getQuote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long quoteId
    ) {
        QuoteResponse response = quoteService.getQuote(userDetails.getMemberId(), quoteId);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/requests/{requestId}/quotes")
    public ResponseEntity<List<QuoteRequestResponse>> getRequestQuotes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long requestId
    ) {
        List<QuoteRequestResponse> response = quoteService.getRequestQuotes(userDetails.getMemberId(), requestId);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/requests/{requestId}/quotes")
    public ResponseEntity<QuoteCreateResponse> createQuote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long requestId,
            @Valid @RequestBody QuoteCreateRequest request
    ) {
        QuoteCreateResponse response = quoteService.createQuote(userDetails.getMemberId(), requestId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/quotes/{quoteId}")
    public ResponseEntity<Void> deleteQuote(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long quoteId) {
        quoteService.deleteQuote(userDetails.getMemberId(), quoteId);
        return ResponseEntity.noContent().build();
    }
}
