// 상단 내비게이션 (기존 와이어프레임 11_크레딧의 헤더 구성)
import { useDummySnapshot } from '../hooks/useDummySnapshot';
import type { RoutePath } from '../hooks/useHashRoute';

export function Gnb({ path }: { path: RoutePath }) {
  const snap = useDummySnapshot();
  const isCharge = path === '/credit' || path === '/checkout' || path === '/result';

  return (
    <header className="gnb">
      <a className="logo" href="#/credit">
        EFY
      </a>
      <nav aria-label="주요 메뉴">
        <span className="muted">받은 요청</span>
        <span className="muted">보낸 견적</span>
        <span className="muted">채팅</span>
        <a href="#/credit" className={isCharge ? 'active' : undefined}>
          크레딧 충전
        </a>
        <a href="#/history" className={path === '/history' ? 'active' : undefined}>
          충전·사용 내역
        </a>
      </nav>
      <span className="chip">
        사용 가능 {snap.balance - snap.reserved} / 총 {snap.balance}
      </span>
      <div className="role-toggle" aria-label="현재 역할: 고수">
        <span>고객</span>
        <span className="on">고수</span>
      </div>
    </header>
  );
}
