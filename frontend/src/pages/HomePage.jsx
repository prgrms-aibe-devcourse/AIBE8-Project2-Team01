import { useCallback, useEffect, useRef, useState } from 'react';
import { navigate } from '../hooks/useHashRoute';
import { getSub } from '../data/categoryMeta.js';
import Header from '../components/Header.jsx';
import Hero from '../components/Hero.jsx';
import CategoryShelf from '../components/CategoryShelf.jsx';
import Collections from '../components/Collections.jsx';
import { PublicExperts } from '../components/PublicExperts';
import { useSession } from '../auth/session';
import Community from '../components/Community.jsx';
import ExpertJoin from '../components/ExpertJoin.jsx';
import Footer from '../components/Footer.jsx';
import RequestSheet, { readSavedDraft } from '../components/RequestSheet.jsx';
import MobileRequestBar from '../components/MobileRequestBar.jsx';

export default function HomePage({ params }) {
  // 요청서 초안: 선택한 소분류와 시군구
  const [draft, setDraft] = useState({ sub: null, region: null });
  const [sheetOpen, setSheetOpen] = useState(false);
  const [slipVisible, setSlipVisible] = useState(true);
  const { status, me } = useSession();
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

  useEffect(() => {
    const el = slipRef.current;
    if (!el) return undefined;
    const io = new IntersectionObserver(([entry]) => setSlipVisible(entry.isIntersecting), {
      rootMargin: '-72px 0px 0px 0px',
    });
    io.observe(el);
    return () => io.disconnect();
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

  return (
    <>
      <Header path="/" draft={draft} showCompact={!slipVisible} onOpenRequest={() => openSheet()} />
      <main id="main" className="home-main">
        <Hero draft={draft} setDraft={setDraft} slipRef={slipRef} onStart={() => openSheet()} />
        <CategoryShelf onPickSub={(sub) => openSheet({ sub })} />
        <Collections onPickSub={(sub) => openSheet({ sub })} />
        {(status === 'guest' || me?.role === 'USER') && <PublicExperts />}
        <Community />
        <ExpertJoin />
      </main>
      <Footer home />
      <MobileRequestBar visible={!slipVisible && !sheetOpen} draft={draft} onOpen={() => openSheet()} />
      <RequestSheet open={sheetOpen} draft={draft} setDraft={setDraft} onClose={closeSheet} />
    </>
  );
}
