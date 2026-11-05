import { sampleQuotesFor } from '../data/sampleData.js';
import { formatWon, regionLabel } from '../lib/homeFormat.js';
import { expertFace, getSub, objectSrc } from '../data/categoryMeta.js';

// 검색창에 고른 서비스와 지역이 요청서 카드에 바로 반영되고 같은 분야 예시 고수의 견적이 도착하는 미리보기.
// 견적은 예시이므로 "예시 화면"으로 표시한다. 모바일에서는 서비스를 고른 뒤에만 짧게 보여준다.
const DEFAULT_SUB = getSub(101);

export default function RequestPreview({ draft }) {
  const sub = draft.sub ?? DEFAULT_SUB;
  const picked = Boolean(draft.sub);
  const quotes = sampleQuotesFor(sub, getSub);
  const runKey = `${sub.id}-${draft.region?.second.id ?? 'none'}`;

  return (
    <aside className={`preview${picked ? ' is-picked' : ''}`} aria-label="요청서를 보내면 견적이 도착하는 과정 예시">
      <span className="sample-tag preview-tag">예시 화면</span>

      <div className="preview-request">
        <img className="preview-object" key={sub.object} src={objectSrc(sub.object)} alt="" width="88" height="88" />
        <div className="preview-request-text">
          <p key={`s-${sub.id}`} className={`preview-service${picked ? ' is-new' : ''}`}>
            {sub.name} 요청서
          </p>
          <p key={`r-${draft.region?.second.id}`} className={`preview-region${draft.region ? ' is-new' : ''}`}>
            {draft.region ? regionLabel(draft.region) : '지역을 고르면 표시됩니다'}
          </p>
        </div>
      </div>

      <div className="preview-quotes" key={runKey}>
        <p className="preview-arrived">
          <span className="pq-stack" aria-hidden="true">
            {quotes.map(({ expert }) => (
              <img key={expert.id} src={expertFace(expert.id)} alt="" width="28" height="28" />
            ))}
          </span>
          <span>
            견적 <span className="num">{quotes.length}</span>건 도착
          </span>
        </p>
        <ul>
          {quotes.map(({ expert, price }, i) => (
            <li key={expert.id} className="pq" style={{ '--i': i }}>
              <img className="pq-avatar" src={expertFace(expert.id)} alt="" width="40" height="40" />
              <p className="pq-name">
                {expert.nickname}
                <span>경력 {expert.careerYears}년</span>
              </p>
              <p className="pq-price num">{formatWon(price)}</p>
            </li>
          ))}
        </ul>
      </div>
    </aside>
  );
}
