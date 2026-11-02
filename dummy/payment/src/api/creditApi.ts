// 크레딧·결제 API 계약
// 화면은 이 인터페이스만 사용한다. 실제 연동 시 mockCreditApi 대신 HTTP 구현체로 교체한다.
import type {
  CreditHistory,
  CreditHistoryQuery,
  CreditProductView,
  CreditReservation,
  MyCreditResponse,
  PageResponse,
  PaymentConfirmRequest,
  PaymentConfirmResponse,
  PaymentCreateRequest,
  PaymentCreateResponse,
  PaymentFailRequest,
  PaymentStatus,
  PaymentStatusResponse
} from '../types';
import { mockCreditApi } from './mockCreditApi';

export interface CreditApi {
  /** [API-65] GET /api/credit-products · 명세 초안·미구현 */
  getCreditProducts(): Promise<CreditProductView[]>;
  /** [API-66] GET /api/members/me/credit · 명세 초안·미구현 (예약·사용 가능 필드는 명세 미반영) */
  getMyCredit(): Promise<MyCreditResponse>;
  /** [API-67] GET /api/members/me/credit-histories · 명세 초안·미구현 */
  getCreditHistories(query: CreditHistoryQuery): Promise<PageResponse<CreditHistory>>;
  /** 예약 중인 견적 목록 · 대응 API 없음 (미확정) */
  getReservations(): Promise<CreditReservation[]>;
  /** [API-68] POST /api/payments · 명세 초안·미구현 */
  createPayment(body: PaymentCreateRequest): Promise<PaymentCreateResponse>;
  /** [API-69] POST /api/payments/confirm · 명세 초안·미구현 */
  confirmPayment(body: PaymentConfirmRequest): Promise<PaymentConfirmResponse>;
  /** [API-70] PATCH /api/payments/{orderId}/fail · 명세 초안·미구현 */
  failPayment(orderId: string, body: PaymentFailRequest): Promise<PaymentStatusResponse>;
}

/** 더미 전용 기능: 결과 재현·상태 확인·초기화 (실서비스 API 아님) */
export interface DummyControls {
  /** 승인 응답 지연 재현 → PENDING (응답 형식 명세 미정) */
  confirmPaymentTimeout(body: PaymentConfirmRequest): Promise<PaymentStatusResponse>;
  /** 결제 결과 재확인 · 대응 API 없음. resolveAs로 외부 결과를 재현한다 */
  recheckPayment(orderId: string, resolveAs: Extract<PaymentStatus, 'DONE' | 'FAILED' | 'PENDING'>): Promise<PaymentConfirmResponse>;
  snapshot(): DummySnapshot;
  reset(): void;
  /** mock 상태가 바뀔 때 알림 (개발 패널·상단 잔액 갱신용) */
  subscribe(listener: () => void): () => void;
}

export interface DummyPayment {
  paymentId: number;
  orderId: string;
  productId: number;
  orderName: string;
  amount: number;
  creditAmount: number;
  status: PaymentStatus;
}

export interface DummySnapshot {
  balance: number;
  reserved: number;
  historyCount: number;
  payments: DummyPayment[];
}

// 실제 연동 시 교체 지점
export const creditApi: CreditApi = mockCreditApi;
export const dummyControls: DummyControls = mockCreditApi;
