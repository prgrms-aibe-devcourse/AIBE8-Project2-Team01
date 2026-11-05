import { useCallback, useEffect, useRef, useState } from 'react';

// 가로 레일: 양 끝에 닿았는지 알려 주고 카드 한 장씩 넘긴다.
export function useRail() {
  const ref = useRef(null);
  const [edge, setEdge] = useState({ start: true, end: false });

  const update = useCallback(() => {
    const el = ref.current;
    if (!el) return;
    setEdge({ start: el.scrollLeft <= 4, end: el.scrollLeft + el.clientWidth >= el.scrollWidth - 4 });
  }, []);

  useEffect(() => {
    update();
    const ro = new ResizeObserver(update);
    if (ref.current) ro.observe(ref.current);
    return () => ro.disconnect();
  }, [update]);

  const step = useCallback((dir) => {
    const el = ref.current;
    if (!el) return;
    const item = el.querySelector('li');
    const w = item ? item.getBoundingClientRect().width + parseFloat(getComputedStyle(el).columnGap || 0) : el.clientWidth;
    el.scrollBy({ left: dir * w, behavior: 'smooth' });
  }, []);

  return { ref, edge, update, step };
}
