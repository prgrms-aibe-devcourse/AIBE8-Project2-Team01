// 화면 간 공유 상태: 결제 결과·방금 충전한 결제·토스트
// 메모리에만 보관하므로 새로고침하면 초기화된다
import { createContext, useCallback, useContext, useMemo, useRef, useState, type ReactNode } from 'react';
import type { PaymentStatus } from './types';

export interface PaymentResult {
  orderId: string;
  paymentId: number;
  productId: number;
  orderName: string;
  amount: number;
  paymentStatus: PaymentStatus;
  chargedCredit: number;
  /** 결과 확인 시점의 총잔액 (DONE일 때만 의미 있음) */
  balance: number | null;
}

interface DummyContextValue {
  results: Record<string, PaymentResult>;
  saveResult: (result: PaymentResult) => void;
  newPaymentIds: number[];
  toast: string | null;
  showToast: (message: string) => void;
  clearAll: () => void;
}

const Ctx = createContext<DummyContextValue | null>(null);

export function DummyProvider({ children }: { children: ReactNode }) {
  const [results, setResults] = useState<Record<string, PaymentResult>>({});
  const [newPaymentIds, setNewPaymentIds] = useState<number[]>([]);
  const [toast, setToast] = useState<string | null>(null);
  const timer = useRef<number>();

  const saveResult = useCallback((result: PaymentResult) => {
    setResults((prev) => ({ ...prev, [result.orderId]: result }));
    if (result.paymentStatus === 'DONE') {
      // 같은 결제를 두 번 강조하지 않는다
      setNewPaymentIds((prev) => (prev.includes(result.paymentId) ? prev : [...prev, result.paymentId]));
    }
  }, []);

  const showToast = useCallback((message: string) => {
    setToast(message);
    window.clearTimeout(timer.current);
    timer.current = window.setTimeout(() => setToast(null), 2600);
  }, []);

  const clearAll = useCallback(() => {
    setResults({});
    setNewPaymentIds([]);
  }, []);

  const value = useMemo(
    () => ({ results, saveResult, newPaymentIds, toast, showToast, clearAll }),
    [results, saveResult, newPaymentIds, toast, showToast, clearAll]
  );

  return <Ctx.Provider value={value}>{children}</Ctx.Provider>;
}

export function useDummy(): DummyContextValue {
  const value = useContext(Ctx);
  if (!value) throw new Error('DummyProvider 안에서 사용해야 합니다.');
  return value;
}
