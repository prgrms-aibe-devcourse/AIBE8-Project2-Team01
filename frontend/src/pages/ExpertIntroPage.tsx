// 최초 고수 전환 안내 (와이어프레임 W·P01 / M·P01)
// 프로필이 이미 있으면 등록 대신 본인 프로필로 보낸다
import { errorText } from '../api/client';
import { useSession } from '../auth/session';
import { ErrorView, LoadingView } from '../components/ui';
import { useMyExpertProfile } from '../hooks/useData';

const STEPS = [
  ['요청서 확인', '내 서비스·지역에 맞는 고객 요청서를 받아요.'],
  ['견적 발송', '마음에 드는 요청서에 견적을 보내요.'],
  ['의뢰 요청으로 매칭', '고객이 견적을 보고 의뢰를 요청하면 매칭돼요.'],
  ['채팅 상담', '매칭된 고객과 일정·비용을 채팅으로 정해요.']
];

export function ExpertIntroPage() {
  const { me } = useSession();
  const { state, reload } = useMyExpertProfile(true);
  if (!me) return null;
  if (state.kind === 'loading') return <main className="page narrow"><LoadingView /></main>;
  if (state.kind === 'error') return <main className="page narrow"><ErrorView title="고수 프로필 정보를 불러오지 못했어요" message={errorText(state.error)} onRetry={reload} /></main>;

  return (
    <main className="page narrow">
      <a className="back" href="#/mypage">‹ 마이페이지</a>
      <section className="card">
        <span className="tag blue self-start">고수로 활동하기</span>
        <h1 className="title">고수 프로필을 만들면 내 서비스에 맞는 요청서가 와요</h1>
        {state.kind === 'ok' ? (
          <>
            <p className="sub">이미 고수 프로필이 있어요.<br />다시 등록하지 않고 마이페이지에서 바로 전환할 수 있어요.</p>
            <div className="row wrap"><a className="btn primary" href="#/mypage">마이페이지로</a><a className="btn" href="#/expert/me">내 고수 프로필</a></div>
          </>
        ) : me.role !== 'USER' ? (
          <p className="sub">고객 상태에서만 고수 프로필을 등록할 수 있어요.</p>
        ) : (
          <>
            <p className="sub">소개 · 서비스 · 지역 · 경력을 입력하면 돼요.<br />등록을 마쳐야 고수로 전환돼요.<br />중간에 나가면 고객 상태가 그대로 유지돼요.</p>
            <div className="row wrap"><a className="btn primary" href="#/expert/register">고수 프로필 만들기</a><a className="btn" href="#/mypage">마이페이지로</a></div>
          </>
        )}
      </section>
      <section className="card">
        <b className="card-title">등록 후에는 이렇게 일해요</b>
        <ol className="steps">
          {STEPS.map(([t, d], i) => <li key={t}><span className="num">{i + 1}</span><span className="col"><b>{t}</b><small className="sub">{d}</small></span></li>)}
        </ol>
      </section>
    </main>
  );
}
