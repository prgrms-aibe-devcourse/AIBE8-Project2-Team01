// 내 요청서 · 받은 견적 (W·03·04 / M·03·04) · 고객 전용
// 웹은 목록과 상세를 나란히, 모바일은 목록 → 상세로 이동한다 (같은 컴포넌트)
// 의뢰 요청은 서버 단일 API(POST /api/chat-rooms) 한 번 호출로 차감·매칭·채팅방 생성을 처리한다
import { useEffect, useState } from 'react';
import { ApiError } from '../api/client';
import {
  cancelServiceRequest, getMyRequests, getRequestQuotes, getServiceRequest, requestQuote,
  type RequestQuote, type ServiceRequestDetail
} from '../api/mvp';
import { Badge, Empty, LoadBox } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, ErrorView, Lines, LoadingView, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import {
  quoteBadge, quoteDeadline, remainText, requestBadge, requestDeadline, requestOpen, shortDateTime, won
} from '../lib/labels';
import { friendly } from '../lib/messages';
import { errorText } from '../api/client';

export function MyRequestsPage({ selected, quote }: { selected: string | null; quote: string | null }) {
  const list = useLoad(() => getMyRequests(), []);
  const selId = selected ? Number(selected) : null;

  // 취소 후: 최신 목록을 받아 남은 첫 요청서로 바로 이동한다 (이전 목록으로 자동 선택되지 않게)
  const onCancelled = async () => {
    try {
      const data = await getMyRequests();
      list.set(data);
      navigate('/requests', data[0] ? { id: data[0].requestId } : undefined, true);
    } catch {
      navigate('/requests', undefined, true);
      list.reload().catch(() => undefined);
    }
  };

  // 웹에서는 선택이 없으면 첫 요청서를 보여 준다 (모바일은 목록부터)
  useEffect(() => {
    if (list.state.kind === 'ok' && selId === null && list.state.data.length && window.matchMedia('(min-width: 768px)').matches) {
      navigate('/requests', { id: list.state.data[0].requestId }, true);
    }
  }, [list.state, selId]);

  return (
    <main className={`page req-page${selId ? ' has-sel' : ''}`}>
      <div className="split">
        <aside className="split-list col gap-s">
          <div className="row between">
            <h1 className="page-title">내 요청서</h1>
            <a className="btn primary small" href="#/">+ 새 요청서</a>
          </div>
          <LoadBox load={list.state} title="요청서 목록을 불러오지 못했어요" onRetry={() => list.reload()}>
            {(items) => items.length === 0 ? (
              <Empty title="아직 보낸 요청서가 없어요" desc={'필요한 서비스를 고르고 요청서를 보내 보세요.\n취소한 요청서는 목록에 보이지 않아요.'} action={<a className="btn primary" href="#/">서비스 고르기</a>} />
            ) : (
              <div className="col gap-s">
                {items.map((r) => {
                  const b = requestBadge(r.requestStatus, r.createdAt);
                  return (
                    <a key={r.requestId} href={`#/requests?id=${r.requestId}`} className={`req-card${r.requestId === selId ? ' on' : ''}`}>
                      <span className="row between"><b>{r.secondCategoryName}</b><Badge {...b} /></span>
                      <span className="sub">{r.secondRegionName} · {shortDateTime(r.createdAt)}</span>
                      {r.requestStatus !== 'CLOSED' && requestOpen(r.createdAt) && <span className="sub strong">마감까지 {remainText(requestDeadline(r.createdAt))}</span>}
                    </a>
                  );
                })}
              </div>
            )}
          </LoadBox>
        </aside>
        <section className="split-detail">
          {selId ? <RequestDetail key={selId} id={selId} quoteId={quote ? Number(quote) : null} onChanged={() => list.reload(true).catch(() => undefined)} onCancelled={onCancelled} />
            : <div className="state card hide-m"><b>요청서를 골라 주세요</b></div>}
        </section>
      </div>
    </main>
  );
}

function RequestDetail({ id, quoteId, onChanged, onCancelled }: { id: number; quoteId: number | null; onChanged: () => Promise<unknown>; onCancelled: () => Promise<void> }) {
  const toast = useToast();
  const detail = useLoad(() => getServiceRequest(id), [id]);
  const quotes = useLoad(() => getRequestQuotes(id), [id]);
  const [askCancel, setAskCancel] = useState(false);
  const cancel = useSingleFlight();
  const [cancelErr, setCancelErr] = useState('');

  const doCancel = () => cancel.run(async () => {
    try {
      await cancelServiceRequest(id);
      setAskCancel(false);
      toast('요청서를 취소했어요.\n받은 견적의 예약 크레딧은 고수에게 돌아가요.');
      await onCancelled();
    } catch (e) {
      setCancelErr(`요청서를 취소하지 못했어요.\n${friendly(e)}`);
    }
  });

  if (detail.state.kind === 'loading') return <div className="card"><LoadingView label="요청서를 불러오는 중이에요" /></div>;
  if (detail.state.kind === 'error') {
    const nf = detail.state.error instanceof ApiError && detail.state.error.status === 404;
    return nf ? <Empty title="요청서를 찾을 수 없어요" desc={'취소되었거나 볼 수 없는 요청서예요.'} action={<a className="btn" href="#/requests">목록으로</a>} />
      : <ErrorView title="요청서를 불러오지 못했어요" message={errorText(detail.state.error)} onRetry={() => detail.reload()} />;
  }
  const r = detail.state.data;
  const b = requestBadge(r.requestStatus, r.createdAt);
  const open = r.requestStatus !== 'CLOSED' && requestOpen(r.createdAt);
  const deadline = requestDeadline(r.createdAt);

  return (
    <div className="col gap">
      <a className="back show-m" href="#/requests">‹ 내 요청서</a>
      <div className="card">
        <div className="row between">
          <div className="col">
            <span className="row"><b className="card-title">{r.secondCategoryName}</b><Badge {...b} /></span>
            <span className="sub">{r.secondRegionName} · 작성 {shortDateTime(r.createdAt)}</span>
          </div>
          {r.requestStatus === 'OPEN' && <button type="button" className="btn danger-ghost small" onClick={() => { setCancelErr(''); setAskCancel(true); }}>요청서 취소</button>}
        </div>
        {open ? (
          <div className="col gap-s">
            <div className="row between"><small className="blue-t strong">견적 접수 중 · 마감까지 {remainText(deadline)}</small><small className="sub">{shortDateTime(deadline.toISOString())} 마감</small></div>
            <div className="bar"><i style={{ width: `${Math.min(100, Math.max(3, 100 - ((deadline.getTime() - Date.now()) / (24 * 3600_000)) * 100))}%` }} /></div>
            {r.requestStatus === 'MATCHED' && <small className="sub">상담 중이어도 마감 전까지 새 견적을 받아요.</small>}
          </div>
        ) : <small className="sub">견적 접수가 끝났어요.<br />유효기간이 남은 기존 견적은 계속 선택할 수 있어요.</small>}
        <div className="answers">
          {r.answers.map((a) => <div className="cell" key={a.questionId}><small>{a.questionContent}</small><span>{a.content}</span></div>)}
        </div>
      </div>

      <div className="row between"><h2 className="card-title">받은 견적 <span className="blue-t">{quotes.state.kind === 'ok' ? quotes.state.data.length : ''}</span></h2><small className="sub">견적은 발송 후 3일 동안 선택할 수 있어요</small></div>
      <LoadBox load={quotes.state} title="받은 견적을 불러오지 못했어요" onRetry={() => quotes.reload()}>
        {(qs) => qs.length === 0 ? <Empty title="아직 도착한 견적이 없어요" desc={open ? '고수가 견적을 보내면 여기에 표시돼요.' : '접수 기간 동안 도착한 견적이 없어요.'} /> : (
          <div className="col gap-s">
            {qs.map((q) => <QuoteCard key={q.quoteId} q={q} onOpen={() => navigate('/requests', { id, quote: q.quoteId })} />)}
          </div>
        )}
      </LoadBox>

      {quoteId && quotes.state.kind === 'ok' && (
        <QuoteModal request={r} quote={quotes.state.data.find((q) => q.quoteId === quoteId) ?? null}
          others={quotes.state.data.filter((q) => q.quoteId !== quoteId && q.quoteStatus === 'REQUESTED').length}
          onClose={() => navigate('/requests', { id }, true)} onChanged={() => { quotes.reload(true).catch(() => undefined); detail.reload(true).catch(() => undefined); onChanged().catch(() => undefined); }} />
      )}

      {askCancel && (
        <Modal title="요청서를 취소할까요?" onClose={() => !cancel.busy && setAskCancel(false)}
          actions={<>
            <button type="button" className="btn" disabled={cancel.busy} onClick={() => setAskCancel(false)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={cancel.busy} onClick={doCancel}>{cancel.busy ? <><Spinner light />취소하는 중</> : '요청서 취소'}</button>
          </>}>
          <p><Lines text={'취소하면 새 견적을 받지 않고 받은 견적에도 의뢰를 요청할 수 없어요.\n취소한 요청서는 목록에서 사라져요.'} /></p>
          {cancelErr && <Banner><Lines text={cancelErr} /></Banner>}
        </Modal>
      )}
    </div>
  );
}

function QuoteCard({ q, onOpen }: { q: RequestQuote; onOpen: () => void }) {
  const b = quoteBadge(q.quoteStatus);
  return (
    <button type="button" className={`q-card${q.quoteStatus === 'REQUESTED' ? ' matched' : ''}`} onClick={onOpen}>
      <div className="col grow">
        <span className="row"><b>{q.expertNickname}</b><Badge {...b} /></span>
        <span className="sub ellipsis">{q.description}</span>
        <small className="sub">{q.quoteStatus === 'SENT' ? `${shortDateTime(quoteDeadline(q.sentAt).toISOString())}까지 선택할 수 있어요` : '채팅에서 상담하고 있어요'}</small>
      </div>
      <div className="col end">
        <span className="price">{won(q.price)}</span>
        <span className="btn small">견적 보기</span>
      </div>
    </button>
  );
}

// 견적 상세 모달: 의뢰 요청하고 채팅 → 성공한 방으로만 이동
function QuoteModal({ request: r, quote: q, others, onClose, onChanged }: {
  request: ServiceRequestDetail; quote: RequestQuote | null; others: number; onClose: () => void; onChanged: () => void;
}) {
  const { busy, run } = useSingleFlight();
  const [err, setErr] = useState('');
  const toast = useToast();
  if (!q) {
    return (
      <Modal full title="견적을 찾을 수 없어요" onClose={onClose} actions={<button type="button" className="btn" onClick={onClose}>닫기</button>}>
        <p>회수되었거나 유효기간이 지난 견적이에요.</p>
      </Modal>
    );
  }
  const cancelled = r.requestStatus === 'CLOSED';
  const doRequest = () => run(async () => {
    setErr('');
    try {
      const res = await requestQuote(q.quoteId);
      toast('의뢰를 요청했어요.\n채팅방에서 상담을 시작해 보세요.');
      onChanged();
      navigate(`/chats/${res.roomId}`);
    } catch (e) {
      // 실패를 매칭 성공으로 표시하지 않는다. 최신 상태를 다시 읽어 버튼을 맞춘다
      setErr(`의뢰를 요청하지 못했어요.\n${friendly(e)}`);
      onChanged();
    }
  });
  return (
    <Modal full title={`${q.expertNickname}님의 견적`} onClose={() => !busy && onClose()}
      actions={<>
        <button type="button" className="btn" disabled={busy} onClick={onClose}>닫기</button>
        {q.quoteStatus === 'SENT' && !cancelled && (
          <button type="button" className="btn primary" disabled={busy} onClick={doRequest}>{busy ? <><Spinner light />요청하는 중</> : '의뢰 요청하고 채팅'}</button>
        )}
        {q.quoteStatus === 'REQUESTED' && <a className="btn primary" href="#/chats">채팅 목록으로</a>}
      </>}>
      <div className="col gap-s">
        <span className="row"><Badge {...quoteBadge(q.quoteStatus)} /><a href={`#/experts/${q.expertProfileId}`} className="small-link">고수 프로필 보기 ›</a></span>
        <div className="cell"><small>제안 가격</small><b className="price">{won(q.price)}</b></div>
        <div className="cell"><small>서비스 설명</small><span className="pre">{q.description}</span></div>
        <small className="sub">{shortDateTime(q.sentAt)} 발송{q.quoteStatus === 'SENT' ? ` · ${shortDateTime(quoteDeadline(q.sentAt).toISOString())}까지 선택할 수 있어요.` : ''}</small>
        {q.quoteStatus === 'SENT' && others > 0 && !cancelled && <Banner kind="info"><Lines text={`이 요청서는 다른 고수 ${others}명과 상담 중이에요.\n이 견적도 선택해 함께 상담할 수 있어요.`} /></Banner>}
        {q.quoteStatus === 'SENT' && !cancelled && <small className="sub">의뢰를 요청하면 채팅방이 열리고 고수와 상담을 시작해요.</small>}
        {err && <Banner><Lines text={err} /></Banner>}
      </div>
    </Modal>
  );
}
