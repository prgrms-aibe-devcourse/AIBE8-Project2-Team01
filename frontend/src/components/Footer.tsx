// 공통 푸터: 서비스 소개 · 실제로 구현된 공개 화면 링크
// 이용약관·개인정보처리방침·사업자 정보는 아직 없어서 넣지 않는다
import { useSession } from '../auth/session';
import { BOARDS, BOARD_LABEL } from '../lib/labels';
import { Lines } from './ui';
import logo from '../assets/efy-logo.png';

// 푸터를 넣지 않는 화면
// 채팅: 화면 높이를 쓰는 대화창이라 입력창이 밀리지 않게 한다
// 작성·수정: 하단 고정 버튼(.bottom-bar)이 있어 푸터와 겹치지 않게 한다
const NO_FOOTER: RegExp[] = [
  /^\/chats(\/\d+)?$/,
  /^\/requests\/new$/,
  /^\/community\/write$/,
  /^\/community\/\d+\/edit$/,
  /^\/mypage\/info$/,
  /^\/expert\/register$/,
  /^\/expert\/me\/edit$/
];

export function showFooter(path: string): boolean {
  return !NO_FOOTER.some((re) => re.test(path));
}

export function Footer() {
  const { status } = useSession();
  return (
    <footer className="ft">
      <div className="ft-in">
        <div className="ft-brand">
          <a href="#/" className="ft-logo" aria-label="EFY 홈"><img src={logo} alt="EFY" /></a>
          <p className="ft-desc">
            <Lines text={'EFY는 필요한 일상 서비스에 맞는 고수를 연결해 드려요.\n요청서 한 장을 보내면 여러 고수의 견적을 비교할 수 있어요.\n마음에 드는 견적을 고르면 채팅으로 일정과 비용을 조율해요.'} />
          </p>
        </div>
        <nav className="ft-links" aria-label="푸터 메뉴">
          <div className="ft-col">
            <b>서비스</b>
            <a href="#/">서비스 찾기</a>
            <a href="#/community">커뮤니티</a>
          </div>
          <div className="ft-col">
            <b>게시판</b>
            {BOARDS.map((b) => <a key={b} href={`#/community?board=${b}`}>{BOARD_LABEL[b]}</a>)}
          </div>
          {status === 'guest' && (
            <div className="ft-col">
              <b>시작하기</b>
              <a href="#/login">로그인</a>
              <a href="#/signup">회원가입</a>
            </div>
          )}
        </nav>
      </div>
      <div className="ft-bottom">
        <small>© EFY · Experts for you</small>
      </div>
    </footer>
  );
}
