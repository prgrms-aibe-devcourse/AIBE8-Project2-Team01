// 관리자 · 회원 관리 (A1·A2) · 탈퇴하지 않은 회원 10명씩 · 정렬 · 역할 변경 · 탈퇴
// 본인 관리자 권한은 바꿀 수 없고, 고수로 바꾸려면 고수 프로필이 있어야 한다(서버 검증)
import { useState } from 'react';
import { adminDeleteMember, adminGetMembers, adminSetRole } from '../api/mvp';
import type { Member, MemberRole } from '../api/types';
import { useSession } from '../auth/session';
import { LoadBox, Pager } from '../components/common';
import { RoleSelect } from '../components/RoleSelect';
import { useToast } from '../components/toast';
import { Banner, Lines, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { formatPhone, roleLabel } from '../lib/format';
import { friendly } from '../lib/messages';
import { AdminNav } from './AdminBaseDataPages';

const SORTS: { key: string; label: string }[] = [{ key: 'id', label: 'ID' }, { key: 'nickname', label: '닉네임' }, { key: 'email', label: '이메일' }, { key: 'createdAt', label: '가입일' }];
const SORT_OPTIONS = SORTS.map(({ key, label }) => ({ value: key, label }));
const ORDER_OPTIONS = [{ value: 'asc', label: '오름차순' }, { value: 'desc', label: '내림차순' }];
const ROLE_OPTIONS = (['USER', 'EXPERT', 'ADMIN'] as MemberRole[]).map((r) => ({ value: r, label: roleLabel(r) }));

export function AdminMembersPage({ params }: { params: URLSearchParams }) {
  const { me } = useSession();
  const toast = useToast();
  const page = Math.max(1, Number(params.get('page') ?? 1) || 1);
  const sortBy = SORTS.some((s) => s.key === params.get('sort')) ? params.get('sort')! : 'id';
  const order = params.get('order') === 'desc' ? 'desc' : 'asc';
  const list = useLoad(() => adminGetMembers(page, sortBy, order), [page, sortBy, order]);
  const go = (p: number, s = sortBy, o = order) => navigate('/admin/members', { page: p, sort: s, order: o });
  const [picked, setPicked] = useState<Record<number, MemberRole>>({});
  const [act, setAct] = useState<{ kind: 'role' | 'delete'; m: Member; role?: MemberRole } | null>(null);
  const [err, setErr] = useState('');
  const { busy, run } = useSingleFlight();

  const doAct = () => act && run(async () => {
    try {
      if (act.kind === 'role') { await adminSetRole(act.m.id, act.role!); toast(`${act.m.nickname}님의 역할을 ${roleLabel(act.role!)}(으)로 바꿨어요.`); }
      else { await adminDeleteMember(act.m.id); toast(`${act.m.nickname}님을 탈퇴 처리했어요.`); }
      setAct(null);
      setPicked((p) => { const n = { ...p }; delete n[act.m.id]; return n; });
      await list.reload(true).catch(() => undefined);
    } catch (e) { setErr(`${act.kind === 'role' ? '역할을 바꾸지' : '탈퇴 처리하지'} 못했어요.\n${friendly(e)}`); }
  });

  return (
    <main className="page admin admin-members">
      <div className="admin-layout">
        <AdminNav current="members" />
        <section className="col gap admin-panel">
          <div className="row between wrap">
            <div className="col"><h1 className="page-title">회원 관리</h1><small className="sub">탈퇴하지 않은 회원만 보여요{list.state.kind === 'ok' ? ` · 총 ${list.state.data.totalElements}명` : ''}</small></div>
            <div className="row admin-sort-controls">
              <span className="sub">정렬</span>
              <RoleSelect value={sortBy} options={SORT_OPTIONS} label="정렬 기준" onChange={(value) => go(1, value)} />
              <RoleSelect value={order} options={ORDER_OPTIONS} label="정렬 방향" onChange={(value) => go(1, sortBy, value)} />
            </div>
          </div>
          <div className="col gap">
          <LoadBox load={list.state} title="회원 목록을 불러오지 못했어요" onRetry={() => list.reload()}>
            {(data) => (
              <>
                <div className="card table-card">
                  <div className="admin-table-scroll" tabIndex={0} role="region" aria-label="관리 목록">
                    <table className="tbl stack-m member-tbl">
                      <thead><tr><th>ID</th><th>이메일</th><th>닉네임</th><th>전화번호</th><th>역할</th><th>관리</th></tr></thead>
                      <tbody>
                        {data.content.map((m) => {
                          const self = m.id === me?.id;
                          const sel = picked[m.id] ?? m.role;
                          return (
                            <tr key={m.id}>
                              <td>{m.id}</td><td className="ellipsis-c">{m.email}</td>
                              <td>{m.nickname}{self && <small className="sub"> (나)</small>}</td>
                              <td>{formatPhone(m.phone)}</td>
                              <td><span className={`role-tag ${m.role === 'EXPERT' ? 'expert' : m.role === 'ADMIN' ? 'admin' : 'user'}`}>{roleLabel(m.role)}</span></td>
                              <td className="act-c">
                                {self ? <small className="sub">본인 권한은 바꿀 수 없어요</small> : (
                                  <span className="row">
                                    <RoleSelect value={sel} current={m.role} options={ROLE_OPTIONS} label={`${m.nickname} 역할 선택`} onChange={(r) => setPicked((p) => ({ ...p, [m.id]: r }))} />
                                    <button type="button" className="btn small" disabled={sel === m.role} onClick={() => { setErr(''); setAct({ kind: 'role', m, role: sel }); }}>변경</button>
                                    <button type="button" className="btn small danger-ghost" onClick={() => { setErr(''); setAct({ kind: 'delete', m }); }}>탈퇴</button>
                                  </span>
                                )}
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                </div>
                <Pager page={data.pageNumber} totalPages={data.totalPages} onPage={(p) => go(p)} />
                <small className="sub center">10명씩 · {data.pageNumber} / {Math.max(1, data.totalPages)} 페이지</small>
              </>
            )}
          </LoadBox>
          </div>
        </section>
      </div>
      {act && (
        <Modal title={act.kind === 'role' ? '역할을 바꿀까요?' : '회원을 탈퇴 처리할까요?'} onClose={() => !busy && setAct(null)}
          actions={<>
            <button type="button" className="btn" disabled={busy} onClick={() => setAct(null)}>돌아가기</button>
            <button type="button" className={`btn ${act.kind === 'delete' ? 'danger' : 'primary'}`} disabled={busy} onClick={doAct}>{busy ? <Spinner light /> : act.kind === 'role' ? '변경' : '탈퇴 처리'}</button>
          </>}>
          <p><Lines text={act.kind === 'role'
            ? `${act.m.nickname}님의 역할을 ${roleLabel(act.m.role)}에서 ${roleLabel(act.role!)}(으)로 바꿔요.\n바꾼 역할은 다음 요청부터 적용돼요.${act.role === 'EXPERT' ? '\n고수로 바꾸려면 고수 프로필이 있어야 해요.' : ''}`
            : `${act.m.nickname}님을 탈퇴 처리하면 로그인할 수 없고 되돌릴 수 없어요.`} /></p>
          {err && <Banner><Lines text={err} /></Banner>}
        </Modal>
      )}
    </main>
  );
}
