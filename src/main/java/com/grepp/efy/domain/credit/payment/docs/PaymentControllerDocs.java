package com.grepp.efy.domain.credit.payment.docs;

import com.grepp.efy.domain.credit.payment.dto.PaymentConfirmRequest;
import com.grepp.efy.domain.credit.payment.dto.PaymentConfirmResponse;
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

    @Operation(summary = "결제 승인",
            description = "토스 결제 성공 후 주문 정보를 검증해 승인하고 크레딧을 지급합니다. 이미 승인된 주문은 다시 지급하지 않고 같은 결과를 반환합니다. 토스 응답이 없으면 PENDING을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "승인 완료(DONE) 또는 확인 중(PENDING)")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 또는 주문 금액 불일치")
    @ApiResponse(responseCode = "401", description = "인증 없음·실패")
    @ApiResponse(responseCode = "403", description = "고수 권한 없음 또는 본인 주문이 아님")
    @ApiResponse(responseCode = "404", description = "없는 주문")
    @ApiResponse(responseCode = "409", description = "실패·취소·환불된 주문")
    @ApiResponse(responseCode = "502", description = "토스 결제 승인 실패")
    ResponseEntity<PaymentConfirmResponse> confirmPayment(@Parameter(hidden = true) CustomUserDetails userDetails,
                                                          PaymentConfirmRequest request);
}