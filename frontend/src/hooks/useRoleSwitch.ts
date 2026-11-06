// 기존 프로필 보유자의 고객 ↔ 고수 전환
// 최초 등록 전에는 쓰지 않는다 (등록 성공 시 서버가 고수로 바꿈)
import { errorText } from '../api/client';
import * as api from '../api/endpoints';
import { useSession } from '../auth/session';
import { useToast } from '../components/toast';
import { useSingleFlight } from './useSingleFlight';

export function useRoleSwitch() {
  const { reloadMe } = useSession();
  const toast = useToast();
  const { busy, run } = useSingleFlight();
  // hasProfile=false: 고수 프로필 없이 고수 역할인 회원이 고객으로 돌아가는 경우 (공개 프로필 안내를 붙이지 않는다)
  const switchTo = (role: 'USER' | 'EXPERT', hasProfile = true) => run(async () => {
    try {
      await api.changeMyRole(role);
      // 전환 결과는 회원 정보를 다시 읽어 확인한다
      await reloadMe().catch(() => undefined);
      toast(role === 'EXPERT' ? '고수로 전환했어요.' : hasProfile ? '고객으로 전환했어요.\n고수 프로필은 계속 공개돼요.' : '고객으로 전환했어요.\n이제 고수 프로필을 등록할 수 있어요.');
      return true;
    } catch (e) {
      toast(`${role === 'EXPERT' ? '고수로' : '고객으로'} 전환하지 못했어요.\n${errorText(e)}`, true);
      return false;
    }
  });
  return { busy, switchTo };
}
