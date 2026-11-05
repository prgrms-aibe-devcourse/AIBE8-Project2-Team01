// 홈의 '최근 2주 사이 새로 합류한 고수'와 '이번 주 찜이 빠르게 늘고 있는 고수'
// 공개 고수 목록 응답(GET /api/public/expert-profiles)만 쓰고 프론트에서 거르고 정렬한다.
// 해당하는 고수가 없으면 섹션을 그리지 않는다.
// 새로 합류한 고수는 응답의 createdAt만 쓴다. 등록일이 없거나 읽을 수 없는 고수는 빼고 예시 날짜를 만들지 않는다.
import { useMemo } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import type { PublicExpertSummary } from '../api/types';
import { useFirstRegions } from '../hooks/useData';
import type { HomeBookmarks } from '../hooks/useHomeBookmarks';
import type { Load } from '../hooks/useLoad';
import { regionLabels } from '../lib/format';
import { BookmarkHeart, PublicExpertCard, regionSummary, uniqueById } from './PublicExperts';
import { Avatar } from './ui';
import { useRail } from './useRail.js';

const DAY = 24 * 60 * 60 * 1000;
const NEW_DAYS = 14;
const RISING_SHOW = 6; // 두 줄 격자라 짝수로 맞춘다

const joinedLabel = (days: number) => (days <= 0 ? '오늘 합류' : `${days}일 전 합류`);

// 서버의 LocalDateTime(예: 2026-10-06T22:53:02.36354)을 시각(ms)으로 바꾼다. 읽을 수 없으면 NaN
// 소수점 아래 초가 3자리를 넘으면 브라우저마다 해석이 달라서 3자리로 자른다
function parseCreatedAt(value: string | null | undefined) {
  if (typeof value !== 'string' || value.trim() === '') return NaN;
  return new Date(value.trim().replace(/(\.\d{3})\d+/, '$1')).getTime();
}

export function NewPublicExperts({ list, bookmarks }: { list: Load<PublicExpertSummary[]>; bookmarks?: HomeBookmarks }) {
  const firstRegions = useFirstRegions();
  const { ref, edge, update, step } = useRail();
  const items = useMemo(() => {
    if (list.kind !== 'ok') return [];
    const now = Date.now();
    return uniqueById(list.data)
      .map((e) => ({ e, t: parseCreatedAt(e.createdAt) }))
      .filter(({ t }) => Number.isFinite(t) && now - t <= NEW_DAYS * DAY)
      .sort((a, b) => b.t - a.t)
      .map(({ e, t }) => ({ e, days: Math.max(0, Math.floor((now - t) / DAY)) }));
  }, [list]);

  if (items.length === 0) return null;

  return (
    <section className="section" aria-labelledby="new-experts-title">
      <div className="container">
        <div className="section-head">
          <div className="title-row">
            <h2 id="new-experts-title">최근 2주 사이 새로 합류한 고수</h2>
          </div>
        </div>
        <div className="rail-viewport">
          <button type="button" className="shelf-arrow prev" aria-label="이전 고수 보기" disabled={edge.start} onClick={() => step(-1)}>
            <ChevronLeft size={22} strokeWidth={2} aria-hidden="true" />
          </button>
          <ul className="rail" ref={ref} onScroll={update}>
            {items.map(({ e, days }) => (
              <li key={e.id}>
                <PublicExpertCard e={e} bookmarks={bookmarks} badge={joinedLabel(days)} regionText={regionSummary(regionLabels(e, firstRegions))} />
              </li>
            ))}
          </ul>
          <button type="button" className="shelf-arrow next" aria-label="다음 고수 보기" disabled={edge.end} onClick={() => step(1)}>
            <ChevronRight size={22} strokeWidth={2} aria-hidden="true" />
          </button>
        </div>
      </div>
    </section>
  );
}

export function RisingPublicExperts({ list, bookmarks }: { list: Load<PublicExpertSummary[]>; bookmarks?: HomeBookmarks }) {
  const firstRegions = useFirstRegions();
  const items = useMemo(() => {
    if (list.kind !== 'ok') return [];
    return uniqueById(list.data)
      .filter((e) => (e.weeklyBookmarkCount ?? 0) >= 1)
      // 증가량이 같으면 누적 찜이 많은 고수를 앞에 둔다
      .sort((a, b) => (b.weeklyBookmarkCount ?? 0) - (a.weeklyBookmarkCount ?? 0) || (b.bookmarkCount ?? 0) - (a.bookmarkCount ?? 0))
      .slice(0, RISING_SHOW);
  }, [list]);

  if (items.length === 0) return null;

  return (
    <section className="section" aria-labelledby="rising-experts-title">
      <div className="container">
        <div className="section-head">
          <div className="title-row">
            <h2 id="rising-experts-title">이번 주 찜이 빠르게 늘고 있는 고수</h2>
          </div>
        </div>
        <ol className="rank">
          {items.map((e, i) => (
            <li key={e.id} className="rank-row">
              <span className="rank-no num">
                {i + 1}
                <span className="sr-only">위</span>
              </span>
              <Avatar url={e.imageUrl} size={52} />
              <div className="rank-body">
                <p className="rank-name">
                  <a href={`#/experts/${e.id}`}>{e.nickname}</a>
                </p>
                <p className="rank-meta">
                  <span>{e.primaryCategory?.name ?? '대표 서비스 미등록'}</span>
                  <span aria-hidden="true" className="sep" />
                  <span>{regionSummary(regionLabels(e, firstRegions))}</span>
                </p>
              </div>
              <p className="rank-stats">
                <span className="rank-gain num">
                  <span className="sr-only">이번 주 찜 </span>
                  <span aria-hidden="true">이번 주 </span>+{e.weeklyBookmarkCount}
                </span>
                {e.bookmarkCount != null && <span className="rank-total num">누적 찜 {e.bookmarkCount}</span>}
              </p>
              <BookmarkHeart e={e} bookmarks={bookmarks} inline />
            </li>
          ))}
        </ol>
      </div>
    </section>
  );
}
