package com.grepp.efy.domain.estimate.quote;

import com.grepp.efy.domain.estimate.quote.docs.QuoteControllerDocs;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateRequest;
import com.grepp.efy.domain.estimate.quote.dto.QuoteCreateResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class QuoteController implements QuoteControllerDocs {

    private final QuoteService quoteService;

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
}
