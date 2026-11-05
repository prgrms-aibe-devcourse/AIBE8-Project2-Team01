import { COLLECTIONS, getSub, objectSrc } from '../data/categoryMeta.js';

// 상황별 묶음. 묶음은 편집 기준이고 안의 소분류는 실제 데이터다.
export default function Collections({ onPickSub }) {
  return (
    <section className="section" aria-labelledby="col-title">
      <div className="container">
        <div className="section-head">
          <div>
            <h2 id="col-title">상황에 맞춰 한 번에 찾기</h2>
          </div>
        </div>

        <ul className="collections">
          {COLLECTIONS.map((c) => (
            <li key={c.id} className={`collection col-${c.id}`}>
              <div className="collection-text">
                <h3>{c.title}</h3>
                <ul className="collection-subs" aria-label={`${c.title} 서비스`}>
                  {c.subs.map((id) => {
                    const s = getSub(id);
                    return (
                      <li key={id}>
                        <button type="button" className="chip" onClick={() => onPickSub(s)}>
                          {s.name}
                        </button>
                      </li>
                    );
                  })}
                </ul>
              </div>
              <div className="collection-objects" aria-hidden="true">
                {c.objects.map((o, i) => (
                  <img key={o} src={objectSrc(o)} alt="" width="96" height="96" loading="lazy" className={`obj obj-${i}`} />
                ))}
              </div>
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
