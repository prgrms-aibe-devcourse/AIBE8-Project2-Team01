// 크레딧·결제 API 타입
// 노션 API 명세서(API-65~70) 초안의 요청·응답 필드를 따른다. 아직 서버에 구현되지 않았다.

/** 결제 주문 상태 (MVP CR-02~04) */
export type PaymentStatus = 'READY' | 'DONE' | 'FAILED' | 'CANCELED' | 'PENDING';

/** 크레딧 내역 유형 (MVP CR-06) */
export type CreditHistoryType = 'CHARGE' | 'USE' | 'REFUND';

/** [API-65] GET /api/credit-products 응답 항목 */
export interface CreditProduct {
  id: number;
  name: string;
  price: number;
  creditAmount: number;
  /** MVP는 추가 크레딧이 없어 항상 0 */
  bonusCredit: number;
}

/** 화면 표시용: 상품 값의 출처 (mock 전용 필드) */
export interface CreditProductView extends CreditProduct {
  /** true면 문서·코드에 없는 예시 데이터 */
  isExample: boolean;
}

/** [API-66] GET /api/members/me/credit 응답 */
export interface MyCreditResponse {
  balance: number;
  creditPerQuote: number;
  availableQuoteCount: number;
  /** 명세 미반영 필드 (추가 필요, 미확정) */
  reservedCredit: number;
  /** 명세 미반영 필드 (추가 필요, 미확정) */
  availableCredit: number;
}

/** [API-67] 크레딧 내역 항목 */
export interface CreditHistory {
  id: number;
  type: CreditHistoryType;
  amount: number;
  balanceAfter: number;
  paymentId: number | null;
  quoteId: number | null;
  createdAt: string;
}

/** 공통 목록 응답 (백엔드 global/common/PageResponse) */
export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  lastPage: boolean;
}

export interface CreditHistoryQuery {
  type?: CreditHistoryType | '';
  page?: number;
  size?: number;
}

/** [API-68] POST /api/payments */
export interface PaymentCreateRequest {
  creditProductId: number;
}

export interface PaymentCreateResponse {
  paymentId: number;
  orderId: string;
  orderName: string;
  amount: number;
}

/** [API-69] POST /api/payments/confirm */
export interface PaymentConfirmRequest {
  paymentKey: string;
  orderId: string;
  amount: number;
}

export interface PaymentConfirmResponse {
  orderId: string;
  paymentStatus: PaymentStatus;
  chargedCredit: number;
  balance: number;
}

/** [API-70] PATCH /api/payments/{orderId}/fail */
export interface PaymentFailRequest {
  code: string;
  message?: string;
}

export interface PaymentStatusResponse {
  orderId: string;
  paymentStatus: PaymentStatus;
}

/** 예약 중인 견적 (대응 API 없음, mock 전용) */
export interface CreditReservation {
  quoteId: number;
  credit: number;
  sentAt: string;
}

/** 실패 응답 (백엔드 ErrorResponse 형식) */
export interface ApiError {
  status: number;
  message: string;
}
