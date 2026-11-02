// 모의 결제창: 토스 테스트 결제창 자리. 결과(성공·실패·취소·응답 지연)를 골라 재현한다
import { useEffect, useRef } from 'react';
import type { PaymentCreateResponse } from '../types';
import { formatWon } from '../lib/format';

export type MockOutcome = 'success' | 'fail' | 'cancel' | 'timeout';

interface Props {
  order: PaymentCreateResponse;
  busy: boolean;
  onSelect: (outcome: MockOutcome) => void;
}

const OUTCOMES: { value: MockOutcome; label: string; primary?: boolean }[] = [
  { value: 'success', label: '결제 성공', primary: true },
  { value: 'fail', label: '결제 실패' },
  { value: 'cancel', label: '사용자 취소' },
  { value: 'timeout', label: '승인 응답 지연' }
];

export function MockPayModal({ order, busy, onSelect }: Props) {
  const firstButton = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    firstButton.current?.focus();
  }, []);

  return (
    <div className="modal">
      <div className="modal-box" role="dialog" aria-modal="true" aria-labelledby="pay-modal-title">
        <div className="modal-head">
          <strong id="pay-modal-title">모의 결제창</strong>
          <span className="tag">테스트 · 실제 결제 없음</span>
        </div>
        <p className="muted small">실제 연동 시 이 자리에 토스 테스트 결제창이 열립니다. 재현할 결과를 고르세요.</p>
        <dl className="kv">
          <dt>주문명</dt>
          <dd>{order.orderName}</dd>
          <dt>결제 금액</dt>
          <dd>{formatWon(order.amount)}</dd>
          <dt>주문번호</dt>
          <dd className="small">{order.orderId}</dd>
        </dl>
        <div className="modal-actions">
          {OUTCOMES.map((o, i) => (
            <button
              key={o.value}
              ref={i === 0 ? firstButton : undefined}
              type="button"
              className={`btn${o.primary ? ' primary' : ''}`}
              disabled={busy}
              data-outcome={o.value}
              onClick={() => onSelect(o.value)}
            >
              {o.label}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
