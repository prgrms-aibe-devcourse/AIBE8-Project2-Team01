import { ChevronLeft, ChevronRight, ChevronRight as More } from 'lucide-react';
import { SAMPLE_EXPERTS, WORK_SHOWCASE } from '../data/sampleData.js';
import { expertImage, getSub } from '../data/categoryMeta.js';
import { expertRegionSummary } from '../lib/homeFormat.js';
import { useRail } from './useRail.js';

// 고수들의 작업 소개 (예시). 카드를 누르면 그 고수의 예시 프로필로 간다.
// 사진은 AI로 만든 가상 인물의 작업 장면이고 실제 작업 사례가 아니다.
export default function WorkShowcase() {
  const { ref, edge, update, step } = useRail();
  const items = WORK_SHOWCASE.map((w) => {
    const e = SAMPLE_EXPERTS.find((x) => x.id === w.expertId);
    return { ...w, expert: e, sub: getSub(e.primaryCategoryId) };
  });

  return (
    <section className="section" aria-labelledby="work-title">
      <div className="container">
        <div className="section-head">
          <div className="title-row">
            <h2 id="work-title">고수들의 작업 소개</h2>
            <span className="sample-tag">예시 데이터</span>
          </div>
          <a className="more" href="#/community?board=TIP">
            고수 노하우 더보기
            <More size={16} aria-hidden="true" />
          </a>
        </div>

        <div className="rail-viewport work-rail">
          <button type="button" className="shelf-arrow prev" aria-label="이전 작업 보기" disabled={edge.start} onClick={() => step(-1)}>
            <ChevronLeft size={22} strokeWidth={2} aria-hidden="true" />
          </button>
          <ul className="rail" ref={ref} onScroll={update}>
            {items.map((w) => (
              <li key={w.id}>
                <a className="work-card" href={`#/experts/sample/${w.expert.id}`}>
                  <span className="work-photo">
                    <img src={expertImage(w.expert.id)} alt="" width="768" height="768" loading="lazy" />
                  </span>
                  <span className="work-title">{w.title}</span>
                  <span className="work-meta">
                    {w.expert.nickname} · {w.sub.name} · {expertRegionSummary(w.expert)}
                  </span>
                </a>
              </li>
            ))}
          </ul>
          <button type="button" className="shelf-arrow next" aria-label="다음 작업 보기" disabled={edge.end} onClick={() => step(1)}>
            <ChevronRight size={22} strokeWidth={2} aria-hidden="true" />
          </button>
        </div>
      </div>
    </section>
  );
}
