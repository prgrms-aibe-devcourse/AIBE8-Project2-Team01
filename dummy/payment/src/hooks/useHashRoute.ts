// 해시 라우팅: #/credit, #/checkout?productId=1, #/result?orderId=..., #/history
// 정적 파일로 열어도 동작하도록 해시 방식을 쓴다
import { useEffect, useState } from 'react';

export type RoutePath = '/credit' | '/checkout' | '/result' | '/history';

export interface Route {
  path: RoutePath;
  params: URLSearchParams;
}

const PATHS: RoutePath[] = ['/credit', '/checkout', '/result', '/history'];

function parse(hash: string): Route {
  const [rawPath, query = ''] = hash.replace(/^#/, '').split('?');
  const path = PATHS.find((p) => p === rawPath) ?? '/credit';
  return { path, params: new URLSearchParams(query) };
}

export function navigate(path: RoutePath, params?: Record<string, string | number>): void {
  const query = params ? `?${new URLSearchParams(Object.entries(params).map(([k, v]) => [k, String(v)]))}` : '';
  window.location.hash = `${path}${query}`;
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
