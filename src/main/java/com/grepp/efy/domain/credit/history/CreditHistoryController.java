package com.grepp.efy.domain.credit.history;

import com.grepp.efy.domain.credit.history.docs.CreditHistoryControllerDocs;
import com.grepp.efy.domain.credit.history.dto.CreditHistoryResponse;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CreditHistoryController implements CreditHistoryControllerDocs {

    private final CreditHistoryService creditHistoryService;

    @GetMapping("/members/me/credit-histories")
    @Override
    public ResponseEntity<PageResponse<CreditHistoryResponse>> getMyHistories(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) CreditHistoryType type,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(creditHistoryService.getMyHistories(userDetails.getMemberId(), type, pageNumber, pageSize));
    }
}