// mock 상태 변경을 구독해 개발 패널·상단 잔액을 갱신한다
import { useSyncExternalStore } from 'react';
import { dummyControls, type DummySnapshot } from '../api/creditApi';

let cached: DummySnapshot = dummyControls.snapshot();

function subscribe(listener: () => void) {
  return dummyControls.subscribe(() => {
    // 변경 시점에만 새 스냅샷을 만든다 (useSyncExternalStore 무한 렌더 방지)
    cached = dummyControls.snapshot();
    listener();
  });
}

export function useDummySnapshot(): DummySnapshot {
  return useSyncExternalStore(subscribe, () => cached);
}
