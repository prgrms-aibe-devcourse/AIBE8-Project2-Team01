// 비동기 조회 상태 관리
import { useEffect, useState, type DependencyList } from 'react';

export interface AsyncState<T> {
  data: T | null;
  error: string | null;
  loading: boolean;
}

export function useAsync<T>(load: () => Promise<T>, deps: DependencyList): AsyncState<T> {
  const [state, setState] = useState<AsyncState<T>>({ data: null, error: null, loading: true });

  useEffect(() => {
    let alive = true; // 화면 이동 후 늦게 도착한 응답 무시
    setState((prev) => ({ ...prev, loading: true, error: null }));
    load()
      .then((data) => alive && setState({ data, error: null, loading: false }))
      .catch((e: { message?: string }) => alive && setState({ data: null, error: e.message ?? '오류가 발생했습니다.', loading: false }));
    return () => {
      alive = false;
    };
  }, deps);

  return state;
}
