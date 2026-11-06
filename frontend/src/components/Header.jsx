import { useEffect, useRef, useState } from 'react';
import {
  Bookmark,
  ChevronDown,
  Coins,
  FileText,
  House,
  Inbox,
  LogOut,
  MessageCircle,
  MessagesSquare,
  Moon,
  Repeat,
  Search,
  Send,
  Sun,
  UserRound,
} from 'lucide-react';
import { useSession } from '../auth/session';
import { navigate } from '../hooks/useHashRoute';
import { useRoleSwitch } from '../hooks/useRoleSwitch';
import { useMyExpertProfile } from '../hooks/useData';
import { roleLabel } from '../lib/format';
import { regionLabel } from '../lib/homeFormat.js';
import { useTheme } from '../lib/theme.js';

// 상단 바(웹)와 하단 탭(모바일).
// 메뉴 구성과 역할별 노출은 팀 프론트엔드(Header.tsx)를 따르고 모양은 EFY 디자인 체계를 따른다.

// 역할별 주 메뉴
function navFor(role) {
  const common = [
    { href: '#/', label: '서비스 찾기', match: /^\/$|^\/requests\/new/ },
    { href: '#/community', label: '커뮤니티', match: /^\/community/ },
  ];
  if (role === 'USER') return [...common, { href: '#/requests', label: '내 요청서', match: /^\/requests(\/\d+)?$/ }];
  if (role === 'EXPERT')
    return [
      ...common,
      { href: '#/received', label: '받은 요청', match: /^\/received/ },
      { href: '#/quotes', label: '보낸 견적', match: /^\/quotes/ },
    ];
  if (role === 'ADMIN')
    return [
      { href: '#/admin/members', label: '회원', match: /^\/admin\/members/ },
      { href: '#/admin/posts', label: '게시글', match: /^\/admin\/posts/ },
      { href: '#/admin/requests', label: '요청서', match: /^\/admin\/requests/ },
      { href: '#/admin/quotes', label: '견적', match: /^\/admin\/quotes/ },
      { href: '#/admin/categories', label: '카테고리', match: /^\/admin\/categories/ },
      { href: '#/admin/regions', label: '지역', match: /^\/admin\/regions/ },
    ];
  return common;
}

// 로그인 후 돌아올 현재 화면
export function currentRedirect() {
  const h = window.location.hash.replace(/^#/, '') || '/';
  return h.startsWith('/login') || h.startsWith('/signup') ? '/' : h;
}

function Avatar({ me, size = 32 }) {
  if (me.imageUrl) return <img className="me-avatar" src={me.imageUrl} alt="" width={size} height={size} />;
  return (
    <span className="me-avatar me-initial" style={{ width: size, height: size }} aria-hidden="true">
      {me.nickname.slice(0, 1)}
    </span>
  );
}

function UserMenu({ me }) {
  const { logout } = useSession();
  const { busy, switchTo } = useRoleSwitch();
  const profile = useMyExpertProfile(me.role !== 'ADMIN');
  const [open, setOpen] = useState(false);
  const ref = useRef(null);

  useEffect(() => {
    if (!open) return undefined;
    const onKey = (e) => e.key === 'Escape' && setOpen(false);
    const onDown = (e) => !ref.current?.contains(e.target) && setOpen(false);
    window.addEventListener('keydown', onKey);
    document.addEventListener('pointerdown', onDown);
    return () => {
      window.removeEventListener('keydown', onKey);
      document.removeEventListener('pointerdown', onDown);
    };
  }, [open]);

  // 등록·전환으로 프로필 보유 여부가 바뀔 수 있어서 메뉴를 열 때마다 다시 확인한다
  const reloadProfile = profile.reload;
  useEffect(() => {
    if (open && me.role !== 'ADMIN') reloadProfile();
  }, [open, me.role, reloadProfile]);

  const hasProfile = profile.state.kind === 'ok';
  const go = (href) => {
    setOpen(false);
    window.location.hash = href;
  };
  const doSwitch = async (role) => {
    setOpen(false);
    await switchTo(role);
  };
  const doLogout = async () => {
    setOpen(false);
    await logout().catch(() => undefined);
    navigate('/', undefined, true);
  };

  return (
    <div className="user-menu" ref={ref}>
      <button type="button" className="user-trigger" aria-haspopup="menu" aria-expanded={open} onClick={() => setOpen((v) => !v)}>
        <Avatar me={me} />
        <span className="user-name hide-sm">{me.nickname}</span>
        <span className={`role-badge role-${me.role.toLowerCase()}`}>{roleLabel(me.role)}</span>
        <ChevronDown size={16} aria-hidden="true" />
      </button>
      {open && (
        <div className="popover user-pop" role="menu">
          <p className="user-pop-head">
            <strong>{me.nickname}</strong>
            <span>{me.email}</span>
          </p>
          <button type="button" role="menuitem" onClick={() => go('/mypage')}>
            <UserRound size={16} aria-hidden="true" />
            마이페이지
          </button>
          {me.role === 'EXPERT' && (
            <button type="button" role="menuitem" onClick={() => go('/expert/me')}>
              <FileText size={16} aria-hidden="true" />내 고수 프로필
            </button>
          )}
          {me.role === 'USER' && (
            <button type="button" role="menuitem" onClick={() => go('/bookmarks')}>
              <Bookmark size={16} aria-hidden="true" />
              찜한 고수
            </button>
          )}
          {me.role === 'EXPERT' && (
            <button type="button" role="menuitem" onClick={() => go('/credit')}>
              <Coins size={16} aria-hidden="true" />
              크레딧
            </button>
          )}
          {me.role === 'EXPERT' && (
            <button type="button" role="menuitem" disabled={busy} onClick={() => doSwitch('USER')}>
              <Repeat size={16} aria-hidden="true" />
              고객으로 전환
            </button>
          )}
          {me.role === 'USER' && hasProfile && (
            <button type="button" role="menuitem" disabled={busy} onClick={() => doSwitch('EXPERT')}>
              <Repeat size={16} aria-hidden="true" />
              고수로 전환
            </button>
          )}
          {me.role === 'USER' && profile.state.kind === 'none' && (
            <button type="button" role="menuitem" onClick={() => go('/expert/intro')}>
              <Repeat size={16} aria-hidden="true" />
              고수 등록
            </button>
          )}
          <button type="button" role="menuitem" className="danger" onClick={doLogout}>
            <LogOut size={16} aria-hidden="true" />
            로그아웃
          </button>
        </div>
      )}
    </div>
  );
}

export default function Header({ path = '/', draft = undefined, showCompact = false, onOpenRequest = undefined }) {
  const { status, me } = useSession();
  const { theme, toggle: toggleTheme } = useTheme();
  const [scrolled, setScrolled] = useState(false);
  const role = status === 'member' && me ? me.role : null;
  const items = navFor(role);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 4);
    onScroll();
    window.addEventListener('scroll', onScroll, { passive: true });
    return () => window.removeEventListener('scroll', onScroll);
  }, []);

  const service = draft?.sub?.name;
  const region = regionLabel(draft?.region);

  return (
    <header className={`site-header${scrolled ? ' is-scrolled' : ''}`}>
      <div className="container header-inner">
        <a className="logo" href="#/">
          <img src="/brand/efy-logo.png" alt="EFY 홈" width="112" height="29" />
        </a>

        <nav className={`header-nav${showCompact ? ' is-hidden' : ''}`} aria-label="주 메뉴">
          {items.map((n) => {
            const on = n.match.test(path);
            return (
              <a key={n.href} href={n.href} className={on ? 'is-current' : ''} aria-current={on ? 'page' : undefined}>
                {n.label}
              </a>
            );
          })}
        </nav>

        {onOpenRequest && (
          <button
            type="button"
            className={`compact-slip${showCompact ? ' is-visible' : ''}`}
            onClick={onOpenRequest}
            tabIndex={showCompact ? 0 : -1}
            aria-hidden={!showCompact}
          >
            <Search size={18} strokeWidth={2.2} aria-hidden="true" />
            <span className={service ? 'filled' : ''}>{service ?? '어떤 서비스가 필요하세요?'}</span>
            <span className="divider" aria-hidden="true" />
            <span className={region ? 'filled' : ''}>{region || '지역'}</span>
            <span className="go">요청서 작성</span>
          </button>
        )}

        <div className="header-actions">
          {role && role !== 'ADMIN' && (
            <a className="icon-btn hide-sm" href="#/chats" aria-label="채팅" title="채팅">
              <MessageCircle size={20} aria-hidden="true" />
            </a>
          )}
          {role === 'EXPERT' && (
            <a className="credit-pill hide-sm" href="#/credit">
              <Coins size={16} aria-hidden="true" />
              크레딧
            </a>
          )}
          {status === 'member' && me && <UserMenu me={me} />}
          {status === 'guest' && (
            <>
              <a className="btn btn-ghost btn-sm" href={`#/login?redirect=${encodeURIComponent(currentRedirect())}`}>
                로그인
              </a>
              <a className="btn btn-secondary btn-sm hide-sm" href="#/signup">
                회원가입
              </a>
            </>
          )}
          <button
            type="button"
            className="icon-btn theme-btn"
            aria-label={theme === 'dark' ? '라이트 모드로 보기' : '다크 모드로 보기'}
            title={theme === 'dark' ? '라이트 모드' : '다크 모드'}
            onClick={toggleTheme}
          >
            {theme === 'dark' ? <Sun size={20} aria-hidden="true" /> : <Moon size={20} aria-hidden="true" />}
          </button>
        </div>
      </div>
    </header>
  );
}

// 모바일 하단 탭. 고객·비로그인 = 홈·커뮤니티·내 요청서·채팅·마이 / 고수 = 받은 요청·보낸 견적·커뮤니티·채팅·마이
// 관리자는 상단 메뉴를 쓰므로 하단 탭이 없다
function tabsFor(role) {
  const community = { href: '#/community', label: '커뮤니티', Icon: MessagesSquare, match: /^\/community/ };
  const chat = { href: '#/chats', label: '채팅', Icon: MessageCircle, match: /^\/chats/ };
  const my = { href: '#/mypage', label: '마이', Icon: UserRound, match: /^\/mypage|^\/expert\/|^\/bookmarks|^\/credit/ };
  if (role === 'EXPERT') {
    return [
      { href: '#/received', label: '받은 요청', Icon: Inbox, match: /^\/received/ },
      { href: '#/quotes', label: '보낸 견적', Icon: Send, match: /^\/quotes/ },
      community,
      chat,
      my,
    ];
  }
  return [
    { href: '#/', label: '홈', Icon: House, match: /^\/$|^\/requests\/new/ },
    community,
    { href: '#/requests', label: '내 요청서', Icon: FileText, match: /^\/requests$/ },
    chat,
    my,
  ];
}

// 하단 탭을 보여 줄 화면 (각 탭의 첫 화면만)
export function showMobileTabs(path, params, role) {
  if (role === 'ADMIN') return false;
  if (path === '/requests' || path === '/received') return !params.get('id');
  return ['/', '/community', '/chats', '/mypage', '/quotes'].includes(path);
}

export function MobileTabBar({ path }) {
  const { status, me } = useSession();
  const role = status === 'member' && me ? me.role : null;
  return (
    <nav className="m-tabbar" aria-label="하단 메뉴">
      {tabsFor(role).map(({ href, label, Icon, match }) => {
        const on = match.test(path);
        return (
          <a key={href} href={href} className={on ? 'on' : ''} aria-current={on ? 'page' : undefined}>
            <Icon size={22} strokeWidth={on ? 2.2 : 1.8} aria-hidden="true" />
            <span>{label}</span>
          </a>
        );
      })}
    </nav>
  );
}
