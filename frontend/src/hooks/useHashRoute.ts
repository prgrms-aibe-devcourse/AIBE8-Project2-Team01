// 해시 라우팅
// 정적 파일로 배포해도 새로고침 시 경로가 유지되도록 해시를 쓴다.
import { useEffect, useState } from 'react';

export interface Route {
  path: string;                 // 예: /mypage, /experts/12
  params: URLSearchParams;
}

function parse(hash: string): Route {
  const [rawPath, query = ''] = hash.replace(/^#/, '').split('?');
  return { path: rawPath || '/', params: new URLSearchParams(query) };
}

export function navigate(path: string, params?: Record<string, string | number>, replace = false): void {
  const query = params ? `?${new URLSearchParams(Object.entries(params).map(([k, v]) => [k, String(v)]))}` : '';
  const next = `#${path}${query}`;
  if (replace) {
    window.history.replaceState(null, '', next);
    window.dispatchEvent(new HashChangeEvent('hashchange'));
  } else {
    window.location.hash = next;
  }
}

export function useHashRoute(): Route {
  const [route, setRoute] = useState<Route>(() => parse(window.location.hash));
  useEffect(() => {
    const onChange = () => {
      setRoute(parse(window.location.hash));
      window.scrollTo(0, 0);
    };
    window.addEventListener('hashchange', onChange);
    return () => window.removeEventListener('hashchange', onChange);
  }, []);
  return route;
}
