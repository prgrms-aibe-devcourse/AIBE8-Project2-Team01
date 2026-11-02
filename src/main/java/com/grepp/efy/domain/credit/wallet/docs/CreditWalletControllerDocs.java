package com.grepp.efy.domain.credit.wallet.docs;

import com.grepp.efy.domain.credit.wallet.dto.CreditBalanceResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "크레딧", description = "크레딧 잔액·내역 API")
public interface CreditWalletControllerDocs {

    @Operation(summary = "내 크레딧 잔액 조회",
            description = "고수가 본인의 보유 크레딧, 견적 1건당 차감 크레딧, 추가 발송 가능한 견적 수를 조회합니다. 지갑이 없으면 잔액 0을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 없음·실패")
    @ApiResponse(responseCode = "403", description = "고수(EXPERT) 권한 없음")
    ResponseEntity<CreditBalanceResponse> getMyCredit(@Parameter(hidden = true) CustomUserDetails userDetails);
}