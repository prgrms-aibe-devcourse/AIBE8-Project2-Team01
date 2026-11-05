import { useCallback, useEffect, useState } from 'react';

// 라이트·다크 테마. index.html의 첫 스크립트가 data-theme를 먼저 붙이고 이 훅은 그 값을 이어받는다.
// 사용자가 직접 고르기 전까지는 시스템 설정이 바뀔 때 함께 바뀐다.
const KEY = 'efy.theme';
const media = () => window.matchMedia('(prefers-color-scheme: dark)');

function readSaved() {
  try {
    const t = localStorage.getItem(KEY);
    return t === 'light' || t === 'dark' ? t : null;
  } catch {
    return null;
  }
}

function apply(theme) {
  document.documentElement.dataset.theme = theme;
}

export function useTheme() {
  const [theme, setTheme] = useState(() => document.documentElement.dataset.theme || (media().matches ? 'dark' : 'light'));

  useEffect(() => {
    const mq = media();
    const onChange = (e) => {
      if (readSaved()) return;
      const next = e.matches ? 'dark' : 'light';
      apply(next);
      setTheme(next);
    };
    mq.addEventListener('change', onChange);
    return () => mq.removeEventListener('change', onChange);
  }, []);

  const toggle = useCallback(() => {
    setTheme((cur) => {
      const next = cur === 'dark' ? 'light' : 'dark';
      apply(next);
      try {
        localStorage.setItem(KEY, next);
      } catch {
        // 저장이 막혀 있어도 이번 방문 동안은 바뀐 테마를 유지한다
      }
      return next;
    });
  }, []);

  return { theme, toggle };
}
