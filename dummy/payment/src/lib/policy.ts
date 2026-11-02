// 확정 정책 상수 (MVP 요구사항 CR-01·CR-07)

/** 1크레딧 가격(원) */
export const CREDIT_UNIT_PRICE = 1000;

/** 견적 1건 발송 시 예약, 매칭 시 차감되는 크레딧 */
export const CREDIT_PER_QUOTE = 2;

/** 토스 결제창 사용자 취소 코드 (API-70 명세 값) */
export const USER_CANCEL_CODE = 'PAY_PROCESS_CANCELED';

/** 결제 실패 코드 예시 (mock 값, 실제 토스 실패 코드 목록 미확인) */
export const MOCK_FAIL_CODE = 'MOCK_PAYMENT_FAILED';
