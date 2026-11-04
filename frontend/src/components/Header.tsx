// 상단 헤더: 로고 · 로그인 상태 · 내 닉네임과 역할
// 이번 범위 밖 메뉴(서비스 찾기·커뮤니티·요청서 등)는 아직 화면이 없어 넣지 않는다
import { useSession } from '../auth/session';
import { roleLabel } from '../lib/format';
import { Avatar } from './ui';
import logo from '../assets/efy-logo.png';

export function Header() {
  const { status, me } = useSession();
  return (
    <header className="hd">
      <a href={status === 'member' ? '#/mypage' : '#/login'} className="logo">
        <img src={logo} alt="EFY 홈" />
      </a>
      <div className="hd-right">
        {status === 'member' && me ? (
          <a className="me-pill" href="#/mypage" aria-label={`마이페이지 · ${me.nickname} · ${roleLabel(me.role)}`}>
            <Avatar url={me.imageUrl} size={28} />
            <span className="me-name">{me.nickname}</span>
            <span className={`role-tag ${me.role === 'EXPERT' ? 'expert' : 'user'}`}>{roleLabel(me.role)}</span>
          </a>
        ) : status === 'guest' ? (
          <a className="btn small" href="#/login">로그인</a>
        ) : null}
      </div>
    </header>
  );
}
