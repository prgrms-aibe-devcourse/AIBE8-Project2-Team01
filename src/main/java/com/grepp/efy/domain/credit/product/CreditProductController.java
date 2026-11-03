package com.grepp.efy.domain.credit.product;

import com.grepp.efy.domain.credit.product.docs.CreditProductControllerDocs;
import com.grepp.efy.domain.credit.product.dto.CreditProductResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CreditProductController implements CreditProductControllerDocs {

    private final CreditProductService creditProductService;

    @GetMapping("/credit-products")
    @Override
    public ResponseEntity<List<CreditProductResponse>> getCreditProducts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(creditProductService.getCreditProducts(userDetails.getMemberId()));
    }
}
