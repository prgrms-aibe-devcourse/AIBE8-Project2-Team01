import { useEffect, useState } from 'react';
import { getPublicExperts } from '../api/endpoints';
import type { PublicExpertSummary } from '../api/types';
import { useFirstRegions } from '../hooks/useData';
import { careerLabel, regionLabels } from '../lib/format';
import { Avatar } from './ui';

export function PublicExperts() {
  const [experts, setExperts] = useState<PublicExpertSummary[] | null>(null);
  const [failed, setFailed] = useState(false);
  const [attempt, setAttempt] = useState(0);
  const firstRegions = useFirstRegions();

  useEffect(() => {
    let active = true;
    getPublicExperts()
      .then((list) => { if (active) setExperts(list); })
      .catch(() => { if (active) setFailed(true); });
    return () => { active = false; };
  }, [attempt]);

  return (
    <section id="experts" className="section" aria-labelledby="public-experts-title">
      <div className="container">
        <div className="section-head">
          <h2 id="public-experts-title">활동 중인 고수</h2>
        </div>
        {failed ? (
          <div className="empty">
            <p>고수 목록을 불러오지 못했어요.</p>
            <button type="button" className="btn btn-secondary" onClick={() => {
              setFailed(false);
              setAttempt((n) => n + 1);
            }}>다시 시도</button>
          </div>
        ) : experts === null ? (
          <p role="status">고수 목록을 불러오는 중이에요.</p>
        ) : experts.length === 0 ? (
          <p className="empty">활동 중인 고수가 아직 없어요.</p>
        ) : (
          <ul className="card-grid">
            {experts.map((expert) => (
              <li key={expert.id}>
                <a className="expert-card" href={`#/experts/${expert.id}`}>
                  <div className="expert-photo">
                    <Avatar url={expert.representativeWorkImageUrl || expert.imageUrl} size={0} />
                  </div>
                  <div className="expert-body">
                    <h3 className="expert-name">{expert.nickname}</h3>
                    <p className="expert-intro">{expert.shortIntro}</p>
                    <ul className="expert-tags" aria-label="서비스 정보">
                      <li className="is-service">{expert.primaryCategory?.name ?? '대표 서비스 미등록'}</li>
                      {expert.careerYears !== null && <li>{careerLabel(expert.careerYears)}</li>}
                      <li>{regionLabels(expert, firstRegions).join(' · ') || '지역 미등록'}</li>
                    </ul>
                  </div>
                </a>
              </li>
            ))}
          </ul>
        )}
      </div>
    </section>
  );
}
