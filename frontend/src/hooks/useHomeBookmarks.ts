// 홈 고수 카드의 찜(하트) 상태
// 찜 여부 조회 API가 없어서 고객이면 내 찜 목록(20개씩 · 최대 500명)을 한 번 모두 읽어 둔다 (BookmarkButton과 같은 방식)
// 고객과 관리자는 찜을 사용할 수 있고 비로그인 사용자는 로그인 안내를 받는다
import { useCallback, useEffect, useRef, useState } from 'react';
import { addBookmark, getBookmarks, removeBookmark } from '../api/mvp';
import { useSession } from '../auth/session';
import { useLoginPrompt } from '../components/loginPrompt';
import { useToast } from '../components/toast';
import { friendly } from '../lib/messages';

const MAX_PAGES = 25;

export type HomeBookmarks = {
  shows: (ownerNickname: string) => boolean;
  ready: boolean;
  isOn: (expertProfileId: number) => boolean;
  toggle: (expertProfileId: number) => void;
};

export function useHomeBookmarks(): HomeBookmarks {
  const { status, me } = useSession();
  const toast = useToast();
  const askLogin = useLoginPrompt();
  const canBookmark = status === 'member' && (me?.role === 'USER' || me?.role === 'ADMIN');
  const [ids, setIds] = useState<Set<number>>(() => new Set());
  const [ready, setReady] = useState(false);
  const pending = useRef(new Set<number>());

  useEffect(() => {
    setIds(new Set());
    setReady(false);
    if (!canBookmark) return undefined;
    let alive = true;
    (async () => {
      const found = new Set<number>();
      for (let p = 1; p <= MAX_PAGES; p++) {
        const page = await getBookmarks(p);
        page.content.forEach((b) => found.add(b.expertProfileId));
        if (page.lastPage || page.content.length === 0) break;
      }
      if (alive) {
        setIds(found);
        setReady(true);
      }
    })().catch(() => undefined); // 찜 목록을 못 읽으면 하트를 잠가 둔다
    return () => {
      alive = false;
    };
  }, [canBookmark, me?.id]);

  const shows = useCallback(
    (ownerNickname: string) => status === 'guest' || (canBookmark && me?.nickname !== ownerNickname),
    [status, canBookmark, me?.nickname],
  );

  const toggle = useCallback(
    (expertProfileId: number) => {
      if (status !== 'member') {
        askLogin({ message: '찜은 로그인한 뒤에 할 수 있어요.\n찜한 고수는 마이페이지에서 모아 볼 수 있어요.' });
        return;
      }
      if (!canBookmark || !ready || pending.current.has(expertProfileId)) return;
      const on = ids.has(expertProfileId);
      const flip = (next: boolean) =>
        setIds((prev) => {
          const s = new Set(prev);
          if (next) s.add(expertProfileId);
          else s.delete(expertProfileId);
          return s;
        });
      pending.current.add(expertProfileId);
      flip(!on); // 누르는 즉시 바꾸고 실패하면 되돌린다
      (on ? removeBookmark(expertProfileId) : addBookmark(expertProfileId))
        .then(() => toast(on ? '찜을 취소했어요.' : '고수를 찜했어요.\n마이페이지의 찜한 고수에서 볼 수 있어요.'))
        .catch((e) => {
          flip(on);
          toast(`${on ? '찜을 취소하지' : '찜하지'} 못했어요.\n${friendly(e)}`, true);
        })
        .finally(() => pending.current.delete(expertProfileId));
    },
    [status, canBookmark, ready, ids, toast, askLogin],
  );

  const isOn = useCallback((id: number) => ids.has(id), [ids]);
  return { shows, ready: status === 'guest' || ready, isOn, toggle };
}
