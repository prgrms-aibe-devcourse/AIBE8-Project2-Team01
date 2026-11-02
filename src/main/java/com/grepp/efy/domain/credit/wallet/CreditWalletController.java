package com.grepp.efy.domain.credit.wallet;

import com.grepp.efy.domain.credit.wallet.docs.CreditWalletControllerDocs;
import com.grepp.efy.domain.credit.wallet.dto.CreditBalanceResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CreditWalletController implements CreditWalletControllerDocs {
    private final CreditWalletService creditWalletService;

    @GetMapping("/members/me/credit")
    @Override
    public ResponseEntity<CreditBalanceResponse> getMyCredit(@AuthenticationPrincipal CustomUserDetails userDetails) {
        CreditBalanceResponse response = creditWalletService.getMyCredit(userDetails.getMemberId());
        return ResponseEntity.ok(response);
    }
}
