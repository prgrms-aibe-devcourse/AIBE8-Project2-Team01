// 로그인이 필요한 행동(찜·좋아요·댓글·글쓰기·요청서 보내기)을 비로그인 상태에서 눌렀을 때 띄우는 안내 모달
// 바로 로그인 화면으로 보내지 않는다. 구경만 하던 사람은 '계속 둘러보기'로 닫고 하던 일을 이어 간다.
// '로그인하러 가기'나 '회원가입'을 누를 때만 이동하고 끝나면 redirect 주소로 돌아온다.
import { createContext, useCallback, useContext, useEffect, useRef, useState, type ReactNode } from 'react';
import { navigate } from '../hooks/useHashRoute';
import { Lines, Modal } from './ui';

export type LoginPromptOptions = {
  message: string; // 무엇을 하려면 로그인이 필요한지 (줄바꿈 \n 가능)
  redirect?: string; // 로그인·회원가입 뒤 돌아올 주소. 없으면 지금 화면
  onLeave?: () => void; // 이동 직전에 할 일 (작성 중인 요청서 보관 등)
};

const Ctx = createContext<(options: LoginPromptOptions) => void>(() => undefined);

// 로그인·회원가입 화면에서 다시 그 화면으로 돌아오지 않게 지금 주소를 고른다 (Header의 currentRedirect와 같은 규칙)
function hereRedirect() {
  const h = window.location.hash.replace(/^#/, '') || '/';
  return h.startsWith('/login') || h.startsWith('/signup') ? '/' : h;
}

export function LoginPromptProvider({ children }: { children: ReactNode }) {
  const [open, setOpen] = useState<LoginPromptOptions | null>(null);
  const returnFocus = useRef<HTMLElement | null>(null);
  const primary = useRef<HTMLButtonElement>(null);

  const ask = useCallback((options: LoginPromptOptions) => {
    returnFocus.current = document.activeElement as HTMLElement | null;
    setOpen(options);
  }, []);

  useEffect(() => {
    if (!open) return undefined;
    primary.current?.focus();
    // 모달이 떠 있는 동안 Tab 이동을 모달 안에 가둔다
    const onKey = (e: KeyboardEvent) => {
      if (e.key !== 'Tab') return;
      const box = document.querySelector('.login-prompt-signup')?.closest('.modal');
      const nodes = box?.querySelectorAll<HTMLElement>('button');
      if (!box || !nodes || nodes.length === 0) return;
      const first = nodes[0];
      const last = nodes[nodes.length - 1];
      if (!box.contains(document.activeElement)) { e.preventDefault(); first.focus(); }
      else if (e.shiftKey && document.activeElement === first) { e.preventDefault(); last.focus(); }
      else if (!e.shiftKey && document.activeElement === last) { e.preventDefault(); first.focus(); }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [open]);

  const close = () => {
    setOpen(null);
    const el = returnFocus.current;
    if (el && typeof el.focus === 'function') requestAnimationFrame(() => el.focus());
  };
  const leave = (to: '/login' | '/signup') => {
    if (!open) return;
    const redirect = open.redirect ?? hereRedirect();
    open.onLeave?.();
    setOpen(null);
    navigate(to, { redirect });
  };

  return (
    <Ctx.Provider value={ask}>
      {children}
      {open && (
        <Modal
          title="로그인이 필요해요"
          onClose={close}
          actions={
            <>
              <button type="button" className="btn" onClick={close}>계속 둘러보기</button>
              <button type="button" className="btn primary" ref={primary} onClick={() => leave('/login')}>로그인하러 가기</button>
            </>
          }
        >
          <p><Lines text={open.message} /></p>
          <p className="sub login-prompt-signup">
            아직 회원이 아니라면{' '}
            <button type="button" className="strong-link" onClick={() => leave('/signup')}>회원가입</button>
          </p>
        </Modal>
      )}
    </Ctx.Provider>
  );
}

export const useLoginPrompt = () => useContext(Ctx);
