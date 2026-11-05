import { useEffect, useId, useRef, useState } from 'react';
import { Check, ChevronDown, MapPin } from 'lucide-react';
import { FIRST_REGIONS } from '../data/baseData.js';
import { regionLabel, shortSido } from '../lib/homeFormat.js';

// 시도를 고르고 시군구를 고르는 두 단계 선택. 요청서에는 시군구 ID 하나가 들어간다.
export default function RegionPicker({ value, onChange, buttonId, label = '지역' }) {
  const [open, setOpen] = useState(false);
  const [firstId, setFirstId] = useState(value?.first.id ?? FIRST_REGIONS[0].id);
  const wrapRef = useRef(null);
  const btnRef = useRef(null);
  const popId = useId();

  useEffect(() => {
    if (value) setFirstId(value.first.id);
  }, [value]);

  useEffect(() => {
    if (!open) return undefined;
    const onDown = (e) => {
      if (!wrapRef.current?.contains(e.target)) setOpen(false);
    };
    const onKey = (e) => {
      if (e.key === 'Escape') {
        e.stopPropagation();
        setOpen(false);
        btnRef.current?.focus();
      }
    };
    document.addEventListener('pointerdown', onDown);
    document.addEventListener('keydown', onKey, true);
    return () => {
      document.removeEventListener('pointerdown', onDown);
      document.removeEventListener('keydown', onKey, true);
    };
  }, [open]);

  const first = FIRST_REGIONS.find((r) => r.id === firstId);

  return (
    <div className="picker" ref={wrapRef}>
      <span className="field-label" id={`${buttonId}-label`}>
        {label}
      </span>
      <button
        ref={btnRef}
        id={buttonId}
        type="button"
        className="field-control field-button"
        aria-haspopup="dialog"
        aria-expanded={open}
        aria-controls={popId}
        aria-labelledby={`${buttonId}-label ${buttonId}`}
        onClick={() => setOpen((v) => !v)}
      >
        <MapPin className="field-icon" size={20} strokeWidth={2.2} aria-hidden="true" />
        <span className={value ? 'field-value' : 'field-placeholder'}>{value ? regionLabel(value) : '시·군·구 선택'}</span>
        <ChevronDown className="field-caret" size={18} aria-hidden="true" />
      </button>

      {open && (
        <div id={popId} className="popover region-pop" role="dialog" aria-label="지역 선택">
          <div className="region-cols">
            <ul className="region-first" aria-label="시·도">
              {FIRST_REGIONS.map((r) => (
                <li key={r.id}>
                  <button
                    type="button"
                    className={`region-item${r.id === firstId ? ' is-current' : ''}`}
                    aria-pressed={r.id === firstId}
                    onClick={() => setFirstId(r.id)}
                  >
                    {shortSido(r.name)}
                  </button>
                </li>
              ))}
            </ul>
            <ul className="region-second" aria-label={`${first.name} 시·군·구`}>
              {first.subs.map((s) => {
                const selected = value?.second.id === s.id;
                return (
                  <li key={s.id}>
                    <button
                      type="button"
                      className={`region-item${selected ? ' is-selected' : ''}`}
                      aria-pressed={selected}
                      onClick={() => {
                        onChange({ first: { id: first.id, name: first.name }, second: s });
                        setOpen(false);
                        btnRef.current?.focus();
                      }}
                    >
                      {s.name}
                      {selected && <Check size={16} aria-hidden="true" />}
                    </button>
                  </li>
                );
              })}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
}
