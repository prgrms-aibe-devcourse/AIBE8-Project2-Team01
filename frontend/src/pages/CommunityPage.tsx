// 커뮤니티 목록 (W·15) · 공개 조회 · 질문·팁·모집·홍보 게시판 · 내 글
// 목록은 최신순 20개씩 페이지 이동 (정렬 옵션 API 없음). 팁 게시판은 이미지 카드로 보여 준다
import { useState, type FormEvent, type MouseEvent } from 'react';
import { getMyPosts, getPosts, type BoardType, type PostSummary } from '../api/mvp';
import { useSession } from '../auth/session';
import * as api from '../api/endpoints';
import { Empty, LoadBox, Pager } from '../components/common';
import { navigate } from '../hooks/useHashRoute';
import { useLoginPrompt } from '../components/loginPrompt';
import { useLoad } from '../hooks/useLoad';
import { BOARDS, BOARD_LABEL, smartTime } from '../lib/labels';
import CategoryIcon from '../components/CategoryIcon';

const COMMUNITY_BOARDS: BoardType[] = ['TIP', 'QUESTION', 'RECRUIT', 'PROMOTION'];

const count = (n: number) => (n > 999 ? '999+' : n.toLocaleString('ko-KR'));

function PostRow({ p, showBoard }: { p: PostSummary; showBoard?: boolean }) {
  return (
    <a className="post-row" href={`#/community/${p.postId}`}>
      <div className="col grow">
        <span className="row">{showBoard && <span className="badge gray">{BOARD_LABEL[p.boardType]}</span>}{!p.isVisible && <span className="badge red">숨김</span>}<b className="ellipsis">{p.title}</b></span>
        <span className="post-meta">
          <span>{p.memberNickname}</span>
          {p.category && <span>{p.category.secondCategoryName}</span>}
          {p.region && <span>{p.region.firstRegionName} {p.region.secondRegionName}</span>}
          <span>조회 {count(p.viewCount)}</span><span>좋아요 {count(p.likeCount)}</span><span>댓글 {count(p.commentCount)}</span>
        </span>
      </div>
      <small className="sub post-time">{smartTime(p.createdAt)}</small>
      {p.thumbnailUrl && <img className="post-thumb" src={p.thumbnailUrl} alt="" />}
    </a>
  );
}

function TipCard({ p }: { p: PostSummary }) {
  return (
    <a className="tip-card" href={`#/community/${p.postId}`}>
      {p.thumbnailUrl ? <img src={p.thumbnailUrl} alt="" /> : (
        <span className="tip-noimg">
          <CategoryIcon secondId={p.category?.secondCategoryId} firstName={p.category?.firstCategoryName} size={96} />
        </span>
      )}
      <b className="ellipsis">{p.title}</b>
      <small className="sub">{p.memberNickname} · 좋아요 {count(p.likeCount)}</small>
    </a>
  );
}

function BoardTabs({ board, mine }: { board?: BoardType; mine?: boolean }) {
  const { status } = useSession();
  const askLogin = useLoginPrompt();
  return (
    <div className="tabs">
      {COMMUNITY_BOARDS.map((b) => <a key={b} className={`tab${b === board ? ' on' : ''}`} href={`#/community?board=${b}`}>{BOARD_LABEL[b]}</a>)}
      <a className={`tab${mine ? ' on' : ''}`} href="#/community/mine" onClick={(e) => { if (status !== 'member') { e.preventDefault(); askLogin({ message: '내가 쓴 글은 로그인한 뒤에 볼 수 있어요.', redirect: '/community/mine' }); } }}>내 글</a>
    </div>
  );
}

export function CommunityPage({ params }: { params: URLSearchParams }) {
  const board = (BOARDS.includes(params.get('board') as BoardType) ? params.get('board') : 'TIP') as BoardType;
  const page = Math.max(1, Number(params.get('page') ?? 1) || 1);
  const keyword = params.get('q') ?? '';
  const [q, setQ] = useState(keyword);
  // 분야·지역 필터 (백엔드 GET /api/public/posts의 firstCategoryId·firstRegionId)
  const fcat = Number(params.get('fcat')) || undefined;
  const freg = Number(params.get('freg')) || undefined;
  const cats = useLoad(() => api.getFirstCategories(), []);
  const regs = useLoad(() => api.getFirstRegions(), []);
  const list = useLoad(() => getPosts(board, page, keyword || undefined, { firstCategoryId: fcat, firstRegionId: freg }), [board, page, keyword, fcat, freg]);
  const { status, me } = useSession();
  const canWrite = board === 'TIP' || board === 'PROMOTION' ? me?.role === 'EXPERT' : status !== 'member' || me?.role !== 'ADMIN';
  const go = (p: number, kw = keyword, fc = fcat, fr = freg) => navigate('/community', { board, page: p, ...(kw ? { q: kw } : {}), ...(fc ? { fcat: fc } : {}), ...(fr ? { freg: fr } : {}) });
  const search = (e: FormEvent) => { e.preventDefault(); const kw = q.trim(); if (kw.length > 50) return; go(1, kw); };
  const askLogin = useLoginPrompt();
  const writeHref = `#/community/write?board=${board}`;
  const onWrite = (e: MouseEvent) => {
    if (status === 'member') return;
    e.preventDefault();
    askLogin({ message: '글은 로그인한 뒤에 쓸 수 있어요.', redirect: `/community/write?board=${board}` });
  };

  return (
    <main className="page">
      <div className="row between"><h1 className="page-title">커뮤니티</h1>{canWrite && <a className="btn primary small fab-m" href={writeHref} onClick={onWrite}>+ 글쓰기</a>}</div>
      <BoardTabs board={board} />
      <form className="row search" onSubmit={search} role="search">
        <input className="input" placeholder="제목·본문 검색 (최대 50자)" value={q} maxLength={50} onChange={(e) => setQ(e.target.value)} aria-label="검색어" />
        <button className="btn" type="submit">검색</button>
        {keyword && <button className="btn" type="button" onClick={() => { setQ(''); go(1, ''); }}>초기화</button>}
        <select className="input sm" aria-label="분야" value={fcat ?? ''} onChange={(e) => go(1, keyword, Number(e.target.value) || undefined, freg)}>
          <option value="">분야 전체</option>
          {cats.state.kind === 'ok' && cats.state.data.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <select className="input sm" aria-label="지역" value={freg ?? ''} onChange={(e) => go(1, keyword, fcat, Number(e.target.value) || undefined)}>
          <option value="">지역 전체</option>
          {regs.state.kind === 'ok' && regs.state.data.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
        </select>
      </form>
      <LoadBox load={list.state} title="게시글을 불러오지 못했어요" onRetry={() => list.reload()}>
        {(data) => data.content.length === 0 ? (
          <Empty title={keyword ? '검색 결과가 없어요' : `${BOARD_LABEL[board]} 게시판에 아직 글이 없어요`} desc={keyword ? '다른 검색어로 찾아보세요.' : canWrite ? '첫 글을 남겨 보세요.' : undefined} />
        ) : (
          <>
            {board === 'TIP'
              ? <div className="tip-grid">{data.content.map((p) => <TipCard key={p.postId} p={p} />)}</div>
              : <div className="card post-list">{data.content.map((p) => <PostRow key={p.postId} p={p} />)}</div>}
            <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => go(p)} />
            <small className="sub center">20개씩 · {data.pageNumber} / {Math.max(1, data.totalPages)} 페이지</small>
          </>
        )}
      </LoadBox>
      {(board === 'TIP' || board === 'PROMOTION') && <small className="sub">팁·홍보 글은 고수만 쓸 수 있어요.</small>}
    </main>
  );
}

export function MyPostsPage({ params }: { params: URLSearchParams }) {
  const page = Math.max(1, Number(params.get('page') ?? 1) || 1);
  const list = useLoad(() => getMyPosts(page), [page]);
  return (
    <main className="page">
      <div className="row between"><h1 className="page-title">커뮤니티</h1><a className="btn primary small" href="#/community/write">+ 글쓰기</a></div>
      <BoardTabs mine />
      <LoadBox load={list.state} title="내 글을 불러오지 못했어요" onRetry={() => list.reload()}>
        {(data) => data.content.length === 0 ? <Empty title="아직 쓴 글이 없어요" desc="궁금한 점이나 모집 글을 남겨 보세요." action={<a className="btn primary" href="#/community/write">글쓰기</a>} /> : (
          <>
            <div className="card post-list">{data.content.map((p) => <PostRow key={p.postId} p={p} showBoard />)}</div>
            <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => navigate('/community/mine', { page: p })} />
          </>
        )}
      </LoadBox>
      <small className="sub">내 댓글 목록은 아직 제공하지 않아요.</small>
    </main>
  );
}
