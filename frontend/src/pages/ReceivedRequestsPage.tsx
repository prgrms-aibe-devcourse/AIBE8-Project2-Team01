// 받은 요청서 · 견적 보내기 (W·09·10 / M·09·10) · 고수 전용
// 내 서비스·활동 지역에 맞고 접수 중인 요청서만 서버가 내려준다
// 발송 시 2크레딧이 예약되고 고객이 의뢰를 요청할 때 실제로 차감된다
import { useEffect, useMemo, useRef, useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import { createQuote, getCredit, getMatchedRequests, getServiceRequest, type CreditBalance } from '../api/mvp';
import { Badge, Empty, LoadBox } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, ErrorView, Field, Lines, LoadingView, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { remainText, requestDeadline, shortDateTime, won } from '../lib/labels';
import { friendly } from '../lib/messages';

const DESC_MAX = 1000;

export function ReceivedRequestsPage({ selected }: { selected: string | null }) {
  const list = useLoad(() => getMatchedRequests(), []);
  const [filter, setFilter] = useState<string>('전체');
  const selId = selected ? Number(selected) : null;

  useEffect(() => {
    if (list.state.kind === 'ok' && selId === null && list.state.data.length && window.matchMedia('(min-width: 768px)').matches) {
      navigate('/received', { id: list.state.data[0].requestId }, true);
    }
  }, [list.state, selId]);

  // 목록에 없는 요청서를 고르면(새로 들어온 요청 등) 목록을 한 번 다시 불러온다
  const refetched = useRef<number | null>(null);
  useEffect(() => {
    if (list.state.kind === 'ok' && selId !== null && !list.state.data.some((r) => r.requestId === selId) && refetched.current !== selId) {
      refetched.current = selId;
      list.reload(true).catch(() => undefined);
    }
  }, [list.state, selId]); // eslint-disable-line react-hooks/exhaustive-deps

  const cats = useMemo(() => list.state.kind === 'ok' ? ['전체', ...Array.from(new Set(list.state.data.map((r) => r.secondCategoryName)))] : ['전체'], [list.state]);

  return (
    <main className={`page req-page${selId ? ' has-sel' : ''}`}>
      <div className="split">
        <aside className="split-list col gap-s">
          <h1 className="page-title">받은 요청서 <span className="blue-t">{list.state.kind === 'ok' ? list.state.data.length : ''}</span></h1>
          <p className="sub">내 서비스와 활동 지역에 맞는 요청이에요</p>
          {cats.length > 2 && <div className="tabs">{cats.map((c) => <button type="button" key={c} className={`tab${c === filter ? ' on' : ''}`} onClick={() => setFilter(c)}>{c}</button>)}</div>}
          <LoadBox load={list.state} title="받은 요청서를 불러오지 못했어요" onRetry={() => list.reload()}>
            {(items) => {
              const shown = items.filter((r) => filter === '전체' || r.secondCategoryName === filter);
              return shown.length === 0 ? (
                <Empty title="지금 받은 요청서가 없어요" desc={'접수 중인 요청서 중 내 서비스·지역에 맞는 요청이 오면 여기에 보여요.\n프로필의 서비스와 활동 지역을 확인해 보세요.'} action={<a className="btn" href="#/expert/me">내 고수 프로필</a>} />
              ) : (
                <div className="col gap-s">
                  {shown.map((r) => (
                    <a key={r.requestId} href={`#/received?id=${r.requestId}`} className={`req-card${r.requestId === selId ? ' on' : ''}`}>
                      <span className="row between"><b>{r.secondCategoryName}</b>{r.sentQuoteId ? <Badge text="견적 보냄" tone="teal" /> : <small className="blue-t strong">마감까지 {remainText(requestDeadline(r.createdAt))}</small>}</span>
                      <span className="sub">{r.secondRegionName} · {shortDateTime(r.createdAt)}</span>
                    </a>
                  ))}
                </div>
              );
            }}
          </LoadBox>
        </aside>
        <section className="split-detail">
          {selId && list.state.kind === 'ok'
            ? <ReceivedDetail key={selId} id={selId} sentQuoteId={list.state.data.find((r) => r.requestId === selId)?.sentQuoteId ?? null}
                inList={list.state.data.some((r) => r.requestId === selId)} onSent={() => list.reload(true).catch(() => undefined)} />
            : selId ? <div className="card"><LoadingView /></div> : <div className="state card hide-m"><b>요청서를 골라 주세요</b></div>}
        </section>
      </div>
    </main>
  );
}

function ReceivedDetail({ id, sentQuoteId, inList, onSent }: { id: number; sentQuoteId: number | null; inList: boolean; onSent: () => void }) {
  const detail = useLoad(() => getServiceRequest(id), [id]);
  const [writing, setWriting] = useState(false);

  if (detail.state.kind === 'loading') return <div className="card"><LoadingView label="요청서를 불러오는 중이에요" /></div>;
  if (detail.state.kind === 'error') {
    const gone = detail.state.error instanceof ApiError && (detail.state.error.status === 404 || detail.state.error.status === 403);
    return gone ? <Empty title="볼 수 없는 요청서예요" desc={'취소되었거나 내 서비스·지역 조건에 맞지 않는 요청서예요.'} action={<a className="btn" href="#/received">목록으로</a>} />
      : <ErrorView title="요청서를 불러오지 못했어요" message={errorText(detail.state.error)} onRetry={() => detail.reload()} />;
  }
  const r = detail.state.data;
  const deadline = requestDeadline(r.createdAt);
  const expired = deadline.getTime() <= Date.now();

  return (
    <div className="col gap">
      <a className="back show-m" href="#/received">‹ 받은 요청서</a>
      <div className="card">
        <span className="sec-title">요청서 상세</span>
        <b className="card-title">{r.secondCategoryName} · {r.secondRegionName}</b>
        <div className="answers">
          {r.answers.map((a) => <div className="cell" key={a.questionId}><small>{a.questionContent}</small><span>{a.content}</span></div>)}
          <div className="cell"><small>접수 마감</small><span>{shortDateTime(deadline.toISOString())}</span></div>
        </div>
        {sentQuoteId ? (
          <Banner kind="info"><Lines text={'이 요청서에는 이미 견적을 보냈어요.\n보낸 견적에서 확인하거나 매칭 전이라면 회수할 수 있어요.'} /></Banner>
        ) : expired ? (
          <Banner kind="warn">견적 접수 기간이 끝난 요청서예요.</Banner>
        ) : !inList ? (
          <Banner kind="warn"><Lines text={'지금은 견적을 보낼 수 없는 요청서예요.\n취소되었거나 내 서비스·지역 조건과 맞지 않아요.'} /></Banner>
        ) : !writing ? (
          <button type="button" className="btn primary m-block" onClick={() => setWriting(true)}>견적 보내기 · 2크레딧 예약</button>
        ) : null}
        {sentQuoteId && <a className="btn" href="#/quotes">보낸 견적 보기</a>}
      </div>
      {writing && !sentQuoteId && !expired && inList && <QuoteForm requestId={id} title={`${r.secondCategoryName} · ${r.secondRegionName}`} onCancel={() => setWriting(false)} onSent={() => { setWriting(false); onSent(); }} />}
    </div>
  );
}

function QuoteForm({ requestId, title, onCancel, onSent }: { requestId: number; title: string; onCancel: () => void; onSent: () => void }) {
  const toast = useToast();
  const wallet = useLoad<CreditBalance>(() => getCredit(), []);
  const [price, setPrice] = useState('');
  const [desc, setDesc] = useState('');
  const [errs, setErrs] = useState<{ price?: string; desc?: string }>({});
  const [confirm, setConfirm] = useState(false);
  const [fail, setFail] = useState('');
  const { busy, run } = useSingleFlight();

  const enough = wallet.state.kind === 'ok' && wallet.state.data.availableCredit >= wallet.state.data.creditPerQuote;
  const priceNum = Number(price);

  const check = (e: FormEvent) => {
    e.preventDefault();
    const next: typeof errs = {};
    if (!price || !Number.isFinite(priceNum) || priceNum <= 0) next.price = '견적 금액을 1원 이상 입력해 주세요.';
    if (!desc.trim()) next.desc = '서비스 설명을 입력해 주세요.';
    else if (desc.trim().length > DESC_MAX) next.desc = `${DESC_MAX}자 이내로 입력해 주세요.`;
    setErrs(next); setFail('');
    if (!Object.keys(next).length) setConfirm(true);
  };

  const send = () => run(async () => {
    try {
      await createQuote(requestId, { description: desc.trim(), price: priceNum });
      setConfirm(false);
      toast('견적을 보냈어요.\n2크레딧이 예약됐어요.');
      onSent();
    } catch (e) {
      setConfirm(false);
      setFail(`견적을 보내지 못했어요.\n${friendly(e)}`);
      wallet.reload(true).catch(() => undefined);
    }
  });

  return (
    <form className="card" onSubmit={check} noValidate>
      <div className="row between"><b className="card-title">견적 보내기</b><small className="sub">{title}</small></div>
      <small className="sub">서비스와 지역은 요청서 내용으로 정해져요.</small>
      <fieldset className="col gap" disabled={busy} style={{ border: 0, padding: 0, margin: 0 }}>
        <Field label="제안 가격" htmlFor="qf-price" error={errs.price}>
          <span className="row"><input id="qf-price" className={`input${errs.price ? ' bad' : ''}`} inputMode="numeric" value={price}
            onChange={(e) => { setPrice(e.target.value.replace(/\D/g, '').slice(0, 10)); setErrs((x) => ({ ...x, price: undefined })); }} /><span>원</span></span>
          {priceNum > 0 && <small className="sub">{won(priceNum)}</small>}
        </Field>
        <Field label="서비스 설명" hint={`${desc.trim().length} / ${DESC_MAX}`} htmlFor="qf-desc" error={errs.desc}>
          <textarea id="qf-desc" className={`input area${errs.desc ? ' bad' : ''}`} value={desc} onChange={(e) => { setDesc(e.target.value); setErrs((x) => ({ ...x, desc: undefined })); }} />
        </Field>
      </fieldset>
      <div className="credit-note">
        <span className="c-ic" aria-hidden="true">C</span>
        <div className="col">
          <b>발송하면 2크레딧이 예약돼요</b>
          <small className="sub">고객이 의뢰를 요청하면 실제로 차감돼요.</small>
          {wallet.state.kind === 'loading' && <small className="sub">크레딧을 확인하는 중이에요</small>}
          {wallet.state.kind === 'error' && <small className="err-t">크레딧을 불러오지 못했어요. <button type="button" className="link-btn inline" onClick={() => wallet.reload()}>다시 시도</button></small>}
          {wallet.state.kind === 'ok' && <small className={enough ? 'blue-t strong' : 'err-t'}>사용 가능 {wallet.state.data.availableCredit}크레딧 · {wallet.state.data.availableQuoteCount}건 더 보낼 수 있어요.</small>}
        </div>
      </div>
      {wallet.state.kind === 'ok' && !enough && (
        <Banner kind="warn"><Lines text={`견적을 보내려면 ${wallet.state.data.creditPerQuote}크레딧이 필요해요.\n충전한 뒤 발송해 주세요.`} /></Banner>
      )}
      {fail && <Banner><Lines text={fail} /></Banner>}
      <div className="row end-row m-actions">
        <button type="button" className="btn" disabled={busy} onClick={onCancel}>취소</button>
        {wallet.state.kind === 'ok' && !enough
          ? <a className="btn primary" href="#/credit">크레딧 충전</a>
          : <button type="submit" className="btn primary" disabled={busy || wallet.state.kind !== 'ok'}>견적 발송</button>}
      </div>
      {confirm && (
        <Modal title="견적을 발송할까요?" onClose={() => !busy && setConfirm(false)}
          actions={<>
            <button type="button" className="btn" disabled={busy} onClick={() => setConfirm(false)}>돌아가기</button>
            <button type="button" className="btn primary" disabled={busy} onClick={send}>{busy ? <><Spinner light />발송하는 중</> : '발송'}</button>
          </>}>
          <div className="cell"><small>최종 금액</small><b className="price">{won(priceNum)}</b></div>
          <p><Lines text={'매칭되면 차감된 크레딧은 돌려받을 수 없어요.\n금액과 내용을 다시 확인해 주세요.'} /></p>
        </Modal>
      )}
    </form>
  );
}
