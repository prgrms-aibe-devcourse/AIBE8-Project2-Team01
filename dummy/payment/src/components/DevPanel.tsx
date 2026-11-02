// 더미 컨트롤 패널: mock 상태 확인·초기화 (실서비스 제외)
import { useState } from 'react';
import { dummyControls } from '../api/creditApi';
import { useDummy } from '../DummyContext';
import { useDummySnapshot } from '../hooks/useDummySnapshot';
import { navigate } from '../hooks/useHashRoute';
import { formatWon } from '../lib/format';

export function DevPanel() {
  const snap = useDummySnapshot();
  const { clearAll, showToast } = useDummy();
  const [collapsed, setCollapsed] = useState(false);

  const reset = () => {
    dummyControls.reset();
    clearAll();
    showToast('mock 데이터를 초기 상태로 되돌렸습니다.');
    navigate('/credit');
  };

  return (
    <aside className={`devpanel${collapsed ? ' collapsed' : ''}`} aria-label="더미 컨트롤">
      <div className="devpanel-head">
        <strong>더미 컨트롤</strong>
        <button type="button" className="btn small" onClick={reset}>
          초기 상태로
        </button>
        <button type="button" className="btn small ghost" aria-expanded={!collapsed} onClick={() => setCollapsed((v) => !v)}>
          {collapsed ? '펼치기' : '접기'}
        </button>
      </div>
      {!collapsed && (
        <div className="devpanel-body">
          <p>
            mock 총잔액 <b>{snap.balance}</b> · 예약 <b>{snap.reserved}</b> · 사용 가능 <b>{snap.balance - snap.reserved}</b> · 내역{' '}
            {snap.historyCount}건 · 주문 {snap.payments.length}건{' '}
            <span className="muted">(DONE이 된 주문만 잔액·내역에 1회 반영)</span>
          </p>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>paymentId</th>
                  <th>orderId</th>
                  <th>상품</th>
                  <th className="num">금액</th>
                  <th>상태</th>
                </tr>
              </thead>
              <tbody>
                {[...snap.payments].reverse().map((p) => (
                  <tr key={p.orderId}>
                    <td>{p.paymentId}</td>
                    <td>{p.orderId}</td>
                    <td>{p.orderName}</td>
                    <td className="num">{formatWon(p.amount)}</td>
                    <td>{p.status}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </aside>
  );
}
