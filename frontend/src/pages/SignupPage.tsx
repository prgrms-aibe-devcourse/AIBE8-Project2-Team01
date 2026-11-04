// 회원가입 (AUTH-01): 이메일·비밀번호·닉네임·전화번호 → USER로 가입한 뒤 바로 로그인
// 고수 전용 가입은 없다. 고수 활동은 가입 후 마이페이지에서 시작한다
import { useState, type FormEvent } from 'react';
import { ApiError, errorText } from '../api/client';
import { signup } from '../api/mvp';
import { useSession } from '../auth/session';
import { Banner, Field, Lines, Spinner } from '../components/ui';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { NICKNAME_MAX, PASSWORD_MAX, PASSWORD_MIN, PHONE_RE } from '../lib/policy';

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
type Key = 'email' | 'password' | 'password2' | 'nickname' | 'phone';

export function SignupPage({ redirect }: { redirect?: string | null }) {
  const { login } = useSession();
  const { busy, run } = useSingleFlight();
  const [v, setV] = useState({ email: '', password: '', password2: '', nickname: '', phone: '' });
  const [errs, setErrs] = useState<Partial<Record<Key, string>>>({});
  const [fail, setFail] = useState('');
  const set = (k: Key, val: string) => { setV((o) => ({ ...o, [k]: val })); setErrs((e) => ({ ...e, [k]: undefined })); };

  const submit = (e: FormEvent) => {
    e.preventDefault();
    const email = v.email.trim().toLowerCase();
    const nickname = v.nickname.trim();
    const next: Partial<Record<Key, string>> = {};
    if (!email) next.email = '이메일을 입력해 주세요.';
    else if (!EMAIL_RE.test(email) || email.length > 50) next.email = '올바른 이메일 형식으로 50자 이내로 입력해 주세요.';
    if (v.password.length < PASSWORD_MIN || v.password.length > PASSWORD_MAX) next.password = `비밀번호는 ${PASSWORD_MIN}~${PASSWORD_MAX}자로 입력해 주세요.`;
    if (v.password2 !== v.password) next.password2 = '비밀번호가 서로 달라요.';
    if (!nickname || nickname.length > NICKNAME_MAX) next.nickname = `닉네임은 1~${NICKNAME_MAX}자로 입력해 주세요.`;
    if (!PHONE_RE.test(v.phone)) next.phone = '전화번호는 숫자 11자리로 입력해 주세요.';
    setErrs(next); setFail('');
    if (Object.keys(next).length) return;
    run(async () => {
      try {
        await signup({ email, password: v.password, nickname, phone: v.phone });
      } catch (err) {
        if (err instanceof ApiError && err.status === 409) {
          // 중복 항목은 서버 문구로 구분한다
          const m = err.message;
          if (m.includes('이메일')) setErrs({ email: '이미 사용 중인 이메일이에요.' });
          else if (m.includes('닉네임')) setErrs({ nickname: '이미 사용 중인 닉네임이에요.' });
          else if (m.includes('전화')) setErrs({ phone: '이미 사용 중인 전화번호예요.' });
          else setFail(errorText(err));
        } else if (err instanceof ApiError && err.status === 400 && Object.keys(err.fieldErrors).length) {
          setErrs(err.fieldErrors as Partial<Record<Key, string>>);
        } else setFail(`가입하지 못했어요.\n${errorText(err)}`);
        return;
      }
      try {
        await login(email, v.password);
        navigate(redirect && redirect.startsWith('/') ? redirect : '/', undefined, true);
      } catch {
        // 가입은 됐지만 자동 로그인이 실패하면 로그인 화면으로 보낸다
        navigate('/login', redirect ? { redirect } : undefined, true);
      }
    });
  };

  const input = (k: Key, label: string, type: string, hint?: string, extra?: Partial<React.InputHTMLAttributes<HTMLInputElement>>) => (
    <Field label={label} hint={hint} htmlFor={`su-${k}`} error={errs[k]}>
      <input id={`su-${k}`} className={`input${errs[k] ? ' bad' : ''}`} type={type} value={v[k]} disabled={busy}
        onChange={(e) => set(k, k === 'phone' ? e.target.value.replace(/\D/g, '').slice(0, 11) : e.target.value)} {...extra} />
    </Field>
  );

  return (
    <main className="page narrow">
      <form className="card login" onSubmit={submit} noValidate>
        <h1 className="title">회원가입</h1>
        <p className="sub">고객과 고수 모두 같은 계정으로 이용해요.<br />고수 활동은 가입 후 마이페이지에서 시작할 수 있어요.</p>
        {input('email', '이메일', 'email', '최대 50자', { autoComplete: 'email' })}
        {input('password', '비밀번호', 'password', `${PASSWORD_MIN}~${PASSWORD_MAX}자`, { autoComplete: 'new-password' })}
        {input('password2', '비밀번호 확인', 'password', undefined, { autoComplete: 'new-password' })}
        {input('nickname', '닉네임', 'text', `1~${NICKNAME_MAX}자`)}
        {input('phone', '전화번호', 'tel', '숫자 11자리', { inputMode: 'numeric', placeholder: '01012345678' })}
        {fail && <Banner><Lines text={fail} /></Banner>}
        <button className="btn primary block" type="submit" disabled={busy}>{busy ? <><Spinner light />가입하는 중</> : '가입하기'}</button>
        <p className="sub center">이미 계정이 있나요? <a className="strong-link" href={`#/login${redirect ? `?redirect=${encodeURIComponent(redirect)}` : ''}`}>로그인</a></p>
      </form>
    </main>
  );
}
