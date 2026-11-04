package com.grepp.efy.domain.credit.history.docs;

import com.grepp.efy.domain.credit.history.CreditHistoryType;
import com.grepp.efy.domain.credit.history.dto.CreditHistoryResponse;
import com.grepp.efy.global.common.PageResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "크레딧", description = "크레딧 잔액·내역 API")
public interface CreditHistoryControllerDocs {

    @Operation(summary = "크레딧 내역 조회",
            description = "고수의 크레딧 충전·사용·환불 내역을 최신순으로 조회합니다. type을 주면 해당 유형만 조회합니다. 지갑이 없으면 빈 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 페이지 번호·크기(1~50) 또는 내역 유형")
    @ApiResponse(responseCode = "401", description = "인증 없음·실패")
    @ApiResponse(responseCode = "403", description = "고수(EXPERT) 권한 없음")
    ResponseEntity<PageResponse<CreditHistoryResponse>> getMyHistories(
            @Parameter(hidden = true) CustomUserDetails userDetails,
            @Parameter(description = "내역 유형 (CHARGE, USE, REFUND). 없으면 전체") CreditHistoryType type,
            @Parameter(description = "페이지 번호 (1부터)") int pageNumber,
            @Parameter(description = "페이지 크기 (최대 50)") int pageSize);
}