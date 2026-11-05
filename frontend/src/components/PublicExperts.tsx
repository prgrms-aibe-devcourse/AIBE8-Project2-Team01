// 고객·비로그인·관리자 홈의 실제 고수 목록 (#196 GET /api/public/expert-profiles)
// 같은 고수는 한 번만 보여 준다. 카드를 누르면 기존 공개 프로필 상세(#/experts/{id})로 간다.
// 카드 사진: 대표 작업 사진(representativeWorkImageUrl) → 프로필 사진(imageUrl) → 기본 이미지 순서
// 지역 필터: 홈에서 고른 시·군·구(히어로 검색창과 같은 값)를 기준으로 목록 응답의 활동 지역을 프론트에서 거른다.
//   전국 활동 고수 + 그 시·도 전체에서 활동하는 고수 + 그 시·군·구에서 활동하는 고수를 모두 보여 주고
//   시·군·구 → 시·도 전체 → 전국 순서로 가까운 고수를 앞에 둔다.
import { useEffect, useMemo } from 'react';
import type { KeyboardEvent } from 'react';
import { ChevronLeft, ChevronRight, Heart } from 'lucide-react';
import { errorText } from '../api/client';
import type { PublicExpertSummary } from '../api/types';
import { useFirstRegions } from '../hooks/useData';
import type { HomeBookmarks } from '../hooks/useHomeBookmarks';
import type { Load } from '../hooks/useLoad';
import { careerLabel, regionLabels } from '../lib/format';
import { FIRST_REGIONS } from '../data/baseData.js';
import { regionLabel, shortSido } from '../lib/homeFormat.js';
import RegionPicker from './RegionPicker.jsx';
import { useRail } from './useRail.js';

// 홈 요청서 초안의 지역 값 (RegionPicker가 만드는 모양)
export type HomeRegion = { first: { id: number; name: string }; second: { id: number; name: string } };

// 0: 이 지역에서 활동하지 않음 · 1: 전국 · 2: 시·도 전체 · 3: 시·군·구
function matchLevel(e: PublicExpertSummary, region: HomeRegion) {
  if (e.regions.some((r) => r.id === region.second.id)) return 3;
  if (e.wholeRegions.some((w) => w.id === region.first.id)) return 2;
  return e.nationwide ? 1 : 0;
}

const CHIP_COUNT = 5;

// 지역 칩: 시·군·구를 직접 고른 고수가 많은 지역부터 몇 개를 보여 준다 (목록 응답의 활동 지역으로 센다)
function topRegions(all: PublicExpertSummary[]): HomeRegion[] {
  const count = new Map<number, number>();
  all.forEach((e) => e.regions.forEach((r) => count.set(r.id, (count.get(r.id) ?? 0) + 1)));
  const out: HomeRegion[] = [];
  [...count.entries()]
    .sort((a, b) => b[1] - a[1] || a[0] - b[0])
    .some(([id]) => {
      const first = FIRST_REGIONS.find((f: { subs: { id: number }[] }) => f.subs.some((s) => s.id === id));
      const second = first?.subs.find((s: { id: number }) => s.id === id);
      if (first && second) out.push({ first: { id: first.id, name: first.name }, second });
      return out.length >= CHIP_COUNT;
    });
  return out;
}

function matchLabel(level: number, region: HomeRegion) {
  if (level === 3) return region.second.name;
  if (level === 2) return `${shortSido(region.first.name)} 전체`;
  return '전국';
}

export function uniqueById(list: PublicExpertSummary[]) {
  const seen = new Set<number>();
  return list.filter((e) => (seen.has(e.id) ? false : (seen.add(e.id), true)));
}

export function regionSummary(labels: string[]) {
  if (labels.length === 0) return '지역 미등록';
  return labels.length === 1 ? labels[0] : `${labels[0]} 외 ${labels.length - 1}곳`;
}

function Photo({ e, badge }: { e: PublicExpertSummary; badge?: string }) {
  const src = e.representativeWorkImageUrl || e.imageUrl;
  return (
    <div className={`expert-photo${src ? '' : ' is-default'}`}>
      {src ? (
        <img src={src} alt="" loading="lazy" onError={(ev) => { ev.currentTarget.style.display = 'none'; ev.currentTarget.parentElement?.classList.add('is-default'); }} />
      ) : null}
      <svg className="expert-default" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8.2" r="4.2" /><path d="M3.6 21c.7-4.4 4.2-7 8.4-7s7.7 2.6 8.4 7z" /></svg>
      {(badge || e.representativeWorkImageUrl) && <span className="expert-badge">{badge ?? '작업 사진'}</span>}
    </div>
  );
}

// 홈의 고수 카드 한 장. 지역 문구와 사진 위 배지는 섹션마다 다르게 넘긴다
// 카드 오른쪽 아래 하트. 고객은 찜을 바꾸고 비로그인은 로그인으로 간다
export function BookmarkHeart({ e, bookmarks, inline = false }: { e: PublicExpertSummary; bookmarks?: HomeBookmarks; inline?: boolean }) {
  if (!bookmarks || !bookmarks.shows(e.nickname)) return null;
  const on = bookmarks.isOn(e.id);
  return (
    <button
      type="button"
      className={`bookmark${inline ? ' bookmark-inline' : ''}${on ? ' is-on' : ''}`}
      aria-pressed={on}
      aria-label={`${e.nickname} 찜하기`}
      disabled={!bookmarks.ready}
      onClick={() => bookmarks.toggle(e.id)}
    >
      <Heart size={20} strokeWidth={2.2} aria-hidden="true" />
    </button>
  );
}

export function PublicExpertCard({ e, regionText, badge, bookmarks }: { e: PublicExpertSummary; regionText: string; badge?: string; bookmarks?: HomeBookmarks }) {
  return (
    <article className="expert-card">
      <Photo e={e} badge={badge} />
      <div className="expert-body">
        <h3 className="expert-name">
          <a href={`#/experts/${e.id}`} className="expert-link">{e.nickname}</a>
        </h3>
        <p className="expert-intro">{e.shortIntro}</p>
        <ul className="expert-tags" aria-label="서비스 정보">
          <li className="is-service">{e.primaryCategory?.name ?? '대표 서비스 미등록'}</li>
          {e.careerYears !== null && <li className="num">{careerLabel(e.careerYears)}</li>}
          <li>{regionText}</li>
        </ul>
        <BookmarkHeart e={e} bookmarks={bookmarks} />
      </div>
    </article>
  );
}

// 홈 공통 목록 상태는 HomePage가 한 번만 불러서 세 섹션에 나눠 준다
type Props = {
  list: Load<PublicExpertSummary[]>;
  onRetry: () => void;
  bookmarks?: HomeBookmarks;
  region?: HomeRegion | null;
  onRegionChange?: (region: HomeRegion | null) => void;
};

export function PublicExperts({ list, onRetry, bookmarks, region = null, onRegionChange }: Props) {
  const firstRegions = useFirstRegions();
  const { ref, edge, update, step } = useRail();
  const all = useMemo(() => (list.kind === 'ok' ? uniqueById(list.data) : []), [list]);
  const experts = useMemo(() => {
    if (!region) return all.map((e) => ({ e, level: 0 }));
    return all
      .map((e) => ({ e, level: matchLevel(e, region) }))
      .filter((x) => x.level > 0)
      .sort((a, b) => b.level - a.level); // 같은 단계 안에서는 응답 순서를 지킨다 (Array.sort는 안정 정렬)
  }, [all, region]);

  // 지역을 바꾸면 레일을 처음으로 되돌리고 화살표 상태를 다시 잰다
  const regionKey = region?.second.id ?? 0;
  useEffect(() => {
    const el = ref.current as HTMLElement | null;
    if (el) el.scrollLeft = 0;
    update();
  }, [regionKey, experts.length, ref, update]);

  const title = region ? `${region.second.name}에서 활동하는 고수` : '활동 중인 고수';

  // 칩 목록: 전체 + 많이 활동하는 지역. 히어로나 '다른 지역'에서 고른 지역이 목록에 없으면 앞에 끼워 넣는다
  const chips = useMemo(() => {
    const top = topRegions(all);
    if (region && !top.some((r) => r.second.id === region.second.id)) top.unshift(region);
    return [null, ...top] as (HomeRegion | null)[];
  }, [all, region]);
  const current = chips.findIndex((c) => (c?.second.id ?? 0) === regionKey);
  const pick = (c: HomeRegion | null) => onRegionChange?.(c);
  const onChipKey = (ev: KeyboardEvent<HTMLDivElement>) => {
    if (ev.key !== 'ArrowRight' && ev.key !== 'ArrowLeft') return;
    ev.preventDefault();
    const i = (current + (ev.key === 'ArrowRight' ? 1 : chips.length - 1)) % chips.length;
    pick(chips[i]);
    requestAnimationFrame(() => document.getElementById(`experts-chip-${chips[i]?.second.id ?? 0}`)?.focus());
  };

  return (
    <section id="experts" className="section" aria-labelledby="public-experts-title">
      <div className="container">
        <div className="section-head">
          <div className="title-row">
            <h2 id="public-experts-title">{title}</h2>
          </div>
        </div>

        {onRegionChange && (
          <div className="expert-filter">
            <div className="tabs expert-chips" role="tablist" aria-label="활동 지역" onKeyDown={onChipKey}>
              {chips.map((c) => {
                const id = c?.second.id ?? 0;
                const on = id === regionKey;
                return (
                  <button
                    key={id}
                    id={`experts-chip-${id}`}
                    type="button"
                    role="tab"
                    className="tab"
                    aria-selected={on}
                    aria-controls="experts-panel"
                    tabIndex={on ? 0 : -1}
                    onClick={() => pick(c)}
                  >
                    {c ? regionLabel(c) : '전체'}
                  </button>
                );
              })}
            </div>
            <RegionPicker buttonId="experts-region" label="다른 지역에서 찾기" buttonText="다른 지역" value={region} onChange={onRegionChange} />
          </div>
        )}

        {list.kind === 'loading' && <div className="home-state" role="status">고수 목록을 불러오는 중이에요.</div>}
        {list.kind === 'error' && (
          <div className="home-state">
            <p>고수 목록을 불러오지 못했어요. {errorText(list.error)}</p>
            <button type="button" className="btn btn-secondary btn-sm" onClick={onRetry}>다시 불러오기</button>
          </div>
        )}
        {list.kind === 'ok' && experts.length === 0 && (
          <div className="home-state">{region ? '이 지역에서 활동하는 고수가 아직 없어요.' : '활동 중인 고수가 아직 없어요.'}</div>
        )}
        {list.kind === 'ok' && experts.length > 0 && (
          <div className="rail-viewport" id="experts-panel" role="tabpanel" aria-labelledby="public-experts-title">
            <button type="button" className="shelf-arrow prev" aria-label="이전 고수 보기" disabled={edge.start} onClick={() => step(-1)}>
              <ChevronLeft size={22} strokeWidth={2} aria-hidden="true" />
            </button>
            <ul className="rail" ref={ref} onScroll={update}>
              {experts.map(({ e, level }) => (
                <li key={e.id}>
                  <PublicExpertCard e={e} bookmarks={bookmarks} regionText={region ? matchLabel(level, region) : regionSummary(regionLabels(e, firstRegions))} />
                </li>
              ))}
            </ul>
            <button type="button" className="shelf-arrow next" aria-label="다음 고수 보기" disabled={edge.end} onClick={() => step(1)}>
              <ChevronRight size={22} strokeWidth={2} aria-hidden="true" />
            </button>
          </div>
        )}
      </div>
    </section>
  );
}
