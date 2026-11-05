// 마이페이지 회원 영역 (와이어프레임 W·P08~P10 / M·P08~P10)
// 요청서·채팅·내 글·찜 등 다른 도메인 영역은 이번 범위 밖이라 넣지 않는다
import { useState } from 'react';
import { errorText } from '../api/client';
import { useSession } from '../auth/session';
import { Avatar, ErrorView, LoadingView, Modal, Spinner } from '../components/ui';
import { useMyExpertProfile } from '../hooks/useData';
import { useRoleSwitch } from '../hooks/useRoleSwitch';
import { useSingleFlight } from '../hooks/useSingleFlight';
import { navigate } from '../hooks/useHashRoute';
import { formatPhone, roleLabel } from '../lib/format';

export function MyPage() {
  const { me, logout } = useSession();
  const { state, reload } = useMyExpertProfile(true);
  const { busy: switching, switchTo } = useRoleSwitch();
  const out = useSingleFlight();
  const [askLogout, setAskLogout] = useState(false);
  if (!me) return null;
  const isExpert = me.role === 'EXPERT';
  const isAdmin = me.role === 'ADMIN';

  const expertCard = () => {
    if (isAdmin) return null;
    if (state.kind === 'loading') return <section className="card"><LoadingView label="고수 프로필을 확인하는 중이에요" /></section>;
    if (state.kind === 'error') return (
      <section className="card">
        <ErrorView title="고수 프로필 정보를 불러오지 못했어요" message={errorText(state.error)} onRetry={reload} />
      </section>
    );
    if (state.kind === 'none') return (
      <section className="card promo">
        {me.role === 'EXPERT' ? (
          // 고수 역할인데 프로필이 없는 경우: 등록은 고객 상태에서만 가능
          <>
            <b className="card-title">고수 프로필이 아직 없어요</b>
            <p className="sub">고수 프로필은 고객 상태에서 등록할 수 있어요.<br />고객으로 전환한 뒤 등록해 주세요.</p>
            <button className="btn primary" disabled={switching} onClick={() => switchTo('USER', false)}>
              {switching ? <><Spinner light />전환하는 중</> : '고객으로 전환'}
            </button>
          </>
        ) : (
          <>
            <b className="card-title">고수로 활동해 보세요</b>
            <p className="sub">같은 계정으로 고수 프로필을 만들 수 있어요.<br />등록을 마치면 고수로 전환돼요.</p>
            <a className="btn primary" href="#/expert/intro">고수로 전환</a>
          </>
        )}
      </section>
    );
    const p = state.profile;
    return (
      <section className="card">
        <div className="row between">
          <b className="card-title">내 고수 프로필</b>
          {p.primaryCategory && <span className="tag teal">대표 · {p.primaryCategory.name}</span>}
        </div>
        <span className="intro">{p.shortIntro}</span>
        {!isExpert && (
          <p className="sub">고객 상태에서도 고수 프로필은 계속 공개돼요.<br />다시 등록하지 않고 바로 고수로 전환할 수 있어요.</p>
        )}
        <div className="row wrap">
          {!isExpert && (
            <button className="btn primary" disabled={switching} onClick={() => switchTo('EXPERT')}>
              {switching ? <><Spinner light />전환하는 중</> : '고수로 전환'}
            </button>
          )}
          <a className="btn" href="#/expert/me" aria-disabled={switching} onClick={(e) => switching && e.preventDefault()}>프로필 보기</a>
          <a className="btn" href="#/expert/me/edit" aria-disabled={switching} onClick={(e) => switching && e.preventDefault()}>프로필 수정</a>
          {isExpert && (
            <button className="btn" disabled={switching} onClick={() => switchTo('USER')}>
              {switching ? <><Spinner />전환하는 중</> : '고객으로 전환'}
            </button>
          )}
        </div>
      </section>
    );
  };

  return (
    <main className="page">
      <h1 className="page-title">마이페이지</h1>
      <div className="grid-2">
        <section className="card me-card">
          <Avatar url={me.imageUrl} size={88} />
          <div className="me-info">
            <span className="row"><b className="me-big">{me.nickname}</b><span className={`role-tag ${isExpert ? 'expert' : 'user'}`}>현재 역할 · {roleLabel(me.role)}</span></span>
            <span className="sub">{me.email}</span>
            {me.phone && <span className="sub">{formatPhone(me.phone)}</span>}
          </div>
          <a className="btn" href="#/mypage/info">내 정보 수정</a>
        </section>
        {expertCard()}
      </div>
      {!isAdmin && (
        <section className="card">
          <b className="card-title">바로 가기</b>
          <div className="quick-grid">
            {isExpert ? <>
              <a className="quick" href="#/received">받은 요청서</a>
              <a className="quick" href="#/quotes">보낸 견적</a>
              <a className="quick" href="#/credit">크레딧</a>
            </> : <a className="quick" href="#/requests">내 요청서</a>}
            <a className="quick" href="#/chats">채팅</a>
            <a className="quick" href="#/community/mine">내 글</a>
            {/* 찜은 고객이 고수를 고를 때 쓰는 기능이라 고수 상태에서는 보이지 않는다 */}
            {!isExpert && <a className="quick" href="#/bookmarks">찜한 고수</a>}
          </div>
        </section>
      )}
      {isAdmin && (
        <section className="card">
          <b className="card-title">관리</b>
          <div className="quick-grid">
            <a className="quick" href="#/admin/members">회원 관리</a>
            <a className="quick" href="#/admin/categories">카테고리 관리</a>
            <a className="quick" href="#/admin/regions">지역 관리</a>
          </div>
        </section>
      )}
      <button className="link-btn" onClick={() => setAskLogout(true)}>로그아웃</button>

      {askLogout && (
        <Modal title="로그아웃할까요?" onClose={() => !out.busy && setAskLogout(false)}
          actions={<>
            <button className="btn" disabled={out.busy} onClick={() => setAskLogout(false)}>취소</button>
            <button className="btn primary" disabled={out.busy} onClick={() => out.run(async () => { await logout().catch(() => undefined); navigate('/', undefined, true); })}>
              {out.busy ? <><Spinner light />로그아웃하는 중</> : '로그아웃'}
            </button>
          </>}>
          <p>다른 기기에서도 함께 로그아웃돼요.</p>
        </Modal>
      )}
    </main>
  );
}
