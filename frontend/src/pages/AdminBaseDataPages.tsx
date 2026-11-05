// 관리자 · 카테고리 관리 (A3·A4) · 지역 관리 (A5·A6)
// 상위 항목을 고르면 오른쪽에 하위 항목이 보인다. 등록·수정·삭제 결과는 서버 응답 뒤 목록을 다시 불러와 반영한다
// 하위 항목이 있거나 다른 데이터가 참조하면 서버가 삭제를 거절한다
// 서버가 원인 없이 409(데이터 무결성 위반)를 주면, 하위 항목이 남아 있는지 다시 조회해 확인된 경우에만 그 원인을 안내한다
import { useEffect, useState, type FormEvent } from 'react';
import { isUnexplainedConflict } from '../api/client';
import * as api from '../api/endpoints';
import * as mvp from '../api/mvp';
import { LoadBox } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, Lines, Modal, Spinner } from '../components/ui';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { friendly } from '../lib/messages';

export type AdminKey = 'members' | 'posts' | 'requests' | 'quotes' | 'categories' | 'regions';
export function AdminNav({ current }: { current: AdminKey }) {
  const items = [['members', '회원 관리'], ['posts', '게시글 관리'], ['requests', '요청서 현황'], ['quotes', '견적 현황'], ['categories', '카테고리 관리'], ['regions', '지역 관리']] as const;
  return (
    <nav className="card admin-nav" aria-label="관리 메뉴">
      <small className="sub">관리 메뉴</small>
      {items.map(([k, l]) => <a key={k} href={`#/admin/${k}`} className={k === current ? 'on' : ''}>{l}</a>)}
    </nav>
  );
}

interface Item { id: number; name: string; code?: string }
interface Config {
  kind: 'categories' | 'regions';
  title: string; desc: string; upper: string; lower: string; withCode: boolean;
  listUpper: () => Promise<Item[]>;
  listLower: (upperId: number) => Promise<Item[]>;
  createUpper: (name: string, code: string) => Promise<unknown>;
  updateUpper: (id: number, name: string, code: string) => Promise<unknown>;
  deleteUpper: (id: number) => Promise<unknown>;
  createLower: (upperId: number, name: string, code: string) => Promise<unknown>;
  updateLower: (id: number, name: string, code: string, upperId: number) => Promise<unknown>;
  deleteLower: (id: number) => Promise<unknown>;
}

const CATEGORY: Config = {
  kind: 'categories', title: '카테고리 관리', desc: '대분류를 고르면 오른쪽에 소분류가 보여요', upper: '대분류', lower: '소분류', withCode: false,
  listUpper: () => api.getFirstCategories(), listLower: (id) => api.getSecondCategories(id),
  createUpper: (n) => mvp.adminCreateFirstCategory(n), updateUpper: (id, n) => mvp.adminUpdateFirstCategory(id, n), deleteUpper: (id) => mvp.adminDeleteFirstCategory(id),
  createLower: (u, n) => mvp.adminCreateSecondCategory(u, n), updateLower: (id, n, _c, u) => mvp.adminUpdateSecondCategory(id, n, u), deleteLower: (id) => mvp.adminDeleteSecondCategory(id)
};
const REGION: Config = {
  kind: 'regions', title: '지역 관리', desc: '시도를 고르면 오른쪽에 시군구가 보여요 · 지역 코드는 숫자 10자리예요', upper: '시도', lower: '시군구', withCode: true,
  listUpper: () => api.getFirstRegions(), listLower: (id) => api.getSecondRegions(id),
  createUpper: (n, c) => mvp.adminCreateFirstRegion(n, c), updateUpper: (id, n, c) => mvp.adminUpdateFirstRegion(id, n, c), deleteUpper: (id) => mvp.adminDeleteFirstRegion(id),
  createLower: (u, n, c) => mvp.adminCreateSecondRegion(u, n, c), updateLower: (id, n, c, u) => mvp.adminUpdateSecondRegion(id, n, c, u), deleteLower: (id) => mvp.adminDeleteSecondRegion(id)
};

export const AdminCategoriesPage = () => <TwoLevelAdmin cfg={CATEGORY} />;
export const AdminRegionsPage = () => <TwoLevelAdmin cfg={REGION} />;

function validate(cfg: Config, name: string, code: string): string | null {
  const n = name.trim();
  if (!n || n.length > 50) return '이름은 1~50자로 입력해 주세요.';
  if (cfg.withCode && !/^\d{10}$/.test(code.trim())) return '지역 코드는 숫자 10자리로 입력해 주세요.';
  return null;
}

function TwoLevelAdmin({ cfg }: { cfg: Config }) {
  const upper = useLoad(() => cfg.listUpper(), [cfg]);
  const [selId, setSelId] = useState<number | null>(null);
  const lower = useLoad(() => (selId ? cfg.listLower(selId) : Promise.resolve([] as Item[])), [cfg, selId], selId !== null);

  useEffect(() => {
    if (upper.state.kind === 'ok' && (selId === null || !upper.state.data.some((u) => u.id === selId))) setSelId(upper.state.data[0]?.id ?? null);
  }, [upper.state, selId]);
  const sel = upper.state.kind === 'ok' ? upper.state.data.find((u) => u.id === selId) : undefined;

  return (
    <main className="page admin">
      <div className="admin-layout">
        <AdminNav current={cfg.kind} />
        <section className="col gap admin-panel">
          <div className="col"><h1 className="page-title">{cfg.title}</h1><small className="sub">{cfg.desc}</small></div>
          <div className="grid-2 align-start admin-base-grid">
            <div className="card">
              <b className="card-title">{cfg.upper} {upper.state.kind === 'ok' ? upper.state.data.length : ''}</b>
              <LoadBox load={upper.state} title={`${cfg.upper}를 불러오지 못했어요`} onRetry={() => upper.reload()}>
                {(items) => items.length === 0 ? <p className="sub">등록된 {cfg.upper}가 없어요.</p> : (
                  <div className="admin-list" tabIndex={0} role="region" aria-label={cfg.upper}>
                    {items.map((u) => (
                      <ItemRow key={u.id} cfg={cfg} item={u} active={u.id === selId} onSelect={() => setSelId(u.id)}
                        onSave={(n, c) => cfg.updateUpper(u.id, n, c)} onDelete={() => cfg.deleteUpper(u.id)} onDone={() => upper.reload(true).catch(() => undefined)} label={cfg.upper}
                        explainConflict={() => childrenLeft(cfg, u.id)} />
                    ))}
                  </div>
                )}
              </LoadBox>
              <AddForm cfg={cfg} title={`새 ${cfg.upper}`} onAdd={(n, c) => cfg.createUpper(n, c)} onDone={() => upper.reload(true).catch(() => undefined)} />
            </div>
            <div className="card">
              {sel ? (
                <>
                  <b className="card-title">{sel.name} · {cfg.lower} {lower.state.kind === 'ok' ? lower.state.data.length : ''}</b>
                  <small className="sub">{cfg.withCode ? '지역명과 코드 앞뒤의 공백은 자동으로 지워져요' : '이름 앞뒤의 공백은 자동으로 지워져요'}</small>
                  <LoadBox load={lower.state} title={`${cfg.lower}를 불러오지 못했어요`} onRetry={() => lower.reload()}>
                    {(items) => items.length === 0 ? <p className="sub">이 {cfg.upper}에는 아직 {cfg.lower}가 없어요.</p> : (
                      <div className="admin-list" tabIndex={0} role="region" aria-label={cfg.lower}>
                        {items.map((l) => (
                          <ItemRow key={l.id} cfg={cfg} item={l} onSave={(n, c) => cfg.updateLower(l.id, n, c, sel.id)} onDelete={() => cfg.deleteLower(l.id)}
                            onDone={() => lower.reload(true).catch(() => undefined)} label={cfg.lower} />
                        ))}
                      </div>
                    )}
                  </LoadBox>
                  <AddForm cfg={cfg} title={`${sel.name}에 ${cfg.lower} 추가`} onAdd={(n, c) => cfg.createLower(sel.id, n, c)} onDone={() => lower.reload(true).catch(() => undefined)} />
                </>
              ) : <p className="sub">{cfg.upper}를 골라 주세요.</p>}
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}

function AddForm({ cfg, title, onAdd, onDone }: { cfg: Config; title: string; onAdd: (n: string, c: string) => Promise<unknown>; onDone: () => void }) {
  const toast = useToast();
  const [name, setName] = useState('');
  const [code, setCode] = useState('');
  const [err, setErr] = useState('');
  const { busy, run } = useSingleFlight();
  const submit = (e: FormEvent) => {
    e.preventDefault();
    const bad = validate(cfg, name, code);
    if (bad) { setErr(bad); return; }
    run(async () => {
      try { await onAdd(name.trim(), code.trim()); toast(`${name.trim()}을(를) 등록했어요.`); setName(''); setCode(''); setErr(''); onDone(); }
      catch (e2) { setErr(`등록하지 못했어요.\n${friendly(e2)}`); }
    });
  };
  return (
    <form className="add-form" onSubmit={submit}>
      <small className="sub">{title} · 필수 · 최대 50자{cfg.withCode ? ' · 코드 숫자 10자리' : ''}</small>
      <div className="row admin-fields">
        <input className={`input sm${err ? ' bad' : ''}`} placeholder="이름" value={name} maxLength={60} onChange={(e) => { setName(e.target.value); setErr(''); }} disabled={busy} />
        {cfg.withCode && <input className={`input sm code${err ? ' bad' : ''}`} placeholder="코드 10자리" inputMode="numeric" value={code} onChange={(e) => { setCode(e.target.value.replace(/\D/g, '').slice(0, 10)); setErr(''); }} disabled={busy} />}
        <button type="submit" className="btn small primary" disabled={busy}>{busy ? <Spinner light /> : '등록'}</button>
      </div>
      {err && <span className="field-error"><Lines text={err} /></span>}
    </form>
  );
}

// 상위 항목 삭제가 원인 없이 거절됐을 때: 하위 항목이 실제로 남아 있으면 그 개수를 안내하고, 아니면 null
async function childrenLeft(cfg: Config, upperId: number): Promise<string | null> {
  try {
    const kids = await cfg.listLower(upperId);
    return kids.length > 0 ? `${cfg.lower} ${kids.length}개가 남아 있어 삭제할 수 없어요.\n${cfg.lower}를 먼저 정리해 주세요.` : null;
  } catch {
    return null;
  }
}

function ItemRow({ cfg, item, active, onSelect, onSave, onDelete, onDone, label, explainConflict }: {
  cfg: Config; item: Item; active?: boolean; onSelect?: () => void; label: string;
  onSave: (n: string, c: string) => Promise<unknown>; onDelete: () => Promise<unknown>; onDone: () => void;
  explainConflict?: () => Promise<string | null>;
}) {
  const toast = useToast();
  const [editing, setEditing] = useState(false);
  const [name, setName] = useState(item.name);
  const [code, setCode] = useState(item.code ?? '');
  const [err, setErr] = useState('');
  const [askDel, setAskDel] = useState(false);
  const [delErr, setDelErr] = useState('');
  const { busy, run } = useSingleFlight();

  const save = () => {
    const bad = validate(cfg, name, code);
    if (bad) { setErr(bad); return; }
    run(async () => {
      try { await onSave(name.trim(), code.trim()); toast('저장했어요.'); setEditing(false); setErr(''); onDone(); }
      catch (e) { setErr(`저장하지 못했어요.\n${friendly(e)}`); }
    });
  };
  const remove = () => run(async () => {
    try { await onDelete(); toast(`${item.name}을(를) 삭제했어요.`); setAskDel(false); onDone(); }
    catch (e) {
      const why = isUnexplainedConflict(e) && explainConflict ? await explainConflict() : null;
      setDelErr(`삭제하지 못했어요.\n${why ?? friendly(e)}`);
    }
  });

  if (editing) {
    return (
      <div className="admin-item editing">
        <div className="row admin-fields">
          <input className={`input sm${err ? ' bad' : ''}`} value={name} onChange={(e) => { setName(e.target.value); setErr(''); }} disabled={busy} aria-label={`${label} 이름`} />
          {cfg.withCode && <input className={`input sm code${err ? ' bad' : ''}`} value={code} inputMode="numeric" onChange={(e) => { setCode(e.target.value.replace(/\D/g, '').slice(0, 10)); setErr(''); }} disabled={busy} aria-label="지역 코드" />}
          <span className="admin-btns">
            <button type="button" className="btn small" disabled={busy} onClick={() => { setEditing(false); setName(item.name); setCode(item.code ?? ''); setErr(''); }}>취소</button>
            <button type="button" className="btn small primary" disabled={busy} onClick={save}>{busy ? <Spinner light /> : '저장'}</button>
          </span>
        </div>
        {err && <span className="field-error"><Lines text={err} /></span>}
      </div>
    );
  }
  return (
    <div className={`admin-item${active ? ' on' : ''}`}>
      <button type="button" className="admin-name" onClick={onSelect} disabled={!onSelect}>
        <span>{item.name}</span>{item.code && <small className="sub">{item.code}</small>}
      </button>
      <button type="button" className="mini" onClick={() => setEditing(true)}>수정</button>
      <button type="button" className="mini danger" onClick={() => { setDelErr(''); setAskDel(true); }}>삭제</button>
      {askDel && (
        <Modal title={`${item.name}을(를) 삭제할까요?`} onClose={() => !busy && setAskDel(false)}
          actions={<>
            <button type="button" className="btn" disabled={busy} onClick={() => setAskDel(false)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={busy} onClick={remove}>{busy ? <Spinner light /> : '삭제'}</button>
          </>}>
          <p><Lines text={`하위 ${cfg.kind === 'categories' ? '소분류' : '시군구'}가 있거나 다른 데이터가 쓰고 있으면 삭제할 수 없어요.\n삭제한 항목은 되돌릴 수 없어요.`} /></p>
          {delErr && <Banner><Lines text={delErr} /></Banner>}
        </Modal>
      )}
    </div>
  );
}
