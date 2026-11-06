// 로그인 상태와 내 회원 정보
// 역할은 토큰을 해석하지 않고 항상 GET /api/members/me 응답으로 판단한다
// (서버도 요청마다 DB의 역할을 다시 읽는다)
import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { ApiError, onAuthLost, tokenStore } from '../api/client';
import * as api from '../api/endpoints';
import type { Member } from '../api/types';

type Status = 'loading' | 'guest' | 'member' | 'error';

interface Session {
  status: Status;
  me: Member | null;
  loadError: unknown;
  login(email: string, password: string): Promise<void>;
  logout(): Promise<void>;
  reloadMe(): Promise<Member | null>;   // 최신 닉네임·사진·역할 반영
  setMe(m: Member): void;
  expired: boolean;                     // 재발급 실패로 세션이 끊겼는지 (직접 로그아웃은 제외)
  dismissExpired(): void;
}

// 로그인 API는 성공했지만 직후 내 정보 조회가 실패한 경우 (아이디·비밀번호 오류와 구분)
export class AfterLoginError extends Error {
  constructor(public readonly reason: unknown) { super('after-login'); }
}

const Ctx = createContext<Session | null>(null);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [status, setStatus] = useState<Status>(tokenStore.get() ? 'loading' : 'guest');
  const [me, setMeState] = useState<Member | null>(null);
  const [loadError, setLoadError] = useState<unknown>(null);
  const [expired, setExpired] = useState(false);

  const reloadMe = useCallback(async () => {
    if (!tokenStore.get()) { setStatus('guest'); setMeState(null); return null; }
    try {
      const m = await api.getMe();
      setMeState(m); setStatus('member'); setLoadError(null);
      return m;
    } catch (e) {
      if (e instanceof ApiError && e.status === 401) {
        tokenStore.clear(); setMeState(null); setStatus('guest');
      } else {
        // 통신·서버 오류는 로그아웃으로 처리하지 않는다
        setLoadError(e); setStatus(me ? 'member' : 'error');
      }
      throw e;
    }
  }, [me]);

  useEffect(() => {
    if (tokenStore.get()) reloadMe().catch(() => undefined);
    // 토큰 재발급까지 실패하면 로그인 만료 안내를 띄운다
    return onAuthLost(() => { setMeState(null); setStatus('guest'); setExpired(true); });
    // 처음 한 번만 실행
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const login = useCallback(async (email: string, password: string) => {
    await api.login(email, password);
    setExpired(false);
    setStatus('loading');
    try {
      await reloadMe();
    } catch (e) {
      throw new AfterLoginError(e);
    }
  }, [reloadMe]);

  const logout = useCallback(async () => {
    try { await api.logout(); } finally { setMeState(null); setStatus('guest'); setExpired(false); }
  }, []);

  const value = useMemo<Session>(() => ({
    status, me, loadError, login, logout, reloadMe,
    setMe: (m: Member) => setMeState(m),
    expired, dismissExpired: () => setExpired(false)
  }), [status, me, loadError, login, logout, reloadMe, expired]);

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useSession(): Session {
  const s = useContext(Ctx);
  if (!s) throw new Error('SessionProvider 밖에서 사용할 수 없어요');
  return s;
}
