import { useEffect, useRef, useState } from 'react';
import { X } from 'lucide-react';
import ServicePicker from './ServicePicker.jsx';
import RegionPicker from './RegionPicker.jsx';
import { REQUEST_QUESTIONS, WHEN_OPTIONS } from '../data/questions.js';
import { regionLabel } from '../lib/homeFormat.js';
import { createServiceRequest, getQuestions } from '../api/mvp';
import { useSession } from '../auth/session';
import { useToast } from './toast';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { friendly } from '../lib/messages';

// 로그인하러 다녀오는 동안 작성 중인 요청서를 잠시 보관한다
export const DRAFT_KEY = 'efy.requestDraft';
export function readSavedDraft() {
  try {
    const raw = sessionStorage.getItem(DRAFT_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

// 요청서 작성 시트. POST /api/requests 의 secondCategoryId, secondRegionId, answers 구조로 보낸다.
// 질문 번호는 GET /api/questions 응답을 표시 순서대로 맞춰 쓰고 응답을 받지 못하면 기준 데이터 번호를 쓴다.
export default function RequestSheet({ open, draft, setDraft, onClose }) {
  const { status, me } = useSession();
  const toast = useToast();
  const { busy, run } = useSingleFlight();
  const [answers, setAnswers] = useState({});
  const [errors, setErrors] = useState({});
  const [fail, setFail] = useState('');
  const [serverIds, setServerIds] = useState(null);
  const panelRef = useRef(null);
  const titleRef = useRef(null);

  useEffect(() => {
    if (!open) return undefined;
    setErrors({});
    setFail('');
    const saved = readSavedDraft();
    if (saved?.answers) setAnswers(saved.answers);
    try {
      sessionStorage.removeItem(DRAFT_KEY);
    } catch {
      // 보관한 초안이 없어도 그대로 진행한다
    }
    getQuestions()
      .then((qs) => setServerIds([...qs].sort((a, b) => a.displayOrder - b.displayOrder).map((q) => q.id)))
      .catch(() => setServerIds(null));
    const prev = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    requestAnimationFrame(() => titleRef.current?.focus());

    const onKey = (e) => {
      if (e.key === 'Escape') onClose();
      if (e.key === 'Tab') {
        const nodes = panelRef.current?.querySelectorAll(
          'button:not([disabled]), [href], input, textarea, select, [tabindex]:not([tabindex="-1"])',
        );
        if (!nodes?.length) return;
        const first = nodes[0];
        const last = nodes[nodes.length - 1];
        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };
    window.addEventListener('keydown', onKey);
    return () => {
      document.body.style.overflow = prev;
      window.removeEventListener('keydown', onKey);
    };
  }, [open, onClose]);

  if (!open) return null;

  const setAnswer = (id, v) => {
    setAnswers((a) => ({ ...a, [id]: v }));
    if (errors[id]) setErrors((er) => ({ ...er, [id]: undefined }));
  };

  const submit = (e) => {
    e.preventDefault();
    const next = {};
    if (!draft.sub) next.sub = '서비스를 골라 주세요.';
    if (!draft.region) next.region = '서비스를 받을 시·군·구를 골라 주세요.';
    REQUEST_QUESTIONS.forEach((q) => {
      if (q.isRequired && !String(answers[q.id] ?? '').trim()) next[q.id] = '필수 질문입니다. 답변을 입력해 주세요.';
    });
    setErrors(next);
    setFail('');
    const firstKey = Object.keys(next)[0];
    if (firstKey) {
      const target = { sub: 'sheet-service', region: 'sheet-region' }[firstKey] ?? `q-${firstKey}`;
      document.getElementById(target)?.focus();
      return;
    }
    // 로그인하지 않았으면 작성한 내용을 보관하고 로그인 화면으로 보낸다
    if (status !== 'member') {
      try {
        sessionStorage.setItem(DRAFT_KEY, JSON.stringify({ subId: draft.sub.id, region: draft.region, answers }));
      } catch {
        // 저장할 수 없으면 로그인 뒤 다시 작성한다
      }
      navigate('/login', { redirect: '/?resume=request' });
      return;
    }
    if (me?.role !== 'USER') {
      setFail(me?.role === 'EXPERT' ? '요청서는 고객 상태에서 보낼 수 있어요.\n마이페이지에서 고객으로 전환한 뒤 보내 주세요.' : '관리자 계정은 요청서를 보내지 않아요.');
      return;
    }
    const body = {
      secondCategoryId: draft.sub.id,
      secondRegionId: draft.region.second.id,
      answers: REQUEST_QUESTIONS.map((q, i) => ({ questionId: serverIds?.[i] ?? q.id, content: String(answers[q.id] ?? '').trim() })).filter((a) => a.content),
    };
    run(async () => {
      try {
        const res = await createServiceRequest(body);
        toast('요청서를 보냈어요.\n24시간 동안 견적을 받아요.');
        setAnswers({});
        onClose();
        navigate('/requests', { id: res.id });
      } catch (err) {
        setFail(`요청서를 보내지 못했어요.\n${friendly(err)}`);
      }
    });
  };

  return (
    <div className="sheet-root">
      <div className="sheet-scrim" onClick={onClose} aria-hidden="true" />
      <div className="sheet" role="dialog" aria-modal="true" aria-labelledby="sheet-title" ref={panelRef}>
        <div className="sheet-head">
          <h2 id="sheet-title" tabIndex={-1} ref={titleRef}>
            요청서 작성
          </h2>
          <button type="button" className="round-btn" aria-label="요청서 닫기" onClick={onClose}>
            <X size={20} aria-hidden="true" />
          </button>
        </div>

        {(
          <form className="sheet-form" onSubmit={submit} noValidate>
            <div className="sheet-body">
              <fieldset className="sheet-group">
                <legend>무엇을 어디에서</legend>
                <div className={errors.sub ? 'has-error' : ''}>
                  <ServicePicker
                    inputId="sheet-service"
                    label="서비스"
                    value={draft.sub}
                    autoFocus={false}
                    onChange={(sub) => {
                      setDraft((d) => ({ ...d, sub }));
                      if (sub) setErrors((er) => ({ ...er, sub: undefined }));
                    }}
                  />
                  {errors.sub && <p className="field-error" role="alert">{errors.sub}</p>}
                </div>
                <div className={errors.region ? 'has-error' : ''}>
                  <RegionPicker
                    buttonId="sheet-region"
                    label="지역"
                    value={draft.region}
                    onChange={(region) => {
                      setDraft((d) => ({ ...d, region }));
                      setErrors((er) => ({ ...er, region: undefined }));
                    }}
                  />
                  {errors.region && <p className="field-error" role="alert">{errors.region}</p>}
                </div>
              </fieldset>

              <fieldset className="sheet-group">
                <legend>고수에게 전할 내용</legend>
                {REQUEST_QUESTIONS.map((q) => (
                  <div key={q.id} className={`question${errors[q.id] ? ' has-error' : ''}`}>
                    <label className="field-label" htmlFor={`q-${q.id}`} id={`q-${q.id}-label`}>
                      {q.content}
                      <span className={q.isRequired ? 'req' : 'opt'}>{q.isRequired ? '필수' : '선택'}</span>
                    </label>
                    {q.kind === 'when' ? (
                      <>
                        <div className="when" role="group" aria-labelledby={`q-${q.id}-label`}>
                          {WHEN_OPTIONS.map((w, i) => (
                            <button
                              key={w}
                              id={i === 0 ? `q-${q.id}` : undefined}
                              type="button"
                              className="chip"
                              aria-pressed={answers[q.id] === w}
                              onClick={() => setAnswer(q.id, w)}
                            >
                              {w}
                            </button>
                          ))}
                        </div>
                      </>
                    ) : q.kind === 'long' ? (
                      <textarea
                        id={`q-${q.id}`}
                        name={`answer-${q.id}`}
                        autoComplete="off"
                        rows={3}
                        maxLength={500}
                        placeholder={q.placeholder}
                        value={answers[q.id] ?? ''}
                        aria-invalid={Boolean(errors[q.id])}
                        aria-describedby={errors[q.id] ? `q-${q.id}-err` : undefined}
                        onChange={(e) => setAnswer(q.id, e.target.value)}
                      />
                    ) : (
                      <input
                        id={`q-${q.id}`}
                        name={`answer-${q.id}`}
                        autoComplete="off"
                        type="text"
                        maxLength={100}
                        placeholder={q.placeholder}
                        value={answers[q.id] ?? ''}
                        onChange={(e) => setAnswer(q.id, e.target.value)}
                      />
                    )}
                    {errors[q.id] && (
                      <p className="field-error" id={`q-${q.id}-err`} role="alert">
                        {errors[q.id]}
                      </p>
                    )}
                  </div>
                ))}
              </fieldset>
            </div>
            <div className="sheet-foot">
              {fail ? (
                <p className="sheet-fail" role="alert">
                  {fail.split('\n').map((l, i) => (
                    <span key={i}>
                      {l}
                      <br />
                    </span>
                  ))}
                </p>
              ) : (
                <p>{status === 'member' ? '보내면 24시간 동안 견적을 받아요. 제출한 요청서는 수정할 수 없어요.' : '보내기를 누르면 로그인한 뒤 이어서 보낼 수 있어요.'}</p>
              )}
              <button type="submit" className="btn btn-primary" disabled={busy}>
                {busy ? '보내는 중' : '요청서 보내기'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
