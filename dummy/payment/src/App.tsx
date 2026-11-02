// 더미 화면 레이아웃과 라우팅
import { DevPanel } from './components/DevPanel';
import { Gnb } from './components/Gnb';
import { DummyProvider, useDummy } from './DummyContext';
import { useHashRoute } from './hooks/useHashRoute';
import { CheckoutPage } from './pages/CheckoutPage';
import { CreditPage } from './pages/CreditPage';
import { HistoryPage } from './pages/HistoryPage';
import { ResultPage } from './pages/ResultPage';

function Screen() {
  const route = useHashRoute();
  const { toast } = useDummy();

  // key로 경로가 바뀔 때 화면 상태를 새로 시작한다
  const routeKey = `${route.path}?${route.params.toString()}`;
  let page;
  switch (route.path) {
    case '/checkout':
      page = <CheckoutPage key={routeKey} productId={Number(route.params.get('productId'))} />;
      break;
    case '/result':
      page = <ResultPage key={routeKey} orderId={route.params.get('orderId') ?? ''} />;
      break;
    case '/history':
      page = <HistoryPage key={routeKey} />;
      break;
    default:
      page = <CreditPage key={routeKey} />;
  }

  return (
    <>
      <div className="dummy-banner">
        더미 화면 · mock 데이터로 동작 · 실제 결제·백엔드 API를 호출하지 않습니다 <a href="./wireframe.html">와이어프레임 보기</a>
      </div>
      <Gnb path={route.path} />
      <main id="app" aria-live="polite">
        {page}
      </main>
      <DevPanel />
      {toast && (
        <div className="toast" id="toast" role="status">
          {toast}
        </div>
      )}
    </>
  );
}

export function App() {
  return (
    <DummyProvider>
      <Screen />
    </DummyProvider>
  );
}
