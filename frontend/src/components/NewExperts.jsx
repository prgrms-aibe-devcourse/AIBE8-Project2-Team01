import { useEffect, useRef, useState } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { NEW_EXPERTS } from '../data/sampleData.js';
import ExpertCard from './ExpertCard.jsx';

// 신규 등록 고수. 백엔드 ExpertProfile에 등록일이 없어서 예시 값(joinedDaysAgo)으로 정렬한다.

const joinedLabel = (d) => (d <= 1 ? '오늘 합류' : `${d}일 전 합류`);

export default function NewExperts({ bookmarks, onToggleBookmark }) {
  const railRef = useRef(null);
  const [edge, setEdge] = useState({ start: true, end: false });

  const update = () => {
    const el = railRef.current;
    if (!el) return;
    setEdge({
      start: el.scrollLeft <= 4,
      end: el.scrollLeft + el.clientWidth >= el.scrollWidth - 4,
    });
  };

  useEffect(() => {
    update();
    const ro = new ResizeObserver(update);
    if (railRef.current) ro.observe(railRef.current);
    return () => ro.disconnect();
  }, []);

  // 카드 한 장씩 넘긴다
  const scrollBy = (dir) => {
    const el = railRef.current;
    const item = el.querySelector('li');
    const step = item ? item.getBoundingClientRect().width + parseFloat(getComputedStyle(el).columnGap || 0) : el.clientWidth;
    el.scrollBy({ left: dir * step, behavior: 'smooth' });
  };

  return (
    <section className="section" aria-labelledby="new-title">
      <div className="container">
        <div className="section-head">
          <div>
            <div className="title-row">
              <h2 id="new-title">최근 2주 사이 새로 합류한 고수</h2>
              <span className="sample-tag">예시 데이터</span>
            </div>
          </div>
        </div>

        <div className="rail-viewport">
          <button type="button" className="shelf-arrow prev" aria-label="이전 고수 보기" disabled={edge.start} onClick={() => scrollBy(-1)}>
            <ChevronLeft size={22} strokeWidth={2} aria-hidden="true" />
          </button>
          <ul className="rail" ref={railRef} onScroll={update}>
            {NEW_EXPERTS.map((e) => (
              <li key={e.id}>
                <ExpertCard expert={e} badge={joinedLabel(e.joinedDaysAgo)} bookmarked={bookmarks.has(e.id)} onToggleBookmark={onToggleBookmark} />
              </li>
            ))}
          </ul>
          <button type="button" className="shelf-arrow next" aria-label="다음 고수 보기" disabled={edge.end} onClick={() => scrollBy(1)}>
            <ChevronRight size={22} strokeWidth={2} aria-hidden="true" />
          </button>
        </div>
      </div>
    </section>
  );
}
