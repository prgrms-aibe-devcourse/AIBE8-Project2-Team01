// 고수 프로필 작업 사진 (최대 10장 · JPG·PNG·WebP · 5MB 이하)
// 서버 규칙(ExpertProfileService.setWorkImages): 내가 올린 공개 파일만 · 중복 없이 · 설명 200자 이내
// 사진은 고르는 즉시 공개 파일로 올리고 프로필을 등록·저장할 때 파일 번호를 함께 보낸다
import { useEffect, useRef, useState } from 'react';
import { uploadPublicFiles } from '../api/mvp';
import { errorText } from '../api/client';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { PHOTO_ACCEPT, checkPhoto } from '../lib/policy';
import { Banner, Lines, Spinner } from './ui';

export interface WorkDraft { fileId: number; url: string; description: string }

const MAX = 10;
const DESC_MAX = 200;

export function WorkImagesField({ value, onChange, onBusy }: { value: WorkDraft[]; onChange: (v: WorkDraft[]) => void; onBusy?: (b: boolean) => void }) {
  const fileRef = useRef<HTMLInputElement>(null);
  const { busy, run } = useSingleFlight();
  const [msg, setMsg] = useState('');
  useEffect(() => { onBusy?.(busy); }, [busy, onBusy]);

  const add = (files: File[]) => run(async () => {
    setMsg('');
    const room = MAX - value.length;
    if (room <= 0) { setMsg(`작업 사진은 ${MAX}장까지 올릴 수 있어요.`); return; }
    const bad = files.find((f) => checkPhoto(f));
    if (bad) { setMsg(`${bad.name}\nJPG·PNG·WebP 사진만 5MB 이하로 올릴 수 있어요.`); return; }
    const pick = files.slice(0, room);
    try {
      const up = await uploadPublicFiles(pick);
      const next = up.filter((u) => u.url && !value.some((v) => v.fileId === u.fileId)).map((u) => ({ fileId: u.fileId, url: u.url as string, description: '' }));
      onChange([...value, ...next]);
      if (files.length > room) setMsg(`${MAX}장을 넘는 사진은 빼고 올렸어요.`);
    } catch (e) {
      setMsg(`사진을 올리지 못했어요.\n${errorText(e)}`);
    }
  });

  const remove = (id: number) => onChange(value.filter((w) => w.fileId !== id));
  const move = (i: number, d: -1 | 1) => {
    const j = i + d;
    if (j < 0 || j >= value.length) return;
    const next = [...value];
    [next[i], next[j]] = [next[j], next[i]];
    onChange(next);
  };
  const setDesc = (id: number, text: string) => onChange(value.map((w) => (w.fileId === id ? { ...w, description: text.slice(0, DESC_MAX) } : w)));

  return (
    <div className="col gap">
      <p className="sub">작업한 결과물이나 현장 사진을 올리면 고객이 고수를 고를 때 참고해요.<br />첫 번째 사진이 대표로 보여요.</p>
      <div className="work-grid">
        {value.map((w, i) => (
          <figure key={w.fileId} className="work-item">
            <div className="work-thumb">
              <img src={w.url} alt={w.description || `작업 사진 ${i + 1}`} />
              {i === 0 && <span className="work-badge">대표</span>}
              <button type="button" className="thumb-x" aria-label={`작업 사진 ${i + 1} 빼기`} onClick={() => remove(w.fileId)}>✕</button>
            </div>
            <input className="input sm" value={w.description} maxLength={DESC_MAX} placeholder="사진 설명 (선택)" aria-label={`작업 사진 ${i + 1} 설명`} onChange={(e) => setDesc(w.fileId, e.target.value)} />
            <div className="row work-move">
              <button type="button" className="x" disabled={i === 0} onClick={() => move(i, -1)} aria-label={`작업 사진 ${i + 1} 앞으로`}>← 앞으로</button>
              <button type="button" className="x" disabled={i === value.length - 1} onClick={() => move(i, 1)} aria-label={`작업 사진 ${i + 1} 뒤로`}>뒤로 →</button>
            </div>
          </figure>
        ))}
        {value.length < MAX && (
          <button type="button" className="work-add" disabled={busy} onClick={() => fileRef.current?.click()}>
            {busy ? <Spinner /> : <span aria-hidden="true">＋</span>}
            <span>{busy ? '올리는 중' : '사진 추가'}</span>
            <small>{value.length} / {MAX}</small>
          </button>
        )}
      </div>
      <input ref={fileRef} type="file" accept={PHOTO_ACCEPT} multiple hidden
        onChange={(e) => { const f = Array.from(e.target.files ?? []); e.target.value = ''; if (f.length) add(f); }} />
      {msg && <Banner><Lines text={msg} /></Banner>}
    </div>
  );
}
