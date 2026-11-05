import { useEffect, useId, useMemo, useRef, useState } from 'react';
import { Search, X } from 'lucide-react';
import { ALL_SUBS, QUICK_PICKS, getSub, objectSrc } from '../data/categoryMeta.js';

const normalize = (s) => s.replace(/[\s/·-]/g, '').toLowerCase();

// 소분류 108개를 검색하는 콤보박스. 한글 조합 중 Enter는 무시한다.
export default function ServicePicker({ value, onChange, inputId, autoFocus = false, label = '서비스' }) {
  const [query, setQuery] = useState(value?.name ?? '');
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(0);
  const listId = useId();
  const wrapRef = useRef(null);
  const inputRef = useRef(null);

  useEffect(() => {
    setQuery(value?.name ?? '');
  }, [value]);

  useEffect(() => {
    if (autoFocus) inputRef.current?.focus();
  }, [autoFocus]);

  useEffect(() => {
    if (!open) return undefined;
    const onDown = (e) => {
      if (!wrapRef.current?.contains(e.target)) setOpen(false);
    };
    document.addEventListener('pointerdown', onDown);
    return () => document.removeEventListener('pointerdown', onDown);
  }, [open]);

  const results = useMemo(() => {
    const q = normalize(query);
    if (!q || (value && query === value.name)) return QUICK_PICKS.map(getSub);
    // 소분류 이름이 맞는 항목을 먼저 보여주고 대분류 이름만 맞는 항목은 뒤에 둔다.
    const byName = ALL_SUBS.filter((s) => normalize(s.name).includes(q));
    const byParent = ALL_SUBS.filter((s) => !byName.includes(s) && normalize(s.parentName).includes(q));
    return [...byName, ...byParent].slice(0, 8);
  }, [query, value]);

  const showingQuick = !normalize(query) || (value && query === value.name);

  const choose = (sub) => {
    onChange(sub);
    setQuery(sub.name);
    setOpen(false);
  };

  const onKeyDown = (e) => {
    if (e.nativeEvent.isComposing) return;
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setOpen(true);
      setActive((i) => Math.min(i + 1, results.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setActive((i) => Math.max(i - 1, 0));
    } else if (e.key === 'Enter') {
      if (open && results[active]) {
        e.preventDefault();
        choose(results[active]);
      }
    } else if (e.key === 'Escape') {
      setOpen(false);
    }
  };

  return (
    <div className="picker" ref={wrapRef}>
      <label className="field-label" htmlFor={inputId}>
        {label}
      </label>
      <div className="field-control">
        <Search className="field-icon" size={20} strokeWidth={2.2} aria-hidden="true" />
        <input
          ref={inputRef}
          id={inputId}
          type="text"
          name="service"
          role="combobox"
          autoComplete="off"
          spellCheck={false}
          enterKeyHint="search"
          placeholder="어떤 서비스가 필요하세요?"
          aria-expanded={open}
          aria-controls={listId}
          aria-autocomplete="list"
          aria-activedescendant={open && results[active] ? `${listId}-${results[active].id}` : undefined}
          value={query}
          onFocus={() => setOpen(true)}
          onChange={(e) => {
            setQuery(e.target.value);
            setActive(0);
            setOpen(true);
            if (value) onChange(null);
          }}
          onKeyDown={onKeyDown}
        />
        {query && (
          <button
            type="button"
            className="field-clear"
            aria-label="서비스 지우기"
            onClick={() => {
              setQuery('');
              onChange(null);
              inputRef.current?.focus();
            }}
          >
            <X size={16} aria-hidden="true" />
          </button>
        )}
      </div>

      {open && (
        <div className="popover picker-pop">
          <p className="pop-caption">{showingQuick ? '바로 고를 수 있는 서비스' : `검색 결과 ${results.length}개`}</p>
          <ul id={listId} role="listbox" aria-label="서비스 목록">
            {results.map((s, i) => (
              <li
                key={s.id}
                id={`${listId}-${s.id}`}
                role="option"
                aria-selected={i === active}
                className={`option${i === active ? ' is-active' : ''}`}
                onPointerDown={(e) => e.preventDefault()}
                onClick={() => choose(s)}
                onMouseEnter={() => setActive(i)}
              >
                <span className="option-thumb" aria-hidden="true">
                  <img src={objectSrc(s.object)} alt="" width="28" height="28" />
                </span>
                <span className="option-name">{s.name}</span>
                <span className="option-parent">{s.parentName}</span>
              </li>
            ))}
          </ul>
          {results.length === 0 && (
            <p className="pop-empty">
              ‘{query}’에 맞는 서비스가 없습니다. 더 짧은 단어로 찾아보거나 아래 카테고리에서 골라 주세요.
            </p>
          )}
        </div>
      )}
    </div>
  );
}
