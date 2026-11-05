import { useMemo, useState } from 'react';
import { SAMPLE_EXPERTS, LOCAL_TABS, FEATURED_IDS } from '../data/sampleData.js';
import { FIRST_REGIONS } from '../data/baseData.js';
import { regionSpecificity } from '../lib/homeFormat.js';
import ExpertCard from './ExpertCard.jsx';

const toRegion = (label) => {
  const [sido, gu] = label.split(' ');
  const first = FIRST_REGIONS.find((r) => r.name.startsWith(sido));
  const second = first?.subs.find((s) => s.name === gu);
  return first && second ? { first: { id: first.id, name: first.name }, second } : null;
};

export default function LocalExperts({ bookmarks, onToggleBookmark, onRequest }) {
  const [tab, setTab] = useState(LOCAL_TABS[0]);
  // 그 지역에 직접 등록한 고수를 먼저 보여주고 아래 목록에 나오는 고수는 뒤로 미룬다.
  const list = useMemo(
    () =>
      SAMPLE_EXPERTS.map((e) => ({ e, score: regionSpecificity(e, tab) * 2 + (FEATURED_IDS.has(e.id) ? 0 : 1) }))
        .filter(({ e }) => regionSpecificity(e, tab) > 0)
        .sort((a, b) => b.score - a.score)
        .slice(0, 4)
        .map(({ e }) => e),
    [tab],
  );

  const onKeyDown = (e) => {
    const i = LOCAL_TABS.indexOf(tab);
    if (e.key === 'ArrowRight' || e.key === 'ArrowLeft') {
      e.preventDefault();
      const next = LOCAL_TABS[(i + (e.key === 'ArrowRight' ? 1 : LOCAL_TABS.length - 1)) % LOCAL_TABS.length];
      setTab(next);
      document.getElementById(`local-tab-${LOCAL_TABS.indexOf(next)}`)?.focus();
    }
  };

  return (
    <section id="experts" className="section" aria-labelledby="local-title">
      <div className="container">
        <div className="section-head">
          <div>
            <div className="title-row">
              <h2 id="local-title">우리 동네에서 활동하는 고수</h2>
              <span className="sample-tag">예시 데이터</span>
            </div>
          </div>
        </div>

        <div className="tabs" role="tablist" aria-label="지역" onKeyDown={onKeyDown}>
          {LOCAL_TABS.map((t, i) => (
            <button
              key={t}
              id={`local-tab-${i}`}
              type="button"
              role="tab"
              className="tab"
              aria-selected={t === tab}
              aria-controls="local-panel"
              tabIndex={t === tab ? 0 : -1}
              onClick={() => setTab(t)}
            >
              {t}
            </button>
          ))}
        </div>

        <div id="local-panel" role="tabpanel" aria-labelledby={`local-tab-${LOCAL_TABS.indexOf(tab)}`} className="local-panel">
          {list.length > 0 ? (
            <ul className="card-grid">
              {list.map((e) => (
                <li key={e.id}>
                  <ExpertCard expert={e} bookmarked={bookmarks.has(e.id)} onToggleBookmark={onToggleBookmark} />
                </li>
              ))}
            </ul>
          ) : (
            <div className="empty">
              <p>{tab}에 등록된 고수가 아직 없습니다.</p>
              <p className="empty-sub">요청서를 남기면 이 지역에서 활동하는 고수가 생겼을 때 견적을 받을 수 있습니다.</p>
            </div>
          )}
          <div className="local-foot">
            <button type="button" className="btn btn-secondary btn-sm" onClick={() => onRequest(toRegion(tab))}>
              {tab}로 요청서 작성
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}
