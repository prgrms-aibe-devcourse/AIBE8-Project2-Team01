// 게시글 상세 · 댓글 (W·17) · 조회와 링크 복사는 공개, 좋아요·댓글은 로그인 필요
// 좋아요·댓글 수는 서버 응답 값으로 갱신한다
import { useEffect, useRef, useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import {
  createComment, deleteComment, deletePost, getComments, getPost, likeComment, likePost, updateComment, uploadPublicFiles,
  type Comment, type Post
} from '../api/mvp';
import { useSession } from '../auth/session';
import { currentRedirect } from '../components/Header';
import { Badge, Empty, Pager } from '../components/common';
import { useToast } from '../components/toast';
import { Avatar, Banner, ErrorView, Lines, LoadingView, Modal, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { BOARD_LABEL, shortDateTime } from '../lib/labels';
import { friendly } from '../lib/messages';
import { checkPostImage, POST_IMAGE_ACCEPT } from '../lib/postPolicy';

const C_MIN = 2, C_MAX = 400;

export function PostDetailPage({ id }: { id: number }) {
  const { status, me } = useSession();
  const toast = useToast();
  const post = useLoad(() => getPost(id), [id]);
  const [cPage, setCPage] = useState(1);
  const comments = useLoad(() => getComments(id, cPage), [id, cPage]);
  const like = useSingleFlight();
  const del = useSingleFlight();
  const [askDel, setAskDel] = useState(false);
  const login = () => navigate('/login', { redirect: currentRedirect() });

  const toggleLike = (p: Post) => {
    if (status !== 'member') { login(); return; }
    like.run(async () => {
      try {
        const r = await likePost(p.postId, !p.liked);
        post.set({ ...p, liked: r.liked, likeCount: r.likeCount });
      } catch (e) {
        toast(`좋아요를 ${p.liked ? '취소하지' : '누르지'} 못했어요.\n${friendly(e)}`, true);
        post.reload(true).catch(() => undefined);
      }
    });
  };
  const copyLink = async () => {
    const url = `${window.location.origin}${window.location.pathname}#/community/${id}`;
    try { await navigator.clipboard.writeText(url); toast('링크를 복사했어요.'); }
    catch { toast(`링크를 복사하지 못했어요.\n주소창의 주소를 직접 복사해 주세요.`, true); }
  };
  const doDelete = () => del.run(async () => {
    try {
      const board = post.state.kind === 'ok' ? post.state.data.boardType : 'QUESTION';
      await deletePost(id);
      toast('게시글을 삭제했어요.');
      navigate('/community', { board }, true);
    } catch (e) { setAskDel(false); toast(`삭제하지 못했어요.\n${friendly(e)}`, true); }
  });

  if (post.state.kind === 'loading') return <main className="page narrow"><LoadingView label="게시글을 불러오는 중이에요" /></main>;
  if (post.state.kind === 'error') {
    const nf = post.state.error instanceof ApiError && post.state.error.status === 404;
    return <main className="page narrow">{nf
      ? <Empty title="게시글을 찾을 수 없어요" desc="삭제되었거나 볼 수 없는 게시글이에요." action={<a className="btn" href="#/community">커뮤니티로</a>} />
      : <ErrorView title="게시글을 불러오지 못했어요" message={errorText(post.state.error)} onRetry={() => post.reload()} />}</main>;
  }
  const p = post.state.data;
  const mine = me?.id === p.memberId;

  return (
    <main className="page narrow">
      <a className="back" href={`#/community?board=${p.boardType}`}>‹ {BOARD_LABEL[p.boardType]} 게시판</a>
      <article className="card">
        <div className="row between">
          <span className="row"><Badge text={BOARD_LABEL[p.boardType]} />{!p.isVisible && <Badge text="숨김" tone="red" />}</span>
          {mine && <span className="row"><a className="btn small" href={`#/community/${id}/edit`}>수정</a><button type="button" className="btn small danger-ghost" onClick={() => setAskDel(true)}>삭제</button></span>}
        </div>
        <h1 className="title">{p.title}</h1>
        <div className="row">
          <Avatar url={p.memberProfileImageUrl} size={36} />
          <div className="col"><b>{p.memberNickname}</b><small className="sub">작성 {shortDateTime(p.createdAt)}{p.updatedAt && p.updatedAt !== p.createdAt ? ` · 수정 ${shortDateTime(p.updatedAt)}` : ''} · 조회 {p.viewCount.toLocaleString('ko-KR')}</small></div>
        </div>
        {(p.category || p.region) && <div className="chips">{p.category && <span className="chip">{p.category.secondCategoryName}</span>}{p.region && <span className="chip">{p.region.firstRegionName} {p.region.secondRegionName}</span>}</div>}
        <p className="body-text">{p.content}</p>
        {p.images.length > 0 && <div className="post-imgs">{p.images.map((img) => <img key={img.fileId} src={img.url} alt="첨부 사진" />)}</div>}
        <div className="row">
          <button type="button" className={`btn small like${p.liked ? ' on' : ''}`} aria-pressed={p.liked} disabled={like.busy} onClick={() => toggleLike(p)}>
            {like.busy ? <Spinner /> : <span aria-hidden="true">{p.liked ? '♥' : '♡'}</span>} 좋아요 {p.likeCount}
          </button>
          <button type="button" className="btn small" onClick={copyLink}>링크 복사</button>
        </div>
      </article>

      <section className="card">
        <b className="card-title">댓글 <span className="blue-t">{p.commentCount}</span></b>
        {status === 'member'
          ? <CommentForm postId={id} onDone={() => { setCPage(1); comments.reload(true).catch(() => undefined); post.reload(true).catch(() => undefined); }} />
          : <button type="button" className="btn" onClick={login}>로그인하고 댓글 쓰기</button>}
        {comments.state.kind === 'loading' && <LoadingView label="댓글을 불러오는 중이에요" />}
        {comments.state.kind === 'error' && <ErrorView title="댓글을 불러오지 못했어요" message={errorText(comments.state.error)} onRetry={() => comments.reload()} />}
        {comments.state.kind === 'ok' && (comments.state.data.content.length === 0 ? <p className="sub">아직 댓글이 없어요.<br />첫 댓글을 남겨 보세요.</p> : (
          <>
            <div className="col">
              {comments.state.data.content.map((c) => (
                <CommentItem key={c.commentId} c={c} mine={me?.id === c.memberId} loggedIn={status === 'member'} onLogin={login}
                  onChanged={() => { comments.reload(true).catch(() => undefined); post.reload(true).catch(() => undefined); }} />
              ))}
            </div>
            <Pager page={comments.state.data.pageNumber} totalPages={comments.state.data.totalPages} onPage={setCPage} />
          </>
        ))}
      </section>

      {askDel && (
        <Modal title="게시글을 삭제할까요?" onClose={() => !del.busy && setAskDel(false)}
          actions={<>
            <button type="button" className="btn" disabled={del.busy} onClick={() => setAskDel(false)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={del.busy} onClick={doDelete}>{del.busy ? <><Spinner light />삭제하는 중</> : '삭제'}</button>
          </>}>
          <p><Lines text={'삭제한 글은 되돌릴 수 없어요.\n댓글과 좋아요도 함께 보이지 않아요.'} /></p>
        </Modal>
      )}
    </main>
  );
}

// 댓글 작성·수정: 2~400자 · 사진 1장
function CommentForm({ postId, edit, onDone, onCancel }: { postId: number; edit?: Comment; onDone: () => void; onCancel?: () => void }) {
  const toast = useToast();
  const [text, setText] = useState(edit?.content ?? '');
  const [img, setImg] = useState<{ fileId: number; url: string } | null>(edit?.images[0] ?? null);
  const [err, setErr] = useState('');
  const up = useSingleFlight();
  const save = useSingleFlight();
  const fileRef = useRef<HTMLInputElement>(null);

  const pickFile = (f: File | undefined) => {
    if (!f) return;
    const bad = checkPostImage(f);
    if (bad) { setErr(bad); return; }
    up.run(async () => {
      try { const [r] = await uploadPublicFiles([f]); setImg({ fileId: r.fileId, url: r.url ?? "" }); setErr(''); }
      catch (e) { setErr(`사진을 올리지 못했어요.\n${friendly(e)}`); }
    });
  };
  const submit = (e: FormEvent) => {
    e.preventDefault();
    const content = text.trim();
    if (content.length < C_MIN || content.length > C_MAX) { setErr(`댓글은 ${C_MIN}~${C_MAX}자로 입력해 주세요.`); return; }
    save.run(async () => {
      try {
        const body = { content, imageIds: img ? [img.fileId] : [] };
        if (edit) await updateComment(edit.commentId, body); else await createComment(postId, body);
        toast(edit ? '댓글을 수정했어요.' : '댓글을 등록했어요.');
        if (!edit) { setText(''); setImg(null); }
        setErr(''); onDone();
      } catch (e2) { setErr(`댓글을 ${edit ? '수정하지' : '등록하지'} 못했어요.\n${friendly(e2)}`); }
    });
  };
  return (
    <form className="comment-form" onSubmit={submit}>
      <textarea className={`input area compact${err ? ' bad' : ''}`} placeholder={`댓글은 ${C_MIN}~${C_MAX}자로 입력해 주세요.`} value={text} disabled={save.busy}
        onChange={(e) => { setText(e.target.value); setErr(''); }} />
      {img && <div className="thumb-row"><span className="thumb"><img src={img.url} alt="첨부 사진" /><button type="button" className="thumb-x" aria-label="사진 빼기" onClick={() => setImg(null)}>×</button></span></div>}
      {err && <Banner><Lines text={err} /></Banner>}
      <div className="row between">
        <span className="row">
          <input ref={fileRef} type="file" hidden accept={POST_IMAGE_ACCEPT} onChange={(e) => { pickFile(e.target.files?.[0]); e.target.value = ''; }} />
          <button type="button" className="btn small" disabled={up.busy || save.busy || !!img} onClick={() => fileRef.current?.click()}>{up.busy ? <><Spinner />올리는 중</> : '사진 1장'}</button>
          <small className={`sub${text.trim().length > C_MAX ? ' err-t' : ''}`}>{text.trim().length}/{C_MAX}</small>
        </span>
        <span className="row">
          {onCancel && <button type="button" className="btn small" disabled={save.busy} onClick={onCancel}>취소</button>}
          <button type="submit" className="btn small primary" disabled={save.busy || up.busy}>{save.busy ? <Spinner light /> : edit ? '저장' : '등록'}</button>
        </span>
      </div>
    </form>
  );
}

function CommentItem({ c, mine, loggedIn, onLogin, onChanged }: { c: Comment; mine: boolean; loggedIn: boolean; onLogin: () => void; onChanged: () => void }) {
  const toast = useToast();
  const [cur, setCur] = useState(c);
  // 목록을 다시 불러오면 서버 값으로 맞춘다 (수정·좋아요 결과 반영)
  useEffect(() => { setCur(c); }, [c]);
  const [editing, setEditing] = useState(false);
  const [askDel, setAskDel] = useState(false);
  const like = useSingleFlight();
  const del = useSingleFlight();
  const toggle = () => {
    if (!loggedIn) { onLogin(); return; }
    like.run(async () => {
      try { const r = await likeComment(cur.commentId, !cur.liked); setCur({ ...cur, liked: r.liked, likeCount: r.likeCount }); }
      catch (e) { toast(`좋아요를 ${cur.liked ? '취소하지' : '누르지'} 못했어요.\n${friendly(e)}`, true); }
    });
  };
  const remove = () => del.run(async () => {
    try { await deleteComment(cur.commentId); setAskDel(false); toast('댓글을 삭제했어요.'); onChanged(); }
    catch (e) { setAskDel(false); toast(`댓글을 삭제하지 못했어요.\n${friendly(e)}`, true); }
  });
  if (editing) return <CommentForm postId={0} edit={cur} onDone={() => { setEditing(false); onChanged(); }} onCancel={() => setEditing(false)} />;
  return (
    <div className="comment">
      <Avatar url={cur.memberProfileImageUrl} size={36} />
      <div className="col grow">
        <span className="row"><b>{cur.memberNickname}</b>{cur.isExpert && <Badge text="고수" tone="teal" />}<small className="sub">{shortDateTime(cur.createdAt)}</small></span>
        <p className="body-text sm">{cur.content}</p>
        {cur.images[0] && <img className="comment-img" src={cur.images[0].url} alt="댓글 사진" />}
        <span className="row">
          <button type="button" className={`link-btn inline${cur.liked ? ' on' : ''}`} aria-pressed={cur.liked} disabled={like.busy} onClick={toggle}>{cur.liked ? '♥' : '♡'} 좋아요 {cur.likeCount}</button>
          {mine && <><button type="button" className="link-btn inline" onClick={() => setEditing(true)}>수정</button><button type="button" className="link-btn inline" onClick={() => setAskDel(true)}>삭제</button></>}
        </span>
      </div>
      {askDel && (
        <Modal title="댓글을 삭제할까요?" onClose={() => !del.busy && setAskDel(false)}
          actions={<>
            <button type="button" className="btn" disabled={del.busy} onClick={() => setAskDel(false)}>돌아가기</button>
            <button type="button" className="btn danger" disabled={del.busy} onClick={remove}>{del.busy ? <Spinner light /> : '삭제'}</button>
          </>}>
          <p>삭제한 댓글은 되돌릴 수 없어요.</p>
        </Modal>
      )}
    </div>
  );
}
