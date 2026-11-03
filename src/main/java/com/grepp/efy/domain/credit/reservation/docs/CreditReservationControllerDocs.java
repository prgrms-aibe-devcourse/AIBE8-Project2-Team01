package com.grepp.efy.domain.credit.reservation.docs;

import com.grepp.efy.domain.credit.reservation.dto.CreditReservationResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "크레딧", description = "크레딧 잔액·내역 API")
public interface CreditReservationControllerDocs {

    @Operation(summary = "예약 중인 견적 목록 조회",
            description = "고수가 견적 발송으로 예약된 크레딧 목록을 최신순으로 조회합니다. 예약은 차감 전이라 크레딧 내역에는 포함되지 않습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공. 예약이 없으면 빈 배열")
    @ApiResponse(responseCode = "401", description = "인증 없음·실패")
    @ApiResponse(responseCode = "403", description = "고수(EXPERT) 권한 없음")
    ResponseEntity<List<CreditReservationResponse>> getMyReservations(@Parameter(hidden = true) CustomUserDetails userDetails);
}