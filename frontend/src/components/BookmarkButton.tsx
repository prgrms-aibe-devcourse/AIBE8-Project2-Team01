// 고수 공개 프로필의 찜 버튼 (W·P07-B)
// 찜 여부 조회 API가 없어 내 찜 목록(20개씩)을 넘겨 보며 판별한다. 확인하지 못하면 버튼을 잠그고 안내한다
import { useEffect, useState } from 'react';
import { addBookmark, getBookmarks, removeBookmark } from '../api/mvp';
import { useSession } from '../auth/session';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { friendly } from '../lib/messages';
import { currentRedirect } from './Header';
import { useToast } from './toast';
import { Spinner } from './ui';

async function findBookmarked(expertProfileId: number): Promise<boolean> {
  for (let p = 1; p <= 25; p++) {             // 찜은 최대 500명 → 20명씩 25페이지
    const page = await getBookmarks(p);
    if (page.content.some((b) => b.expertProfileId === expertProfileId)) return true;
    if (page.lastPage || page.content.length === 0) return false;
  }
  return false;
}

export function BookmarkButton({ expertProfileId, ownerMemberNickname }: { expertProfileId: number; ownerMemberNickname?: string }) {
  const { status, me } = useSession();
  const toast = useToast();
  const [on, setOn] = useState<boolean | null>(null);
  const [checkErr, setCheckErr] = useState(false);
  const { busy, run } = useSingleFlight();

  const check = () => {
    setCheckErr(false); setOn(null);
    findBookmarked(expertProfileId).then(setOn).catch(() => setCheckErr(true));
  };
  useEffect(() => { if (status === 'member' && me?.role === 'USER') check(); }, [status, expertProfileId]); // eslint-disable-line react-hooks/exhaustive-deps

  if (status === 'loading') return null;
  if (status !== 'member') {
    return <button type="button" className="btn like" onClick={() => navigate('/login', { redirect: currentRedirect() })}>♡ 로그인하고 찜하기</button>;
  }
  // 찜은 고객 기능이다. 관리자·고수 상태·본인 프로필에서는 버튼을 보이지 않는다
  if (me?.role === 'ADMIN' || me?.role === 'EXPERT' || (ownerMemberNickname && me?.nickname === ownerMemberNickname)) return null;
  if (checkErr) return <button type="button" className="btn like" onClick={check}>찜 상태 다시 확인</button>;

  const toggle = () => on !== null && run(async () => {
    try {
      if (on) { await removeBookmark(expertProfileId); setOn(false); toast('찜을 취소했어요.'); }
      else { await addBookmark(expertProfileId); setOn(true); toast('고수를 찜했어요.\n마이페이지의 찜한 고수에서 볼 수 있어요.'); }
    } catch (e) {
      toast(`${on ? '찜을 취소하지' : '찜하지'} 못했어요.\n${friendly(e)}`, true);
      check();
    }
  });
  return (
    <button type="button" className={`btn like${on ? ' on' : ''}`} aria-pressed={!!on} disabled={on === null || busy} onClick={toggle}>
      {on === null || busy ? <Spinner /> : <span aria-hidden="true">{on ? '♥' : '♡'}</span>} {on ? '찜함' : '찜하기'}
    </button>
  );
}
