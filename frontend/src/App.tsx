// 화면 연결 (해시 라우팅)
// 공개: #/ (홈) · #/login · #/signup · #/community · #/community/{id} · #/experts/{expertProfileId}
// 로그인 필요: 마이페이지·내 정보·고수 프로필·요청서·견적·채팅·크레딧·찜·관리자
// 비로그인으로 로그인 필요 화면에 들어오면 로그인 후 원래 화면으로 돌아온다
import { useEffect } from 'react';
import { errorText } from './api/client';
import type { MemberRole } from './api/types';
import { useSession } from './auth/session';
import { RoleNotice } from './components/common';
import Footer, { showFooter } from './components/Footer.jsx';
import Header, { MobileTabBar, showMobileTabs } from './components/Header.jsx';
import { ErrorView, Lines, LoadingView, Modal } from './components/ui';
import { navigate, useHashRoute } from './hooks/useHashRoute';
import { AdminCategoriesPage, AdminRegionsPage } from './pages/AdminBaseDataPages';
import { AdminMembersPage } from './pages/AdminMembersPage';
import { AdminPostsPage, AdminQuotesPage, AdminRequestsPage } from './pages/AdminActivityPages';
import { BookmarksPage } from './pages/BookmarksPage';
import { ChatListPage } from './pages/ChatListPage';
import { CommunityPage, MyPostsPage } from './pages/CommunityPage';
import { CreditHistoryPage, CreditPage } from './pages/CreditPages';
import { ExpertFormPage } from './pages/ExpertFormPage';
import { ExpertIntroPage } from './pages/ExpertIntroPage';
import { MyExpertProfilePage, PublicExpertProfilePage, SampleExpertProfilePage } from './pages/ExpertProfilePages';
import HomePage from './pages/HomePage.jsx';
import { LoginPage } from './pages/LoginPage';
import { MyInfoPage } from './pages/MyInfoPage';
import { MyPage } from './pages/MyPage';
import { MyRequestsPage } from './pages/MyRequestsPage';
import { PaymentResultPage } from './pages/PaymentResultPage';
import { PostDetailPage } from './pages/PostDetailPage';
import { PostFormPage } from './pages/PostFormPage';
import { ReceivedRequestsPage } from './pages/ReceivedRequestsPage';
import { RequestFormPage } from './pages/RequestFormPage';
import { SentQuotesPage } from './pages/SentQuotesPage';
import { SignupPage } from './pages/SignupPage';

// public: 누구나 · member: 로그인 회원 · 역할 배열: 해당 역할만
type Access = 'public' | 'member' | MemberRole[];
interface Route {
  re: RegExp;
  access: Access;
  render: (m: RegExpMatchArray, q: URLSearchParams) => JSX.Element;
}

const ROUTES: Route[] = [
  { re: /^\/$/, access: 'public', render: (_m, q) => <HomePage params={q} /> },
  { re: /^\/signup$/, access: 'public', render: (_m, q) => <SignupPage redirect={q.get('redirect')} /> },
  { re: /^\/experts\/sample\/(\d+)$/, access: 'public', render: (m) => <SampleExpertProfilePage id={Number(m[1])} /> },
  { re: /^\/experts\/(\d+)$/, access: 'public', render: (m) => <PublicExpertProfilePage id={Number(m[1])} /> },
  { re: /^\/community$/, access: 'public', render: (_m, q) => <CommunityPage params={q} /> },
  { re: /^\/community\/mine$/, access: 'member', render: (_m, q) => <MyPostsPage params={q} /> },
  { re: /^\/community\/write$/, access: 'member', render: (_m, q) => <PostFormPage board={q.get('board')} /> },
  { re: /^\/community\/(\d+)\/edit$/, access: 'member', render: (m) => <PostFormPage postId={Number(m[1])} /> },
  { re: /^\/community\/(\d+)$/, access: 'public', render: (m) => <PostDetailPage id={Number(m[1])} /> },

  { re: /^\/mypage$/, access: 'member', render: () => <MyPage /> },
  { re: /^\/mypage\/info$/, access: 'member', render: () => <MyInfoPage /> },
  { re: /^\/expert\/intro$/, access: 'member', render: () => <ExpertIntroPage /> },
  { re: /^\/expert\/register$/, access: 'member', render: () => <ExpertFormPage mode="create" /> },
  { re: /^\/expert\/me$/, access: 'member', render: () => <MyExpertProfilePage /> },
  { re: /^\/expert\/me\/edit$/, access: 'member', render: (_m, q) => <ExpertFormPage mode="edit" focus={q.get('section')} /> },

  { re: /^\/requests\/new$/, access: ['USER'], render: (_m, q) => <RequestFormPage fc={q.get('fc')} cat={q.get('cat')} /> },
  { re: /^\/requests$/, access: ['USER'], render: (_m, q) => <MyRequestsPage selected={q.get('id')} quote={q.get('quote')} /> },
  { re: /^\/received$/, access: ['EXPERT'], render: (_m, q) => <ReceivedRequestsPage selected={q.get('id')} /> },
  { re: /^\/quotes$/, access: ['EXPERT'], render: () => <SentQuotesPage /> },
  { re: /^\/credit$/, access: ['EXPERT'], render: () => <CreditPage /> },
  { re: /^\/credit\/history$/, access: ['EXPERT'], render: (_m, q) => <CreditHistoryPage params={q} /> },
  { re: /^\/credit\/result$/, access: ['EXPERT'], render: (_m, q) => <PaymentResultPage params={q} /> },
  { re: /^\/chats$/, access: ['USER', 'EXPERT'], render: () => <ChatListPage /> },
  { re: /^\/chats\/(\d+)$/, access: ['USER', 'EXPERT'], render: (m) => <ChatListPage roomId={Number(m[1])} /> },
  { re: /^\/bookmarks$/, access: ['USER', 'ADMIN'], render: (_m, q) => <BookmarksPage params={q} /> },

  { re: /^\/admin\/members$/, access: ['ADMIN'], render: (_m, q) => <AdminMembersPage params={q} /> },
  { re: /^\/admin\/posts$/, access: ['ADMIN'], render: (_m, q) => <AdminPostsPage params={q} /> },
  { re: /^\/admin\/requests$/, access: ['ADMIN'], render: (_m, q) => <AdminRequestsPage params={q} /> },
  { re: /^\/admin\/quotes$/, access: ['ADMIN'], render: (_m, q) => <AdminQuotesPage params={q} /> },
  { re: /^\/admin\/categories$/, access: ['ADMIN'], render: () => <AdminCategoriesPage /> },
  { re: /^\/admin\/regions$/, access: ['ADMIN'], render: () => <AdminRegionsPage /> }
];

function findRoute(path: string): [Route, RegExpMatchArray] | null {
  for (const r of ROUTES) { const m = path.match(r.re); if (m) return [r, m]; }
  return null;
}

// 역할이 맞지 않을 때 안내 (와이어프레임 W·24 권한 안내)
function roleNotice(access: MemberRole[], role: MemberRole) {
  if (access.includes('EXPERT') && !access.includes('USER')) {
    return role === 'USER'
      ? <RoleNotice title="고수만 이용할 수 있어요" desc={'고수 프로필이 있으면 마이페이지에서 고수로 전환할 수 있어요.'} href="#/mypage" label="마이페이지로" />
      : <RoleNotice title="고수만 이용할 수 있어요" desc="이 화면은 고수 계정에서 이용해 주세요." href="#/" label="홈으로" />;
  }
  if (access.includes('USER') && !access.includes('EXPERT')) {
    return role === 'EXPERT'
      ? <RoleNotice title="고객 상태에서 이용할 수 있어요" desc={'요청서와 찜은 고객 상태에서 이용해요.\n마이페이지에서 고객으로 전환할 수 있어요.'} href="#/mypage" label="마이페이지로" />
      : <RoleNotice title="고객만 이용할 수 있어요" desc="이 화면은 고객 계정에서 이용해 주세요." href="#/" label="홈으로" />;
  }
  if (access.includes('ADMIN')) return <RoleNotice title="관리자만 볼 수 있어요" desc="관리자 계정으로 로그인해 주세요." href="#/" label="홈으로" />;
  return <RoleNotice title="이용할 수 없는 화면이에요" desc="고객·고수 계정에서 이용해 주세요." href="#/" label="홈으로" />;
}

export function App() {
  const { path, params } = useHashRoute();
  const { status, me, loadError, reloadMe, expired, dismissExpired } = useSession();
  const found = findRoute(path);
  const needsLogin = !!found && found[0].access !== 'public';
  const fullPath = path + (params.toString() ? `?${params.toString()}` : '');

  // 토스 결제창에서 돌아오면 쿼리(?paymentKey·orderId·amount 또는 ?code·message·orderId)를 결과 화면으로 넘긴다
  useEffect(() => {
    const s = new URLSearchParams(window.location.search);
    if (s.get('toss') && s.get('orderId')) {
      const hash = `#/credit/result?${s.toString()}`;
      window.history.replaceState(null, '', window.location.pathname + hash);
      window.dispatchEvent(new HashChangeEvent('hashchange'));
    }
  }, []);

  // 로그인이 필요한 화면에 비로그인으로 들어오면 로그인 화면으로 (돌아올 경로 기억)
  // 로그인 만료로 끊긴 경우에는 안내 모달을 먼저 보여 준다
  useEffect(() => {
    if (status === 'guest' && needsLogin && !expired) navigate('/login', { redirect: fullPath }, true);
    if (status === 'member' && path === '/login') navigate(params.get('redirect') || '/', undefined, true);
  }, [status, path, params, expired, needsLogin, fullPath]);

  const goLogin = () => { dismissExpired(); navigate('/login', needsLogin ? { redirect: fullPath } : undefined, true); };

  let body: JSX.Element;
  if (path === '/login') body = status === 'member' ? <main className="page"><LoadingView /></main> : <LoginPage redirect={params.get('redirect')} />;
  else if (!found) body = <main className="page"><div className="state card"><b>페이지를 찾을 수 없어요</b><a className="btn" href="#/">홈으로</a></div></main>;
  else {
    const [route, m] = found;
    if (route.access === 'public') body = route.render(m, params);
    else if (status === 'member' && me) {
      body = Array.isArray(route.access) && !route.access.includes(me.role) ? roleNotice(route.access, me.role) : route.render(m, params);
    } else if (status === 'guest' && expired) body = <main className="page" />;
    else if (status === 'error') body = <main className="page"><ErrorView title="내 정보를 불러오지 못했어요" message={errorText(loadError)} onRetry={() => reloadMe().catch(() => undefined)} /></main>;
    else body = <main className="page"><LoadingView /></main>;
  }

  // 모바일: 탭 첫 화면에만 하단 탭, 채팅방은 헤더 없이 전체 화면 (웹은 영향 없음)
  const tabs = showMobileTabs(path, params, status === 'member' && me ? me.role : null);
  const fullScreen = /^\/chats\/\d+$/.test(path);
  // 메인 화면은 축약 검색창이 붙은 헤더와 예시 안내가 붙은 푸터를 직접 그린다
  const isHome = path === '/' && !!found;
  return (
    <div className={`app${tabs ? ' m-tabs' : ''}${fullScreen ? ' m-full' : ''}${isHome ? ' is-home' : ''}`}>
      <a className="skip-link" href="#main" onClick={(e) => { e.preventDefault(); const m = document.querySelector('main'); if (m) { m.setAttribute('tabindex', '-1'); m.focus(); } }}>본문으로 건너뛰기</a>
      {!isHome && <Header path={path} />}
      {body}
      {!isHome && showFooter(path) && <Footer />}
      {tabs && <MobileTabBar path={path} />}
      {expired && status === 'guest' && path !== '/login' && (
        <Modal
          title="다시 로그인해 주세요"
          onClose={needsLogin ? undefined : dismissExpired}
          actions={<button type="button" className="btn primary" onClick={goLogin}>로그인</button>}
        >
          <p><Lines text={'로그인 시간이 지났어요.\n다시 로그인하면 보던 화면을 이용할 수 있어요.'} /></p>
        </Modal>
      )}
    </div>
  );
}
