import { useState } from 'react';
import { ChevronRight, Heart, MessageSquare } from 'lucide-react';
import { getPosts } from '../api/mvp';
import { errorText } from '../api/client';
import { useLoad } from '../hooks/useLoad';
import { CATEGORIES, categoryIconSrc } from '../data/categoryMeta.js';

// 메인의 커뮤니티 미리보기. 공개 게시글 API(GET /api/public/posts)의 실제 글을 보여 주고 글 상세로 이동한다.
// 고수 노하우: 첫 페이지(최신 20개)에서 사진이 있는 글을 앞에 두고 4개를 고른다. 사진이 없으면 분야 아이콘을 그린다.
// 고수에게 묻다: 대분류마다 최신 질문 1개씩 최신순으로 4개를 고른다.
const BOARDS = [
  { key: 'TIP', label: '고수 노하우' },
  { key: 'QUESTION', label: '고수에게 묻다' },
];
const TINTS = ['var(--col-repair)', 'var(--col-moving)', 'var(--col-learn)', 'var(--col-event)'];
const SHOW = 4;

function pick(list) {
  const withPhoto = list.filter((p) => p.thumbnailUrl);
  const rest = list.filter((p) => !p.thumbnailUrl);
  return [...withPhoto, ...rest].slice(0, SHOW);
}

// '고수에게 묻다'는 여러 서비스가 골고루 보이도록 대분류마다 가장 최근 질문 1개만 고른다 (질문이 없는 분야는 건너뜀)
// 기존 목록 API만 쓴다: 최신순 페이지를 차례로 읽으며 처음 나온 분야의 글을 모으고 4개가 차면 멈춘다(보통 1~2번).
// 5페이지까지 읽어도 모자라면 남은 분야는 분야 필터(firstCategoryId)로 1페이지씩 확인한다.
const MAX_PAGES = 5;
const firstIdOf = (p) => p.category?.firstCategoryId ?? null;

async function latestQuestionPerCategory() {
  const picked = new Map();
  for (let page = 1; page <= MAX_PAGES && picked.size < SHOW; page++) {
    const res = await getPosts('QUESTION', page);
    for (const p of res.content) {
      const id = firstIdOf(p);
      if (id != null && !picked.has(id)) picked.set(id, p);
    }
    if (res.lastPage || res.content.length === 0) break;
    if (page === MAX_PAGES && picked.size < SHOW) {
      const rest = CATEGORIES.filter((c) => !picked.has(c.id));
      const found = await Promise.all(rest.map((c) => getPosts('QUESTION', 1, undefined, { firstCategoryId: c.id }).then((r) => r.content[0] ?? null).catch(() => null)));
      found.forEach((p) => { if (p && firstIdOf(p) != null && !picked.has(firstIdOf(p))) picked.set(firstIdOf(p), p); });
    }
  }
  const content = [...picked.values()].sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()).slice(0, SHOW);
  return { content, picked: true };
}

const loadBoard = (board) => (board === 'QUESTION' ? latestQuestionPerCategory() : getPosts(board, 1));

function PostThumb({ p, i }) {
  if (p.thumbnailUrl) {
    return (
      <div className="post-thumb has-photo" aria-hidden="true">
        <img className="pt-photo" src={p.thumbnailUrl} alt="" loading="lazy" />
      </div>
    );
  }
  const icon = categoryIconSrc({ secondId: p.category?.secondCategoryId, firstName: p.category?.firstCategoryName });
  return (
    <div className="post-thumb" style={{ '--thumb': TINTS[i % TINTS.length] }} aria-hidden="true">
      <img className="pt-single" src={icon} alt="" width="256" height="256" loading="lazy" />
    </div>
  );
}

export default function Community() {
  const [board, setBoard] = useState('TIP');
  const list = useLoad(() => loadBoard(board), [board]);

  return (
    <section id="community" className="section" aria-labelledby="com-title">
      <div className="container">
        <div className="section-head">
          <div className="title-row">
            <h2 id="com-title">커뮤니티 놀러가기</h2>
          </div>
          <a className="more" href={`#/community?board=${board}`}>
            더보기
            <ChevronRight size={16} aria-hidden="true" />
          </a>
        </div>

        <div className="tabs tabs-quiet" role="tablist" aria-label="게시판">
          {BOARDS.map((b) => (
            <button
              key={b.key}
              id={`board-${b.key}`}
              type="button"
              role="tab"
              className="tab"
              aria-selected={board === b.key}
              aria-controls="board-panel"
              tabIndex={board === b.key ? 0 : -1}
              onClick={() => setBoard(b.key)}
              onKeyDown={(e) => {
                if (e.key === 'ArrowRight' || e.key === 'ArrowLeft') {
                  e.preventDefault();
                  const next = board === 'TIP' ? 'QUESTION' : 'TIP';
                  setBoard(next);
                  document.getElementById(`board-${next}`)?.focus();
                }
              }}
            >
              {b.label}
            </button>
          ))}
        </div>

        <div id="board-panel" role="tabpanel" aria-labelledby={`board-${board}`} aria-busy={list.state.kind === 'loading'}>
          {list.state.kind === 'loading' && (
            <ul className="post-grid" aria-hidden="true">
              {Array.from({ length: SHOW }, (_, i) => (
                <li key={i}>
                  <div className="post-card is-skeleton">
                    <div className="post-thumb" />
                    <span className="sk-line" />
                    <span className="sk-line short" />
                  </div>
                </li>
              ))}
            </ul>
          )}
          {list.state.kind === 'error' && (
            <div className="home-state">
              <p>게시글을 불러오지 못했어요. {errorText(list.state.error)}</p>
              <button type="button" className="btn btn-secondary btn-sm" onClick={() => list.reload()}>
                다시 불러오기
              </button>
            </div>
          )}
          {list.state.kind === 'ok' && list.state.data.content.length === 0 && (
            <div className="home-state">
              <p>아직 올라온 글이 없어요.</p>
              <a className="btn btn-secondary btn-sm" href={`#/community?board=${board}`}>
                커뮤니티로 가기
              </a>
            </div>
          )}
          {list.state.kind === 'ok' && list.state.data.content.length > 0 && (
            <ul className="post-grid">
              {(list.state.data.picked ? list.state.data.content : pick(list.state.data.content)).map((p, i) => (
                <li key={p.postId}>
                  <article className="post-card">
                    <PostThumb p={p} i={i} />
                    {p.category && <p className="post-cat">{p.category.secondCategoryName}</p>}
                    <h3 className="post-title">
                      <a href={`#/community/${p.postId}`}>{p.title}</a>
                    </h3>
                    <p className="post-meta">
                      <span className="post-author">{p.memberNickname}</span>
                      <span className="num">
                        <MessageSquare size={14} aria-hidden="true" />
                        <span className="sr-only">댓글</span>
                        {p.commentCount}
                      </span>
                      <span className="num">
                        <Heart size={14} aria-hidden="true" />
                        <span className="sr-only">좋아요</span>
                        {p.likeCount}
                      </span>
                    </p>
                  </article>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </section>
  );
}
