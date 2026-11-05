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
import { useMyExpertProfile } from '../hooks/useData';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { remainText, requestDeadline, shortDateTime, won } from '../lib/labels';
import { friendly } from '../lib/messages';
import CategoryIcon from '../components/CategoryIcon';

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

  // 내가 맡은 서비스를 기준으로 거른다. 서버도 서비스·활동 지역이 맞는 요청서만 내려 준다
  const mine = useMyExpertProfile(true);
  const myServices = mine.state.kind === 'ok' ? mine.state.profile.services.map((x) => x.name) : [];
  const cats = useMemo(() => {
    const fromList = list.state.kind === 'ok' ? list.state.data.map((r) => r.secondCategoryName) : [];
    return ['전체', ...Array.from(new Set([...myServices, ...fromList]))];
  }, [list.state, myServices.join('|')]); // eslint-disable-line react-hooks/exhaustive-deps
  const countOf = (c: string) => (list.state.kind === 'ok' ? list.state.data.filter((r) => c === '전체' || r.secondCategoryName === c).length : 0);

  return (
    <main className={`page req-page${selId ? ' has-sel' : ''}`}>
      <div className="split received-split">
        <aside className="split-list col gap-s">
          <h1 className="page-title">받은 요청서 <span className="blue-t">{list.state.kind === 'ok' ? list.state.data.length : ''}</span></h1>
          <p className="sub">내가 맡은 서비스와 활동 지역에 맞는 요청서만 보여요</p>
          {cats.length > 2 && <div className="tabs wrap-tabs">{cats.map((c) => <button type="button" key={c} className={`tab${c === filter ? ' on' : ''}`} onClick={() => setFilter(c)}>{c} <span className="tab-count">{countOf(c)}</span></button>)}</div>}
          <LoadBox load={list.state} title="받은 요청서를 불러오지 못했어요" onRetry={() => list.reload()}>
            {(items) => {
              const shown = items.filter((r) => filter === '전체' || r.secondCategoryName === filter);
              return shown.length === 0 ? (
                <Empty title="지금 받은 요청서가 없어요" desc={'접수 중인 요청서 중 내 서비스·지역에 맞는 요청이 오면 여기에 보여요.\n프로필의 서비스와 활동 지역을 확인해 보세요.'} action={<a className="btn" href="#/expert/me">내 고수 프로필</a>} />
              ) : (
                <div className="col gap-s">
                  {shown.map((r) => (
                    <a key={r.requestId} href={`#/received?id=${r.requestId}`} className={`req-card${r.requestId === selId ? ' on' : ''}`}>
                      <CategoryIcon secondName={r.secondCategoryName} size={44} />
                      <span className="req-card-body">
                        <span className="row between"><b>{r.secondCategoryName}</b>{r.sentQuoteId ? <Badge text="견적 보냄" tone="teal" /> : <small className="blue-t strong">마감까지 {remainText(requestDeadline(r.createdAt))}</small>}</span>
                        <span className="sub">{r.secondRegionName} · {shortDateTime(r.createdAt)}</span>
                      </span>
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

  if (detail.state.kind === 'loading') return <div className="card"><LoadingView label="요청서를 불러오는 중이에요" /></div>;
  if (detail.state.kind === 'error') {
    const gone = detail.state.error instanceof ApiError && (detail.state.error.status === 404 || detail.state.error.status === 403);
    return gone ? <Empty title="볼 수 없는 요청서예요" desc={'취소되었거나 내 서비스·지역 조건에 맞지 않는 요청서예요.'} action={<a className="btn" href="#/received">목록으로</a>} />
      : <ErrorView title="요청서를 불러오지 못했어요" message={errorText(detail.state.error)} onRetry={() => detail.reload()} />;
  }
  const r = detail.state.data;
  const deadline = requestDeadline(r.createdAt);
  const expired = deadline.getTime() <= Date.now();
  // 견적을 보낼 수 있으면 요청 내용 옆(좁은 화면은 바로 아래)에 견적 입력을 바로 펼친다
  const canQuote = !sentQuoteId && !expired && inList;
  const answered = r.answers.filter((a) => a.content?.trim());

  return (
    <div className="received-detail">
      <a className="back show-m" href="#/received">‹ 받은 요청서</a>
      {/* 요청 내용(위)과 견적 보내기(아래)를 한 카드 안에 두어 읽고 바로 쓴다 */}
      <div className={`card received-card${canQuote ? ' with-form' : ''}`}>
      <section className="req-summary" aria-labelledby="req-summary-title">
        <div className="req-summary-head">
          <CategoryIcon secondName={r.secondCategoryName} size={48} />
          <div className="col">
            <span className="row wrap">
              <b id="req-summary-title" className="card-title">{r.secondCategoryName}</b>
              {!expired && <span className="badge blue">마감까지 {remainText(deadline)}</span>}
            </span>
            <small className="sub">{r.secondRegionName} · {r.customerNickname}님 · {shortDateTime(r.createdAt)} 요청</small>
          </div>
        </div>
        {answered.length > 0 ? (
          <dl className="req-answers" tabIndex={canQuote ? 0 : undefined} aria-label="고객 요청 답변">
            {answered.map((a) => (
              <div key={a.questionId}>
                <dt>{a.questionContent}</dt>
                <dd>{a.content}</dd>
              </div>
            ))}
          </dl>
        ) : canQuote ? <div className="req-answers" aria-hidden="true" /> : <p className="sub">고객이 남긴 요청 내용이 없어요.</p>}
        <small className="sub">접수 마감 {shortDateTime(deadline.toISOString())}</small>
        {sentQuoteId ? (
          <>
            <Banner kind="info"><Lines text={'이 요청서에는 이미 견적을 보냈어요.\n보낸 견적에서 확인하거나 매칭 전이라면 회수할 수 있어요.'} /></Banner>
            <a className="btn self-start" href="#/quotes">보낸 견적 보기</a>
          </>
        ) : expired ? (
          <Banner kind="warn">견적 접수 기간이 끝난 요청서예요.</Banner>
        ) : !inList ? (
          <Banner kind="warn"><Lines text={'지금은 견적을 보낼 수 없는 요청서예요.\n취소되었거나 내 서비스·지역 조건과 맞지 않아요.'} /></Banner>
        ) : null}
      </section>
      {canQuote && <QuoteForm key={id} requestId={id} title={`${r.secondCategoryName} · ${r.secondRegionName}`} onSent={onSent} />}
      </div>
    </div>
  );
}

function QuoteForm({ requestId, title, onSent }: { requestId: number; title: string; onSent: () => void }) {
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
    <form className="quote-form" onSubmit={check} noValidate aria-label={`${title} 견적 보내기`}>
      <div className="col">
        <b className="card-title">견적 보내기</b>
        <small className="sub">요청 내용을 확인하고 금액과 설명을 적어 주세요.</small>
      </div>
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
        {wallet.state.kind === 'ok' && !enough
          ? <a className="btn primary" href="#/credit">크레딧 충전</a>
          : <button type="submit" className="btn primary block" disabled={busy || wallet.state.kind !== 'ok'}>견적 발송 · {wallet.state.kind === 'ok' ? wallet.state.data.creditPerQuote : 2}크레딧 예약</button>}
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
