// 토스페이먼츠 테스트 결제창 (SDK v2 스크립트를 결제할 때만 불러온다)
// 성공·실패 시 토스가 successUrl·failUrl에 쿼리를 붙여 돌려보낸다 → App이 #/credit/result로 넘긴다
const SDK = 'https://js.tosspayments.com/v2/standard';

export const tossClientKey = () => (import.meta.env.VITE_TOSS_CLIENT_KEY as string | undefined)?.trim() || '';

interface TossWindow { TossPayments?: (key: string) => { payment: (o: { customerKey: string }) => { requestPayment: (o: unknown) => Promise<void> } }; ANONYMOUS?: string }

function loadSdk(): Promise<void> {
  const w = window as unknown as TossWindow;
  if (w.TossPayments) return Promise.resolve();
  return new Promise((resolve, reject) => {
    const s = document.createElement('script');
    s.src = SDK;
    s.onload = () => resolve();
    s.onerror = () => reject(new Error('결제 모듈을 불러오지 못했어요.'));
    document.head.appendChild(s);
  });
}

export async function openTossPayment(order: { orderId: string; orderName: string; amount: number }): Promise<void> {
  const key = tossClientKey();
  if (!key) throw new Error('결제 클라이언트 키가 설정되지 않았어요.');
  await loadSdk();
  const w = window as unknown as TossWindow;
  if (!w.TossPayments) throw new Error('결제 모듈을 불러오지 못했어요.');
  const back = `${window.location.origin}${window.location.pathname}`;
  const payment = w.TossPayments(key).payment({ customerKey: w.ANONYMOUS ?? '__ANONYMOUS__' });
  await payment.requestPayment({
    method: 'CARD',
    amount: { currency: 'KRW', value: order.amount },
    orderId: order.orderId,
    orderName: order.orderName,
    successUrl: `${back}?toss=success`,
    failUrl: `${back}?toss=fail`
  });
}
