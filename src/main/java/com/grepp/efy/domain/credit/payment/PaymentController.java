package com.grepp.efy.domain.credit.payment;

import com.grepp.efy.domain.credit.payment.docs.PaymentControllerDocs;
import com.grepp.efy.domain.credit.payment.dto.*;
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

    @PostMapping("/payments/confirm")
    @Override
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PaymentConfirmRequest request) {
        return ResponseEntity.ok(paymentService.confirmPayment(userDetails.getMemberId(), request));
    }

    @PatchMapping("/payments/{orderId}/fail")
    @Override
    public ResponseEntity<PaymentFailResponse> failPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String orderId,
            @Valid @RequestBody PaymentFailRequest request) {
        return ResponseEntity.ok(paymentService.failPayment(userDetails.getMemberId(), orderId, request));
    }
}
