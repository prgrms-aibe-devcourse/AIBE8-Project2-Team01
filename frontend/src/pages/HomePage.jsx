import { useCallback, useEffect, useRef, useState } from 'react';
import { navigate } from '../hooks/useHashRoute';
import { useSession } from '../auth/session';
import ExpertHomeRequests from '../components/ExpertHomeRequests.jsx';
import { getSub } from '../data/categoryMeta.js';
import Header from '../components/Header.jsx';
import Hero from '../components/Hero.jsx';
import CategoryShelf from '../components/CategoryShelf.jsx';
import Collections from '../components/Collections.jsx';
import Community from '../components/Community.jsx';
import { PublicExperts } from '../components/PublicExperts';
import { NewPublicExperts, RisingPublicExperts } from '../components/PublicExpertHighlights';
import { getPublicExperts } from '../api/endpoints';
import { useLoad } from '../hooks/useLoad';
import { useHomeBookmarks } from '../hooks/useHomeBookmarks';
import ExpertJoin from '../components/ExpertJoin.jsx';
import Footer from '../components/Footer.jsx';
import RequestSheet, { readSavedDraft } from '../components/RequestSheet.jsx';
import MobileRequestBar from '../components/MobileRequestBar.jsx';

// 홈은 역할에 따라 다르게 보인다.
// 고객·비로그인: 서비스 찾기(히어로 검색창·분야 아이콘·상황별 묶음)와 요청서 작성 모달
// 고수: 지금 들어온 요청서를 맨 위에 두고 요청서 작성 입구(히어로·분야 아이콘·상황별 묶음·작성 버튼)는 숨긴다
// 역할은 세션의 회원 정보(me.role)를 따르므로 고수·고객 전환 직후 바로 바뀐다
export default function HomePage({ params }) {
  const { status, me } = useSession();
  const isExpert = status === 'member' && me?.role === 'EXPERT';
  // 공개 고수 목록은 한 번만 불러서 활동 고수·새로 합류한 고수·찜이 늘고 있는 고수 섹션이 함께 쓴다
  // 고수 홈에서는 부르지 않는다. 로그인 확인 중에는 역할을 모르므로 확인이 끝난 뒤 부른다
  const experts = useLoad(() => getPublicExperts(), [], status !== 'loading' && !isExpert);
  const bookmarks = useHomeBookmarks();
  // 요청서 초안: 선택한 소분류와 시군구
  const [draft, setDraft] = useState({ sub: null, region: null });
  const [sheetOpen, setSheetOpen] = useState(false);
  const [slipVisible, setSlipVisible] = useState(true);
  const slipRef = useRef(null);
  const returnFocusRef = useRef(null);

  // 다른 화면에서 #/?section=join 같은 주소로 들어오면 그 섹션으로 이동한다
  const section = params?.get('section');
  useEffect(() => {
    if (!section) return undefined;
    const t = setTimeout(() => document.getElementById(section)?.scrollIntoView({ block: 'start' }), 60);
    return () => clearTimeout(t);
  }, [section]);

  // 고수 프로필의 "이 서비스로 요청서 작성"에서 넘어오면 서비스를 채우고 요청서를 연다
  // 로그인하러 다녀왔으면 작성하던 요청서를 다시 연다
  const resume = params?.get('resume');
  useEffect(() => {
    if (resume !== 'request') return;
    const saved = readSavedDraft();
    if (saved) {
      setDraft({ sub: getSub(saved.subId) ?? null, region: saved.region ?? null });
      setSheetOpen(true);
    }
    navigate('/', undefined, true);
  }, [resume]);

  const serviceParam = params?.get('service');
  useEffect(() => {
    const sub = getSub(Number(serviceParam));
    if (sub) {
      setDraft((d) => ({ ...d, sub }));
      setSheetOpen(true);
      navigate('/', undefined, true);
    }
  }, [serviceParam]);

  // 히어로 검색창이 화면 밖으로 나가면 상단 바에 축약 검색창을 띄운다. 고수↔고객 전환으로 검색창이 다시 생기면 다시 관찰한다
  useEffect(() => {
    const el = slipRef.current;
    if (!el) {
      setSlipVisible(true);
      return undefined;
    }
    const io = new IntersectionObserver(([entry]) => setSlipVisible(entry.isIntersecting), {
      rootMargin: '-72px 0px 0px 0px',
    });
    io.observe(el);
    return () => io.disconnect();
  }, [isExpert]);

  // 분야 아이콘: 그 분야를 고른 채로 모달을 연다. 이미 고른 서비스가 다른 분야면 비운다
  const openCategory = useCallback((c) => {
    returnFocusRef.current = document.activeElement;
    setDraft((d) => ({ ...d, firstId: c.id, sub: d.sub?.parentId === c.id ? d.sub : null }));
    setSheetOpen(true);
  }, []);

  const openSheet = useCallback((patch) => {
    returnFocusRef.current = document.activeElement;
    if (patch) setDraft((d) => ({ ...d, ...patch }));
    setSheetOpen(true);
  }, []);

  const closeSheet = useCallback(() => {
    setSheetOpen(false);
    const el = returnFocusRef.current;
    if (el && typeof el.focus === 'function') requestAnimationFrame(() => el.focus());
  }, []);

  useEffect(() => {
    if (isExpert) setSheetOpen(false);
  }, [isExpert]);


  return (
    <>
      <Header path="/" draft={draft} showCompact={!isExpert && !slipVisible} onOpenRequest={isExpert ? undefined : () => openSheet()} />
      <main id="main" className={`home-main${isExpert ? ' is-expert' : ''}`}>
        {isExpert ? (
          <ExpertHomeRequests />
        ) : (
          <>
            <Hero draft={draft} setDraft={setDraft} slipRef={slipRef} onStart={() => openSheet()} />
            <CategoryShelf onPickCategory={openCategory} />
            <Collections onPickSub={(sub) => openSheet({ sub, firstId: sub?.parentId })} />
            <PublicExperts list={experts.state} bookmarks={bookmarks} onRetry={() => experts.reload()} region={draft.region} onRegionChange={(region) => setDraft((d) => ({ ...d, region }))} />
            <NewPublicExperts list={experts.state} bookmarks={bookmarks} />
            <RisingPublicExperts list={experts.state} bookmarks={bookmarks} />
          </>
        )}
        <Community />
        {isExpert && (
          <section className="section work-cta" aria-labelledby="work-cta-title">
            <div className="container">
              <div className="work-cta-inner">
                <div>
                  <h2 id="work-cta-title">내 작업을 고객에게 보여 주세요</h2>
                  <p>작업 사진과 설명을 올리면 공개 프로필에 작업 소개로 보여요.</p>
                </div>
                <a className="btn btn-primary" href="#/expert/me/edit?section=works">
                  나도 작업 소개 작성하기
                </a>
              </div>
            </div>
          </section>
        )}
        {!isExpert && <ExpertJoin />}
      </main>
      <Footer home={!isExpert} />
      {!isExpert && <MobileRequestBar visible={!slipVisible && !sheetOpen} draft={draft} onOpen={() => openSheet()} />}
      {!isExpert && <RequestSheet open={sheetOpen} draft={draft} setDraft={setDraft} onClose={closeSheet} />}
    </>
  );
}
