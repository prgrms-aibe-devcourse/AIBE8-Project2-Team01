// 요청서 작성 (W·02 / M·02) · 고객 전용
// 서비스(대분류→소분류)·지역(시도→시군구)·질문 답변 → POST /api/requests
// 작성 취소는 홈으로, 발송 성공은 내 요청서로 이동한다
import { useEffect, useMemo, useState, type FormEvent } from 'react';
import { ApiError } from '../api/client';
import * as api from '../api/endpoints';
import { createServiceRequest, getQuestions } from '../api/mvp';
import type { SecondCategory, SecondRegion } from '../api/types';
import { LoadBox, PageHead } from '../components/common';
import { useToast } from '../components/toast';
import { Banner, Field, Lines, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useLoad } from '../hooks/useLoad';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { friendly } from '../lib/messages';

const ANSWER_MAX = 500;

export function RequestFormPage({ fc, cat }: { fc: string | null; cat: string | null }) {
  const toast = useToast();
  const { busy, run } = useSingleFlight();
  const base = useLoad(async () => {
    const [questions, firstCats, firstRegions] = await Promise.all([getQuestions(), api.getFirstCategories(), api.getFirstRegions()]);
    return { questions: [...questions].sort((a, b) => a.displayOrder - b.displayOrder), firstCats, firstRegions };
  }, []);

  const [firstCatId, setFirstCatId] = useState<number | null>(fc ? Number(fc) : null);
  const [secondCatId, setSecondCatId] = useState<number | null>(cat ? Number(cat) : null);
  const [subCats, setSubCats] = useState<SecondCategory[] | 'loading' | 'error'>('loading');
  const [firstRegionId, setFirstRegionId] = useState<number | null>(null);
  const [secondRegionId, setSecondRegionId] = useState<number | null>(null);
  const [subRegions, setSubRegions] = useState<SecondRegion[] | 'loading' | 'error' | null>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [errs, setErrs] = useState<Record<string, string>>({});
  const [fail, setFail] = useState('');

  useEffect(() => {
    if (firstCatId === null) { setSubCats([]); return; }
    setSubCats('loading');
    api.getSecondCategories(firstCatId).then(setSubCats).catch(() => setSubCats('error'));
  }, [firstCatId]);
  useEffect(() => {
    if (firstRegionId === null) { setSubRegions(null); return; }
    setSubRegions('loading');
    api.getSecondRegions(firstRegionId).then(setSubRegions).catch(() => setSubRegions('error'));
  }, [firstRegionId]);

  const catName = useMemo(() => (Array.isArray(subCats) ? subCats.find((s) => s.id === secondCatId)?.name : undefined), [subCats, secondCatId]);

  const submit = (e: FormEvent) => {
    e.preventDefault();
    if (base.state.kind !== 'ok') return;
    const qs = base.state.data.questions;
    const next: Record<string, string> = {};
    if (!secondCatId) next.cat = '서비스를 골라 주세요.';
    if (!secondRegionId) next.region = '시·군·구까지 골라 주세요.';
    for (const q of qs) {
      const a = (answers[q.id] ?? '').trim();
      if (q.isRequired && !a) next[`q${q.id}`] = '필수 질문이에요. 답변을 입력해 주세요.';
      if (a.length > ANSWER_MAX) next[`q${q.id}`] = `${ANSWER_MAX}자 이내로 입력해 주세요.`;
    }
    setErrs(next); setFail('');
    if (Object.keys(next).length) return;
    const body = {
      secondCategoryId: secondCatId!, secondRegionId: secondRegionId!,
      answers: qs.filter((q) => (answers[q.id] ?? '').trim()).map((q) => ({ questionId: q.id, content: answers[q.id].trim() }))
    };
    run(async () => {
      try {
        const res = await createServiceRequest(body);
        toast('요청서를 보냈어요.\n24시간 동안 견적을 받아요.');
        navigate('/requests', { id: res.id }, true);
      } catch (err) {
        // 실패하면 입력값을 그대로 두고 사유를 안내한다
        if (err instanceof ApiError && err.status === 400 && Object.keys(err.fieldErrors).length) setFail(Object.values(err.fieldErrors).join('\n'));
        else setFail(`요청서를 보내지 못했어요.\n${friendly(err)}`);
      }
    });
  };

  return (
    <main className="page narrow with-bar">
      <PageHead title="어떤 도움이 필요한지 알려 주세요" back={{ href: '#/', label: '서비스 고르기' }} sub={catName ? `${catName} 요청서` : undefined} />
      <LoadBox load={base.state} title="요청서 질문을 불러오지 못했어요" onRetry={() => base.reload()}>
        {({ questions, firstCats, firstRegions }) => (
          <form id="request-form" className="col gap" onSubmit={submit} noValidate>
            <fieldset className="card" disabled={busy}>
              <legend className="sec-head"><span className="num">1</span>서비스</legend>
              <div className="grid-2">
                <Field label="분야" htmlFor="rf-fc">
                  <select id="rf-fc" className="input" value={firstCatId ?? ''} onChange={(e) => { setFirstCatId(e.target.value ? Number(e.target.value) : null); setSecondCatId(null); }}>
                    <option value="">분야 선택</option>
                    {firstCats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </Field>
                <Field label="세부 서비스" htmlFor="rf-sc" error={errs.cat}>
                  <select id="rf-sc" className={`input${errs.cat ? ' bad' : ''}`} value={secondCatId ?? ''} disabled={!Array.isArray(subCats) || !subCats.length}
                    onChange={(e) => { setSecondCatId(e.target.value ? Number(e.target.value) : null); setErrs((x) => ({ ...x, cat: '' })); }}>
                    <option value="">{subCats === 'loading' ? '불러오는 중이에요' : subCats === 'error' ? '불러오지 못했어요' : Array.isArray(subCats) && !subCats.length ? '세부 서비스가 없어요' : '세부 서비스 선택'}</option>
                    {Array.isArray(subCats) && subCats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
                  </select>
                </Field>
              </div>
            </fieldset>

            <fieldset className="card" disabled={busy}>
              <legend className="sec-head"><span className="num">2</span>지역</legend>
              <div className="grid-2">
                <Field label="시·도" htmlFor="rf-fr">
                  <select id="rf-fr" className="input" value={firstRegionId ?? ''} onChange={(e) => { setFirstRegionId(e.target.value ? Number(e.target.value) : null); setSecondRegionId(null); }}>
                    <option value="">시·도 선택</option>
                    {firstRegions.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
                  </select>
                </Field>
                <Field label="시·군·구" htmlFor="rf-sr" error={errs.region}>
                  <select id="rf-sr" className={`input${errs.region ? ' bad' : ''}`} value={secondRegionId ?? ''} disabled={!Array.isArray(subRegions) || !subRegions.length}
                    onChange={(e) => { setSecondRegionId(e.target.value ? Number(e.target.value) : null); setErrs((x) => ({ ...x, region: '' })); }}>
                    <option value="">{subRegions === 'loading' ? '불러오는 중이에요' : subRegions === 'error' ? '불러오지 못했어요' : Array.isArray(subRegions) && !subRegions.length ? '시·군·구가 없어요' : '시·군·구 선택'}</option>
                    {Array.isArray(subRegions) && subRegions.map((r) => <option key={r.id} value={r.id}>{r.name}</option>)}
                  </select>
                </Field>
              </div>
              <small className="sub">시·도를 바꾸면 시·군·구를 다시 골라야 해요.</small>
            </fieldset>

            <fieldset className="card" disabled={busy}>
              <legend className="sec-head"><span className="num">3</span>질문</legend>
              {questions.map((q) => (
                <Field key={q.id} label={q.content} hint={`${q.isRequired ? '필수' : '선택'} · ${(answers[q.id] ?? '').length} / ${ANSWER_MAX}`} htmlFor={`q${q.id}`} error={errs[`q${q.id}`]}>
                  <textarea id={`q${q.id}`} className={`input area compact${errs[`q${q.id}`] ? ' bad' : ''}`} maxLength={ANSWER_MAX + 50}
                    value={answers[q.id] ?? ''} onChange={(e) => { setAnswers((a) => ({ ...a, [q.id]: e.target.value })); setErrs((x) => ({ ...x, [`q${q.id}`]: '' })); }} />
                </Field>
              ))}
            </fieldset>
            <Banner kind="warn"><Lines text={'제출한 요청서는 수정할 수 없어요.\n조건이 바뀌면 취소한 뒤 새로 작성해 주세요.'} /></Banner>
            {fail && <Banner><Lines text={fail} /></Banner>}
          </form>
        )}
      </LoadBox>
      <div className="bottom-bar">
        <div className="bar-inner">
          <small className="sub bar-note">보내면 24시간 동안 견적을 받아요.</small>
          <a className="btn" href="#/" aria-disabled={busy} onClick={(e) => busy && e.preventDefault()}>취소</a>
          <button className="btn primary" type="submit" form="request-form" disabled={busy || base.state.kind !== 'ok'}>
            {busy ? <><Spinner light />보내는 중</> : '요청서 보내기'}
          </button>
        </div>
      </div>
    </main>
  );
}
