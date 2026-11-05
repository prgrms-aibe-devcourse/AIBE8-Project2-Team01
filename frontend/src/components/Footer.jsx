import { useSession } from '../auth/session';
import { BOARDS, BOARD_LABEL } from '../lib/labels';

// 공통 푸터. 실제로 구현된 화면으로만 연결한다.
// 채팅과 작성·수정 화면에는 넣지 않는다 (팀 프론트엔드 규칙)
const NO_FOOTER = [
  /^\/chats(\/\d+)?$/,
  /^\/requests\/new$/,
  /^\/community\/write$/,
  /^\/community\/\d+\/edit$/,
  /^\/mypage\/info$/,
  /^\/expert\/register$/,
  /^\/expert\/me\/edit$/,
];

export function showFooter(path) {
  return !NO_FOOTER.some((re) => re.test(path));
}

export default function Footer({ home = false }) {
  const { status } = useSession();
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-top">
          <div className="footer-brand">
            <img src="/brand/efy-logo.png" alt="EFY" width="96" height="25" />
            <p>필요한 일상 서비스와 나에게 맞는 고수를 연결합니다.</p>
          </div>
          <nav aria-label="서비스" className="footer-col">
            <h2>서비스</h2>
            <ul>
              <li>
                <a href="#/">서비스 찾기</a>
              </li>
              <li>
                <a href="#/requests/new">요청서 작성</a>
              </li>
              <li>
                <a href="#/expert/intro">고수 등록</a>
              </li>
            </ul>
          </nav>
          <nav aria-label="커뮤니티" className="footer-col">
            <h2>커뮤니티</h2>
            <ul>
              {BOARDS.map((b) => (
                <li key={b}>
                  <a href={`#/community?board=${b}`}>{BOARD_LABEL[b]}</a>
                </li>
              ))}
            </ul>
          </nav>
          {status === 'guest' && (
            <nav aria-label="시작하기" className="footer-col">
              <h2>시작하기</h2>
              <ul>
                <li>
                  <a href="#/login">로그인</a>
                </li>
                <li>
                  <a href="#/signup">회원가입</a>
                </li>
              </ul>
            </nav>
          )}
        </div>
        <div className="footer-bottom">
          {home && <p>요청서 작성 흐름의 견적 금액과 진행 화면은 예시입니다. 고수 목록과 카테고리·지역·요청서 질문은 실제 데이터를 사용합니다.</p>}
          <p>© 2026 EFY. 프로그래머스 AIBE8 Team01 프로젝트</p>
        </div>
      </div>
    </footer>
  );
}
