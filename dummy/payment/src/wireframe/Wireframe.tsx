// 크레딧 충전 와이어프레임: 더미 화면과 같은 구성·이동 흐름을 무채색 박스로 표시한다
import type { ReactNode } from 'react';

/** 이동 버튼: 클릭하면 대상 프레임으로 스크롤 */
function Btn({ to, label, target, primary, dashed }: { to?: string; label: string; target: string; primary?: boolean; dashed?: boolean }) {
  const className = `btn${primary ? ' pri' : ''}`;
  const content = (
    <>
      {label}
      <span className="to">{target}</span>
    </>
  );
  // 이동 대상이 없는 더미 전용 버튼은 링크로 만들지 않는다
  return to ? (
    <a className={className} href={`#${to}`}>
      {content}
    </a>
  ) : (
    <span className={className} style={dashed ? { borderStyle: 'dashed' } : undefined}>
      {content}
    </span>
  );
}

function Frame({ id, title, code, note, children, screenClass }: { id?: string; title: string; code: string; note: string; children: ReactNode; screenClass?: string }) {
  return (
    <section className="frame" id={id}>
      <div className="frame-title">
        {title} <span className="id">{code}</span>
      </div>
      <div className={`screen${screenClass ? ` ${screenClass}` : ''}`}>{children}</div>
      <div className="note">{note}</div>
    </section>
  );
}

function OrderInfo({ status }: { status: string }) {
  return (
    <dl className="kv ph">
      <dt>주문명</dt>
      <dd>상품명</dd>
      <dt>결제 금액</dt>
      <dd>₩N</dd>
      <dt>주문번호</dt>
      <dd>orderId</dd>
      <dt>결제 상태</dt>
      <dd>{status}</dd>
    </dl>
  );
}

function BalanceRow() {
  return (
    <div className="row">
      <div className="card">
        총잔액<b>N</b>
      </div>
      <div className="card" style={{ borderStyle: 'dashed' }}>
        예약됨<b>N</b>
      </div>
      <div className="card on">
        사용 가능<b>N</b>견적 N건 가능
      </div>
    </div>
  );
}

/** 화면 흐름도 */
function FlowDiagram() {
  return (
    <div className="flow">
      <svg viewBox="0 0 960 300" role="img" aria-label="W1 상품 선택에서 W2 결제 확인, 모의 결제창을 거쳐 W3 결과 네 가지, W4 내역으로 이어지는 흐름도">
        <defs>
          <marker id="arw" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M0,0 L10,5 L0,10 z" fill="#222" />
          </marker>
        </defs>
        <g fontSize="12" fill="#222" stroke="#222">
          {/* 노드 */}
          <rect x="10" y="120" width="130" height="50" fill="#fff" />
          <text x="75" y="141" textAnchor="middle" stroke="none" fontWeight="700">W1</text>
          <text x="75" y="158" textAnchor="middle" stroke="none">잔액·상품 선택</text>

          <rect x="190" y="120" width="130" height="50" fill="#fff" />
          <text x="255" y="141" textAnchor="middle" stroke="none" fontWeight="700">W2</text>
          <text x="255" y="158" textAnchor="middle" stroke="none">결제 정보 확인</text>

          <rect x="370" y="120" width="130" height="50" fill="#fff" strokeDasharray="5 3" />
          <text x="435" y="141" textAnchor="middle" stroke="none" fontWeight="700">W2-1</text>
          <text x="435" y="158" textAnchor="middle" stroke="none">모의 결제창</text>

          <rect x="580" y="20" width="150" height="44" fill="#fff" />
          <text x="655" y="47" textAnchor="middle" stroke="none">W3-a 성공 (DONE)</text>
          <rect x="580" y="84" width="150" height="44" fill="#fff" />
          <text x="655" y="111" textAnchor="middle" stroke="none">W3-b 실패 (FAILED)</text>
          <rect x="580" y="148" width="150" height="44" fill="#fff" />
          <text x="655" y="175" textAnchor="middle" stroke="none">W3-c 취소 (CANCELED)</text>
          <rect x="580" y="212" width="150" height="44" fill="#fff" />
          <text x="655" y="239" textAnchor="middle" stroke="none">W3-d 확인 중 (PENDING)</text>

          <rect x="810" y="20" width="130" height="50" fill="#fff" />
          <text x="875" y="41" textAnchor="middle" stroke="none" fontWeight="700">W4</text>
          <text x="875" y="58" textAnchor="middle" stroke="none">충전·사용 내역</text>

          {/* 화살표 */}
          <line x1="140" y1="145" x2="188" y2="145" markerEnd="url(#arw)" />
          <text x="164" y="136" textAnchor="middle" stroke="none" fontSize="10">다음</text>
          <line x1="320" y1="145" x2="368" y2="145" markerEnd="url(#arw)" />
          <text x="344" y="136" textAnchor="middle" stroke="none" fontSize="10">결제하기</text>

          <path d="M500,145 L540,145 L540,42 L578,42" fill="none" markerEnd="url(#arw)" />
          <path d="M540,106 L578,106" fill="none" markerEnd="url(#arw)" />
          <path d="M540,145 L540,170 L578,170" fill="none" markerEnd="url(#arw)" />
          <path d="M540,170 L540,234 L578,234" fill="none" markerEnd="url(#arw)" />

          <line x1="730" y1="42" x2="808" y2="42" markerEnd="url(#arw)" />
          <text x="769" y="34" textAnchor="middle" stroke="none" fontSize="10">내역 확인</text>

          {/* 재확인: PENDING → 성공·실패 */}
          <path d="M730,234 L770,234 L770,106 L732,106" fill="none" strokeDasharray="4 3" markerEnd="url(#arw)" />
          <path d="M770,106 L770,52 L732,52" fill="none" strokeDasharray="4 3" markerEnd="url(#arw)" />
          <text x="776" y="200" stroke="none" fontSize="10">결과 다시 확인</text>

          {/* 실패·취소 → 다시 결제(W2) */}
          <path d="M580,120 L560,120 L560,280 L255,280 L255,172" fill="none" markerEnd="url(#arw)" />
          <text x="400" y="274" textAnchor="middle" stroke="none" fontSize="10">다시 결제하기 (새 주문)</text>

          {/* W4 → W1 */}
          <path d="M875,20 L875,8 L75,8 L75,118" fill="none" strokeDasharray="2 3" markerEnd="url(#arw)" />
          <text x="420" y="20" textAnchor="middle" stroke="none" fontSize="10">크레딧 충전하기 / 상품 다시 선택 → W1</text>
        </g>
      </svg>
      <div className="legend">
        <span>화면</span>
        <span className="dash">모달·재확인(점선)</span>
      </div>
    </div>
  );
}

export function Wireframe() {
  return (
    <div className="wrap">
      <h1>EFY 크레딧 충전 · 와이어프레임</h1>
      <p className="muted small">
        더미 화면(index.html)과 같은 구성·이동 흐름. 고수(EXPERT) 전용. 버튼 옆 "→ W?"는 이동 대상이며 클릭하면 해당 프레임으로
        이동합니다. 점선 박스 = 명세 미반영·미확정 요소.
      </p>

      <h2>화면 흐름</h2>
      <FlowDiagram />

      <h2>화면 구성</h2>
      <div className="frames">
        <Frame id="w1" title="잔액 확인·상품 선택" code="W1 · #/credit" note="상품 미선택 시 [다음] 비활성. 상품 구간은 예시 데이터. 예약 카드는 API 명세 미반영(점선).">
          <div className="gnb">
            <span>
              EFY · 받은 요청 · 보낸 견적 · 채팅 · <u>크레딧 충전</u> · 내역
            </span>
            <span>
              [사용 가능 N / 총 N] 고객|<b>고수</b>
            </span>
          </div>
          <BalanceRow />
          <div className="ph">안내: 견적 1건 발송 시 2크레딧 예약, 매칭 시 차감 · 1크레딧 = 1,000원</div>
          <div className="ph">사용 가능 &lt; 2이면 "견적 발송 불가" 안내 표시</div>
          <div className="muted small">충전 상품 (가격 오름차순, 단일 선택)</div>
          <div className="row">
            <div className="card">
              상품명<b>₩가격</b>N크레딧
              <br />
              [예시]
            </div>
            <div className="card on">
              상품명<b>₩가격</b>N크레딧
              <br />
              [선택됨]
            </div>
            <div className="card">
              상품명<b>₩가격</b>N크레딧
              <br />
              [예시]
            </div>
          </div>
          <div className="btns">
            <Btn to="w2" label="다음: 결제 정보 확인" target="→ W2" primary />
            <Btn to="w4" label="충전·사용 내역" target="→ W4" />
          </div>
        </Frame>

        <Frame id="w2" title="결제 정보 확인" code="W2 · #/checkout" note="[결제하기] 클릭 시 READY 주문 생성 후 결제창 열림. 처리 중 버튼 잠금(중복 주문 방지).">
          <div className="gnb">
            <span>EFY · 상단 메뉴</span>
            <span>[크레딧]</span>
          </div>
          <dl className="kv ph">
            <dt>선택 상품</dt>
            <dd>상품명</dd>
            <dt>충전 크레딧</dt>
            <dd>N크레딧</dd>
            <dt>결제 금액</dt>
            <dd>
              <b>₩N</b>
            </dd>
            <dt>충전 후 총잔액</dt>
            <dd>N → N (예상)</dd>
            <dt>충전 후 사용 가능</dt>
            <dd>N → N (예상)</dd>
          </dl>
          <div className="ph">안내: 테스트 결제 · 금액은 서버 상품 가격 기준</div>
          <div className="btns">
            <Btn to="w2-1" label="₩N 결제하기" target="→ W2-1" primary />
            <Btn to="w1" label="상품 다시 선택" target="→ W1" />
          </div>
        </Frame>

        <Frame id="w2-1" title="모의 결제창 (모달)" code="W2-1" screenClass="dim" note="실제 연동 시 토스 테스트 결제창 자리. 네 버튼은 결과 재현용(더미 전용). 한 번 누르면 모든 버튼 잠금.">
          <div className="modal" style={{ background: '#fff' }}>
            <b>
              모의 결제창 <span className="muted small">테스트 · 실제 결제 없음</span>
            </b>
            <dl className="kv">
              <dt>주문명</dt>
              <dd>상품명</dd>
              <dt>결제 금액</dt>
              <dd>₩N</dd>
              <dt>주문번호</dt>
              <dd>orderId</dd>
            </dl>
            <div className="btns">
              <Btn to="w3a" label="결제 성공" target="→ W3-a" primary />
              <Btn to="w3b" label="결제 실패" target="→ W3-b" />
              <Btn to="w3c" label="사용자 취소" target="→ W3-c" />
              <Btn to="w3d" label="승인 응답 지연" target="→ W3-d" />
            </div>
          </div>
        </Frame>

        <Frame id="w3a" title="결제 결과 · 성공" code="W3-a · DONE" note="성공 시에만 잔액·내역(CHARGE)에 1회 반영.">
          <div className="ph">
            <b>충전이 완료되었습니다</b>
            <br />
            N크레딧 충전 · 총잔액 N
          </div>
          <OrderInfo status="승인 완료" />
          <div className="btns">
            <Btn to="w4" label="잔액·내역 확인" target="→ W4" primary />
            <Btn to="w1" label="크레딧 화면으로" target="→ W1" />
            <Btn label="같은 주문 승인 재요청" target="(더미 전용 · 재지급 없음 확인)" dashed />
          </div>
        </Frame>

        <Frame id="w3b" title="결제 결과 · 실패" code="W3-b · FAILED" note="잔액·내역 변화 없음.">
          <div className="ph">
            <b>결제에 실패했습니다</b>
            <br />
            크레딧은 지급되지 않았습니다
          </div>
          <OrderInfo status="실패" />
          <div className="btns">
            <Btn to="w2" label="다시 결제하기" target="→ W2 (새 주문)" primary />
            <Btn to="w1" label="상품 다시 선택" target="→ W1" />
          </div>
        </Frame>

        <Frame id="w3c" title="결제 결과 · 사용자 취소" code="W3-c · CANCELED" note="잔액·내역 변화 없음. 환불 안내 문구 없음(환불 정책 미확정).">
          <div className="ph">
            <b>결제를 취소했습니다</b>
            <br />
            크레딧은 지급되지 않았습니다
          </div>
          <OrderInfo status="사용자 취소" />
          <div className="btns">
            <Btn to="w2" label="다시 결제하기" target="→ W2 (새 주문)" primary />
            <Btn to="w1" label="상품 다시 선택" target="→ W1" />
          </div>
        </Frame>

        <Frame id="w3d" title="결제 결과 · 처리 확인 중" code="W3-d · PENDING" note="재확인(결제 조회) API 경로·응답은 미확정(점선).">
          <div className="ph">
            <b>결제 결과를 확인하고 있습니다</b>
            <br />
            확인 전에는 크레딧이 지급되지 않습니다
          </div>
          <OrderInfo status="처리 확인 중" />
          <div className="ph dash">[재확인 시 재현할 결과 ▼] (더미 전용 선택 상자)</div>
          <div className="btns">
            <Btn to="w3a" label="결과 다시 확인" target="→ W3-a / W3-b / 유지" primary />
            <Btn to="w4" label="내역 보기" target="→ W4" />
          </div>
        </Frame>

        <Frame id="w4" title="충전·사용 내역" code="W4 · #/history" note="최신순 · 유형 필터 · 페이지. 방금 충전한 행 강조. 예약은 내역 유형(CHARGE·USE·REFUND)에 없음.">
          <div className="gnb">
            <span>
              EFY · 상단 메뉴 · <u>내역</u>
            </span>
            <span>[크레딧]</span>
          </div>
          <BalanceRow />
          <div className="btns" style={{ marginTop: 0 }}>
            {['전체', '충전', '사용', '환불'].map((f) => (
              <span key={f} className="btn">
                {f}
              </span>
            ))}
          </div>
          <table className="tbl">
            <thead>
              <tr>
                <th>일시</th>
                <th>유형</th>
                <th>관련</th>
                <th>변동</th>
                <th>변동 후</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>MM/DD</td>
                <td>충전</td>
                <td>결제 #N</td>
                <td>+N</td>
                <td>N</td>
              </tr>
              <tr>
                <td>MM/DD</td>
                <td>사용</td>
                <td>견적 #N</td>
                <td>-2</td>
                <td>N</td>
              </tr>
            </tbody>
          </table>
          <div className="ph" style={{ textAlign: 'center' }}>
            [이전] N / N 페이지 [다음]
          </div>
          <div className="ph dash">견적에 예약된 크레딧 표 (발송 일시 · 견적 · 예약 크레딧) — 조회 API 미정</div>
          <div className="btns">
            <Btn to="w1" label="크레딧 충전하기" target="→ W1" primary />
          </div>
        </Frame>
      </div>

      <h2>공통 요소</h2>
      <div className="frames">
        <Frame title="더미 컨트롤 패널 (하단 고정)" code="더미 전용" note="실서비스 화면에는 포함하지 않음.">
          <div className="row">
            <div className="ph">mock 총잔액 · 예약 · 사용 가능 · 주문 목록(상태)</div>
          </div>
          <div className="btns">
            <Btn label="초기 상태로" target="→ W1 · mock 초기화" />
            <Btn label="접기/펼치기" target="" />
          </div>
        </Frame>
        <Frame title="화면별 데이터·API 패널" code="더미 전용" note="결제 담당자 확인용. 실서비스 화면에는 포함하지 않음.">
          <div className="ph dash">▶ 이 화면의 데이터 · API 연결 지점 (접이식) — 경로·상태(초안·미구현 / 대응 API 없음)·마지막 mock 응답</div>
        </Frame>
      </div>
      <p className="small muted" style={{ marginTop: 24 }}>
        <a href="./index.html">← 더미 화면으로</a>
      </p>
    </div>
  );
}
