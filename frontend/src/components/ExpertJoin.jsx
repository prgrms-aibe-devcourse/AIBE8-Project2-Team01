import { useSession } from '../auth/session';
import { useMyExpertProfile } from '../hooks/useData';
import { useRoleSwitch } from '../hooks/useRoleSwitch';

// 고수는 따로 가입하지 않는다. 일반 회원으로 가입하고 로그인한 뒤 고수 프로필을 등록한다.
// 고수 프로필 등록을 마치면 서버가 역할을 고수로 바꾼다.
const STEPS = [
  { title: '회원가입', body: '일반 회원으로 가입합니다. 고수 전용 가입은 따로 없습니다.' },
  { title: '로그인', body: '가입한 이메일과 비밀번호로 로그인합니다.' },
  { title: '고수 등록', body: '제공 서비스와 활동 지역과 경력을 적어 등록하면 바로 고수로 전환됩니다.' },
];

export default function ExpertJoin() {
  const { status, me } = useSession();
  const member = status === 'member' ? me : null;
  const profile = useMyExpertProfile(!!member && member.role !== 'ADMIN');
  const { busy, switchTo } = useRoleSwitch();
  const hasProfile = profile.state.kind === 'ok';

  // 지금 회원이 몇 번째 단계에 있는지. 로그인 전이면 첫 단계, 고수면 모든 단계를 마친 상태다.
  const current = !member ? 0 : member.role === 'EXPERT' ? 3 : 2;
  const next = encodeURIComponent('/expert/intro');

  return (
    <section id="join" className="section" aria-labelledby="join-title">
      <div className="container">
        <div className="join">
          <div className="join-text">
            <h2 id="join-title">고수로 활동하고 싶으신가요?</h2>
            <p>
              일반 회원으로 가입한 뒤 고수 프로필을 등록하면 고수로 전환됩니다. 고수는 조건이 맞는 요청서에 크레딧을 사용해
              견적을 보내고 채팅으로 상담을 이어갈 수 있습니다.
            </p>
            <div className="join-actions">
              {status === 'guest' && (
                <>
                  <a className="btn btn-primary" href={`#/signup?redirect=${next}`}>
                    회원가입하고 시작하기
                  </a>
                  <a className="btn btn-secondary" href={`#/login?redirect=${next}`}>
                    로그인
                  </a>
                </>
              )}
              {member && current === 2 && profile.state.kind === 'none' && (
                <a className="btn btn-primary" href="#/expert/intro">
                  고수 등록하기
                </a>
              )}
              {member && current === 2 && hasProfile && (
                <button type="button" className="btn btn-primary" disabled={busy} onClick={() => switchTo('EXPERT')}>
                  고수로 전환하기
                </button>
              )}
              {member && current === 3 && (
                <a className="btn btn-primary" href="#/received">
                  받은 요청 보기
                </a>
              )}
            </div>
          </div>
          <ol className="join-steps">
            {STEPS.map((s, i) => {
              const state = i < current ? 'is-done' : i === current ? 'is-current' : '';
              return (
                <li key={s.title} className={state} aria-current={i === current ? 'step' : undefined}>
                  <span className="join-no num" aria-hidden="true">
                    {i + 1}
                  </span>
                  <div>
                    <h3>
                      <span className="sr-only">{i + 1}단계 </span>
                      {s.title}
                      {i < current && <span className="sr-only"> 완료</span>}
                    </h3>
                    <p>{s.body}</p>
                  </div>
                </li>
              );
            })}
          </ol>
        </div>
      </div>
    </section>
  );
}
