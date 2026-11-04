// 로그인 (와이어프레임 W·00 / M·00)
// 회원가입은 이번 범위 밖이라 진입 링크를 두지 않는다
import { useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import { AfterLoginError, useSession } from '../auth/session';
import { Banner, Field, Lines, Spinner } from '../components/ui';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { navigate } from '../hooks/useHashRoute';

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export function LoginPage({ redirect }: { redirect?: string | null }) {
  const { login } = useSession();
  const { busy, run } = useSingleFlight();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errs, setErrs] = useState<{ email?: string; password?: string }>({});
  const [fail, setFail] = useState('');

  const submit = (e: FormEvent) => {
    e.preventDefault();
    const next: typeof errs = {};
    const em = email.trim();
    if (!em) next.email = '이메일을 입력해 주세요.';
    else if (!EMAIL_RE.test(em)) next.email = '올바른 이메일 형식으로 입력해 주세요.';
    if (!password) next.password = '비밀번호를 입력해 주세요.';
    setErrs(next); setFail('');
    if (Object.keys(next).length) return;
    run(async () => {
      try {
        await login(em, password);
        navigate(redirect && redirect.startsWith('/') ? redirect : '/', undefined, true);
      } catch (err) {
        if (err instanceof AfterLoginError) {
          const r = err.reason;
          setFail(r instanceof ApiError && r.status === 401
            ? '로그인 정보를 확인하지 못했어요.\n다시 로그인해 주세요.'
            : `내 정보를 불러오지 못했어요.\n${errorText(r)}`);
        } else if (err instanceof ApiError && err.status === 401) setFail('이메일 또는 비밀번호가 일치하지 않아요.');
        else if (err instanceof ApiError && err.status === 400) setFail(Object.values(err.fieldErrors)[0] ?? errorText(err));
        else setFail(errorText(err));
      }
    });
  };

  return (
    <main className="page narrow">
      <form className="card login" onSubmit={submit} noValidate>
        <h1 className="title">필요한 일상 서비스에 딱 맞는 고수를 연결해 드려요</h1>
        <p className="sub">고객과 고수 모두 같은 계정으로 이용해요.</p>
        <Field label="이메일" htmlFor="email" error={errs.email}>
          <input id="email" className={`input${errs.email ? ' bad' : ''}`} type="email" autoComplete="email"
            value={email} onChange={(e) => setEmail(e.target.value)} disabled={busy} />
        </Field>
        <Field label="비밀번호" htmlFor="password" error={errs.password}>
          <input id="password" className={`input${errs.password ? ' bad' : ''}`} type="password" autoComplete="current-password"
            value={password} onChange={(e) => setPassword(e.target.value)} disabled={busy} />
        </Field>
        {fail && <Banner><Lines text={fail} /></Banner>}
        <button className="btn primary block" type="submit" disabled={busy}>
          {busy ? <><Spinner light />로그인하는 중</> : '로그인'}
        </button>
        <p className="sub center">처음이신가요? <a href={`#/signup${redirect ? `?redirect=${encodeURIComponent(redirect)}` : ''}`} className="strong-link">회원가입</a></p>
      </form>
    </main>
  );
}
