// 본인 고수 프로필 (W·P14 / M·P14) · 로그인 없이 보는 공개 프로필 (W·P07 / M·P07)
import { useCallback, useEffect, useState } from 'react';
import { ApiError, errorText } from '../api/client';
import * as api from '../api/endpoints';
import type { ExpertProfile } from '../api/types';
import { useSession } from '../auth/session';
import { BookmarkButton } from '../components/BookmarkButton';
import { ProfileView } from '../components/ProfileView';
import { ErrorView, LoadingView, Spinner } from '../components/ui';
import { useMyExpertProfile } from '../hooks/useData';
import { useRoleSwitch } from '../hooks/useRoleSwitch';
import { SAMPLE_EXPERTS } from '../data/sampleData.js';
import { expertImage, getSub } from '../data/categoryMeta.js';

export function MyExpertProfilePage() {
  const { me } = useSession();
  const { state, reload } = useMyExpertProfile(true);
  const { busy, switchTo } = useRoleSwitch();
  if (!me) return null;
  const head = <><a className="back" href="#/mypage">‹ 마이페이지</a><h1 className="page-title">내 고수 프로필</h1></>;
  if (state.kind === 'loading') return <main className="page">{head}<LoadingView /></main>;
  if (state.kind === 'error') return <main className="page">{head}<ErrorView title="고수 프로필을 불러오지 못했어요" message={errorText(state.error)} onRetry={reload} /></main>;
  if (state.kind === 'none') return (
    <main className="page">{head}
      <div className="state card"><b>아직 고수 프로필이 없어요</b><span className="sub">고수 프로필을 만들면 고수로 활동할 수 있어요.</span><a className="btn primary" href="#/expert/intro">고수로 전환</a></div>
    </main>
  );
  const p = state.profile;
  const isUser = me.role === 'USER';
  return (
    <main className="page">{head}
      {isUser && (
        <div className="card notice">
          <b>지금은 고객 상태예요</b>
          <p className="sub">고수 프로필은 계속 공개돼요.<br />받은 요청 확인과 견적 발송은 고수로 전환하면 이용할 수 있어요.</p>
          <button className="btn primary self-start" disabled={busy} onClick={() => switchTo('EXPERT')}>{busy ? <><Spinner light />전환하는 중</> : '고수로 전환'}</button>
        </div>
      )}
      <ProfileView p={p} side={<div className="col gap-s full">
        <a className="btn primary block" href="#/expert/me/edit">프로필 수정</a>
        {/* 공개 프로필은 응답의 id(고수 프로필 ID)로 연다 */}
        <a className="btn block" href={`#/experts/${p.id}`}>공개 화면 보기</a>
      </div>} />
    </main>
  );
}

export function PublicExpertProfilePage({ id }: { id: number }) {
  const [state, setState] = useState<{ kind: 'loading' } | { kind: 'ok'; p: ExpertProfile } | { kind: 'none' } | { kind: 'error'; e: unknown }>({ kind: 'loading' });
  const load = useCallback(async () => {
    setState({ kind: 'loading' });
    try { setState({ kind: 'ok', p: await api.getPublicExpertProfile(id) }); }
    catch (e) { setState(e instanceof ApiError && e.status === 404 ? { kind: 'none' } : { kind: 'error', e }); }
  }, [id]);
  useEffect(() => { load(); }, [load]);
  if (!Number.isFinite(id) || id <= 0 || state.kind === 'none') return (
    <main className="page"><div className="state card"><b>고수 프로필을 찾을 수 없어요</b><span className="sub">주소를 다시 확인해 주세요.</span></div></main>
  );
  if (state.kind === 'loading') return <main className="page"><LoadingView /></main>;
  if (state.kind === 'error') return <main className="page"><ErrorView title="고수 프로필을 불러오지 못했어요" message={errorText(state.e)} onRetry={load} /></main>;
  return <main className="page"><ProfileView p={state.p} side={<BookmarkButton expertProfileId={state.p.id} ownerMemberNickname={state.p.nickname} />} /></main>;
}

// 메인 화면의 예시 고수 (AI로 만든 가상 인물 사진 · 실제 등록 고수가 아님)
// 실제 고수 프로필 번호와 섞이지 않도록 #/experts/sample/{id} 주소를 따로 쓴다
export function SampleExpertProfilePage({ id }: { id: number }) {
  const e = SAMPLE_EXPERTS.find((x: { id: number }) => x.id === id);
  if (!e) return <main className="page"><div className="state card"><b>예시 고수를 찾을 수 없어요</b><a className="btn" href="#/">메인으로</a></div></main>;
  const primary = getSub(e.primaryCategoryId);
  const p: ExpertProfile = {
    id: e.id,
    nickname: e.nickname,
    shortIntro: e.shortIntro,
    description: `${e.shortIntro}.\n이 고수는 메인 화면을 보여 주기 위해 만든 예시예요. 실제 고수 프로필은 고수 등록을 마친 회원의 프로필에서 확인할 수 있어요.`,
    careerYears: e.careerYears,
    careerDescription: null,
    imageId: null,
    imageUrl: expertImage(e.id),
    services: primary ? [{ id: primary.id, name: primary.name, firstCategoryId: primary.parentId }] : [],
    primaryCategory: primary ? { id: primary.id, name: primary.name, firstCategoryId: primary.parentId } : null,
    nationwide: e.nationwide,
    wholeRegions: e.wholeRegions.map((name: string, i: number) => ({ id: -1 - i, code: '', name })),
    regions: e.regions.map((name: string, i: number) => ({ id: -100 - i, code: '', name, firstRegionId: -1 }))
  };
  return (
    <main className="page">
      <a className="back" href="#/">‹ 메인으로</a>
      <ProfileView
        p={p}
        note={<span className="sample-tag">예시 고수 · AI로 만든 가상 인물 사진</span>}
        side={primary ? <a className="btn primary block" href={`#/?service=${primary.id}`}>{primary.name} 요청서 작성</a> : undefined}
      />
    </main>
  );
}
