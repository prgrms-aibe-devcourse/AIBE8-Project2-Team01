// 간단한 완료·실패 안내 (몇 초 뒤 사라짐)
import { createContext, useCallback, useContext, useState, type ReactNode } from 'react';
import { Lines } from './ui';

interface Toast { id: number; text: string; error: boolean }
const Ctx = createContext<(text: string, error?: boolean) => void>(() => undefined);

export function ToastProvider({ children }: { children: ReactNode }) {
  const [list, setList] = useState<Toast[]>([]);
  const show = useCallback((text: string, error = false) => {
    const id = Date.now() + Math.random();
    setList((l) => [...l, { id, text, error }]);
    setTimeout(() => setList((l) => l.filter((t) => t.id !== id)), error ? 5000 : 3000);
  }, []);
  return (
    <Ctx.Provider value={show}>
      {children}
      <div className="toasts" aria-live="polite">
        {list.map((t) => (
          <div key={t.id} className={`toast${t.error ? ' error' : ''}`} role={t.error ? 'alert' : 'status'}>
            {!t.error && <span aria-hidden="true">✓</span>}<span><Lines text={t.text} /></span>
          </div>
        ))}
      </div>
    </Ctx.Provider>
  );
}

export const useToast = () => useContext(Ctx);
