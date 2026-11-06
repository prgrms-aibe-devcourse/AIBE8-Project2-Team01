// 게시글 작성·수정 (W·16) · 로그인 필요 · 팁·홍보는 고수만 · 팁은 사진 1장 이상
// 사진은 공통 업로드(공개)로 올린 뒤 fileId를 본문에 담는다. 실패하면 입력값을 유지한다
import { useEffect, useRef, useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import * as api from '../api/endpoints';
import { createPost, getPost, updatePost, uploadPublicFiles, type BoardType, type PostImage } from '../api/mvp';
import type { FirstCategory, FirstRegion, SecondCategory, SecondRegion } from '../api/types';
import { useSession } from '../auth/session';
import { Empty, PageHead } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, ErrorView, Field, Lines, LoadingView, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { BOARDS, BOARD_LABEL, EXPERT_ONLY_BOARDS } from '../lib/labels';
import { friendly } from '../lib/messages';
import { checkPostImage, CONTENT_MAX, CONTENT_MIN, POST_IMAGE_ACCEPT, POST_IMAGE_MAX, TITLE_MAX, TITLE_MIN } from '../lib/postPolicy';

export function PostFormPage({ board: initBoard, postId }: { board?: string | null; postId?: number }) {
  const { me } = useSession();
  const toast = useToast();
  const isExpert = me?.role === 'EXPERT';
  const edit = !!postId;
  const [loadState, setLoadState] = useState<'loading' | 'ok' | 'error' | 'notmine'>(edit ? 'loading' : 'ok');
  const [loadErr, setLoadErr] = useState<unknown>(null);
  const [board, setBoard] = useState<BoardType>(() => {
    const b = initBoard as BoardType;
    return BOARDS.includes(b) && (isExpert || !EXPERT_ONLY_BOARDS.includes(b)) ? b : 'QUESTION';
  });
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [images, setImages] = useState<PostImage[]>([]);
  const [fc, setFc] = useState<number | null>(null);
  const [sc, setSc] = useState<number | null>(null);
  const [fr, setFr] = useState<number | null>(null);
  const [sr, setSr] = useState<number | null>(null);
  const [firstCats, setFirstCats] = useState<FirstCategory[]>([]);
  const [subCats, setSubCats] = useState<SecondCategory[]>([]);
  const [firstRegions, setFirstRegions] = useState<FirstRegion[]>([]);
  const [subRegions, setSubRegions] = useState<SecondRegion[]>([]);
  const [errs, setErrs] = useState<Record<string, string>>({});
  const [fail, setFail] = useState('');
  const save = useSingleFlight();
  const up = useSingleFlight();
  const fileRef = useRef<HTMLInputElement>(null);

  useEffect(() => { api.getFirstCategories().then(setFirstCats).catch(() => undefined); api.getFirstRegions().then(setFirstRegions).catch(() => undefined); }, []);
  useEffect(() => { if (fc) api.getSecondCategories(fc).then(setSubCats).catch(() => setSubCats([])); else setSubCats([]); }, [fc]);
  useEffect(() => { if (fr) api.getSecondRegions(fr).then(setSubRegions).catch(() => setSubRegions([])); else setSubRegions([]); }, [fr]);

  const load = () => {
    if (!postId) return;
    setLoadState('loading');
    getPost(postId).then((p) => {
      if (p.memberId !== me?.id) { setLoadState('notmine'); return; }
      setBoard(p.boardType); setTitle(p.title); setContent(p.content); setImages(p.images);
      setFc(p.category?.firstCategoryId ?? null); setSc(p.category?.secondCategoryId ?? null);
      setFr(p.region?.firstRegionId ?? null); setSr(p.region?.secondRegionId ?? null);
      setLoadState('ok');
    }).catch((e) => { setLoadErr(e); setLoadState('error'); });
  };
  useEffect(load, [postId]); // eslint-disable-line react-hooks/exhaustive-deps

  const addFiles = (files: FileList | null) => {
    if (!files?.length) return;
    const list = Array.from(files);
    if (images.length + list.length > POST_IMAGE_MAX) { setErrs((x) => ({ ...x, images: `사진은 최대 ${POST_IMAGE_MAX}장까지 올릴 수 있어요.` })); return; }
    for (const f of list) { const bad = checkPostImage(f); if (bad) { setErrs((x) => ({ ...x, images: bad })); return; } }
    up.run(async () => {
      try {
        const res = await uploadPublicFiles(list);
        setImages((cur) => [...cur, ...res.map((r) => ({ fileId: r.fileId, url: r.url ?? "" }))]);
        setErrs((x) => ({ ...x, images: '' }));
      } catch (e) { setErrs((x) => ({ ...x, images: `사진을 올리지 못했어요.\n${friendly(e)}` })); }
    });
  };

  const submit = (e: FormEvent) => {
    e.preventDefault();
    const t = title.trim(); const c = content.trim();
    const next: Record<string, string> = {};
    if (t.length < TITLE_MIN || t.length > TITLE_MAX) next.title = `제목은 ${TITLE_MIN}~${TITLE_MAX}자로 입력해 주세요.`;
    if (c.length < CONTENT_MIN || c.length > CONTENT_MAX) next.content = `본문은 ${CONTENT_MIN}~${CONTENT_MAX.toLocaleString('ko-KR')}자로 입력해 주세요.`;
    if (board === 'TIP' && images.length === 0) next.images = '팁 글은 사진을 1장 이상 첨부해 주세요.';
    if (EXPERT_ONLY_BOARDS.includes(board) && !isExpert) next.board = '팁·홍보 글은 고수만 쓸 수 있어요.';
    if (fc && !sc) next.cat = '세부 서비스까지 골라 주세요.';
    if (fr && !sr) next.region = '시·군·구까지 골라 주세요.';
    setErrs(next); setFail('');
    if (Object.keys(next).length) return;
    const body = { title: t, content: c, secondCategoryId: sc, secondRegionId: sr, imageIds: images.map((i) => i.fileId) };
    save.run(async () => {
      try {
        const p = edit ? await updatePost(postId!, body) : await createPost({ ...body, boardType: board });
        toast(edit ? '게시글을 저장했어요.' : '게시글을 등록했어요.');
        navigate(`/community/${p.postId}`, undefined, true);
      } catch (err) {
        if (err instanceof ApiError && err.status === 400 && Object.keys(err.fieldErrors).length) setFail(Object.values(err.fieldErrors).join('\n'));
        else setFail(`게시글을 ${edit ? '저장하지' : '등록하지'} 못했어요.\n입력한 내용과 사진은 그대로 남아 있어요.\n${friendly(err)}`);
      }
    });
  };

  if (loadState === 'loading') return <main className="page narrow"><LoadingView label="게시글을 불러오는 중이에요" /></main>;
  if (loadState === 'error') return <main className="page narrow"><ErrorView title="게시글을 불러오지 못했어요" message={errorText(loadErr)} onRetry={load} /></main>;
  if (loadState === 'notmine') return <main className="page narrow"><Empty title="본인 글만 수정할 수 있어요" action={<a className="btn" href={`#/community/${postId}`}>게시글로</a>} /></main>;

  const busy = save.busy || up.busy;
  return (
    <main className="page narrow with-bar">
      <PageHead title={edit ? '게시글 수정' : '게시글 작성'} back={edit ? { href: `#/community/${postId}`, label: '게시글' } : { href: `#/community?board=${board}`, label: '커뮤니티' }} sub={edit ? '기존 내용을 불러왔어요.' : undefined} />
      <form id="post-form" className="card" onSubmit={submit} noValidate>
        <fieldset className="col gap" disabled={save.busy} style={{ border: 0, padding: 0, margin: 0 }}>
          <Field label="게시판" hint={edit ? '게시판은 수정할 수 없어요' : isExpert ? undefined : '팁·홍보는 고수만 쓸 수 있어요'} error={errs.board}>
            <div className="tabs">
              {BOARDS.map((b) => {
                const locked = edit ? b !== board : EXPERT_ONLY_BOARDS.includes(b) && !isExpert;
                return <button type="button" key={b} className={`tab${b === board ? ' on' : ''}`} disabled={locked} onClick={() => !locked && setBoard(b)}>{BOARD_LABEL[b]}</button>;
              })}
            </div>
          </Field>
          <Field label="제목" hint={`${title.trim().length} / ${TITLE_MAX}`} htmlFor="pf-title" error={errs.title}>
            <input id="pf-title" className={`input${errs.title ? ' bad' : ''}`} value={title} maxLength={TITLE_MAX + 20} onChange={(e) => setTitle(e.target.value)} />
          </Field>
          <Field label="본문" hint={`${content.trim().length.toLocaleString('ko-KR')} / ${CONTENT_MAX.toLocaleString('ko-KR')}`} htmlFor="pf-content" error={errs.content}>
            <textarea id="pf-content" className={`input area tall${errs.content ? ' bad' : ''}`} value={content} onChange={(e) => setContent(e.target.value)} />
          </Field>
          <div className="grid-2">
            <Field label="서비스" hint="선택" error={errs.cat}>
              <div className="row">
                <select className="input" value={fc ?? ''} onChange={(e) => { setFc(e.target.value ? Number(e.target.value) : null); setSc(null); }}>
                  <option value="">지정 안 함</option>{firstCats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
                <select className="input" value={sc ?? ''} disabled={!fc} onChange={(e) => setSc(e.target.value ? Number(e.target.value) : null)}>
                  <option value="">세부 선택</option>{subCats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                </select>
              </div>
            </Field>
            <Field label="지역" hint="선택" error={errs.region}>
              <div className="row">
                <select className="input" value={fr ?? ''} onChange={(e) => { setFr(e.target.value ? Number(e.target.value) : null); setSr(null); }}>
                  <option value="">지정 안 함</option>{firstRegions.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
                </select>
                <select className="input" value={sr ?? ''} disabled={!fr} onChange={(e) => setSr(e.target.value ? Number(e.target.value) : null)}>
                  <option value="">시·군·구</option>{subRegions.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
                </select>
              </div>
            </Field>
          </div>
          <Field label="사진" hint={`${board === 'TIP' ? '팁은 1장 이상 · ' : ''}최대 ${POST_IMAGE_MAX}장 · 5MB · JPG·PNG·GIF·WebP`} error={errs.images}>
            <div className="thumb-row">
              {images.map((img) => (
                <span className="thumb" key={img.fileId}><img src={img.url} alt="첨부 사진" /><button type="button" className="thumb-x" aria-label="사진 빼기" onClick={() => setImages((cur) => cur.filter((x) => x.fileId !== img.fileId))}>×</button></span>
              ))}
              {images.length < POST_IMAGE_MAX && (
                <button type="button" className="thumb add" disabled={busy} onClick={() => fileRef.current?.click()}>{up.busy ? <Spinner /> : '+'}</button>
              )}
              <input ref={fileRef} type="file" hidden multiple accept={POST_IMAGE_ACCEPT} onChange={(e) => { addFiles(e.target.files); e.target.value = ''; }} />
            </div>
            <small className="sub">{images.length} / {POST_IMAGE_MAX}{edit ? ' · 기존 사진을 유지·삭제·추가할 수 있어요.' : ''}</small>
          </Field>
        </fieldset>
        {fail && <Banner><Lines text={fail} /></Banner>}
      </form>
      <div className="bottom-bar">
        <div className="bar-inner">
          <a className="btn" href={edit ? `#/community/${postId}` : `#/community?board=${board}`} aria-disabled={save.busy} onClick={(e) => save.busy && e.preventDefault()}>취소</a>
          <button className="btn primary" type="submit" form="post-form" disabled={busy}>{save.busy ? <><Spinner light />{edit ? '저장하는 중' : '등록하는 중'}</> : edit ? '저장' : '등록'}</button>
        </div>
      </div>
    </main>
  );
}
