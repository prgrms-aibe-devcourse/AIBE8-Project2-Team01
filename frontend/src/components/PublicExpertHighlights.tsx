// 홈의 '최근 2주 사이 새로 합류한 고수'와 '이번 주 찜이 빠르게 늘고 있는 고수'
// 공개 고수 목록 응답(GET /api/public/expert-profiles)만 쓰고 프론트에서 거르고 정렬한다.
// 해당하는 고수가 없으면 섹션을 그리지 않는다.
// [임시 미리보기] 백엔드가 createdAt · bookmarkCount · weeklyBookmarkCount를 아직 내려 주지 않아서
//   그 값이 응답에 없을 때만 실제 고수에 미리보기용 값을 채워 화면 모양을 확인한다.
//   백엔드 필드가 들어오면 실제 값을 그대로 쓰고 미리보기 값은 쓰지 않는다. 배포 전에는 PREVIEW_WHEN_MISSING을 false로 바꾸거나 이 블록을 지운다.
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

// 응답에 그 값이 실려 오는지 (필드 자체가 없으면 undefined)
const hasField = (list: PublicExpertSummary[], key: keyof PublicExpertSummary) => list.some((e) => e[key] !== undefined);

const joinedLabel = (days: number) => (days <= 0 ? '오늘 합류' : `${days}일 전 합류`);

const PREVIEW_WHEN_MISSING = true;
const PREVIEW_DAYS_AGO = [1, 2, 4, 6, 9, 12, 20, 30, 45, 60]; // 10명 중 6명이 14일 안
const PREVIEW_WEEKLY = [7, 0, 5, 3, 0, 9, 2, 0, 4, 1, 0, 6]; // 0은 이번 주 증가 없음

// 응답에 없는 값만 고수 id 순서로 정해진 미리보기 값으로 채운다 (새로 고쳐도 같은 결과)
function withPreview(all: PublicExpertSummary[]): PublicExpertSummary[] {
  if (!PREVIEW_WHEN_MISSING) return all;
  const needDate = !hasField(all, 'createdAt');
  const needBookmark = !hasField(all, 'weeklyBookmarkCount');
  if (!needDate && !needBookmark) return all;
  const order = new Map([...all].sort((a, b) => a.id - b.id).map((e, i) => [e.id, i]));
  const now = Date.now();
  return all.map((e) => {
    const i = order.get(e.id) ?? 0;
    const weekly = PREVIEW_WEEKLY[i % PREVIEW_WEEKLY.length];
    return {
      ...e,
      ...(needDate && { createdAt: new Date(now - PREVIEW_DAYS_AGO[i % PREVIEW_DAYS_AGO.length] * DAY - 2 * 60 * 60 * 1000).toISOString() }),
      ...(needBookmark && { weeklyBookmarkCount: weekly, bookmarkCount: weekly + 8 + ((e.id * 7) % 40) }),
    };
  });
}

export function NewPublicExperts({ list, bookmarks }: { list: Load<PublicExpertSummary[]>; bookmarks?: HomeBookmarks }) {
  const firstRegions = useFirstRegions();
  const { ref, edge, update, step } = useRail();
  const items = useMemo(() => {
    if (list.kind !== 'ok') return [];
    const all = withPreview(uniqueById(list.data));
    if (!hasField(all, 'createdAt')) return [];
    const now = Date.now();
    return all
      .map((e) => ({ e, t: e.createdAt ? new Date(e.createdAt).getTime() : NaN }))
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
    const all = withPreview(uniqueById(list.data));
    if (!hasField(all, 'weeklyBookmarkCount')) return [];
    return all
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
