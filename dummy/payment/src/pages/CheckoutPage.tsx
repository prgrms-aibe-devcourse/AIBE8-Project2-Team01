// 화면 2: 선택 상품·충전 크레딧·결제 금액 확인 + 모의 결제
import { useState } from 'react';
import { creditApi, dummyControls } from '../api/creditApi';
import { ApiPanel } from '../components/ApiPanel';
import { LinkButton } from '../components/LinkButton';
import { MockPayModal, type MockOutcome } from '../components/MockPayModal';
import { StepHeader } from '../components/StepHeader';
import { useDummy } from '../DummyContext';
import { useAsync } from '../hooks/useAsync';
import { navigate } from '../hooks/useHashRoute';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { formatWon } from '../lib/format';
import { MOCK_FAIL_CODE, USER_CANCEL_CODE } from '../lib/policy';
import type { ApiError, PaymentCreateResponse, PaymentStatusResponse, PaymentConfirmResponse } from '../types';

export function CheckoutPage({ productId }: { productId: number }) {
  const { data, error } = useAsync(() => Promise.all([creditApi.getMyCredit(), creditApi.getCreditProducts()]), [productId]);
  const { saveResult, showToast } = useDummy();
  const { busy, run } = useSingleFlight();
  // 결제창이 열린 주문. 결과가 나오면 비워서 같은 주문으로 다시 결제창을 열지 않는다
  const [order, setOrder] = useState<PaymentCreateResponse | null>(null);

  if (error) return <p className="empty">{error}</p>;
  if (!data) return <p className="empty">불러오는 중…</p>;
  const [credit, products] = data;
  const product = products.find((p) => p.id === productId);

  if (!product) {
    return (
      <p className="empty">
        선택한 상품이 없습니다. <a href="#/credit">상품 다시 선택</a>
      </p>
    );
  }

  const charge = product.creditAmount + product.bonusCredit;

  // [결제하기]: READY 주문 생성 후 결제창 열기
  const startPayment = () =>
    run(async () => {
      try {
        setOrder(await creditApi.createPayment({ creditProductId: product.id }));
      } catch (e) {
        showToast((e as ApiError).message);
      }
    });

  // 결제창 결과 처리
  const finishPayment = (outcome: MockOutcome) => {
    if (!order) return;
    const current = order;
    return run(async () => {
      const mockPaymentKey = `mock_pk_${Date.now().toString(36)}`;
      try {
        let res: PaymentConfirmResponse | PaymentStatusResponse;
        if (outcome === 'success') {
          // successUrl로 받은 값으로 승인 요청 (paymentKey는 mock 값)
          res = await creditApi.confirmPayment({ paymentKey: mockPaymentKey, orderId: current.orderId, amount: current.amount });
        } else if (outcome === 'timeout') {
          res = await dummyControls.confirmPaymentTimeout({ paymentKey: mockPaymentKey, orderId: current.orderId, amount: current.amount });
        } else {
          // failUrl로 받은 code·message 전달
          res = await creditApi.failPayment(current.orderId, {
            code: outcome === 'cancel' ? USER_CANCEL_CODE : MOCK_FAIL_CODE,
            message: outcome === 'cancel' ? '사용자에 의해 결제가 취소되었습니다.' : '모의 결제 실패'
          });
        }
        saveResult({
          orderId: current.orderId,
          paymentId: current.paymentId,
          productId: product.id,
          orderName: current.orderName,
          amount: current.amount,
          paymentStatus: res.paymentStatus,
          chargedCredit: 'chargedCredit' in res ? res.chargedCredit : 0,
          balance: 'balance' in res ? res.balance : null
        });
        setOrder(null);
        navigate('/result', { orderId: current.orderId });
      } catch (e) {
        showToast((e as ApiError).message);
      }
    });
  };

  return (
    <>
      <StepHeader step={2} label="결제 정보 확인" title="결제 정보 확인" />
      <div className="box">
        <dl className="kv">
          <dt>선택 상품</dt>
          <dd>
            {product.name} {product.isExample && <span className="tag example">예시 데이터</span>}
          </dd>
          <dt>충전 크레딧</dt>
          <dd>{charge}크레딧</dd>
          <dt>결제 금액</dt>
          <dd className="total">{formatWon(product.price)}</dd>
          <dt>충전 후 총잔액</dt>
          <dd>
            {credit.balance} → {credit.balance + charge} (예상)
          </dd>
          <dt>충전 후 사용 가능</dt>
          <dd>
            {credit.availableCredit} → {credit.availableCredit + charge} (예상)
          </dd>
        </dl>
      </div>
      <p className="notice">테스트 결제입니다. 결제 금액은 서버의 상품 가격으로 정해지며 화면에서 금액을 보내지 않습니다.</p>
      <div className="actions">
        <button type="button" className="btn primary" id="btn-pay" disabled={busy || order !== null} onClick={startPayment}>
          {formatWon(product.price)} 결제하기
        </button>
        <LinkButton href="#/credit">상품 다시 선택</LinkButton>
      </div>

      <ApiPanel
        points={[
          { method: 'POST', path: '/api/payments', id: 'API-68', status: 'draft', note: '요청 { creditProductId } → 응답 paymentId·orderId·orderName·amount. READY 주문 생성' },
          { method: '—', path: '토스 테스트 결제창 호출', status: 'draft', note: 'orderId·orderName·amount를 그대로 전달. 이 더미에서는 모의 결제창으로 대체' }
        ]}
        response={order ?? { product, credit }}
      />

      {order && <MockPayModal order={order} busy={busy} onSelect={finishPayment} />}
    </>
  );
}
