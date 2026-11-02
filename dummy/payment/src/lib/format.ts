// 표시 형식 유틸
import type { CreditHistoryType, PaymentStatus } from '../types';

/** 10000 → "10,000원" */
export const formatWon = (value: number): string => `${value.toLocaleString('ko-KR')}원`;

/** "2026-11-01T14:20:00" → "11/01 14:20" */
export function formatDateTime(iso: string): string {
  const m = /^\d{4}-(\d{2})-(\d{2})T(\d{2}):(\d{2})/.exec(iso);
  return m ? `${m[1]}/${m[2]} ${m[3]}:${m[4]}` : iso;
}

/** 2 → "+2", -2 → "-2" */
export const formatSigned = (value: number): string => (value > 0 ? `+${value}` : String(value));

export const HISTORY_TYPE_LABEL: Record<CreditHistoryType, string> = {
  CHARGE: '충전',
  USE: '사용',
  REFUND: '환불'
};

export const PAYMENT_STATUS_LABEL: Record<PaymentStatus, string> = {
  READY: '결제 대기',
  DONE: '승인 완료',
  FAILED: '실패',
  CANCELED: '사용자 취소',
  PENDING: '처리 확인 중'
};
