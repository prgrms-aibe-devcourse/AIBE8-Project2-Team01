package com.grepp.efy.domain.credit.product.docs;

import com.grepp.efy.domain.credit.product.dto.CreditProductResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "크레딧", description = "크레딧 잔액·내역 API")
public interface CreditProductControllerDocs {

    @Operation(summary = "충전 상품 목록 조회",
            description = "고수가 판매 중인 충전 상품을 가격 오름차순으로 조회합니다. 판매 중인 상품이 없으면 빈 배열을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "401", description = "인증 없음·실패")
    @ApiResponse(responseCode = "403", description = "고수(EXPERT) 권한 없음")
    ResponseEntity<List<CreditProductResponse>> getCreditProducts(@Parameter(hidden = true) CustomUserDetails userDetails);
}