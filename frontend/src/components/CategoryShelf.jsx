import { useCallback, useEffect, useRef, useState } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { CATEGORIES, getSub, objectSrc } from '../data/categoryMeta.js';

// 대분류 11개를 같은 크기·같은 기준선의 입체 오브젝트로 한 줄에 보여준다.
// 창이 좁아지면 한 번에 보이는 개수를 줄이고 양옆 화살표나 가로 스와이프로 넘긴다.
// 하나를 고르면 아래에 실제 소분류가 펼쳐지고 소분류를 누르면 요청서가 열린다.
export default function CategoryShelf({ onPickSub }) {
  const [openId, setOpenId] = useState(null);
  const [edge, setEdge] = useState({ start: true, end: true });
  const railRef = useRef(null);
  const current = CATEGORIES.find((c) => c.id === openId);

  const update = useCallback(() => {
    const el = railRef.current;
    if (!el) return;
    setEdge({ start: el.scrollLeft <= 2, end: el.scrollLeft + el.clientWidth >= el.scrollWidth - 2 });
  }, []);

  useEffect(() => {
    update();
    const ro = new ResizeObserver(update);
    if (railRef.current) ro.observe(railRef.current);
    return () => ro.disconnect();
  }, [update]);

  // 한 번에 지금 보이는 개수만큼 넘긴다
  const page = (dir) => {
    const el = railRef.current;
    const item = el.querySelector('li');
    const step = item ? item.getBoundingClientRect().width + parseFloat(getComputedStyle(el).columnGap || 0) : el.clientWidth;
    const visible = Math.max(1, Math.round(el.clientWidth / step));
    el.scrollBy({ left: dir * visible * step, behavior: 'smooth' });
  };

  const scrollable = !(edge.start && edge.end);

  return (
    <section id="categories" className="section shelf-section" aria-labelledby="shelf-title">
      <div className="container">
        <div className="section-head">
          <div>
            <h2 id="shelf-title">어떤 일을 맡기고 싶으세요?</h2>
          </div>
        </div>

        <div className={`shelf-viewport${scrollable ? ' is-scrollable' : ''}${edge.start ? ' at-start' : ''}${edge.end ? ' at-end' : ''}`}>
          <button type="button" className="shelf-arrow prev" aria-label="이전 분야 보기" hidden={!scrollable} disabled={edge.start} onClick={() => page(-1)}>
            <ChevronLeft size={22} strokeWidth={2} aria-hidden="true" />
          </button>

          <ul className="shelf" ref={railRef} onScroll={update}>
            {CATEGORIES.map((c) => {
              const on = c.id === openId;
              return (
                <li key={c.id}>
                  <button
                    type="button"
                    className={`shelf-tile${on ? ' is-open' : ''}`}
                    aria-expanded={on}
                    aria-controls="shelf-panel"
                    onClick={() => setOpenId(on ? null : c.id)}
                  >
                    <span className="shelf-plate">
                      <img src={objectSrc(c.object)} alt="" width="80" height="80" loading="eager" />
                    </span>
                    <span className="shelf-name">{c.short}</span>
                  </button>
                </li>
              );
            })}
          </ul>

          <button type="button" className="shelf-arrow next" aria-label="다음 분야 보기" hidden={!scrollable} disabled={edge.end} onClick={() => page(1)}>
            <ChevronRight size={22} strokeWidth={2} aria-hidden="true" />
          </button>
        </div>

        <div id="shelf-panel" className={`shelf-panel${current ? ' is-open' : ''}`} aria-live="polite">
          {current && (
            <div className="shelf-panel-inner" key={current.id}>
              <div className="shelf-panel-head">
                <h3>
                  {current.name}
                  <span className="count num">{current.subs.length}개 서비스</span>
                </h3>
              </div>
              <ul className="shelf-subs">
                {current.subs.map((s) => (
                  <li key={s.id}>
                    <button type="button" className="chip" onClick={() => onPickSub(getSub(s.id))}>
                      {s.name}
                    </button>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </div>
    </section>
  );
}
