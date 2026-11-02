// 크레딧·결제 mock 구현
// 실제 결제 API·운영 DB를 호출하지 않는다. 모든 데이터는 브라우저 메모리에만 있다.
import type { CreditApi, DummyControls, DummyPayment, DummySnapshot } from './creditApi';
import type {
  ApiError,
  CreditHistory,
  CreditProductView,
  CreditReservation,
  PaymentConfirmResponse
} from '../types';
import { CREDIT_PER_QUOTE } from '../lib/policy';

/** 응답 지연 흉내 (로딩·중복 클릭 확인용) */
const LATENCY_MS = 500;

interface MockState {
  products: CreditProductView[];
  histories: CreditHistory[];
  reservations: CreditReservation[];
  payments: (DummyPayment & { paymentKey: string | null })[];
  seq: { payment: number; history: number };
}

function createInitialState(): MockState {
  return {
    // 상품 구간은 문서·코드에 없어 예시 데이터로 둔다 (Q-06 미확정)
    // 가격 = 크레딧 × 1,000원 규칙만 확정 정책이다
    products: [
      { id: 1, name: '크레딧 10개', price: 10000, creditAmount: 10, bonusCredit: 0, isExample: true },
      { id: 2, name: '크레딧 30개', price: 30000, creditAmount: 30, bonusCredit: 0, isExample: true },
      { id: 3, name: '크레딧 50개', price: 50000, creditAmount: 50, bonusCredit: 0, isExample: true },
      // API-65 예시 응답의 이름·가격. 예시의 bonusCredit 10은 MVP 정책에 따라 0으로 둔다
      { id: 4, name: '크레딧 100개', price: 100000, creditAmount: 100, bonusCredit: 0, isExample: false }
    ],
    // 원장(내역). 잔액 = 내역 합계
    histories: [
      { id: 1, type: 'CHARGE', amount: 10, balanceAfter: 10, paymentId: 1, quoteId: null, createdAt: '2026-10-28T09:10:00' },
      { id: 2, type: 'USE', amount: -2, balanceAfter: 8, paymentId: null, quoteId: 21, createdAt: '2026-10-30T15:20:00' },
      { id: 3, type: 'CHARGE', amount: 10, balanceAfter: 18, paymentId: 2, quoteId: null, createdAt: '2026-10-31T11:02:00' },
      { id: 4, type: 'USE', amount: -2, balanceAfter: 16, paymentId: null, quoteId: 27, createdAt: '2026-11-01T14:20:00' }
    ],
    // 매칭 전 SENT 견적의 예약 (조회 API 미정)
    reservations: [
      { quoteId: 34, credit: 2, sentAt: '2026-11-01T18:44:00' },
      { quoteId: 35, credit: 2, sentAt: '2026-11-02T08:30:00' }
    ],
    payments: [
      { paymentId: 1, orderId: 'efy-mock-0001', productId: 1, orderName: '크레딧 10개', amount: 10000, creditAmount: 10, status: 'DONE', paymentKey: 'mock_pk_0001' },
      { paymentId: 2, orderId: 'efy-mock-0002', productId: 1, orderName: '크레딧 10개', amount: 10000, creditAmount: 10, status: 'DONE', paymentKey: 'mock_pk_0002' }
    ],
    seq: { payment: 2, history: 4 }
  };
}

let state = createInitialState();
const listeners = new Set<() => void>();

function notify() {
  listeners.forEach((listener) => listener());
}

/** 깊은 복사로 내부 상태가 화면에서 바뀌지 않게 한다 */
function clone<T>(value: T): T {
  return JSON.parse(JSON.stringify(value)) as T;
}

function respond<T>(value: T): Promise<T> {
  return new Promise((resolve) => setTimeout(() => resolve(clone(value)), LATENCY_MS));
}

function reject(status: number, message: string): Promise<never> {
  const error: ApiError = { status, message };
  return new Promise((_, rej) => setTimeout(() => rej(error), LATENCY_MS));
}

function nowIso(): string {
  const d = new Date();
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}T${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
}

const totalBalance = () => state.histories.reduce((sum, h) => sum + h.amount, 0);
const reservedCredit = () => state.reservations.reduce((sum, r) => sum + r.credit, 0);
const findPayment = (orderId: string) => state.payments.find((p) => p.orderId === orderId);

/** 크레딧 지급: 주문당 한 번만 실행된다 (CR-03 멱등성) */
function grantCredit(payment: MockState['payments'][number]) {
  if (payment.status === 'DONE') return;
  payment.status = 'DONE';
  state.seq.history += 1;
  state.histories.push({
    id: state.seq.history,
    type: 'CHARGE',
    amount: payment.creditAmount,
    balanceAfter: totalBalance() + payment.creditAmount,
    paymentId: payment.paymentId,
    quoteId: null,
    createdAt: nowIso()
  });
}

function confirmResult(payment: MockState['payments'][number]): PaymentConfirmResponse {
  return {
    orderId: payment.orderId,
    paymentStatus: payment.status,
    chargedCredit: payment.status === 'DONE' ? payment.creditAmount : 0,
    balance: totalBalance()
  };
}

export const mockCreditApi: CreditApi & DummyControls = {
  getCreditProducts() {
    // 활성 상품만 가격 오름차순
    return respond([...state.products].sort((a, b) => a.price - b.price));
  },

  getMyCredit() {
    const balance = totalBalance();
    const reserved = reservedCredit();
    const available = balance - reserved;
    return respond({
      balance,
      creditPerQuote: CREDIT_PER_QUOTE,
      // 계산에 포함할 견적 범위는 Q-03 협의 중
      availableQuoteCount: Math.floor(available / CREDIT_PER_QUOTE),
      reservedCredit: reserved,
      availableCredit: available
    });
  },

  getCreditHistories({ type = '', page = 1, size = 10 }) {
    // size 기본값·최대값은 Q-06 미정. mock은 10
    const all = state.histories
      .filter((h) => !type || h.type === type)
      .sort((a, b) => b.createdAt.localeCompare(a.createdAt) || b.id - a.id);
    const totalPages = Math.max(1, Math.ceil(all.length / size));
    return respond({
      content: all.slice((page - 1) * size, page * size),
      pageNumber: page,
      pageSize: size,
      totalElements: all.length,
      totalPages,
      lastPage: page >= totalPages
    });
  },

  getReservations() {
    return respond(state.reservations);
  },

  createPayment({ creditProductId }) {
    const product = state.products.find((p) => p.id === creditProductId);
    if (!product) return reject(404, '충전 상품을 찾을 수 없습니다. (mock 메시지)');
    state.seq.payment += 1;
    // 금액은 서버 상품 가격으로 정한다 (클라이언트 금액 미사용)
    const payment = {
      paymentId: state.seq.payment,
      orderId: `efy-mock-${String(state.seq.payment).padStart(4, '0')}-${Date.now().toString(36)}`,
      productId: product.id,
      orderName: product.name,
      amount: product.price,
      creditAmount: product.creditAmount + product.bonusCredit,
      status: 'READY' as const,
      paymentKey: null
    };
    state.payments.push(payment);
    notify();
    return respond({ paymentId: payment.paymentId, orderId: payment.orderId, orderName: payment.orderName, amount: payment.amount });
  },

  confirmPayment({ paymentKey, orderId, amount }) {
    const payment = findPayment(orderId);
    if (!payment) return reject(404, '주문을 찾을 수 없습니다. (mock 메시지)');
    if (payment.amount !== amount) return reject(400, '결제 금액이 주문 금액과 일치하지 않습니다. (mock 메시지)');
    // 이미 승인된 주문은 재지급 없이 같은 결과를 반환
    if (payment.status === 'DONE') return respond(confirmResult(payment));
    if (payment.status !== 'READY' && payment.status !== 'PENDING') {
      return reject(409, '승인할 수 없는 주문 상태입니다. (mock 메시지)');
    }
    payment.paymentKey = paymentKey;
    grantCredit(payment);
    notify();
    return respond(confirmResult(payment));
  },

  failPayment(orderId, { code }) {
    const payment = findPayment(orderId);
    if (!payment) return reject(404, '주문을 찾을 수 없습니다. (mock 메시지)');
    // DONE 주문은 실패·취소로 덮어쓰지 않는다 (CR-04)
    if (payment.status === 'READY' || payment.status === 'PENDING') {
      payment.status = code === 'PAY_PROCESS_CANCELED' ? 'CANCELED' : 'FAILED';
      notify();
    }
    return respond({ orderId: payment.orderId, paymentStatus: payment.status });
  },

  confirmPaymentTimeout({ paymentKey, orderId }) {
    const payment = findPayment(orderId);
    if (!payment) return reject(404, '주문을 찾을 수 없습니다. (mock 메시지)');
    // 외부 결과를 단정하지 않고 PENDING으로 둔다 (CR-03)
    if (payment.status === 'READY') {
      payment.status = 'PENDING';
      payment.paymentKey = paymentKey;
      notify();
    }
    return respond({ orderId: payment.orderId, paymentStatus: payment.status });
  },

  recheckPayment(orderId, resolveAs) {
    const payment = findPayment(orderId);
    if (!payment) return reject(404, '주문을 찾을 수 없습니다. (mock 메시지)');
    if (payment.status === 'PENDING') {
      if (resolveAs === 'DONE') grantCredit(payment);
      else if (resolveAs === 'FAILED') payment.status = 'FAILED';
      notify();
    }
    return respond(confirmResult(payment));
  },

  snapshot(): DummySnapshot {
    return {
      balance: totalBalance(),
      reserved: reservedCredit(),
      historyCount: state.histories.length,
      payments: state.payments.map(({ paymentKey: _paymentKey, ...rest }) => ({ ...rest }))
    };
  },

  reset() {
    state = createInitialState();
    notify();
  },

  subscribe(listener) {
    listeners.add(listener);
    return () => {
      listeners.delete(listener);
    };
  }
};
