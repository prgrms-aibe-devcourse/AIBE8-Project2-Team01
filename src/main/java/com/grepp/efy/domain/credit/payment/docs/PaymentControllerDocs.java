package com.grepp.efy.domain.credit.payment.docs;

import com.grepp.efy.domain.credit.payment.dto.PaymentCreateRequest;
import com.grepp.efy.domain.credit.payment.dto.PaymentCreateResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "크레딧", description = "크레딧 잔액·내역 API")
public interface PaymentControllerDocs {

    @Operation(summary = "충전 주문 생성",
            description = "고수가 충전 상품을 선택하면 서버의 상품 가격으로 결제 대기(READY) 주문을 만들고 토스 결제창에 필요한 값을 반환합니다.")
    @ApiResponse(responseCode = "201", description = "주문 생성 성공")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 잘못된 JSON")
    @ApiResponse(responseCode = "401", description = "인증 없음·실패")
    @ApiResponse(responseCode = "403", description = "고수(EXPERT) 권한 없음")
    @ApiResponse(responseCode = "404", description = "없거나 판매 중지된 충전 상품")
    ResponseEntity<PaymentCreateResponse> createPayment(@Parameter(hidden = true) CustomUserDetails userDetails,
                                                        PaymentCreateRequest request);
}