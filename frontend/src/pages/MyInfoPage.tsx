// 내 정보 조회·수정 + 공통 프로필 사진 (와이어프레임 W·P11~P12 / M·P11~P12)
// 사진 변경·연결 해제는 저장 버튼과 별개로 바로 반영된다
// 회원 탈퇴는 이번 범위 밖이라 넣지 않는다
import { useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import * as api from '../api/endpoints';
import type { MemberUpdateRequest } from '../api/types';
import { useSession } from '../auth/session';
import { useToast } from '../components/toast';
import { Banner, Field, Lines, Spinner } from '../components/ui';
import { ProfilePhotoCard } from '../components/ProfilePhotoCard';
import { deleteMe } from '../api/mvp';
import { Modal } from '../components/ui';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { navigate } from '../hooks/useHashRoute';
import { NICKNAME_MAX, PASSWORD_MAX, PASSWORD_MIN, PHONE_RE } from '../lib/policy';

type Errs = Partial<Record<'nickname' | 'phone' | 'password' | 'password2', string>>;

export function MyInfoPage() {
  const { me, setMe, reloadMe } = useSession();
  const toast = useToast();
  const save = useSingleFlight();
  const [nickname, setNickname] = useState(me?.nickname ?? '');
  const [phone, setPhone] = useState(me?.phone ?? '');
  const [password, setPassword] = useState('');
  const [password2, setPassword2] = useState('');
  const [errs, setErrs] = useState<Errs>({});
  const [saveFail, setSaveFail] = useState('');
  if (!me) return null;

  // ── 회원 정보 저장: 바뀐 필드만 보낸다 (비밀번호는 입력했을 때만) ──
  const submit = (e: FormEvent) => {
    e.preventDefault();
    const nick = nickname.trim();
    const ph = phone.trim();
    const next: Errs = {};
    if (nick.length < 1 || nick.length > NICKNAME_MAX) next.nickname = '닉네임은 1~20자로 입력해 주세요.';
    if (ph !== me.phone && !PHONE_RE.test(ph)) next.phone = '전화번호는 숫자 11자리로 입력해 주세요.';
    if (password) {
      if (password.length < PASSWORD_MIN || password.length > PASSWORD_MAX) next.password = '비밀번호는 8~20자로 입력해 주세요.';
      else if (password !== password2) next.password2 = '새 비밀번호가 서로 달라요.';
    }
    setErrs(next); setSaveFail('');
    if (Object.keys(next).length) return;

    const body: MemberUpdateRequest = {};
    if (nick !== me.nickname) body.nickname = nick;
    if (ph !== me.phone) body.phone = ph;
    if (password) body.password = password;
    if (Object.keys(body).length === 0) { navigate('/mypage'); return; }

    save.run(async () => {
      try {
        const updated = await api.updateMe(body);
        setMe(updated);
        toast('내 정보를 저장했어요.');
        navigate('/mypage');
      } catch (err) {
        if (err instanceof ApiError && err.status === 409) {
          if (err.message.includes('닉네임')) setErrs({ nickname: '이미 사용 중인 닉네임이에요.' });
          else if (err.message.includes('전화번호')) setErrs({ phone: '이미 사용 중인 전화번호예요.' });
          else setSaveFail(errorText(err));
        } else if (err instanceof ApiError && err.status === 400 && Object.keys(err.fieldErrors).length) {
          setErrs(err.fieldErrors as Errs);
        } else {
          setSaveFail(`내 정보를 저장하지 못했어요.\n입력한 내용은 그대로 남아 있어요.\n${errorText(err)}`);
        }
      }
    });
  };

  const locked = save.busy;
  return (
    <main className="page narrow with-bar">
      <a className="back" href="#/mypage">‹ 마이페이지</a>
      <h1 className="page-title">내 정보 수정</h1>

      <ProfilePhotoCard title={<b className="card-title">프로필 사진</b>} desc="마이페이지·고수 프로필에 같은 사진이 보여요" />

      <form className="card" onSubmit={submit} noValidate id="info-form">
        <Field label="이메일" hint="변경할 수 없어요"><input className="input" value={me.email} readOnly disabled /></Field>
        <Field label="닉네임" hint="1~20자" htmlFor="nickname" error={errs.nickname}>
          <input id="nickname" className={`input${errs.nickname ? ' bad' : ''}`} value={nickname} maxLength={40}
            onChange={(e) => setNickname(e.target.value)} disabled={locked} />
        </Field>
        <Field label="전화번호" hint="숫자 11자리" htmlFor="phone" error={errs.phone}>
          <input id="phone" className={`input${errs.phone ? ' bad' : ''}`} value={phone} inputMode="numeric" maxLength={11}
            onChange={(e) => setPhone(e.target.value.replace(/\D/g, ''))} disabled={locked} />
        </Field>
        <Field label="새 비밀번호" hint="선택 · 8~20자" htmlFor="pw" error={errs.password}>
          <input id="pw" type="password" autoComplete="new-password" className={`input${errs.password ? ' bad' : ''}`} value={password}
            placeholder="변경할 때만 입력" onChange={(e) => setPassword(e.target.value)} disabled={locked} />
        </Field>
        {password && (
          <Field label="새 비밀번호 확인" htmlFor="pw2" error={errs.password2}>
            <input id="pw2" type="password" autoComplete="new-password" className={`input${errs.password2 ? ' bad' : ''}`} value={password2}
              onChange={(e) => setPassword2(e.target.value)} disabled={locked} />
          </Field>
        )}
        {saveFail && <Banner><Lines text={saveFail} /></Banner>}
      </form>

      <WithdrawCard />

      <div className="bottom-bar">
        <div className="bar-inner">
          <a className="btn" href="#/mypage" aria-disabled={locked} onClick={(e) => locked && e.preventDefault()}>취소</a>
          <button className="btn primary" type="submit" form="info-form" disabled={locked}>
            {locked ? <><Spinner light />저장하는 중</> : '저장'}
          </button>
        </div>
      </div>

    </main>
  );
}

// 회원 탈퇴 (DELETE /api/members/me · 204)
// 서버가 리프레시 쿠키를 만료시키고 화면은 로그아웃 상태로 돌아간다
function WithdrawCard() {
  const { me, logout } = useSession();
  const toast = useToast();
  const { busy, run } = useSingleFlight();
  const [ask, setAsk] = useState(false);
  const [agree, setAgree] = useState(false);
  const [fail, setFail] = useState('');
  if (!me) return null;
  const doIt = () => run(async () => {
    setFail('');
    try {
      await deleteMe();
      await logout().catch(() => undefined);
      toast('탈퇴했어요.\n그동안 이용해 주셔서 고마워요.');
      navigate('/', undefined, true);
    } catch (e) {
      setFail(`탈퇴하지 못했어요.\n${errorText(e)}`);
    }
  });
  return (
    <section className="card withdraw">
      <div className="row between">
        <div className="col">
          <b className="card-title">회원 탈퇴</b>
          <small className="sub">탈퇴하면 같은 계정으로 다시 로그인할 수 없어요.</small>
        </div>
        <button type="button" className="btn danger-ghost small" onClick={() => { setAgree(false); setFail(''); setAsk(true); }}>탈퇴하기</button>
      </div>
      {ask && (
        <Modal title="정말 탈퇴할까요?" onClose={() => !busy && setAsk(false)}
          actions={<>
            <button type="button" className="btn" disabled={busy} onClick={() => setAsk(false)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={busy || !agree} onClick={doIt}>{busy ? <><Spinner light />탈퇴하는 중</> : '탈퇴'}</button>
          </>}>
          <p><Lines text={`${me.nickname}님의 계정을 탈퇴 처리해요.\n탈퇴하면 되돌릴 수 없고 다시 로그인할 수 없어요.${me.role === 'EXPERT' ? '\n고수 프로필도 더 이상 보이지 않아요.' : ''}`} /></p>
          <label className="check-row"><input type="checkbox" checked={agree} onChange={(e) => setAgree(e.target.checked)} /> 안내를 확인했고 탈퇴에 동의해요</label>
          {fail && <Banner><Lines text={fail} /></Banner>}
        </Modal>
      )}
    </section>
  );
}
