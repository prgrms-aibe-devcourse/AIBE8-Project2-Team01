// 처리 중 반복 클릭 방지: 첫 요청이 끝나기 전 추가 호출을 무시한다 (dummy/payment와 같은 방식)
import { useCallback, useRef, useState } from 'react';

export function useSingleFlight() {
  // ref는 렌더를 기다리지 않고 즉시 바뀌므로 같은 틱의 연타도 막는다
  const runningRef = useRef(false);
  const [busy, setBusy] = useState(false);

  const run = useCallback(async <T,>(task: () => Promise<T>): Promise<T | undefined> => {
    if (runningRef.current) return undefined;
    runningRef.current = true;
    setBusy(true);
    try {
      return await task();
    } finally {
      runningRef.current = false;
      setBusy(false);
    }
  }, []);

  return { busy, run };
}
