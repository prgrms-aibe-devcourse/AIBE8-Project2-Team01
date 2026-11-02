// 해시 링크를 버튼 모양으로
import type { ReactNode } from 'react';

export function LinkButton({ href, primary, children }: { href: string; primary?: boolean; children: ReactNode }) {
  return (
    <a className={`btn link ${primary ? 'primary' : 'ghost'}`} href={href}>
      {children}
    </a>
  );
}
