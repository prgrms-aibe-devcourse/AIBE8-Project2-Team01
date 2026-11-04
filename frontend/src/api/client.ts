// 공통 API 호출
// - Access 토큰: 로그인 응답 본문으로 받고 Authorization: Bearer 로 보낸다.
// - Refresh 토큰: 서버가 HttpOnly 쿠키(경로 /api/public/auth/refresh)로 관리한다. 화면에서 읽지 않는다.
// - 재발급: 만료된 Access 토큰을 Authorization에 담고 쿠키와 함께 POST /api/public/auth/refresh
//   (백엔드 AuthService.refresh 동작에 맞춤)
import type { ErrorBody, LoginResponse } from './types';

const BASE = (import.meta.env.VITE_API_BASE_URL as string | undefined)?.replace(/\/$/, '') ?? '';
const TOKEN_KEY = 'efy.accessToken';

export class ApiError extends Error {
  // status 0은 통신 실패(서버에 닿지 못함)
  constructor(
    public status: number,
    message: string,
    public fieldErrors: Record<string, string> = {}
  ) {
    super(message);
  }
  get isNetwork() { return this.status === 0; }
}

export const tokenStore = {
  get(): string | null {
    try { return localStorage.getItem(TOKEN_KEY); } catch { return null; }
  },
  set(token: string) {
    try { localStorage.setItem(TOKEN_KEY, token); } catch { /* 저장소를 쓸 수 없으면 메모리 세션만 유지 */ }
  },
  clear() {
    try { localStorage.removeItem(TOKEN_KEY); } catch { /* 무시 */ }
  }
};

// 인증이 끝났을 때(재발급 실패 등) 화면에 알리는 구독
type Listener = () => void;
const authLost = new Set<Listener>();
export function onAuthLost(fn: Listener) { authLost.add(fn); return () => { authLost.delete(fn); }; }

interface RequestOptions {
  method?: string;
  body?: unknown;          // JSON 본문
  form?: FormData;         // multipart 본문
  auth?: boolean;          // Authorization 헤더 사용 여부 (기본 true)
  retry?: boolean;         // 401일 때 재발급 후 한 번 다시 시도 (기본 true)
}

async function parseError(res: Response): Promise<ApiError> {
  let body: ErrorBody = {};
  try { body = await res.json(); } catch { /* 본문 없음 */ }
  return new ApiError(res.status, body.message ?? '', body.errors ?? {});
}

async function send(path: string, opt: RequestOptions, token: string | null): Promise<Response> {
  const headers: Record<string, string> = {};
  if (opt.body !== undefined) headers['Content-Type'] = 'application/json';
  if (opt.auth !== false && token) headers.Authorization = `Bearer ${token}`;
  try {
    return await fetch(BASE + path, {
      method: opt.method ?? 'GET',
      headers,
      body: opt.form ?? (opt.body !== undefined ? JSON.stringify(opt.body) : undefined),
      credentials: 'include'
    });
  } catch {
    throw new ApiError(0, '');
  }
}

// 동시에 여러 요청이 401을 받아도 재발급은 한 번만
let refreshing: Promise<boolean> | null = null;
export function refreshAccessToken(): Promise<boolean> {
  if (refreshing) return refreshing;
  const old = tokenStore.get();
  refreshing = (async () => {
    if (!old) return false;
    try {
      const res = await fetch(BASE + '/api/public/auth/refresh', {
        method: 'POST',
        headers: { Authorization: `Bearer ${old}` },
        credentials: 'include'
      });
      if (!res.ok) return false;
      const data = (await res.json()) as LoginResponse;
      tokenStore.set(data.accessToken);
      return true;
    } catch {
      return false;
    }
  })().finally(() => { refreshing = null; });
  return refreshing;
}

export async function request<T>(path: string, opt: RequestOptions = {}): Promise<T> {
  let res = await send(path, opt, tokenStore.get());
  if (res.status === 401 && opt.auth !== false && opt.retry !== false && tokenStore.get()) {
    const ok = await refreshAccessToken();
    if (ok) {
      res = await send(path, opt, tokenStore.get());
      // 새 토큰으로도 인증에 실패하면 세션이 끊긴 것으로 본다 (재발급 실패와 같은 안내)
      if (res.status === 401) {
        tokenStore.clear();
        authLost.forEach((fn) => fn());
      }
    } else {
      tokenStore.clear();
      authLost.forEach((fn) => fn());
    }
  }
  if (!res.ok) throw await parseError(res);
  if (res.status === 204) return undefined as T;
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

// 화면에 보여 줄 오류 문구 (서버 message가 있으면 우선)
export function errorText(e: unknown, fallback = '잠시 후 다시 시도해 주세요.'): string {
  if (e instanceof ApiError) {
    if (e.isNetwork) return '서버에 연결하지 못했어요.\n네트워크 상태를 확인해 주세요.';
    if (e.status >= 500) return '일시적인 오류가 생겼어요.\n잠시 후 다시 시도해 주세요.';
    if (e.message) return e.message;
  }
  return fallback;
}
