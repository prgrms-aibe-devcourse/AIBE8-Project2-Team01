// 화면 연결 (해시 라우팅)
// #/login · #/mypage · #/mypage/info · #/expert/intro · #/expert/register
// #/expert/me · #/expert/me/edit · #/experts/{expertProfileId} (공개)
import { useEffect } from 'react';
import { errorText } from './api/client';
import { useSession } from './auth/session';
import { Header } from './components/Header';
import { ErrorView, Lines, LoadingView, Modal } from './components/ui';
import { navigate, useHashRoute } from './hooks/useHashRoute';
import { ExpertFormPage } from './pages/ExpertFormPage';
import { ExpertIntroPage } from './pages/ExpertIntroPage';
import { MyExpertProfilePage, PublicExpertProfilePage } from './pages/ExpertProfilePages';
import { LoginPage } from './pages/LoginPage';
import { MyInfoPage } from './pages/MyInfoPage';
import { MyPage } from './pages/MyPage';

const PRIVATE: Record<string, () => JSX.Element> = {
  '/mypage': () => <MyPage />,
  '/mypage/info': () => <MyInfoPage />,
  '/expert/intro': () => <ExpertIntroPage />,
  '/expert/register': () => <ExpertFormPage mode="create" />,
  '/expert/me': () => <MyExpertProfilePage />,
  '/expert/me/edit': () => <ExpertFormPage mode="edit" />
};

export function App() {
  const { path, params } = useHashRoute();
  const { status, loadError, reloadMe, expired, dismissExpired } = useSession();

  // 로그인이 필요한 화면에 비로그인으로 들어오면 로그인 화면으로 (돌아올 경로 기억)
  // 로그인 만료로 끊긴 경우에는 안내 모달을 먼저 보여 준다
  useEffect(() => {
    if (status === 'guest' && PRIVATE[path] && !expired) navigate('/login', { redirect: path }, true);
    if (status === 'member' && path === '/login') navigate(params.get('redirect') || '/mypage', undefined, true);
    if (path === '/') navigate(status === 'guest' ? '/login' : '/mypage', undefined, true);
  }, [status, path, params, expired]);

  const goLogin = () => { dismissExpired(); navigate('/login', PRIVATE[path] ? { redirect: path } : undefined, true); };

  let body: JSX.Element;
  const pub = path.match(/^\/experts\/(\d+)$/);
  if (pub) body = <PublicExpertProfilePage id={Number(pub[1])} />;
  else if (path === '/login') body = status === 'member' ? <main className="page"><LoadingView /></main> : <LoginPage redirect={params.get('redirect')} />;
  else if (PRIVATE[path]) {
    if (status === 'member') body = PRIVATE[path]();
    else if (status === 'guest' && expired) body = <main className="page" />;
    else if (status === 'error') body = <main className="page"><ErrorView title="내 정보를 불러오지 못했어요" message={errorText(loadError)} onRetry={() => reloadMe().catch(() => undefined)} /></main>;
    else body = <main className="page"><LoadingView /></main>;
  } else if (path === '/') body = <main className="page"><LoadingView /></main>;
  else body = <main className="page"><div className="state card"><b>페이지를 찾을 수 없어요</b><a className="btn" href="#/mypage">마이페이지로</a></div></main>;

  return (
    <div className="app">
      <Header />
      {body}
      {expired && status === 'guest' && path !== '/login' && (
        <Modal
          title="다시 로그인해 주세요"
          onClose={PRIVATE[path] ? undefined : dismissExpired}
          actions={<button type="button" className="btn primary" onClick={goLogin}>로그인</button>}
        >
          <p><Lines text={'로그인 시간이 지났어요.\n다시 로그인하면 보던 화면을 이용할 수 있어요.'} /></p>
        </Modal>
      )}
    </div>
  );
}
