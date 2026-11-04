// 결제 결과 (W·13) · 토스 결제창에서 돌아온 뒤 서버 승인(confirm) 또는 실패 기록(fail)을 한 번만 호출한다
// 실패·취소·확인 중 상태를 충전 완료로 표시하지 않는다
import { useEffect, useRef, useState } from 'react';
import { confirmPayment, failPayment } from '../api/mvp';
import { PageHead } from '../components/common';
import { Lines, LoadingView } from '../components/ui';
import { friendly } from '../lib/messages';

type Result =
  | { kind: 'working' }
  | { kind: 'done'; charged: number; balance: number }
  | { kind: 'pending' }
  | { kind: 'canceled' }
  | { kind: 'failed'; reason: string };

const DONE_KEY = 'efy.paymentHandled';

export function PaymentResultPage({ params }: { params: URLSearchParams }) {
  const [res, setRes] = useState<Result>({ kind: 'working' });
  const ran = useRef(false);
  useEffect(() => {
    if (ran.current) return; ran.current = true;
    const orderId = params.get('orderId') ?? '';
    // 새로고침으로 같은 결과를 다시 처리하지 않는다
    let handled: Record<string, Result> = {};
    try { handled = JSON.parse(sessionStorage.getItem(DONE_KEY) ?? '{}'); } catch { /* 무시 */ }
    if (orderId && handled[orderId]) { setRes(handled[orderId]); return; }
    const save = (r: Result) => { setRes(r); try { sessionStorage.setItem(DONE_KEY, JSON.stringify({ ...handled, [orderId]: r })); } catch { /* 무시 */ } };

    (async () => {
      if (params.get('toss') === 'success') {
        try {
          const r = await confirmPayment({ paymentKey: params.get('paymentKey') ?? '', orderId, amount: Number(params.get('amount')) });
          save(r.paymentStatus === 'DONE' ? { kind: 'done', charged: r.chargedCredit, balance: r.balance } : { kind: 'pending' });
        } catch (e) { setRes({ kind: 'failed', reason: friendly(e) }); }
      } else {
        const code = params.get('code') ?? 'UNKNOWN';
        try {
          const r = await failPayment(orderId, { code, message: params.get('message') ?? undefined });
          save(r.paymentStatus === 'CANCELED' ? { kind: 'canceled' } : { kind: 'failed', reason: params.get('message') ?? '결제가 완료되지 않았어요.' });
        } catch (e) { setRes({ kind: 'failed', reason: friendly(e) }); }
      }
    })();
  }, [params]);

  return (
    <main className="page narrow">
      <PageHead title="결제 결과" />
      <div className="card result">
        {res.kind === 'working' && <LoadingView label="결제 결과를 확인하는 중이에요" />}
        {res.kind === 'done' && <>
          <span className="r-ic ok" aria-hidden="true">✓</span>
          <b className="card-title">{res.charged}크레딧을 충전했어요</b>
          <span className="sub">지금 보유 크레딧은 {res.balance}크레딧이에요.</span>
        </>}
        {res.kind === 'pending' && <>
          <span className="r-ic warn" aria-hidden="true">!</span>
          <b className="card-title">결제 결과를 아직 확인하지 못했어요</b>
          <span className="sub"><Lines text={'결제사 응답을 확인하는 중이에요.\n크레딧은 승인이 확인된 뒤 지급돼요.\n충전·사용 내역에서 잠시 후 다시 확인해 주세요.'} /></span>
        </>}
        {res.kind === 'canceled' && <>
          <span className="r-ic" aria-hidden="true">–</span>
          <b className="card-title">결제를 취소했어요</b>
          <span className="sub">크레딧은 충전되지 않았어요.</span>
        </>}
        {res.kind === 'failed' && <>
          <span className="r-ic err" aria-hidden="true">!</span>
          <b className="card-title">결제하지 못했어요</b>
          <span className="sub"><Lines text={`${res.reason}\n크레딧은 충전되지 않았어요.`} /></span>
        </>}
        {res.kind !== 'working' && (
          <div className="row center-row">
            <a className="btn" href="#/credit/history">충전·사용 내역</a>
            <a className="btn primary" href="#/credit">크레딧 화면으로</a>
          </div>
        )}
      </div>
    </main>
  );
}
