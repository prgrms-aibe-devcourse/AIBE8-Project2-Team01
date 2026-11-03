package com.grepp.efy.domain.credit.reservation;

import com.grepp.efy.domain.credit.reservation.docs.CreditReservationControllerDocs;
import com.grepp.efy.domain.credit.reservation.dto.CreditReservationResponse;
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
public class CreditReservationController implements CreditReservationControllerDocs {

    private final CreditReservationService creditReservationService;

    @GetMapping("/members/me/credit-reservations")
    @Override
    public ResponseEntity<List<CreditReservationResponse>> getMyReservations(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(creditReservationService.getMyReservations(userDetails.getMemberId()));
    }
}