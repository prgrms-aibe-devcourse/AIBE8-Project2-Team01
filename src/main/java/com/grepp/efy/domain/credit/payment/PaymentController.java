package com.grepp.efy.domain.credit.payment;

import com.grepp.efy.domain.credit.payment.docs.PaymentControllerDocs;
import com.grepp.efy.domain.credit.payment.dto.PaymentCreateRequest;
import com.grepp.efy.domain.credit.payment.dto.PaymentCreateResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    @PostMapping("/payments")
    @Override
    public ResponseEntity<PaymentCreateResponse> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(userDetails.getMemberId(), request));
    }
}
