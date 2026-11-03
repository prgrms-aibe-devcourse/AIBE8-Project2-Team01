package com.grepp.efy.domain.credit.payment;

public enum PaymentStatus {
    READY, // 주문 생성, 결제창 진행 전
    PENDING, // 승인 응답을 확인하지 못함
    DONE, // 승인 완료, 크레딧 지급
    FAILED, // 결제 실패
    CANCELED, // 사용자 취소
    REFUNDED // 환불 (후순위)
}
