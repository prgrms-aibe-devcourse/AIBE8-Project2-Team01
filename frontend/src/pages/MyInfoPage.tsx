// 내 정보 조회·수정 + 공통 프로필 사진 (와이어프레임 W·P11~P12 / M·P11~P12)
// 사진 변경·연결 해제는 저장 버튼과 별개로 바로 반영된다
// 회원 탈퇴는 이번 범위 밖이라 넣지 않는다
import { useRef, useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import * as api from '../api/endpoints';
import type { MemberUpdateRequest } from '../api/types';
import { useSession } from '../auth/session';
import { useToast } from '../components/toast';
import { Avatar, Banner, Field, Lines, Modal, Spinner } from '../components/ui';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { navigate } from '../hooks/useHashRoute';
import { NICKNAME_MAX, PASSWORD_MAX, PASSWORD_MIN, PHONE_RE, PHOTO_ACCEPT, checkPhoto } from '../lib/policy';

type Errs = Partial<Record<'nickname' | 'phone' | 'password' | 'password2', string>>;

export function MyInfoPage() {
  const { me, setMe, reloadMe } = useSession();
  const toast = useToast();
  const save = useSingleFlight();
  const photo = useSingleFlight();
  const fileRef = useRef<HTMLInputElement>(null);
  const [nickname, setNickname] = useState(me?.nickname ?? '');
  const [phone, setPhone] = useState(me?.phone ?? '');
  const [password, setPassword] = useState('');
  const [password2, setPassword2] = useState('');
  const [errs, setErrs] = useState<Errs>({});
  const [saveFail, setSaveFail] = useState('');
  const [photoMsg, setPhotoMsg] = useState<{ text: string; retry?: File } | null>(null);
  const [askDelete, setAskDelete] = useState(false);
  if (!me) return null;

  // ── 사진: 업로드 성공 → 회원 이미지 연결 성공까지 끝나야 변경 완료 ──
  const changePhoto = (file: File) => photo.run(async () => {
    setPhotoMsg(null);
    const bad = checkPhoto(file);
    if (bad === 'size') { setPhotoMsg({ text: '5MB를 넘는 사진은 올릴 수 없어요.\n기존 사진을 그대로 써요.' }); return; }
    if (bad === 'type') { setPhotoMsg({ text: 'JPG·JPEG·PNG·WebP 사진만 올릴 수 있어요.\n기존 사진을 그대로 써요.' }); return; }
    try {
      const uploaded = await api.uploadPublicImage(file);
      const updated = await api.setMyImage(uploaded.fileId);
      setMe(updated);
      toast('프로필 사진을 바꿨어요.');
    } catch (e) {
      if (e instanceof ApiError && e.status === 413) setPhotoMsg({ text: '5MB를 넘는 사진은 올릴 수 없어요.\n기존 사진을 그대로 써요.' });
      else if (e instanceof ApiError && e.status === 400) setPhotoMsg({ text: `${errorText(e)}\n기존 사진을 그대로 써요.` });
      else setPhotoMsg({ text: '사진을 올리지 못했어요.\n기존 사진은 그대로예요.\n다시 시도해 주세요.', retry: file });
      // 서버 상태와 화면을 맞춘다
      reloadMe().catch(() => undefined);
    }
  });

  const deletePhoto = () => photo.run(async () => {
    try {
      await api.unlinkMyImage();          // 204: 회원과 사진의 연결만 해제
      await reloadMe();
      setAskDelete(false);
      toast('기본 이미지로 바꿨어요.');
    } catch (e) {
      setAskDelete(false);
      setPhotoMsg({ text: `사진을 삭제하지 못했어요.\n기존 사진은 그대로예요.\n${errorText(e)}` });
    }
  });

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

      <section className="card">
        <div className="row between"><b className="card-title">프로필 사진</b><small className="sub">마이페이지·고수 프로필에 같은 사진이 보여요</small></div>
        <div className="photo-row">
          <span className="photo-wrap">
            <Avatar url={me.imageUrl} size={96} alt="프로필 사진" />
            {photo.busy && <span className="photo-busy"><Spinner light /></span>}
          </span>
          <div className="col">
            <div className="row wrap">
              <button className="btn" type="button" disabled={photo.busy} onClick={() => fileRef.current?.click()}>
                {photo.busy ? <><Spinner />처리하는 중</> : (photoMsg?.retry ? '다른 사진 선택' : '사진 변경')}
              </button>
              {photoMsg?.retry && <button className="btn primary" type="button" disabled={photo.busy} onClick={() => changePhoto(photoMsg.retry!)}>다시 시도</button>}
              {me.imageUrl && !photoMsg?.retry && <button className="btn danger-ghost" type="button" disabled={photo.busy} onClick={() => setAskDelete(true)}>사진 삭제</button>}
            </div>
            <small className="sub">JPG·JPEG·PNG·WebP · 5MB 이하</small>
          </div>
          <input ref={fileRef} type="file" accept={PHOTO_ACCEPT} hidden
            onChange={(e) => { const f = e.target.files?.[0]; e.target.value = ''; if (f) changePhoto(f); }} />
        </div>
        {photoMsg && <Banner><Lines text={photoMsg.text} /></Banner>}
        <Banner kind="info">사진 변경·삭제는 바로 반영돼요.<br />아래의 저장·취소 버튼과는 별개예요.</Banner>
      </section>

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

      <div className="bottom-bar">
        <div className="bar-inner">
          <a className="btn" href="#/mypage" aria-disabled={locked} onClick={(e) => locked && e.preventDefault()}>취소</a>
          <button className="btn primary" type="submit" form="info-form" disabled={locked}>
            {locked ? <><Spinner light />저장하는 중</> : '저장'}
          </button>
        </div>
      </div>

      {askDelete && (
        <Modal title="사진을 삭제할까요?" onClose={() => !photo.busy && setAskDelete(false)}
          actions={<>
            <button className="btn" disabled={photo.busy} onClick={() => setAskDelete(false)}>취소</button>
            <button className="btn danger" disabled={photo.busy} onClick={deletePhoto}>{photo.busy ? <><Spinner light />삭제하는 중</> : '삭제'}</button>
          </>}>
          <p>삭제하면 바로 기본 이미지로 바뀌어요.<br />고수 프로필에도 함께 반영돼요.</p>
        </Modal>
      )}
    </main>
  );
}
