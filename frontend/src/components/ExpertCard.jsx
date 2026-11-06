import { Heart } from 'lucide-react';
import { expertImage } from '../data/categoryMeta.js';
import { expertRegionSummary, primaryOf } from '../lib/homeFormat.js';

// 고수 카드. 프로필 이미지(백엔드 imageUrl 자리)를 크게 보여주고 아래에 이름·한 줄 소개·서비스 태그를 모은다.
// 이미지는 시안용으로 직접 렌더링한 예시 캐릭터다. 평점과 후기는 백엔드에 없으므로 넣지 않는다.
export default function ExpertCard({ expert, badge, bookmarked, onToggleBookmark, headingLevel = 3 }) {
  const primary = primaryOf(expert);
  const H = `h${headingLevel}`;

  return (
    <article className="expert-card">
      <div className="expert-photo">
        <img src={expertImage(expert.id)} alt="" width="512" height="512" loading="lazy" />
        {badge && <span className="expert-badge">{badge}</span>}
      </div>
      <div className="expert-body">
        <H className="expert-name">
          <a href={`#/experts/sample/${expert.id}`} className="expert-link">
            {expert.nickname}
          </a>
        </H>
        <p className="expert-intro">{expert.shortIntro}</p>
        <ul className="expert-tags" aria-label="서비스 정보">
          <li className="is-service">{primary.name}</li>
          <li className="num">{expert.careerYears == null ? '경력 미입력' : `경력 ${expert.careerYears}년`}</li>
          <li>{expertRegionSummary(expert)}</li>
        </ul>
        <button
          type="button"
          className={`bookmark${bookmarked ? ' is-on' : ''}`}
          aria-pressed={bookmarked}
          aria-label={`${expert.nickname} 찜하기`}
          onClick={() => onToggleBookmark(expert.id)}
        >
          <Heart size={20} strokeWidth={2.2} aria-hidden="true" />
        </button>
      </div>
    </article>
  );
}
