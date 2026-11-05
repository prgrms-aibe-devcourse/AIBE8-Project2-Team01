// 관리자 · 게시글 공개 관리 · 요청서 현황 · 견적 현황
// 백엔드 GET /api/admin/posts · PATCH /api/admin/posts/{id}/visibility · GET .../visibility-history
//        GET /api/admin/requests · GET /api/admin/quotes 를 그대로 쓴다
import { useState, type FormEvent } from 'react';
import {
  adminGetPostVisibilityHistory, adminGetPosts, adminGetQuotes, adminGetRequests, adminSetPostVisibility,
  type BoardType, type PostSort, type PostSummary, type QuoteStatus, type RequestStatus, type VisibilityHistory
} from '../api/mvp';
import { Badge, Empty, LoadBox, Pager } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, Field, Lines, LoadingView, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { BOARDS, BOARD_LABEL, quoteBadge, requestBadge, shortDateTime, won } from '../lib/labels';
import { friendly } from '../lib/messages';
import { AdminNav } from './AdminBaseDataPages';

const pageOf = (q: URLSearchParams) => Math.max(1, Number(q.get('page') ?? 1) || 1);
import { RoleSelect } from '../components/RoleSelect';

const ORDER_OPTIONS = [{ value: 'desc', label: '내림차순' }, { value: 'asc', label: '오름차순' }];
const orderOf = (q: URLSearchParams) => (q.get('order') === 'asc' ? 'asc' : 'desc');

function Head({ title, sub }: { title: string; sub: string }) {
  return (
    <div className="row between wrap">
      <div className="col"><h1 className="page-title">{title}</h1><small className="sub">{sub}</small></div>
    </div>
  );
}

// ───────── 게시글 공개 관리 ─────────
const POST_SORTS: { key: PostSort; label: string }[] = [
  { key: 'createdAt', label: '작성일' }, { key: 'viewCount', label: '조회수' }, { key: 'likeCount', label: '좋아요' }, { key: 'commentCount', label: '댓글' }
];

export function AdminPostsPage({ params }: { params: URLSearchParams }) {
  const toast = useToast();
  const page = pageOf(params);
  const order = orderOf(params);
  const board = BOARDS.includes(params.get('board') as BoardType) ? (params.get('board') as BoardType) : undefined;
  const vis = params.get('vis') === 'shown' ? true : params.get('vis') === 'hidden' ? false : undefined;
  const sortBy = POST_SORTS.some((s) => s.key === params.get('sort')) ? (params.get('sort') as PostSort) : 'createdAt';
  const keyword = params.get('q') ?? '';
  const [q, setQ] = useState(keyword);
  const list = useLoad(() => adminGetPosts({ pageNumber: page, boardType: board, isVisible: vis, sortBy, sortOrder: order, keyword: keyword || undefined }), [page, board, vis, sortBy, order, keyword]);
  const go = (patch: Record<string, string | number | undefined>) => {
    const cur: Record<string, string | number | undefined> = { page, board, vis: params.get('vis') ?? undefined, sort: sortBy, order, q: keyword || undefined };
    const next = { ...cur, page: 1, ...patch };
    navigate('/admin/posts', Object.fromEntries(Object.entries(next).filter(([, v]) => v !== undefined && String(v) !== '')) as Record<string, string | number>);
  };
  const search = (e: FormEvent) => { e.preventDefault(); const kw = q.trim(); if (kw.length > 50) return; go({ q: kw || undefined }); };

  const [target, setTarget] = useState<PostSummary | null>(null);
  const [reason, setReason] = useState('');
  const [err, setErr] = useState('');
  const { busy, run } = useSingleFlight();
  const [historyOf, setHistoryOf] = useState<PostSummary | null>(null);

  const toggle = () => target && run(async () => {
    try {
      const next = !target.isVisible;
      await adminSetPostVisibility(target.postId, next, reason.trim() || undefined);
      toast(next ? '게시글을 다시 공개했어요.' : '게시글을 숨겼어요.\n작성자와 관리자만 볼 수 있어요.');
      setTarget(null);
      await list.reload(true).catch(() => undefined);
    } catch (e) {
      setErr(`공개 상태를 바꾸지 못했어요.\n${friendly(e)}`);
    }
  });

  return (
    <main className="page admin">
      <div className="admin-layout">
        <AdminNav current="posts" />
        <section className="col gap admin-panel">
          <div className="row between wrap admin-filters admin-post-heading">
            <Head title="게시글 관리" sub={`문제가 있는 글을 숨기거나 다시 공개해요${list.state.kind === 'ok' ? ` · 총 ${list.state.data.totalElements}개` : ''}`} />
            <form className="row search" onSubmit={search} role="search">
              <input className="input" placeholder="제목·본문 검색 (최대 50자)" value={q} maxLength={50} onChange={(e) => setQ(e.target.value)} aria-label="검색어" />
              <button className="btn" type="submit">검색</button>
            </form>
          </div>
          <div className="col gap admin-results">
            <div className="row between wrap">
              <div className="tabs">
                <button type="button" className={`tab${!board ? ' on' : ''}`} onClick={() => go({ board: undefined })}>전체</button>
                {BOARDS.map((b) => <button type="button" key={b} className={`tab${b === board ? ' on' : ''}`} onClick={() => go({ board: b })}>{BOARD_LABEL[b]}</button>)}
              </div>
              <div className="row wrap admin-sort-controls">
                <RoleSelect label="공개 상태" value={params.get('vis') ?? ''} options={[{ value: '', label: '공개 상태 전체' }, { value: 'shown', label: '공개 중' }, { value: 'hidden', label: '숨김' }]} onChange={(value) => go({ vis: value || undefined })} />
                <span className="sub">정렬</span>
                <RoleSelect label="정렬 기준" value={sortBy} options={POST_SORTS.map(({ key, label }) => ({ value: key, label }))} onChange={(value) => go({ sort: value })} />
                <RoleSelect label="정렬 방향" value={order} options={ORDER_OPTIONS} onChange={(value) => go({ order: value })} />
              </div>
            </div>

            <LoadBox load={list.state} title="게시글을 불러오지 못했어요" onRetry={() => list.reload()}>
              {(data) => data.content.length === 0 ? <Empty title="조건에 맞는 게시글이 없어요" /> : (
                <>
                  <div className="card table-card">
                    <div className="admin-table-scroll" tabIndex={0} role="region" aria-label="관리 목록">
                      <table className="tbl stack-m admin-post-tbl">
                        <thead><tr><th>게시판</th><th>제목</th><th>작성자</th><th className="num-c">조회·좋아요·댓글</th><th>상태</th><th className="act-c">관리</th></tr></thead>
                        <tbody>
                          {data.content.map((p) => (
                            <tr key={p.postId}>
                              <td><Badge text={BOARD_LABEL[p.boardType]} tone="gray" /></td>
                              <td className="ellipsis-c"><a className="strong-link" href={`#/community/${p.postId}`}>{p.title}</a><br /><small className="sub">{shortDateTime(p.createdAt)}</small></td>
                              <td>{p.memberNickname}</td>
                              <td className="num-c">{p.viewCount} · {p.likeCount} · {p.commentCount}</td>
                              <td><Badge text={p.isVisible ? '공개' : '숨김'} tone={p.isVisible ? 'blue' : 'red'} /></td>
                              <td className="act-c">
                                <span className="row">
                                  <button type="button" className="btn small" onClick={() => setHistoryOf(p)}>이력</button>
                                  <button type="button" className={`btn small ${p.isVisible ? 'danger-ghost' : 'primary'}`} onClick={() => { setErr(''); setReason(''); setTarget(p); }}>{p.isVisible ? '숨기기' : '다시 공개'}</button>
                                </span>
                              </td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                  <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => go({ page: p })} />
                </>
              )}
            </LoadBox>
          </div>
        </section>
      </div>

      {target && (
        <Modal title={target.isVisible ? '게시글을 숨길까요?' : '게시글을 다시 공개할까요?'} onClose={() => !busy && setTarget(null)}
          actions={<>
            <button type="button" className="btn" disabled={busy} onClick={() => setTarget(null)}>돌아가기</button>
            <button type="button" className={`btn ${target.isVisible ? 'danger' : 'primary'}`} disabled={busy} onClick={toggle}>{busy ? <Spinner light /> : target.isVisible ? '숨기기' : '다시 공개'}</button>
          </>}>
          <p><Lines text={`「${target.title}」\n${target.isVisible ? '숨기면 목록과 상세에서 다른 회원에게 보이지 않아요.' : '다시 공개하면 모든 회원이 볼 수 있어요.'}`} /></p>
          <Field label="사유" hint={`선택 · ${reason.trim().length} / 200`} htmlFor="vis-reason">
            <textarea id="vis-reason" className="input area compact" maxLength={200} value={reason} onChange={(e) => setReason(e.target.value)} placeholder="예: 광고성 글이라 숨겨요" />
          </Field>
          {err && <Banner><Lines text={err} /></Banner>}
        </Modal>
      )}
      {historyOf && <VisibilityHistoryModal post={historyOf} onClose={() => setHistoryOf(null)} />}
    </main>
  );
}

function VisibilityHistoryModal({ post, onClose }: { post: PostSummary; onClose: () => void }) {
  const h = useLoad(() => adminGetPostVisibilityHistory(post.postId), [post.postId]);
  return (
    <Modal title="공개 상태 변경 이력" onClose={onClose} actions={<button type="button" className="btn" onClick={onClose}>닫기</button>}>
      <p className="sub">{post.title}</p>
      {h.state.kind === 'loading' && <LoadingView />}
      {h.state.kind === 'error' && <Banner><Lines text={`이력을 불러오지 못했어요.\n${friendly(h.state.error)}`} /></Banner>}
      {h.state.kind === 'ok' && (h.state.data.histories.length === 0 ? <p className="sub">아직 바뀐 적이 없어요.</p> : (
        <ol className="vis-history">
          {h.state.data.histories.map((x: VisibilityHistory) => (
            <li key={x.historyId}>
              <Badge text={x.isVisible ? '공개' : '숨김'} tone={x.isVisible ? 'blue' : 'red'} />
              <span className="col">
                <span>{x.reason || '사유 없음'}</span>
                <small className="sub">{x.adminNickname} · {shortDateTime(x.createdAt)}</small>
              </span>
            </li>
          ))}
        </ol>
      ))}
    </Modal>
  );
}

// ───────── 요청서 현황 ─────────
const REQ_STATUS: { key: RequestStatus | ''; label: string }[] = [{ key: '', label: '전체' }, { key: 'OPEN', label: '접수 중' }, { key: 'MATCHED', label: '매칭' }, { key: 'CLOSED', label: '취소' }];

export function AdminRequestsPage({ params }: { params: URLSearchParams }) {
  const page = pageOf(params);
  const order = orderOf(params);
  const status = REQ_STATUS.some((s) => s.key && s.key === params.get('status')) ? (params.get('status') as RequestStatus) : undefined;
  const sortBy = ['createdAt', 'id', 'requestStatus'].includes(params.get('sort') ?? '') ? params.get('sort')! : 'createdAt';
  const list = useLoad(() => adminGetRequests(page, sortBy, order, status), [page, sortBy, order, status]);
  const go = (patch: Record<string, string | number | undefined>) => {
    const next = { page: 1, status, sort: sortBy, order, ...patch };
    navigate('/admin/requests', Object.fromEntries(Object.entries(next).filter(([, v]) => v !== undefined && String(v) !== '')) as Record<string, string | number>);
  };
  return (
    <main className="page admin">
      <div className="admin-layout">
        <AdminNav current="requests" />
        <section className="col gap admin-panel">
          <Head title="요청서 현황" sub={`고객이 보낸 요청서를 상태별로 봐요${list.state.kind === 'ok' ? ` · 총 ${list.state.data.totalElements}건` : ''}`} />
          <div className="col gap admin-results">
            <div className="row between wrap">
              <div className="tabs">{REQ_STATUS.map((s) => <button type="button" key={s.label} className={`tab${(status ?? '') === s.key ? ' on' : ''}`} onClick={() => go({ status: s.key || undefined })}>{s.label}</button>)}</div>
              <div className="row wrap admin-sort-controls">
                <span className="sub">정렬</span>
                <RoleSelect label="정렬 기준" value={sortBy} options={[{ value: 'createdAt', label: '작성일' }, { value: 'id', label: '번호' }, { value: 'requestStatus', label: '상태' }]} onChange={(value) => go({ sort: value })} />
                <RoleSelect label="정렬 방향" value={order} options={ORDER_OPTIONS} onChange={(value) => go({ order: value })} />
              </div>
            </div>
            <LoadBox load={list.state} title="요청서 목록을 불러오지 못했어요" onRetry={() => list.reload()}>
              {(data) => data.content.length === 0 ? <Empty title="조건에 맞는 요청서가 없어요" /> : (
                <>
                  <div className="card table-card">
                    <div className="admin-table-scroll" tabIndex={0} role="region" aria-label="관리 목록">
                      <table className="tbl stack-m admin-req-tbl">
                        <thead><tr><th>번호</th><th>서비스</th><th>지역</th><th>고객</th><th>상태</th><th className="num-c">작성</th></tr></thead>
                        <tbody>
                          {data.content.map((r) => (
                            <tr key={r.requestId}>
                              <td>{r.requestId}</td>
                              <td><b>{r.secondCategoryName}</b></td>
                              <td>{r.secondRegionName}</td>
                              <td>{r.customerNickname}</td>
                              <td><Badge {...requestBadge(r.requestStatus, r.createdAt)} /></td>
                              <td className="num-c">{shortDateTime(r.createdAt)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                  <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => go({ page: p })} />
                </>
              )}
            </LoadBox>
          </div>
        </section>
      </div>
    </main>
  );
}

// ───────── 견적 현황 ─────────
const QT_STATUS: { key: QuoteStatus | ''; label: string }[] = [{ key: '', label: '전체' }, { key: 'SENT', label: '매칭 대기' }, { key: 'REQUESTED', label: '매칭 완료' }];

export function AdminQuotesPage({ params }: { params: URLSearchParams }) {
  const page = pageOf(params);
  const order = orderOf(params);
  const status = QT_STATUS.some((s) => s.key && s.key === params.get('status')) ? (params.get('status') as QuoteStatus) : undefined;
  const sortBy = ['sentAt', 'id', 'price', 'quoteStatus'].includes(params.get('sort') ?? '') ? params.get('sort')! : 'sentAt';
  const list = useLoad(() => adminGetQuotes(page, sortBy, order, status), [page, sortBy, order, status]);
  const go = (patch: Record<string, string | number | undefined>) => {
    const next = { page: 1, status, sort: sortBy, order, ...patch };
    navigate('/admin/quotes', Object.fromEntries(Object.entries(next).filter(([, v]) => v !== undefined && String(v) !== '')) as Record<string, string | number>);
  };
  return (
    <main className="page admin">
      <div className="admin-layout">
        <AdminNav current="quotes" />
        <section className="col gap admin-panel">
          <Head title="견적 현황" sub={`고수가 보낸 견적을 상태별로 봐요${list.state.kind === 'ok' ? ` · 총 ${list.state.data.totalElements}건` : ''}`} />
          <div className="col gap admin-results">
            <div className="row between wrap">
              <div className="tabs">{QT_STATUS.map((s) => <button type="button" key={s.label} className={`tab${(status ?? '') === s.key ? ' on' : ''}`} onClick={() => go({ status: s.key || undefined })}>{s.label}</button>)}</div>
              <div className="row wrap admin-sort-controls">
                <span className="sub">정렬</span>
                <RoleSelect label="정렬 기준" value={sortBy} options={[{ value: 'sentAt', label: '발송일' }, { value: 'price', label: '금액' }, { value: 'id', label: '번호' }, { value: 'quoteStatus', label: '상태' }]} onChange={(value) => go({ sort: value })} />
                <RoleSelect label="정렬 방향" value={order} options={ORDER_OPTIONS} onChange={(value) => go({ order: value })} />
              </div>
            </div>
            <LoadBox load={list.state} title="견적 목록을 불러오지 못했어요" onRetry={() => list.reload()}>
              {(data) => data.content.length === 0 ? <Empty title="조건에 맞는 견적이 없어요" /> : (
                <>
                  <div className="card table-card">
                    <div className="admin-table-scroll" tabIndex={0} role="region" aria-label="관리 목록">
                      <table className="tbl stack-m admin-quote-tbl">
                        <thead><tr><th>번호</th><th>요청서</th><th>고수 → 고객</th><th className="num-c">금액</th><th>상태</th><th className="num-c">발송</th></tr></thead>
                        <tbody>
                          {data.content.map((x) => (
                            <tr key={x.quoteId}>
                              <td>{x.quoteId}</td>
                              <td><b>{x.secondCategoryName}</b><br /><small className="sub">{x.secondRegionName} · 요청서 {x.serviceRequestId}</small></td>
                              <td><a className="strong-link" href={`#/experts/${x.expertProfileId}`}>{x.expertNickname}</a> → {x.customerNickname}</td>
                              <td className="num-c"><b>{won(x.price)}</b></td>
                              <td><Badge {...quoteBadge(x.quoteStatus)} /></td>
                              <td className="num-c">{shortDateTime(x.sentAt)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                  <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => go({ page: p })} />
                </>
              )}
            </LoadBox>
          </div>
        </section>
      </div>
    </main>
  );
}
