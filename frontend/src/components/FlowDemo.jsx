import { Avatar } from './ui';
import { useEffect, useRef, useState } from 'react';
import { Check, ChevronLeft, Pause, Play, Send } from 'lucide-react';
import { sampleQuotesFor } from '../data/sampleData.js';
import { expertFace, getSub, objectSrc } from '../data/categoryMeta.js';
import { formatWon, regionLabel } from '../lib/homeFormat.js';

// 요청서를 보내고 견적을 고른 뒤 채팅이 열리기까지의 흐름을 자동으로 반복해 보여주는 예시 화면.
// 검색창에서 고른 서비스와 지역이 첫 장면의 요청서에 그대로 들어간다.
const prefersReduced = () =>
  typeof window !== 'undefined' && window.matchMedia?.('(prefers-reduced-motion: reduce)').matches;

const STEPS = [
  { label: '요청서', caption: '요청서 한 장이면', ms: 3000 },
  { label: '전달', caption: '조건 맞는 고수에게 바로 전달', ms: 2000 },
  { label: '견적 비교', caption: '고수가 먼저 견적을 보내요', ms: 2800 },
  { label: '견적 상세', caption: '마음에 드는 견적을 골라', ms: 3200 },
  { label: '채팅', caption: '채팅으로 바로 상담해요', ms: 3600 },
];

// 견적 금액을 0에서 목표 값까지 올려 센다. 동작 줄이기 설정이면 바로 최종 값을 보여준다.
function CountUp({ value, delay = 300, duration = 900 }) {
  const [n, setN] = useState(() => (prefersReduced() ? value : 0));
  useEffect(() => {
    if (prefersReduced()) return undefined;
    let raf;
    const start = performance.now() + delay;
    const tick = (now) => {
      const t = Math.min(1, Math.max(0, (now - start) / duration));
      const eased = 1 - Math.pow(1 - t, 4);
      setN(Math.round((value * eased) / 10000) * 10000);
      if (t < 1) raf = requestAnimationFrame(tick);
    };
    raf = requestAnimationFrame(tick);
    return () => cancelAnimationFrame(raf);
  }, [value, delay, duration]);
  return <>{formatWon(n || 0)}</>;
}

const DEFAULT_SUB = getSub(101);

export default function FlowDemo({ draft }) {
  const sub = draft.sub ?? DEFAULT_SUB;
  const region = draft.region ? regionLabel(draft.region) : '서울 강남구';
  const quotes = sampleQuotesFor(sub, getSub);
  const picked = quotes[0];

  const [step, setStep] = useState(0);
  const [cycle, setCycle] = useState(0);
  const [playing, setPlaying] = useState(() => !prefersReduced());
  const [inView, setInView] = useState(true);
  const [pageVisible, setPageVisible] = useState(true);
  const rootRef = useRef(null);

  // 화면 밖에 있거나 탭이 숨겨지면 멈춘다
  useEffect(() => {
    const io = new IntersectionObserver(([e]) => setInView(e.isIntersecting), { threshold: 0.2 });
    if (rootRef.current) io.observe(rootRef.current);
    const onVis = () => setPageVisible(document.visibilityState === 'visible');
    document.addEventListener('visibilitychange', onVis);
    return () => {
      io.disconnect();
      document.removeEventListener('visibilitychange', onVis);
    };
  }, []);

  const running = playing && inView && pageVisible;

  useEffect(() => {
    if (!running) return undefined;
    const t = setTimeout(() => {
      if (step === STEPS.length - 1) {
        setStep(0);
        setCycle((c) => c + 1);
      } else setStep(step + 1);
    }, STEPS[step].ms);
    return () => clearTimeout(t);
  }, [step, running]);

  // 서비스를 새로 고르면 처음 장면부터 다시 보여준다
  useEffect(() => {
    setStep(0);
    setCycle((c) => c + 1);
  }, [sub.id, draft.region?.second.id]);

  const sceneKey = `${cycle}-${step}`;

  return (
    <aside className="flow" ref={rootRef} aria-label="요청서를 보내고 고수와 채팅하기까지의 흐름 예시">
      <div className="flow-top">
        <span className="sample-tag">예시 화면</span>
        <button
          type="button"
          className="flow-toggle"
          aria-pressed={!playing}
          aria-label={playing ? '자동 재생 멈추기' : '자동 재생 시작'}
          onClick={() => setPlaying((p) => !p)}
        >
          {playing ? <Pause size={14} aria-hidden="true" /> : <Play size={14} aria-hidden="true" />}
        </button>
      </div>

      <p className="flow-caption" key={`c-${sceneKey}`} aria-hidden="true">
        <span>{STEPS[step].caption}</span>
      </p>

      <div className={`flow-screen${running ? '' : ' is-paused'}`} aria-live="polite">
        <p className="sr-only">
          {step + 1}단계 {STEPS[step].label}
        </p>

        {step === 0 && (
          <div className="scene scene-request" key={sceneKey} aria-hidden="true">
            <div className="fr-head">
              <img src={objectSrc(sub.object)} alt="" width="52" height="52" />
              <div>
                <p className="fr-title">{sub.name} 요청서</p>
                <p className="fr-sub">{region}</p>
              </div>
            </div>
            <ul className="fr-answers">
              <li style={{ '--d': '250ms' }}>
                <span>희망 시기</span>다음 주 중
              </li>
              <li style={{ '--d': '650ms' }}>
                <span>예산</span>생각해 둔 금액 안에서
              </li>
              <li style={{ '--d': '1050ms' }}>
                <span>요청사항</span>시간 맞춰 와 주세요
              </li>
            </ul>
            <span className="fr-btn tap-target">
              요청서 보내기
              <span className="tap" style={{ '--d': '2100ms' }} />
            </span>
          </div>
        )}

        {step === 1 && (
          <div className="scene scene-sent" key={sceneKey} aria-hidden="true">
            <span className="sent-plane">
              <Send size={26} strokeWidth={2.2} />
            </span>
            <p className="fr-title">요청서를 보냈어요</p>
            <p className="fr-sub">서비스와 지역이 맞는 고수에게 전달했습니다</p>
            <span className="sent-stack">
              {quotes.map(({ expert }, i) => (
                <Avatar key={expert.id} url={expertFace(expert.id)} size={36} />
              ))}
            </span>
          </div>
        )}

        {step === 2 && (
          <div className="scene scene-list" key={sceneKey} aria-hidden="true">
            <p className="fl-head">
              견적 <b className="num">{quotes.length}</b>건 도착
            </p>
            <ul>
              {quotes.map(({ expert, price }, i) => (
                <li key={expert.id} className={i === 0 ? 'fl-row tap-target is-first' : 'fl-row'} style={{ '--i': i }}>
                  <Avatar url={expertFace(expert.id)} size={36} />
                  <span className="fl-name">
                    {expert.nickname}
                    <small>경력 {expert.careerYears}년</small>
                  </span>
                  <span className="fl-price num">{formatWon(price)}</span>
                  {i === 0 && <span className="tap" style={{ '--d': '1900ms' }} />}
                </li>
              ))}
            </ul>
          </div>
        )}

        {step === 3 && (
          <div className="scene scene-detail" key={sceneKey} aria-hidden="true">
            <p className="fd-back">
              <ChevronLeft size={16} />
              견적 상세
            </p>
            <div className="fd-profile">
              <Avatar url={expertFace(picked.expert.id)} size={64} />
              <div>
                <p className="fr-title">{picked.expert.nickname}</p>
                <p className="fr-sub">경력 {picked.expert.careerYears}년</p>
              </div>
            </div>
            <p className="fd-price num">
              <CountUp value={picked.price} />
            </p>
            <p className="fd-desc">{picked.expert.shortIntro}</p>
            <span className="fr-btn tap-target">
              의뢰 요청하기
              <span className="tap" style={{ '--d': '1900ms' }} />
            </span>
            <span className="fd-stamp">의뢰 요청</span>
          </div>
        )}

        {step === 4 && (
          <div className="scene scene-chat" key={sceneKey} aria-hidden="true">
            <p className="fc-head">
              <Avatar url={expertFace(picked.expert.id)} size={28} />
              {picked.expert.nickname}
            </p>
            <p className="fc-system">
              <Check size={13} strokeWidth={2.6} />
              채팅방이 열렸어요
            </p>
            <p className="bubble from-expert" style={{ '--d': '450ms' }}>
              안녕하세요. 요청서 잘 받았습니다. 방문 가능한 날짜를 알려 주세요.
            </p>
            <p className="bubble from-me" style={{ '--d': '1500ms' }}>
              다음 주 화요일 오전 괜찮아요
            </p>
            <p className="bubble from-expert typing" style={{ '--d': '2300ms' }}>
              <span />
              <span />
              <span />
            </p>
          </div>
        )}
      </div>

      <ol className="flow-steps" style={{ '--progress': step / (STEPS.length - 1) }}>
        {STEPS.map((s, i) => (
          <li key={s.label}>
            <button
              type="button"
              className={i === step ? 'is-current' : i < step ? 'is-done' : ''}
              aria-current={i === step ? 'step' : undefined}
              onClick={() => {
                setStep(i);
                setCycle((c) => c + 1);
              }}
            >
              <span className="flow-dot" aria-hidden="true" />
              <span className="flow-label">{s.label}</span>
            </button>
          </li>
        ))}
      </ol>
    </aside>
  );
}
