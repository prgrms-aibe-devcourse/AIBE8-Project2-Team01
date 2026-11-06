import { Avatar } from './ui';
import { Heart } from 'lucide-react';
import { RISING_EXPERTS as RISING } from '../data/sampleData.js';
import { expertFace } from '../data/categoryMeta.js';
import { expertRegionSummary, primaryOf } from '../lib/homeFormat.js';

// 찜이 빠르게 늘고 있는 고수. 기간별 찜 증가 집계가 백엔드에 없어서 예시 값(bookmarkGain7d)을 쓴다.

export default function RisingExperts({ bookmarks, onToggleBookmark }) {
  return (
    <section className="section" aria-labelledby="rising-title">
      <div className="container">
        <div className="section-head">
          <div>
            <div className="title-row">
              <h2 id="rising-title">이번 주 찜이 빠르게 늘고 있는 고수</h2>
              <span className="sample-tag">예시 데이터</span>
            </div>
          </div>
        </div>

        <ol className="rank">
          {RISING.map((e, i) => {
            const primary = primaryOf(e);
            const on = bookmarks.has(e.id);
            return (
              <li key={e.id} className="rank-row">
                <span className="rank-no num">
                  {i + 1}
                  <span className="sr-only">위</span>
                </span>
                <Avatar url={expertFace(e.id)} size={52} />
                <div className="rank-body">
                  <p className="rank-name">
                    <a href={`#/experts/sample/${e.id}`}>{e.nickname}</a>
                  </p>
                  <p className="rank-meta">
                    <span>{primary.name}</span>
                    <span aria-hidden="true" className="sep" />
                    <span>{expertRegionSummary(e)}</span>
                  </p>
                </div>
                <p className="rank-gain num">
                  <span className="sr-only">최근 7일 </span>찜 +{e.bookmarkGain7d}
                </p>
                <button
                  type="button"
                  className={`bookmark bookmark-inline${on ? ' is-on' : ''}`}
                  aria-pressed={on}
                  aria-label={`${e.nickname} 찜하기`}
                  onClick={() => onToggleBookmark(e.id)}
                >
                  <Heart size={18} strokeWidth={2.2} aria-hidden="true" />
                </button>
              </li>
            );
          })}
        </ol>
      </div>
    </section>
  );
}
