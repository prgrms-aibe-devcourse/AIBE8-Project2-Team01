// #173 공통 조각: 상태 배지 · 페이지 번호 · 조회 상태 래퍼 · 빈 상태 · 권한 안내
import type { ReactNode } from 'react';
import { errorText } from '../api/client';
import type { Load } from '../hooks/useLoad';
import type { Tone } from '../lib/labels';
import { ErrorView, Lines, LoadingView } from './ui';

export function Badge({ text, tone = 'gray' }: { text: string; tone?: Tone }) {
  return <span className={`badge ${tone}`}>{text}</span>;
}

// 일반 목록의 페이지 이동 (채팅 이전 메시지는 스크롤 조회로 따로 처리)
export function Pager({ page, totalPages, onPage, disabled }: { page: number; totalPages: number; onPage: (p: number) => void; disabled?: boolean }) {
  if (totalPages <= 1) return null;
  const start = Math.max(1, Math.min(page - 2, totalPages - 4));
  const nums = Array.from({ length: Math.min(5, totalPages) }, (_, i) => start + i);
  return (
    <nav className="pager" aria-label="페이지 이동">
      <button type="button" className="pg" disabled={disabled || page <= 1} onClick={() => onPage(page - 1)} aria-label="이전 페이지">‹</button>
      {nums.map((n) => (
        <button type="button" key={n} className={`pg${n === page ? ' on' : ''}`} aria-current={n === page ? 'page' : undefined} disabled={disabled} onClick={() => n !== page && onPage(n)}>{n}</button>
      ))}
      <button type="button" className="pg" disabled={disabled || page >= totalPages} onClick={() => onPage(page + 1)} aria-label="다음 페이지">›</button>
    </nav>
  );
}

export function Empty({ title, desc, action }: { title: string; desc?: string; action?: ReactNode }) {
  return (
    <div className="state card empty">
      <span className="empty-ic" aria-hidden="true" />
      <b>{title}</b>
      {desc && <span className="sub"><Lines text={desc} /></span>}
      {action}
    </div>
  );
}

// 조회 상태에 따라 로딩·실패·내용을 그린다
export function LoadBox<T>({ load, title, onRetry, children, loadingLabel }: {
  load: Load<T>; title: string; onRetry: () => void; children: (data: T) => ReactNode; loadingLabel?: string;
}) {
  if (load.kind === 'loading') return <LoadingView label={loadingLabel} />;
  if (load.kind === 'error') return <ErrorView title={title} message={errorText(load.error)} onRetry={onRetry} />;
  return <>{children(load.data)}</>;
}

// 역할이 맞지 않는 화면에 들어왔을 때
export function RoleNotice({ title, desc, href, label }: { title: string; desc: string; href: string; label: string }) {
  return (
    <main className="page narrow">
      <div className="state card">
        <b>{title}</b>
        <span className="sub"><Lines text={desc} /></span>
        <a className="btn primary" href={href}>{label}</a>
      </div>
    </main>
  );
}

export function PageHead({ title, back, right, sub }: { title: string; back?: { href: string; label: string }; right?: ReactNode; sub?: string }) {
  return (
    <div className="page-head">
      {back && <a className="back" href={back.href}>‹ {back.label}</a>}
      <div className="row between">
        <h1 className="page-title">{title}</h1>
        {right}
      </div>
      {sub && <p className="sub"><Lines text={sub} /></p>}
    </div>
  );
}
