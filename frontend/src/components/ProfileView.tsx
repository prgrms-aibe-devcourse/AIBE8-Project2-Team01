// 고수 프로필 본문 (공개 프로필 · 본인 프로필 공통)
// 이메일·전화번호는 응답에도 없고 화면에도 넣지 않는다
// 작업 사진·리뷰·별점·완료 건수·찜은 이번 범위 밖
import type { ReactNode } from 'react';
import type { ExpertProfile } from '../api/types';
import { useFirstRegions } from '../hooks/useData';
import { careerLabel, regionLabels } from '../lib/format';
import { Avatar } from './ui';

export function ProfileView({ p, side, note }: { p: ExpertProfile; side?: ReactNode; note?: ReactNode }) {
  const firstRegions = useFirstRegions();
  const services = [...p.services].sort((a, b) => (a.id === p.primaryCategory?.id ? -1 : b.id === p.primaryCategory?.id ? 1 : 0));
  return (
    <div className="profile-grid">
      <aside className="card profile-side">
        <div className="profile-photo"><Avatar url={p.imageUrl} size={0} alt={`${p.nickname} 프로필 사진`} /></div>
        {note}
        <b className="me-big">{p.nickname}</b>
        {p.primaryCategory && <span className="tag teal">대표 · {p.primaryCategory.name}</span>}
        <span className="intro center">{p.shortIntro}</span>
        {p.careerYears !== null && <span className="sub">{careerLabel(p.careerYears)}</span>}
        {side}
      </aside>
      <div className="col gap">
        <section className="card">
          <b className="sec-title">제공 서비스</b>
          <div className="chips">{services.map((s) => <span key={s.id} className={`chip${s.id === p.primaryCategory?.id ? ' teal' : ''}`}>{s.name}{s.id === p.primaryCategory?.id ? ' · 대표' : ''}</span>)}</div>
        </section>
        <section className="card">
          <b className="sec-title">활동 지역</b>
          <div className="chips">{regionLabels(p, firstRegions).map((r) => <span key={r} className="chip">{r}</span>)}</div>
        </section>
        {p.workImages && p.workImages.length > 0 && (
          <section className="card">
            <b className="sec-title">작업 사진 <span className="blue-t">{p.workImages.length}</span></b>
            <div className="work-gallery">
              {p.workImages.map((w, i) => (
                <figure key={w.fileId}>
                  <img src={w.url} alt={w.description || `작업 사진 ${i + 1}`} loading="lazy" />
                  {w.description && <figcaption>{w.description}</figcaption>}
                </figure>
              ))}
            </div>
          </section>
        )}
        <section className="card">
          <b className="sec-title">서비스 소개</b>
          <p className="body-text">{p.description}</p>
        </section>
        {(p.careerYears !== null || p.careerDescription) && (
          <section className="card">
            <b className="sec-title">경력</b>
            <p className="body-text">{p.careerYears !== null && <b>{p.careerYears}년</b>}{p.careerYears !== null && p.careerDescription ? ' · ' : ''}{p.careerDescription}</p>
          </section>
        )}
      </div>
    </div>
  );
}
