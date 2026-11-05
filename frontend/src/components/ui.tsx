// 공통 화면 조각: 아바타 · 상태 화면 · 모달 · 입력 필드 · 줄바꿈 문구
import { useEffect, type ReactNode } from 'react';

// 안내문은 문장 단위로 줄바꿈 (\n 기준)
export function Lines({ text }: { text: string }) {
  const parts = text.split('\n');
  return <>{parts.map((p, i) => <span key={i}>{p}{i < parts.length - 1 && <br />}</span>)}</>;
}

// 사진이 없거나 불러오지 못하면 기본 이미지
// size가 0이면 크기를 정하지 않고 감싸는 요소의 크기를 따른다
export function Avatar({ url, size = 64, alt = '' }: { url?: string | null; size?: number; alt?: string }) {
  return (
    <span className={`avatar${size > 0 && (url?.includes("/seed/expert-portraits/") || url?.includes("/seed/accounts/")) ? " is-face" : ""}`} style={size ? { width: size, height: size } : undefined}>
      {url ? <img src={url} alt={alt} onError={(e) => { (e.currentTarget.style.display = 'none'); }} /> : null}
      <svg viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8.2" r="4.2" /><path d="M3.6 21c.7-4.4 4.2-7 8.4-7s7.7 2.6 8.4 7z" /></svg>
    </span>
  );
}

export function Spinner({ light = false }: { light?: boolean }) {
  return <span className={`spinner${light ? ' light' : ''}`} aria-hidden="true" />;
}

// 조회 중 · 조회 실패 · 빈 상태
export function LoadingView({ label = '불러오는 중이에요' }: { label?: string }) {
  return <div className="state" role="status"><Spinner /><span>{label}</span></div>;
}
export function ErrorView({ title, message, onRetry }: { title: string; message?: string; onRetry?: () => void }) {
  return (
    <div className="state card" role="alert">
      <b>{title}</b>
      {message && <span className="sub"><Lines text={message} /></span>}
      {onRetry && <button className="btn" onClick={onRetry}>다시 시도</button>}
    </div>
  );
}

// full: 모바일에서 모달 대신 전체 화면으로 보여 준다 (와이어프레임 "모바일은 모달 대신 전체 화면") · 웹은 그대로 모달
export function Modal({ title, children, actions, onClose, full = false }: { title: string; children?: ReactNode; actions: ReactNode; onClose?: () => void; full?: boolean }) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => { if (e.key === 'Escape') onClose?.(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);
  return (
    <div className={`dim${full ? ' full-m' : ''}`} onClick={(e) => { if (e.target === e.currentTarget) onClose?.(); }}>
      <div className="modal" role="dialog" aria-modal="true" aria-label={title}>
        {full && onClose && <button type="button" className="m-close show-m" aria-label="닫기" onClick={onClose}>‹</button>}
        <h2>{title}</h2>
        {children && <div className="modal-body">{children}</div>}
        <div className="modal-actions">{actions}</div>
      </div>
    </div>
  );
}

export function Field({ label, hint, error, children, htmlFor }: { label: string; hint?: string; error?: string; children: ReactNode; htmlFor?: string }) {
  return (
    <div className="field">
      <label className="field-label" htmlFor={htmlFor}><span>{label}</span>{hint && <small>{hint}</small>}</label>
      {children}
      {error && <span className="field-error" role="alert">{error}</span>}
    </div>
  );
}

export function Banner({ kind = 'error', children }: { kind?: 'error' | 'info' | 'warn'; children: ReactNode }) {
  return <div className={`banner ${kind}`} role={kind === 'error' ? 'alert' : 'status'}>{children}</div>;
}
