// 공개 홈 · 서비스 선택 (와이어프레임 W·01 기준)
// 로그인 없이 볼 수 있고 요청서 작성처럼 인증이 필요한 동작에서만 로그인을 요구한다
// 와이어프레임의 예시 고수·금액 카드는 실제 데이터처럼 보이지 않도록 장식 도형만 남겼다
// "이 조건의 고수 둘러보기"는 고수 검색 API가 없어 넣지 않았다
import { useEffect, useState } from 'react';
import { errorText } from '../api/client';
import * as api from '../api/endpoints';
import type { FirstCategory, SecondCategory } from '../api/types';
import { useSession } from '../auth/session';
import { ErrorView, LoadingView } from '../components/ui';
import { useLoad } from '../hooks/useLoad';

// 분야 아이콘 색 (와이어프레임 W·01 팔레트 순서)
const TONES = [
  { bg: '#EEF3FD', fg: '#3F6FCC' }, { bg: '#E6F5F4', fg: '#04706D' }, { bg: '#FFF4E6', fg: '#B4690E' },
  { bg: '#F3EEFC', fg: '#6B4FC2' }, { bg: '#FDEFF2', fg: '#C2415B' }, { bg: '#F2F4F6', fg: '#4E5968' }
];
// 분야 이름에 맞는 선 아이콘 (없으면 기본 격자)
function CatIcon({ name }: { name: string }) {
  const p = { width: 28, height: 28, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.8, strokeLinecap: 'round' as const, strokeLinejoin: 'round' as const, 'aria-hidden': true };
  if (/이사|청소/.test(name)) return <svg {...p}><path d="M3 7h11v9H3zM14 10h4l3 3v3h-7z" /><circle cx="7" cy="18" r="1.8" /><circle cx="17" cy="18" r="1.8" /></svg>;
  if (/설치|수리/.test(name)) return <svg {...p}><path d="M14.7 6.3a4 4 0 0 0-5.4 5.4L3 18l3 3 6.3-6.3a4 4 0 0 0 5.4-5.4l-2.5 2.5-2.4-.6-.6-2.4z" /></svg>;
  if (/인테리어/.test(name)) return <svg {...p}><rect x="3" y="4" width="14" height="6" rx="1.5" /><path d="M17 7h3v5h-8v3" /><rect x="10" y="15" width="4" height="6" rx="1" /></svg>;
  if (/이벤트|뷰티/.test(name)) return <svg {...p}><path d="M12 3l1.6 4.4L18 9l-4.4 1.6L12 15l-1.6-4.4L6 9l4.4-1.6z" /><path d="M18 15l.8 2.2L21 18l-2.2.8L18 21l-.8-2.2L15 18l2.2-.8z" /></svg>;
  if (/과외|레슨|취미|자기계발|취업|직무/.test(name)) return <svg {...p}><path d="M4 5a2 2 0 0 1 2-2h13v16H6a2 2 0 0 0-2 2z" /><path d="M4 19V5M8 7h7" /></svg>;
  if (/외주/.test(name)) return <svg {...p}><rect x="3" y="4" width="18" height="12" rx="2" /><path d="M8 20h8M12 16v4" /></svg>;
  if (/자동차/.test(name)) return <svg {...p}><path d="M5 16l1.5-5h11L19 16" /><rect x="3" y="16" width="18" height="3" rx="1" /><circle cx="7.5" cy="19.5" r="1.3" /><circle cx="16.5" cy="19.5" r="1.3" /></svg>;
  if (/법률|금융/.test(name)) return <svg {...p}><path d="M12 3v18M5 7h14M7 7l-3 7h6zM17 7l-3 7h6z" /></svg>;
  return <svg {...p}><rect x="4" y="4" width="6" height="6" rx="1.5" /><rect x="14" y="4" width="6" height="6" rx="1.5" /><rect x="4" y="14" width="6" height="6" rx="1.5" /><rect x="14" y="14" width="6" height="6" rx="1.5" /></svg>;
}

export function HomePage() {
  const { status, me } = useSession();
  const cats = useLoad(() => api.getFirstCategories(), []);
  const [catId, setCatId] = useState<number | null>(null);
  const [subs, setSubs] = useState<Record<number, SecondCategory[] | 'error'>>({});
  const [subId, setSubId] = useState<number | null>(null);

  // 처음에는 첫 번째 분야를 펼친다
  useEffect(() => {
    if (cats.state.kind === 'ok' && catId === null && cats.state.data.length) setCatId(cats.state.data[0].id);
  }, [cats.state, catId]);

  const loadSubs = (id: number) => {
    setSubs((m) => { const n = { ...m }; delete n[id]; return n; });
    api.getSecondCategories(id).then((l) => setSubs((m) => ({ ...m, [id]: l }))).catch(() => setSubs((m) => ({ ...m, [id]: 'error' })));
  };
  useEffect(() => { if (catId !== null && subs[catId] === undefined) loadSubs(catId); }, [catId]); // eslint-disable-line react-hooks/exhaustive-deps

  // 다른 분야를 고르면 세부 서비스 선택을 초기화한다
  const pickCat = (c: FirstCategory) => { if (c.id !== catId) { setCatId(c.id); setSubId(null); } };
  const cur = catId !== null ? subs[catId] : undefined;
  const curCat = cats.state.kind === 'ok' ? cats.state.data.find((c) => c.id === catId) : undefined;
  const subName = Array.isArray(cur) ? cur.find((s) => s.id === subId)?.name : undefined;
  const role = status === 'member' ? me?.role : undefined;
  const toServices = () => document.getElementById('services')?.scrollIntoView({ behavior: 'smooth', block: 'start' });

  return (
    <main className="home">
      <section className="home-hero">
        <div className="home-hero-in">
          <div className="col hero-text">
            <span className="hero-pill">요청서 한 장으로 견적을 비교해요</span>
            <h1>이사부터 레슨까지 <em>딱 맞는 고수</em>를 찾아 드려요</h1>
            <p>필요한 서비스와 지역을 고르고 요청서를 보내면 조건에 맞는 고수들이 24시간 동안 견적을 보내요.</p>
            <div className="row hero-cta">
              <button type="button" className="hbtn p" onClick={toServices}>서비스 고르기</button>
              {role === 'EXPERT'
                ? <a className="hbtn g" href="#/received">받은 요청 보기</a>
                : <a className="hbtn g" href="#/requests">받은 견적 보기</a>}
            </div>
          </div>
          {/* 장식 그림: 실제 고수·금액이 아닌 도형만 표시 */}
          <div className="hero-art" aria-hidden="true">
            <span className="blob b1" /><span className="blob b2" />
            <div className="qc qc1"><span className="av44 teal" /><div className="grow"><i className="bar-s w60" /><i className="bar-s w40 light" /></div><i className="bar-s w20 blue" /></div>
            <div className="qc qc2"><span className="av44" /><div className="grow"><i className="bar-s w55" /><i className="bar-s w35 light" /></div><i className="bar-s w20 blue" /></div>
            <div className="hero-done"><span className="ck">✓</span>견적이 도착하면 알려 드려요</div>
          </div>
        </div>
      </section>

      <div className="home-body">
        <section id="services" className="home-sec">
          <div className="row between baseline"><h2 className="home-h2">어떤 서비스가 필요하세요?</h2><span className="home-note">다른 분야를 고르면 세부 서비스 선택이 초기화돼요</span></div>
          {cats.state.kind === 'loading' && <div className="card"><LoadingView label="서비스 분야를 불러오는 중이에요" /></div>}
          {cats.state.kind === 'error' && <ErrorView title="서비스 분야를 불러오지 못했어요" message={errorText(cats.state.error)} onRetry={() => cats.reload()} />}
          {cats.state.kind === 'ok' && (cats.state.data.length === 0 ? <div className="state card"><b>등록된 서비스 분야가 없어요</b></div> : (
            <>
              <div className="cat-grid">
                {cats.state.data.map((c, i) => {
                  const t = TONES[i % TONES.length];
                  return (
                    <button type="button" key={c.id} className={`cat${c.id === catId ? ' on' : ''}`} aria-pressed={c.id === catId} onClick={() => pickCat(c)}>
                      <span className="ico" style={{ background: t.bg, color: t.fg }}><CatIcon name={c.name} /></span>{c.name}
                    </button>
                  );
                })}
              </div>
              <div className="sub-panel">
                <div className="col grow gap-s">
                  <b className="sub-title">{curCat?.name ?? ''} · 세부 서비스</b>
                  {cur === undefined && <LoadingView label="세부 서비스를 불러오는 중이에요" />}
                  {cur === 'error' && <ErrorView title="세부 서비스를 불러오지 못했어요" onRetry={() => catId !== null && loadSubs(catId)} />}
                  {Array.isArray(cur) && (cur.length === 0 ? <span className="sub">이 분야에는 아직 세부 서비스가 없어요.</span> : (
                    <div className="chips">
                      {cur.map((s) => (
                        <button type="button" key={s.id} className={`hchip${s.id === subId ? ' on' : ''}`} aria-pressed={s.id === subId} onClick={() => setSubId(s.id)}>{s.name}</button>
                      ))}
                    </div>
                  ))}
                  {role === 'EXPERT' && <small className="sub">요청서는 고객 상태에서 작성할 수 있어요.</small>}
                  {role === 'ADMIN' && <small className="sub">관리자 계정은 요청서를 작성하지 않아요.</small>}
                </div>
                {role !== 'EXPERT' && role !== 'ADMIN' && (
                  subId
                    ? <a className="hbtn p" href={`#/requests/new?fc=${catId}&cat=${subId}`}>{subName} 요청서 쓰기 →</a>
                    : <button type="button" className="hbtn p" disabled>세부 서비스를 골라 주세요</button>
                )}
              </div>
            </>
          ))}
        </section>

        <section className="home-steps">
          <div className="hstep"><span className="hnum">1</span><b>요청서 보내기</b><span>질문 몇 개에 답하면 요청서가 완성돼요.<br />제출 후 24시간 동안 견적을 받아요.</span></div>
          <div className="hstep"><span className="hnum">2</span><b>견적 비교하기</b><span>가격·설명·고수 프로필을 나란히 보고 고를 수 있어요.</span></div>
          <div className="hstep"><span className="hnum">3</span><b>채팅으로 상담하기</b><span>마음에 드는 견적에 의뢰를 요청하면 바로 채팅이 열려요.</span></div>
          <a className="hpromo" href="#/mypage">
            <small>고수로 활동하고 싶다면</small>
            <b>내 실력으로 새 고객을 만나 보세요</b>
            <span>마이페이지에서 고수 전환 →</span>
          </a>
        </section>
      </div>
    </main>
  );
}
