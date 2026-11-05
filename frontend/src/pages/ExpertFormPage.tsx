// 고수 프로필 등록·수정 (와이어프레임 W·P02 / W·P13 · 모바일 M·P02~P06 / M·P13)
// 등록: POST · 성공하면 서버가 고객 → 고수로 전환 (역할 전환 API를 먼저 부르지 않음)
// 수정: PATCH · 작업 사진(workImages)은 보내지 않아 기존 값이 유지된다
import { useCallback, useEffect, useMemo, useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import * as api from '../api/endpoints';
import type { ExpertProfile, ExpertProfileCreateRequest, ExpertProfileUpdateRequest, FirstCategory, FirstRegion, SecondCategory, SecondRegion } from '../api/types';
import { useSession } from '../auth/session';
import { useToast } from '../components/toast';
import { Banner, ErrorView, Field, Lines, LoadingView, Spinner } from '../components/ui';
import { loadFirstRegions, useMyExpertProfile } from '../hooks/useData';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { navigate } from '../hooks/useHashRoute';
import { CAREER_DESC_MAX, CAREER_YEARS_MAX, DESCRIPTION_MAX, DESCRIPTION_MIN, SHORT_INTRO_MAX } from '../lib/policy';
import { ProfilePhotoCard } from '../components/ProfilePhotoCard';
import { WorkImagesField, type WorkDraft } from '../components/WorkImagesField';

type ErrKey = 'shortIntro' | 'description' | 'careerYears' | 'careerDescription' | 'services' | 'primary' | 'regions';
type Errs = Partial<Record<ErrKey, string>>;

// 서버 필드 이름 → 화면 항목
const SERVER_FIELD: Record<string, ErrKey> = {
  shortIntro: 'shortIntro', description: 'description', careerYears: 'careerYears', careerDescription: 'careerDescription',
  serviceIds: 'services', primaryCategoryId: 'primary', wholeRegionIds: 'regions', regionIds: 'regions', nationwide: 'regions'
};

export function ExpertFormPage({ mode }: { mode: 'create' | 'edit' }) {
  const { me } = useSession();
  const mine = useMyExpertProfile(true);
  if (!me) return null;
  const head = (
    <>
      <a className="back" href={mode === 'edit' ? '#/expert/me' : '#/expert/intro'}>‹ {mode === 'edit' ? '내 고수 프로필' : '고수 전환 안내'}</a>
      <h1 className="page-title">{mode === 'edit' ? '고수 프로필 수정' : '고수 프로필 등록'}</h1>
    </>
  );
  if (mine.state.kind === 'loading') return <main className="page narrow">{head}<LoadingView /></main>;
  if (mine.state.kind === 'error') return <main className="page narrow">{head}<ErrorView title="고수 프로필 정보를 불러오지 못했어요" message={errorText(mine.state.error)} onRetry={mine.reload} /></main>;
  if (mode === 'create' && mine.state.kind === 'ok') return (
    <main className="page narrow">{head}<div className="state card"><b>이미 고수 프로필이 있어요</b><span className="sub">다시 등록하지 않고 마이페이지에서 바로 전환할 수 있어요.</span><a className="btn primary" href="#/mypage">마이페이지로</a></div></main>
  );
  if (mode === 'create' && me.role !== 'USER') return (
    <main className="page narrow">{head}<div className="state card"><b>고객 상태에서만 등록할 수 있어요</b><a className="btn" href="#/mypage">마이페이지로</a></div></main>
  );
  if (mode === 'edit' && mine.state.kind === 'none') return (
    <main className="page narrow">{head}<div className="state card"><b>아직 고수 프로필이 없어요</b><a className="btn primary" href="#/expert/intro">고수로 전환</a></div></main>
  );
  return <ExpertForm mode={mode} initial={mine.state.kind === 'ok' ? mine.state.profile : null} head={head} />;
}

function ExpertForm({ mode, initial, head }: { mode: 'create' | 'edit'; initial: ExpertProfile | null; head: JSX.Element }) {
  const { me, reloadMe } = useSession();
  const toast = useToast();
  const { busy, run } = useSingleFlight();

  // ── 입력값 ──
  const [shortIntro, setShortIntro] = useState(initial?.shortIntro ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');
  const [careerYears, setCareerYears] = useState(initial?.careerYears != null ? String(initial.careerYears) : '');
  const [careerDescription, setCareerDescription] = useState(initial?.careerDescription ?? '');
  const [services, setServices] = useState<SecondCategory[]>(initial ? [...initial.services] : []);
  const [primaryId, setPrimaryId] = useState<number | null>(initial?.primaryCategory?.id ?? null);
  const [nationwide, setNationwide] = useState(initial?.nationwide ?? false);
  const [wholes, setWholes] = useState<FirstRegion[]>(initial ? [...initial.wholeRegions] : []);
  const [regions, setRegions] = useState<SecondRegion[]>(initial ? [...initial.regions] : []);
  // 작업 사진: 고를 때 바로 올리고 등록·저장할 때 파일 번호를 보낸다
  const initialWorks: WorkDraft[] = (initial?.workImages ?? []).map((w) => ({ fileId: w.fileId, url: w.url, description: w.description ?? '' }));
  const [works, setWorks] = useState<WorkDraft[]>(initialWorks);
  const [worksBusy, setWorksBusy] = useState(false);
  const [errs, setErrs] = useState<Errs>({});
  const [fail, setFail] = useState('');

  // ── 기준 데이터 ──
  const [firstCats, setFirstCats] = useState<FirstCategory[] | null>(null);
  const [catId, setCatId] = useState<number | null>(null);
  const [secondCats, setSecondCats] = useState<Record<number, SecondCategory[]>>({});
  const [firstRegions, setFirstRegions] = useState<FirstRegion[] | null>(null);
  const [regId, setRegId] = useState<number | null>(null);
  const [secondRegs, setSecondRegs] = useState<Record<number, SecondRegion[]>>({});
  const [baseErr, setBaseErr] = useState<unknown>(null);

  const loadBase = useCallback(async () => {
    setBaseErr(null);
    try {
      const [fc, fr] = await Promise.all([api.getFirstCategories(), loadFirstRegions()]);
      setFirstCats(fc); setFirstRegions(fr);
      setCatId((v) => v ?? initial?.primaryCategory?.firstCategoryId ?? fc[0]?.id ?? null);
      setRegId((v) => v ?? fr[0]?.id ?? null);
    } catch (e) { setBaseErr(e); }
  }, [initial]);
  useEffect(() => { loadBase(); }, [loadBase]);

  const [subErr, setSubErr] = useState('');
  useEffect(() => {
    if (catId == null || secondCats[catId]) return;
    api.getSecondCategories(catId).then((l) => setSecondCats((m) => ({ ...m, [catId]: l }))).catch(() => setSubErr('세부 서비스를 불러오지 못했어요.\n다른 분류를 골랐다가 다시 골라 주세요.'));
  }, [catId, secondCats]);
  useEffect(() => {
    if (regId == null || secondRegs[regId]) return;
    api.getSecondRegions(regId).then((l) => setSecondRegs((m) => ({ ...m, [regId]: l }))).catch(() => setSubErr('시군구를 불러오지 못했어요.\n다른 시도를 골랐다가 다시 골라 주세요.'));
  }, [regId, secondRegs]);

  // ── 서비스 선택: 하나면 자동 대표, 대표를 빼면 다시 골라야 함 ──
  const toggleService = (s: SecondCategory) => {
    setErrs((e) => ({ ...e, services: undefined, primary: undefined }));
    const on = services.some((x) => x.id === s.id);
    const next = on ? services.filter((x) => x.id !== s.id) : [...services, s];
    setServices(next);
    if (next.length === 1) setPrimaryId(next[0].id);
    else if (next.length === 0) setPrimaryId(null);
    else if (on && primaryId === s.id) setPrimaryId(null);
  };
  const primaryMissing = services.length > 1 && (primaryId == null || !services.some((s) => s.id === primaryId));

  // ── 지역 선택 ──
  const regionName = (id: number) => firstRegions?.find((f) => f.id === id)?.name ?? '';
  const setWholeFor = (fr: FirstRegion) => {
    setErrs((e) => ({ ...e, regions: undefined }));
    setWholes((list) => list.some((w) => w.id === fr.id) ? list.filter((w) => w.id !== fr.id) : [...list, fr]);
    // 같은 시도의 전체와 개별 시군구는 함께 고를 수 없음
    setRegions((list) => list.filter((r) => r.firstRegionId !== fr.id));
  };
  const toggleRegion = (r: SecondRegion) => {
    setErrs((e) => ({ ...e, regions: undefined }));
    setRegions((list) => list.some((x) => x.id === r.id) ? list.filter((x) => x.id !== r.id) : [...list, r]);
  };
  const chooseNationwide = (on: boolean) => {
    setErrs((e) => ({ ...e, regions: undefined }));
    setNationwide(on);
    if (on) { setWholes([]); setRegions([]); }
  };

  // ── 검증 ──
  const validate = (): Errs => {
    const e: Errs = {};
    const si = shortIntro.trim(); const de = description.trim();
    if (si.length < 1 || si.length > SHORT_INTRO_MAX) e.shortIntro = '한 줄 소개를 1~100자로 입력해 주세요.';
    if (de.length < DESCRIPTION_MIN || de.length > DESCRIPTION_MAX) e.description = '서비스 상세 설명을 10~2,000자로 입력해 주세요.';
    if (careerYears !== '') {
      const n = Number(careerYears);
      if (!/^\d+$/.test(careerYears) || n > CAREER_YEARS_MAX) e.careerYears = '경력 연수는 0~80 사이 정수로 입력해 주세요.';
    }
    if (careerDescription.length > CAREER_DESC_MAX) e.careerDescription = '경력 설명은 1,000자 이내로 입력해 주세요.';
    if (services.length === 0) e.services = '제공 서비스를 하나 이상 골라 주세요.';
    else if (primaryMissing) e.primary = '대표 서비스를 다시 골라 주세요.';
    if (!nationwide && wholes.length === 0 && regions.length === 0) e.regions = '활동 지역을 하나 이상 골라 주세요.';
    return e;
  };

  const finalPrimary = services.length === 1 ? services[0].id : primaryId;

  const submit = (ev: FormEvent) => {
    ev.preventDefault();
    const e = validate();
    setErrs(e); setFail('');
    if (Object.keys(e).length) { setFail('입력한 내용을 확인해 주세요.'); return; }
    // 목록은 중복·null 없이 최종 선택만 보낸다
    const serviceIds = [...new Set(services.map((s) => s.id))];
    const wholeRegionIds = nationwide ? [] : [...new Set(wholes.map((w) => w.id))];
    const regionIds = nationwide ? [] : [...new Set(regions.map((r) => r.id))];

    run(async () => {
      try {
        if (mode === 'create') {
          const body: ExpertProfileCreateRequest = {
            shortIntro: shortIntro.trim(), description: description.trim(),
            serviceIds, primaryCategoryId: finalPrimary!, nationwide, wholeRegionIds, regionIds
          };
          if (careerYears !== '') body.careerYears = Number(careerYears);
          if (careerDescription.trim()) body.careerDescription = careerDescription.trim();
          if (works.length) body.workImages = works.map((w) => ({ fileId: w.fileId, description: w.description.trim() || undefined }));
          await api.createExpertProfile(body);
          await reloadMe().catch(() => undefined);   // 서버가 바꾼 역할(고수) 확인
          toast('고수 프로필을 등록했어요.\n이제 고수로 활동할 수 있어요.');
        } else {
          const body: ExpertProfileUpdateRequest = {
            shortIntro: shortIntro.trim(), description: description.trim(),
            serviceIds, primaryCategoryId: finalPrimary!, nationwide, wholeRegionIds, regionIds
          };
          // 경력: 바뀐 경우에만 보내고 지운 경우 null로 삭제
          const oldYears = initial?.careerYears ?? null;
          const newYears = careerYears === '' ? null : Number(careerYears);
          if (newYears !== oldYears) body.careerYears = newYears;
          const oldDesc = initial?.careerDescription ?? null;
          const newDesc = careerDescription.trim() === '' ? null : careerDescription.trim();
          if (newDesc !== oldDesc) body.careerDescription = newDesc;
          // 작업 사진: 바뀐 경우에만 보낸다 (생략하면 서버가 기존 사진을 유지)
          const sig = (l: WorkDraft[]) => JSON.stringify(l.map((w) => [w.fileId, w.description.trim()]));
          if (sig(works) !== sig(initialWorks)) body.workImages = works.map((w) => ({ fileId: w.fileId, description: w.description.trim() || undefined }));
          await api.updateExpertProfile(body);
          await reloadMe().catch(() => undefined);
          toast('고수 프로필을 저장했어요.');
        }
        navigate('/expert/me');
      } catch (err) {
        if (err instanceof ApiError && err.status === 400 && Object.keys(err.fieldErrors).length) {
          const mapped: Errs = {};
          Object.entries(err.fieldErrors).forEach(([k, v]) => { const key = SERVER_FIELD[k]; if (key) mapped[key] = v; });
          setErrs(mapped);
          setFail('입력한 내용을 확인해 주세요.');
        } else if (err instanceof ApiError && err.status === 409) {
          setFail('이미 고수 프로필이 있어요.\n마이페이지에서 확인해 주세요.');
        } else {
          setFail(`${mode === 'create' ? '고수 프로필을 등록하지 못했어요.\n고객 상태가 그대로 유지돼요.' : '고수 프로필을 저장하지 못했어요.\n기존 프로필은 그대로예요.'}\n입력한 내용은 그대로 남아 있어요.\n${errorText(err)}`);
        }
      }
    });
  };

  const regionChips = useMemo(() => nationwide ? ['전국'] : [
    ...wholes.map((w) => `${w.name} 전체`),
    ...regions.map((r) => `${regionName(r.firstRegionId)} ${r.name}`.trim())
  ], [nationwide, wholes, regions, firstRegions]); // eslint-disable-line react-hooks/exhaustive-deps

  if (baseErr) return <main className="page narrow">{head}<ErrorView title="서비스·지역 목록을 불러오지 못했어요" message={errorText(baseErr)} onRetry={loadBase} /></main>;
  if (!firstCats || !firstRegions) return <main className="page narrow">{head}<LoadingView /></main>;

  const subs = catId != null ? secondCats[catId] : undefined;
  const regs = regId != null ? secondRegs[regId] : undefined;
  const wholeOn = regId != null && wholes.some((w) => w.id === regId);
  const curRegion = firstRegions.find((f) => f.id === regId);

  return (
    <main className="page narrow with-bar">
      {head}
      <p className="sub">{mode === 'edit' ? '기존 내용을 불러왔어요.' : '고객에게 보여줄 프로필을 만들어 주세요.'}<br />닉네임은 내 정보에 등록된 것을 써요.</p>
      {/* 고객이 고수를 고를 때 가장 먼저 보는 것이 사진이라 맨 위에 둔다 */}
      <ProfilePhotoCard
        large
        title={<b className="sec-head"><span className="num">1</span>프로필 사진</b>}
        note={<p className="sub">고객이 견적과 프로필에서 가장 먼저 보는 사진이에요.<br />얼굴이나 작업 모습이 잘 보이는 사진을 올려 주세요.<br />마이페이지에도 같은 사진이 보여요.</p>}
      />
      <form id="expert-form" className="col gap" onSubmit={submit} noValidate>
        <fieldset className="card" disabled={busy}>
          <legend className="sec-head"><span className="num">2</span>소개</legend>
          <Field label="한 줄 소개" hint={`${shortIntro.trim().length} / ${SHORT_INTRO_MAX}`} htmlFor="si" error={errs.shortIntro}>
            <input id="si" className={`input${errs.shortIntro ? ' bad' : ''}`} value={shortIntro} maxLength={SHORT_INTRO_MAX + 20} onChange={(e) => setShortIntro(e.target.value)} placeholder="예: 꼼꼼한 원룸·소형 이사 전문" />
          </Field>
          <Field label="서비스 상세 설명" hint={`${description.trim().length} / 2,000`} htmlFor="de" error={errs.description}>
            <textarea id="de" className={`input area${errs.description ? ' bad' : ''}`} value={description} maxLength={DESCRIPTION_MAX + 200} onChange={(e) => setDescription(e.target.value)} placeholder="10자 이상 입력해 주세요" />
          </Field>
        </fieldset>

        <fieldset className="card" disabled={busy}>
          <legend className="sec-head"><span className="num">3</span>제공 서비스 · 대표 서비스</legend>
          <p className="sub">하나 이상 고르고 그중 하나를 대표로 정해 주세요.</p>
          <div className="tabs" role="tablist">{firstCats.map((c) => <button type="button" role="tab" aria-selected={c.id === catId} key={c.id} className={`tab${c.id === catId ? ' on' : ''}`} onClick={() => setCatId(c.id)}>{c.name}</button>)}</div>
          <div className="chips">
            {!subs ? <LoadingView label="세부 서비스를 불러오는 중이에요" /> : subs.length === 0 ? <span className="sub">선택할 세부 서비스가 없어요.</span> :
              subs.map((s) => { const on = services.some((x) => x.id === s.id); return <button type="button" key={s.id} aria-pressed={on} className={`opt${on ? ' on' : ''}`} onClick={() => toggleService(s)}>{s.name}</button>; })}
          </div>
          {services.length > 0 && (
            <div className="picked">
              <small className="sub">선택한 서비스 {services.length} · {primaryMissing ? '대표 없음' : '대표 지정됨'}</small>
              {services.map((s) => {
                const isP = (services.length === 1 ? services[0].id : primaryId) === s.id;
                return (
                  <div key={s.id} className={`pick${isP ? ' rep' : ''}`}>
                    <span className="grow">{s.name}</span>
                    {isP ? <span className="tag teal">대표</span> : <button type="button" className="mini" onClick={() => { setPrimaryId(s.id); setErrs((e) => ({ ...e, primary: undefined })); }}>대표로 지정</button>}
                    <button type="button" className="x" aria-label={`${s.name} 빼기`} onClick={() => toggleService(s)}>✕</button>
                  </div>
                );
              })}
              <small className="sub">{services.length === 1 ? '서비스가 하나면 자동으로 대표가 돼요.' : '대표 서비스는 프로필 맨 위에 보여요.'}</small>
            </div>
          )}
          {(errs.services || errs.primary) && <span className="field-error" role="alert">{errs.services ?? errs.primary}</span>}
        </fieldset>

        <fieldset className="card" disabled={busy}>
          <legend className="sec-head"><span className="num">4</span>활동 지역</legend>
          <div className="seg" role="radiogroup" aria-label="활동 범위">
            <button type="button" role="radio" aria-checked={nationwide} className={nationwide ? 'on' : ''} onClick={() => chooseNationwide(true)}>전국</button>
            <button type="button" role="radio" aria-checked={!nationwide} className={!nationwide ? 'on' : ''} onClick={() => chooseNationwide(false)}>지역 고르기</button>
          </div>
          {nationwide ? <p className="sub">전국을 고르면 다른 지역은 함께 고를 수 없어요.</p> : (
            <>
              {/* 왼쪽은 시도, 오른쪽은 고른 시도의 시군구. 두 목록은 각자 아래로 스크롤된다 */}
              <div className="region-split">
                <ul className="rs-first" aria-label="시·도">
                  {firstRegions.map((f) => {
                    const n = wholes.some((w) => w.id === f.id) ? '전체' : regions.filter((r) => r.firstRegionId === f.id).length || '';
                    return (
                      <li key={f.id}>
                        <button type="button" className={`rs-item${f.id === regId ? ' on' : ''}`} aria-current={f.id === regId ? 'true' : undefined} onClick={() => setRegId(f.id)}>
                          <span>{f.name}</span>{n !== '' && <span className="rs-count">{n}</span>}
                        </button>
                      </li>
                    );
                  })}
                </ul>
                <div className="rs-second" aria-label={curRegion ? `${curRegion.name} 시·군·구` : '시·군·구'}>
                  {curRegion && (
                    <>
                      <button type="button" aria-pressed={wholeOn} className={`rs-option rs-whole${wholeOn ? ' on' : ''}`} onClick={() => setWholeFor(curRegion)}>
                        {curRegion.name} 전체
                      </button>
                      {!regs ? <LoadingView label="시군구를 불러오는 중이에요" /> : (
                        <div className="rs-grid">
                          {regs.map((r) => { const on = regions.some((x) => x.id === r.id); return <button type="button" key={r.id} disabled={wholeOn} aria-pressed={on} className={`rs-option${on ? ' on' : ''}`} onClick={() => toggleRegion(r)}>{r.name}</button>; })}
                        </div>
                      )}
                    </>
                  )}
                </div>
              </div>
              {curRegion && <small className="sub">{curRegion.name} 전체를 고르면 {curRegion.name}의 시군구는 따로 고를 수 없어요. 전체를 한 번 더 누르면 다시 시군구를 고를 수 있어요.</small>}
            </>
          )}
          {regionChips.length > 0 && !nationwide && (
            <div className="chips">
              {wholes.map((w) => <span key={`w${w.id}`} className="chip sel">{w.name} 전체<button type="button" className="x" aria-label={`${w.name} 전체 빼기`} onClick={() => setWholeFor(w)}>✕</button></span>)}
              {regions.map((r) => <span key={`r${r.id}`} className="chip sel">{`${regionName(r.firstRegionId)} ${r.name}`.trim()}<button type="button" className="x" aria-label={`${r.name} 빼기`} onClick={() => toggleRegion(r)}>✕</button></span>)}
            </div>
          )}
          {errs.regions && <span className="field-error" role="alert">{errs.regions}</span>}
        </fieldset>

        <fieldset className="card" disabled={busy}>
          <legend className="sec-head"><span className="num">5</span>경력 <small className="sub">선택</small></legend>
          <Field label="경력 연수" hint="0~80년 · 비워 두면 표시하지 않아요" htmlFor="cy" error={errs.careerYears}>
            <span className="row"><input id="cy" className={`input short${errs.careerYears ? ' bad' : ''}`} inputMode="numeric" value={careerYears} onChange={(e) => setCareerYears(e.target.value.replace(/[^\d]/g, '').slice(0, 2))} /><span>년</span></span>
          </Field>
          <Field label="경력 설명" hint={`${careerDescription.length} / 1,000`} htmlFor="cd" error={errs.careerDescription}>
            <textarea id="cd" className={`input area compact${errs.careerDescription ? ' bad' : ''}`} value={careerDescription} onChange={(e) => setCareerDescription(e.target.value)} />
          </Field>
        </fieldset>

        <fieldset className="card" disabled={busy}>
          <legend className="sec-head"><span className="num">6</span>작업 사진 <small className="sub">선택</small></legend>
          <WorkImagesField value={works} onChange={setWorks} onBusy={setWorksBusy} />
        </fieldset>
        {subErr && <Banner kind="warn"><Lines text={subErr} /></Banner>}
        {fail && <Banner><Lines text={fail} /></Banner>}
      </form>

      <div className="bottom-bar">
        <div className="bar-inner">
          <small className="sub bar-note">{mode === 'create' ? (primaryMissing ? '대표 서비스를 고르면 등록할 수 있어요.' : '등록을 마치면 고수로 전환돼요.') : (me?.role === 'USER' ? '고객 상태에서도 수정할 수 있어요.' : '')}</small>
          <a className="btn" href={mode === 'edit' ? '#/expert/me' : '#/expert/intro'} aria-disabled={busy} onClick={(e) => busy && e.preventDefault()}>취소</a>
          <button className="btn primary" type="submit" form="expert-form" disabled={busy || worksBusy}>
            {busy ? <><Spinner light />{mode === 'create' ? '등록하는 중' : '저장하는 중'}</> : (mode === 'create' ? '등록하고 고수로 전환' : '저장')}
          </button>
        </div>
      </div>
    </main>
  );
}
