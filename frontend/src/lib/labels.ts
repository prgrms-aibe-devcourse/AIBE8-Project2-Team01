// 서버 상태값 → 사용자 문구, 날짜·금액 표시
// 내부 상태값(OPEN·SENT 등)은 화면에 그대로 쓰지 않는다
import type { BoardType, CreditHistoryType, QuoteStatus, RequestStatus } from '../api/mvp';

export const REQUEST_VALID_HOURS = 24;   // 요청서 견적 접수 기간
export const QUOTE_VALID_DAYS = 3;       // SENT 견적 유효기간

const HOUR = 3600_000;

export const toDate = (s: string) => new Date(s);
const pad = (n: number) => String(n).padStart(2, '0');

// 11/03 09:00
export function shortDateTime(s: string | null | undefined): string {
  if (!s) return '';
  const d = toDate(s);
  return `${pad(d.getMonth() + 1)}/${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
// 2026.11.03
export function dotDate(s: string): string {
  const d = toDate(s);
  return `${d.getFullYear()}.${pad(d.getMonth() + 1)}.${pad(d.getDate())}`;
}
// 채팅·목록용: 오늘이면 시각, 아니면 날짜
export function smartTime(s: string | null | undefined): string {
  if (!s) return '';
  const d = toDate(s); const now = new Date();
  if (d.toDateString() === now.toDateString()) return `${pad(d.getHours())}:${pad(d.getMinutes())}`;
  return `${d.getMonth() + 1}/${d.getDate()}`;
}
export const timeOnly = (s: string) => { const d = toDate(s); return `${pad(d.getHours())}:${pad(d.getMinutes())}`; };

export const won = (n: number) => `${n.toLocaleString('ko-KR')}원`;
export const credit = (n: number) => `${n.toLocaleString('ko-KR')}크레딧`;

// 요청서: 등록 후 24시간이 견적 접수 기간 (시간 만료는 서버에 별도 상태가 없어 등록 시각으로 계산)
export function requestDeadline(createdAt: string): Date { return new Date(toDate(createdAt).getTime() + REQUEST_VALID_HOURS * HOUR); }
export function requestOpen(createdAt: string, now = Date.now()): boolean { return requestDeadline(createdAt).getTime() > now; }
export function remainText(until: Date, now = Date.now()): string {
  const ms = until.getTime() - now;
  if (ms <= 0) return '마감';
  const h = Math.floor(ms / HOUR);
  if (h >= 1) return `${h}시간`;
  return `${Math.max(1, Math.ceil(ms / 60000))}분`;
}

export type Tone = 'blue' | 'teal' | 'gray' | 'red' | 'warn';
// 요청서 상태 배지 (OPEN이라도 24시간이 지나면 접수 마감)
export function requestBadge(status: RequestStatus, createdAt: string): { text: string; tone: Tone } {
  if (status === 'MATCHED') return { text: '매칭', tone: 'teal' };
  if (status === 'CLOSED') return { text: '마감 · 직접 취소', tone: 'red' };
  return requestOpen(createdAt) ? { text: '접수 중', tone: 'blue' } : { text: '마감 · 시간 만료', tone: 'gray' };
}

// 견적 유효기간: 발송 후 3일 (REQUESTED는 기한 없음)
export function quoteDeadline(sentAt: string): Date { return new Date(toDate(sentAt).getTime() + QUOTE_VALID_DAYS * 24 * HOUR); }
export function quoteBadge(status: QuoteStatus): { text: string; tone: Tone } {
  return status === 'REQUESTED' ? { text: '매칭 완료', tone: 'teal' } : { text: '매칭 대기', tone: 'blue' };
}

export const BOARD_LABEL: Record<BoardType, string> = { QUESTION: '질문', TIP: '팁', RECRUIT: '모집', PROMOTION: '홍보' };
export const BOARDS: BoardType[] = ['QUESTION', 'TIP', 'RECRUIT', 'PROMOTION'];
export const EXPERT_ONLY_BOARDS: BoardType[] = ['TIP', 'PROMOTION'];

export const HISTORY_LABEL: Record<CreditHistoryType, string> = { CHARGE: '충전', USE: '사용', REFUND: '환불' };
