// 조회 훅: 본인 고수 프로필 · 시도 목록
import { useCallback, useEffect, useState } from 'react';
import { ApiError } from '../api/client';
import * as api from '../api/endpoints';
import type { ExpertProfile, FirstRegion } from '../api/types';

// 본인 고수 프로필: 200 보유 / 404 미등록 / 그 밖(401·통신·서버 오류)은 조회 실패로 구분
export type MyProfileState =
  | { kind: 'loading' }
  | { kind: 'none' }
  | { kind: 'ok'; profile: ExpertProfile }
  | { kind: 'error'; error: unknown };

export function useMyExpertProfile(enabled = true) {
  const [state, setState] = useState<MyProfileState>({ kind: 'loading' });
  const load = useCallback(async () => {
    setState({ kind: 'loading' });
    try {
      const profile = await api.getMyExpertProfile();
      setState({ kind: 'ok', profile });
    } catch (e) {
      if (e instanceof ApiError && e.status === 404) setState({ kind: 'none' });
      else setState({ kind: 'error', error: e });
    }
  }, []);
  useEffect(() => { if (enabled) load(); }, [enabled, load]);
  return { state, reload: load, setProfile: (profile: ExpertProfile) => setState({ kind: 'ok', profile }) };
}

// 시도 목록은 여러 화면에서 쓰므로 한 번만 불러와 둔다 (지역 이름 표시용)
let firstRegionCache: Promise<FirstRegion[]> | null = null;
export function loadFirstRegions(): Promise<FirstRegion[]> {
  if (!firstRegionCache) {
    firstRegionCache = api.getFirstRegions().catch((e) => { firstRegionCache = null; throw e; });
  }
  return firstRegionCache;
}
export function useFirstRegions() {
  const [list, setList] = useState<FirstRegion[]>([]);
  useEffect(() => { loadFirstRegions().then(setList).catch(() => undefined); }, []);
  return list;
}
