// 화면 3: 결제 결과 (성공·실패·사용자 취소·처리 확인 중)
import { useState, type ReactNode } from 'react';
import { creditApi, dummyControls } from '../api/creditApi';
import { ApiPanel, type ApiPoint } from '../components/ApiPanel';
import { LinkButton } from '../components/LinkButton';
import { StepHeader } from '../components/StepHeader';
import { useDummy, type PaymentResult } from '../DummyContext';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { formatWon, PAYMENT_STATUS_LABEL } from '../lib/format';
import type { ApiError } from '../types';

type RecheckAs = 'DONE' | 'FAILED' | 'PENDING';

export function ResultPage({ orderId }: { orderId: string }) {
  const { results, saveResult, showToast } = useDummy();
  const { busy, run } = useSingleFlight();
  const [recheckAs, setRecheckAs] = useState<RecheckAs>('DONE');
  const result = results[orderId];

  if (!result) {
    return (
      <p className="empty">
        결제 결과를 찾을 수 없습니다. (새로고침하면 mock 상태가 초기화됩니다)
        <br />
        <a href="#/credit">크레딧 화면으로</a>
      </p>
    );
  }

  // 같은 주문 승인 재요청: 재지급되지 않는지 확인 (더미 전용)
  const reconfirm = () =>
    run(async () => {
      try {
        const res = await creditApi.confirmPayment({ paymentKey: 'mock_pk_retry', orderId: result.orderId, amount: result.amount });
        showToast(`이미 승인된 주문입니다 · 재지급 없음 (총잔액 ${res.balance})`);
      } catch (e) {
        showToast((e as ApiError).message);
      }
    });

  // PENDING 결과 재확인 (대응 API 없음)
  const recheck = () =>
    run(async () => {
      try {
        const res = await dummyControls.recheckPayment(result.orderId, recheckAs);
        saveResult({ ...result, paymentStatus: res.paymentStatus, chargedCredit: res.chargedCredit, balance: res.balance });
        if (res.paymentStatus === 'PENDING') showToast('아직 결과를 확인하고 있습니다.');
      } catch (e) {
        showToast((e as ApiError).message);
      }
    });

  // 실패·취소 후 재결제는 새 주문으로 진행
  const retry = () => navigate('/checkout', { productId: result.productId });

  const view = buildView(result, { busy, reconfirm, retry, recheck, recheckAs, setRecheckAs });

  return (
    <>
      <StepHeader step={3} label="결제 결과" title="결제 결과" />
      {view.head}
      <div className="box">
        <dl className="kv">
          <dt>주문명</dt>
          <dd>{result.orderName}</dd>
          <dt>결제 금액</dt>
          <dd>{formatWon(result.amount)}</dd>
          <dt>주문번호</dt>
          <dd className="small">{result.orderId}</dd>
          <dt>결제 상태</dt>
          <dd>
            {PAYMENT_STATUS_LABEL[result.paymentStatus]} ({result.paymentStatus})
          </dd>
        </dl>
      </div>
      <div className="actions">{view.actions}</div>
      <ApiPanel points={view.points} response={result} />
    </>
  );
}

interface Handlers {
  busy: boolean;
  reconfirm: () => void;
  retry: () => void;
  recheck: () => void;
  recheckAs: RecheckAs;
  setRecheckAs: (value: RecheckAs) => void;
}

const FAIL_API: ApiPoint = {
  method: 'PATCH',
  path: '/api/payments/{orderId}/fail',
  id: 'API-70',
  status: 'draft',
  note: '요청 code·message(failUrl 값) → paymentStatus. PAY_PROCESS_CANCELED는 CANCELED, 그 외 FAILED. 실패 코드 값은 mock'
};

function buildView(result: PaymentResult, h: Handlers): { head: ReactNode; actions: ReactNode; points: ApiPoint[] } {
  switch (result.paymentStatus) {
    case 'DONE':
      return {
        head: (
          <div className="result ok">
            <strong>충전이 완료되었습니다</strong>
            <span>
              {result.chargedCredit}크레딧이 충전되어 총잔액이 {result.balance}크레딧이 되었습니다.
            </span>
          </div>
        ),
        actions: (
          <>
            <LinkButton href="#/history" primary>
              잔액·내역 확인
            </LinkButton>
            <LinkButton href="#/credit">크레딧 화면으로</LinkButton>
            <button type="button" className="btn ghost" id="btn-reconfirm" disabled={h.busy} onClick={h.reconfirm}>
              같은 주문 승인 재요청 (중복 지급 확인용)
            </button>
          </>
        ),
        points: [
          {
            method: 'POST',
            path: '/api/payments/confirm',
            id: 'API-69',
            status: 'draft',
            note: '요청 paymentKey·orderId·amount → 응답 orderId·paymentStatus·chargedCredit·balance. 이미 DONE이면 재지급 없이 같은 결과'
          }
        ]
      };
    case 'FAILED':
    case 'CANCELED': {
      const canceled = result.paymentStatus === 'CANCELED';
      return {
        head: (
          <div className={`result ${canceled ? 'cancel' : 'err'}`}>
            <strong>{canceled ? '결제를 취소했습니다' : '결제에 실패했습니다'}</strong>
            <span>
              크레딧은 지급되지 않았습니다.{canceled ? '' : ' 다시 시도하거나 다른 상품을 선택해 주세요.'}
            </span>
          </div>
        ),
        actions: (
          <>
            <button type="button" className="btn primary" id="btn-retry" onClick={h.retry}>
              다시 결제하기
            </button>
            <LinkButton href="#/credit">상품 다시 선택</LinkButton>
          </>
        ),
        points: [FAIL_API]
      };
    }
    default:
      // PENDING(그 외 상태 포함): 결과를 단정하지 않고 재확인
      return {
        head: (
          <div className="result pending">
            <strong>결제 결과를 확인하고 있습니다</strong>
            <span>승인 결과가 확인되기 전에는 크레딧이 지급되지 않습니다. 잠시 후 결과를 다시 확인해 주세요.</span>
          </div>
        ),
        actions: (
          <>
            <label className="small muted recheck">
              재확인 시 재현할 결과(mock)
              <select id="recheck-as" value={h.recheckAs} onChange={(e) => h.setRecheckAs(e.target.value as RecheckAs)}>
                <option value="DONE">승인 완료</option>
                <option value="FAILED">실패</option>
                <option value="PENDING">아직 확인 중</option>
              </select>
            </label>
            <button type="button" className="btn primary" id="btn-recheck" disabled={h.busy} onClick={h.recheck}>
              결과 다시 확인
            </button>
            <LinkButton href="#/history">내역 보기</LinkButton>
          </>
        ),
        points: [
          { method: 'POST', path: '/api/payments/confirm', id: 'API-69', status: 'draft', note: '토스 응답 지연 시 응답 형식은 명세 미정' },
          { method: '?', path: '결제 결과 조회(재확인)', status: 'none', note: 'CR-03 "결제 조회로 재확인"의 경로·응답 미정' }
        ]
      };
  }
}
