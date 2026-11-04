// 크레딧 · 충전 상품 (W·12) · 충전·사용 내역 (W·14) · 고수 전용
// 결제는 토스페이먼츠 테스트 결제창으로 진행한다 (클라이언트 키: VITE_TOSS_CLIENT_KEY)
// 예약 크레딧(견적 발송)과 실제 차감(의뢰 요청)을 구분해 보여 준다
import { useState } from 'react';
import { createPayment, getCredit, getCreditHistories, getCreditProducts, getCreditReservations, type CreditBalance, type CreditHistoryType, type CreditProduct } from '../api/mvp';
import { Empty, LoadBox, PageHead, Pager } from '../components/common';
import { Banner, Lines, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { HISTORY_LABEL, shortDateTime, won } from '../lib/labels';
import { friendly } from '../lib/messages';
import { openTossPayment, tossClientKey } from '../lib/toss';

function BalanceCards({ b }: { b: CreditBalance }) {
  return (
    <div className="bal-grid">
      <div className="card bal"><small>총잔액</small><b>{b.balance}</b><span className="sub">충전·사용 내역 합계</span></div>
      <div className="card bal"><small>견적에 예약됨</small><b className="warn-t">{b.reservedCredit}</b><span className="sub">매칭 전 견적 {b.creditPerQuote ? Math.round(b.reservedCredit / b.creditPerQuote) : 0}건</span></div>
      <div className="card bal on"><small>사용 가능</small><b className="blue-t">{b.availableCredit}</b><span className="sub">견적 {b.availableQuoteCount}건 더 보낼 수 있어요</span></div>
    </div>
  );
}

export function CreditPage() {
  const data = useLoad(async () => {
    const [balance, products] = await Promise.all([getCredit(), getCreditProducts()]);
    return { balance, products };
  }, []);
  const [pick, setPick] = useState<CreditProduct | null>(null);
  const [confirm, setConfirm] = useState(false);
  const [fail, setFail] = useState('');
  const pay = useSingleFlight();
  const key = tossClientKey();

  const startPay = () => pick && pay.run(async () => {
    setFail('');
    let order;
    try {
      order = await createPayment(pick.id);
    } catch (e) { setFail(`주문을 만들지 못했어요.\n${friendly(e)}`); return; }
    try {
      // 결제창으로 이동한다. 결과는 돌아온 뒤 결과 화면에서 서버 승인으로 확정한다
      await openTossPayment(order);
    } catch (e) {
      setFail(`결제창을 열지 못했어요.\n${e instanceof Error && e.message ? e.message : '잠시 후 다시 시도해 주세요.'}`);
    }
  });

  return (
    <main className="page">
      <PageHead title="크레딧 충전" right={<a className="strong-link" href="#/credit/history">충전·사용 내역 ›</a>} />
      <LoadBox load={data.state} title="크레딧 정보를 불러오지 못했어요" onRetry={() => data.reload()}>
        {({ balance, products }) => (
          <div className="col gap">
            <BalanceCards b={balance} />
            <Banner kind="info"><Lines text={`견적을 보낼 때 ${balance.creditPerQuote}크레딧이 예약돼요.\n고객이 의뢰를 요청하면 그때 차감돼요.`} /></Banner>
            <div className="card">
              <b className="card-title">충전 상품</b>
              {products.length === 0 ? <p className="sub">지금 판매 중인 충전 상품이 없어요.</p> : (
                <div className="prod-grid">
                  {products.map((p) => (
                    <button type="button" key={p.id} className={`prod${pick?.id === p.id ? ' on' : ''}`} aria-pressed={pick?.id === p.id} onClick={() => setPick(p)}>
                      <b>{p.name}</b><span className="price">{won(p.price)}</span>
                      <small className="sub">견적 {balance.creditPerQuote ? Math.floor(p.creditAmount / balance.creditPerQuote) : 0}건분</small>
                    </button>
                  ))}
                </div>
              )}
              <div className="row between wrap m-actions">
                <span className="sub">{pick ? <>선택한 상품 <b className="t1">{pick.name} · {won(pick.price)}</b></> : '충전할 상품을 골라 주세요.'}</span>
                <button type="button" className="btn primary" disabled={!pick || pay.busy} onClick={() => { setFail(''); setConfirm(true); }}>다음: 결제 정보 확인</button>
              </div>
              {!key && <Banner kind="warn"><Lines text={'테스트 결제창의 클라이언트 키가 설정되지 않았어요.\n.env.local에 VITE_TOSS_CLIENT_KEY를 넣으면 결제할 수 있어요.'} /></Banner>}
              {fail && <Banner><Lines text={fail} /></Banner>}
            </div>
          </div>
        )}
      </LoadBox>
      {confirm && pick && (
        <Modal full title="결제 정보를 확인해 주세요" onClose={() => !pay.busy && setConfirm(false)}
          actions={<>
            <button type="button" className="btn" disabled={pay.busy} onClick={() => setConfirm(false)}>돌아가기</button>
            <button type="button" className="btn primary" disabled={pay.busy || !key} onClick={startPay}>{pay.busy ? <><Spinner light />결제창 여는 중</> : `${won(pick.price)} 결제하기`}</button>
          </>}>
          <div className="cell"><small>충전 상품</small><b>{pick.name}</b></div>
          <div className="cell"><small>결제 금액</small><b className="price">{won(pick.price)}</b></div>
          <p><Lines text={'토스페이먼츠 테스트 결제창으로 이동해요.\n승인이 끝나면 크레딧이 바로 지급돼요.'} /></p>
          {fail && <Banner><Lines text={fail} /></Banner>}
        </Modal>
      )}
    </main>
  );
}

const TYPES: (CreditHistoryType | '')[] = ['', 'CHARGE', 'USE', 'REFUND'];

export function CreditHistoryPage({ params }: { params: URLSearchParams }) {
  const page = Math.max(1, Number(params.get('page') ?? 1) || 1);
  const type = (params.get('type') ?? '') as CreditHistoryType | '';
  const list = useLoad(() => getCreditHistories(page, type || undefined), [page, type]);
  const side = useLoad(async () => { const [balance, reservations] = await Promise.all([getCredit(), getCreditReservations()]); return { balance, reservations }; }, []);
  const go = (p: number, t = type) => navigate('/credit/history', { ...(t ? { type: t } : {}), page: p });

  return (
    <main className="page">
      <PageHead title="충전·사용 내역" back={{ href: '#/credit', label: '크레딧 충전' }} />
      <div className="split hist-split">
        <section className="col gap">
          <div className="tabs">
            {TYPES.map((t) => <button type="button" key={t || 'all'} className={`tab${t === type ? ' on' : ''}`} onClick={() => go(1, t)}>{t ? HISTORY_LABEL[t] : '전체'}</button>)}
          </div>
          <LoadBox load={list.state} title="내역을 불러오지 못했어요" onRetry={() => list.reload()}>
            {(data) => data.content.length === 0 ? <Empty title={type ? `${HISTORY_LABEL[type]} 내역이 없어요` : '아직 크레딧 내역이 없어요'} desc={'예약된 크레딧은 아직 쓰기 전이라 내역에 나오지 않아요.'} /> : (
              <>
                <div className="card hist-list">
                  {data.content.map((h) => (
                    <div className="hist" key={h.id}>
                      <span className={`h-ic ${h.amount >= 0 ? 'plus' : 'minus'}`} aria-hidden="true">{h.amount >= 0 ? '+' : '−'}</span>
                      <div className="col grow"><b>{HISTORY_LABEL[h.historyType]}</b><small className="sub">{shortDateTime(h.createAt)}{h.paymentId ? ` · 결제 #${h.paymentId}` : ''}{h.quoteId ? ` · 견적 #${h.quoteId}` : ''}</small></div>
                      <div className="col end"><b className={h.amount >= 0 ? 'blue-t' : ''}>{h.amount >= 0 ? `+${h.amount}` : h.amount}</b><small className="sub">잔액 {h.balanceAfter}</small></div>
                    </div>
                  ))}
                </div>
                <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => go(p)} />
                <small className="sub center">{data.pageNumber} / {Math.max(1, data.totalPages)} 페이지 · 총 {data.totalElements}건</small>
              </>
            )}
          </LoadBox>
        </section>
        <aside className="col gap">
          <LoadBox load={side.state} title="잔액을 불러오지 못했어요" onRetry={() => side.reload()}>
            {({ balance, reservations }) => (
              <div className="card">
                <div className="kv"><span>총잔액</span><b>{balance.balance}</b></div>
                <div className="kv"><span>견적에 예약됨</span><b className="warn-t">{balance.reservedCredit}</b></div>
                <div className="kv"><span>사용 가능</span><b className="blue-t">{balance.availableCredit}</b></div>
                <b className="sec-title">예약 중인 견적</b>
                {reservations.length === 0 ? <small className="sub">예약 중인 견적이 없어요.</small> : reservations.map((r) => (
                  <div className="kv" key={r.quoteId}><span>견적 #{r.quoteId} · {shortDateTime(r.reservedAt)}</span><b>{r.amount}</b></div>
                ))}
                <small className="sub"><Lines text={'예약된 크레딧은 아직 쓰기 전이라 내역에 나오지 않아요.\n매칭되면 사용 내역에 기록돼요.'} /></small>
                <a className="btn primary" href="#/credit">크레딧 충전하기</a>
              </div>
            )}
          </LoadBox>
        </aside>
      </div>
    </main>
  );
}
