// 공통 프로필 사진 카드 (내 정보 수정 · 고수 프로필 등록·수정)
// 회원 사진 하나를 마이페이지와 고수 프로필이 함께 쓴다
// 사진 변경·연결 해제는 폼 저장 버튼과 별개로 바로 반영된다 (팀 코드 MyInfoPage의 동작을 그대로 옮김)
import { useRef, useState, type ReactNode } from 'react';
import { ApiError, errorText } from '../api/client';
import * as api from '../api/endpoints';
import { useSession } from '../auth/session';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { PHOTO_ACCEPT, checkPhoto } from '../lib/policy';
import { useToast } from './toast';
import { Avatar, Banner, Lines, Modal, Spinner } from './ui';

export function ProfilePhotoCard({ title, desc, note, large = false }: { title: ReactNode; desc?: string; note?: ReactNode; large?: boolean }) {
  const { me, setMe, reloadMe } = useSession();
  const toast = useToast();
  const photo = useSingleFlight();
  const fileRef = useRef<HTMLInputElement>(null);
  const [photoMsg, setPhotoMsg] = useState<{ text: string; retry?: File } | null>(null);
  const [askDelete, setAskDelete] = useState(false);
  if (!me) return null;

  // 업로드 성공 → 회원 이미지 연결 성공까지 끝나야 변경 완료
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

  return (
    <section className={`card photo-card${large ? ' is-large' : ''}`}>
      <div className="row between">{title}{desc && <small className="sub">{desc}</small>}</div>
      <div className="photo-row">
        <span className="photo-wrap">
          <Avatar url={me.imageUrl} size={large ? 0 : 96} alt="프로필 사진" />
          {photo.busy && <span className="photo-busy"><Spinner light /></span>}
        </span>
        <div className="col gap-s">
          {note}
          <div className="row wrap">
            <button className={`btn${large && !me.imageUrl ? ' primary' : ''}`} type="button" disabled={photo.busy} onClick={() => fileRef.current?.click()}>
              {photo.busy ? <><Spinner />처리하는 중</> : (photoMsg?.retry ? '다른 사진 선택' : me.imageUrl ? '사진 변경' : '사진 올리기')}
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
      <Banner kind="info">사진 변경·삭제는 바로 반영돼요.<br />아래의 저장 버튼과는 별개예요.</Banner>

      {askDelete && (
        <Modal title="사진을 삭제할까요?" onClose={() => !photo.busy && setAskDelete(false)}
          actions={<>
            <button className="btn" disabled={photo.busy} onClick={() => setAskDelete(false)}>취소</button>
            <button className="btn danger" disabled={photo.busy} onClick={deletePhoto}>{photo.busy ? <><Spinner light />삭제하는 중</> : '삭제'}</button>
          </>}>
          <p>삭제하면 바로 기본 이미지로 바뀌어요.<br />마이페이지와 고수 프로필에 함께 반영돼요.</p>
        </Modal>
      )}
    </section>
  );
}
