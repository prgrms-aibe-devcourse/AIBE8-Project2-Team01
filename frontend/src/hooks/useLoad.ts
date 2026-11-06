// 조회 상태 공통 훅: 불러오는 중 · 성공 · 실패를 구분한다 (빈 결과는 화면에서 데이터로 판단)
import { useCallback, useEffect, useRef, useState } from 'react';

export type Load<T> =
  | { kind: 'loading' }
  | { kind: 'ok'; data: T }
  | { kind: 'error'; error: unknown };

export function useLoad<T>(fn: () => Promise<T>, deps: unknown[], enabled = true) {
  const [state, setState] = useState<Load<T>>({ kind: 'loading' });
  const seq = useRef(0);
  const load = useCallback(async (quiet = false) => {
    const my = ++seq.current;
    if (!quiet) setState({ kind: 'loading' });
    try {
      const data = await fn();
      if (my === seq.current) setState({ kind: 'ok', data });
    } catch (e) {
      // 조용한 갱신(quiet)이 실패하면 기존 화면을 유지한다
      if (my === seq.current && !quiet) setState({ kind: 'error', error: e });
      if (quiet) throw e;
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);
  useEffect(() => { if (enabled) load().catch(() => undefined); }, [load, enabled]);
  const set = useCallback((data: T) => setState({ kind: 'ok', data }), []);
  return { state, reload: load, set };
}
