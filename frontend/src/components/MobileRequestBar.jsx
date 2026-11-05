import { regionLabel } from '../lib/homeFormat.js';

// 모바일에서 히어로 슬립이 화면 밖으로 나가면 아래에 요청서 버튼을 고정한다.
export default function MobileRequestBar({ visible, draft, onOpen }) {
  const summary = [draft.sub?.name, regionLabel(draft.region)].filter(Boolean).join(' / ');
  return (
    <div className={`mobile-bar${visible ? ' is-visible' : ''}`} aria-hidden={!visible}>
      <button type="button" className="btn btn-primary" tabIndex={visible ? 0 : -1} onClick={onOpen}>
        {summary ? `${summary} 요청서 작성` : '요청서 작성하기'}
      </button>
    </div>
  );
}
