// 관리자 회원 관리의 역할 선택 드롭다운 (select-only combobox 패턴)
// 포커스는 버튼에 그대로 두고 aria-activedescendant로 목록의 현재 항목을 알린다.
// 표 카드가 가로 스크롤 영역이라 목록은 body로 띄워서(fixed) 잘리지 않게 한다.
import { useCallback, useEffect, useId, useLayoutEffect, useRef, useState } from 'react';
import type { KeyboardEvent } from 'react';
import { createPortal } from 'react-dom';
import { Check, ChevronDown } from 'lucide-react';

export type RoleOption<T extends string> = { value: T; label: string };

type Props<T extends string> = {
  value: T;
  current: T; // 회원의 지금 역할 (목록에 '현재'로 표시)
  options: RoleOption<T>[];
  onChange: (value: T) => void;
  label: string; // 스크린리더용 이름 (예: '고객하나 역할 선택')
};

const GAP = 6;

export function RoleSelect<T extends string>({ value, current, options, onChange, label }: Props<T>) {
  const id = useId();
  const btnRef = useRef<HTMLButtonElement>(null);
  const listRef = useRef<HTMLUListElement>(null);
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);
  const [pos, setPos] = useState<{ top: number; left: number; width: number; up: boolean } | null>(null);
  const selIndex = Math.max(0, options.findIndex((o) => o.value === value));
  const selected = options[selIndex];

  const place = useCallback(() => {
    const btn = btnRef.current;
    if (!btn) return;
    const r = btn.getBoundingClientRect();
    const h = listRef.current?.offsetHeight ?? options.length * 42 + 12;
    const up = r.bottom + GAP + h > window.innerHeight && r.top - GAP - h > 0;
    setPos({ top: up ? r.top - GAP - h : r.bottom + GAP, left: r.left, width: r.width, up });
  }, [options.length]);

  useLayoutEffect(() => {
    if (open) place();
  }, [open, place]);

  useEffect(() => {
    if (!open) return;
    const onMove = () => place();
    window.addEventListener('scroll', onMove, true);
    window.addEventListener('resize', onMove);
    return () => {
      window.removeEventListener('scroll', onMove, true);
      window.removeEventListener('resize', onMove);
    };
  }, [open, place]);

  const show = () => {
    setActive(selIndex);
    setOpen(true);
  };
  const close = () => {
    setOpen(false);
    setPos(null);
  };
  const choose = (i: number) => {
    const o = options[i];
    if (o && o.value !== value) onChange(o.value);
    close();
  };

  const onKeyDown = (e: KeyboardEvent<HTMLButtonElement>) => {
    if (e.nativeEvent.isComposing) return;
    const last = options.length - 1;
    if (!open) {
      if (['ArrowDown', 'ArrowUp', 'Enter', ' '].includes(e.key)) {
        e.preventDefault();
        show();
      }
      return;
    }
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setActive((a) => Math.min(last, a + 1));
        break;
      case 'ArrowUp':
        e.preventDefault();
        if (e.altKey) choose(active);
        else setActive((a) => Math.max(0, a - 1));
        break;
      case 'Home':
        e.preventDefault();
        setActive(0);
        break;
      case 'End':
        e.preventDefault();
        setActive(last);
        break;
      case 'Enter':
      case ' ':
        e.preventDefault();
        choose(active);
        break;
      case 'Escape':
        e.preventDefault();
        e.stopPropagation();
        close();
        break;
      case 'Tab':
        close();
        break;
    }
  };

  const changed = value !== current;

  return (
    <>
      <button
        ref={btnRef}
        type="button"
        role="combobox"
        className={`role-pick${open ? ' is-open' : ''}${changed ? ' is-changed' : ''}`}
        aria-label={label}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={`${id}-list`}
        aria-activedescendant={open ? `${id}-opt-${active}` : undefined}
        onClick={() => (open ? close() : show())}
        onKeyDown={onKeyDown}
        onBlur={close}
      >
        <span className="role-pick-value">{selected?.label}</span>
        <ChevronDown className="role-pick-icon" size={16} strokeWidth={2.2} aria-hidden="true" />
      </button>
      {open &&
        createPortal(
          <ul
            ref={listRef}
            id={`${id}-list`}
            role="listbox"
            aria-label={label}
            className={`role-menu${pos?.up ? ' is-up' : ''}`}
            style={pos ? { top: pos.top, left: pos.left, minWidth: pos.width } : { visibility: 'hidden' }}
          >
            {options.map((o, i) => (
              <li
                key={o.value}
                id={`${id}-opt-${i}`}
                role="option"
                aria-selected={o.value === value}
                className={`role-opt${i === active ? ' is-active' : ''}`}
                onMouseDown={(e) => e.preventDefault()}
                onMouseEnter={() => setActive(i)}
                onClick={() => choose(i)}
              >
                <Check className="role-opt-check" size={16} strokeWidth={2.6} aria-hidden="true" />
                <span className="role-opt-label">{o.label}</span>
                {o.value === current && <span className="role-opt-now">현재</span>}
              </li>
            ))}
          </ul>,
          document.body,
        )}
    </>
  );
}
