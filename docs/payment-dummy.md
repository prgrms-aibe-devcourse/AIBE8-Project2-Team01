# 크레딧 충전 더미 화면 정리

결제 담당자가 고수 크레딧 충전 흐름과 화면별 데이터를 확인하기 위한 클릭 가능한 더미 화면과 와이어프레임이다. 실제 결제와 백엔드 구현은 포함하지 않는다.

- 작성일: 2026-11-02 · 최종 갱신: 2026-11-03 (기준 코드: `origin/dev` `c1375a0`, PR #72 크레딧 지갑 병합 이후)
- 대상: 고수(EXPERT)의 크레딧 충전
- 고객이 고수에게 지급하는 서비스 대금은 다루지 않는다. 플랫폼에서 결제하지 않는다.
- 노션 정책 문서는 수정하지 않았다.

## 1. 기술 스택과 파일 위치

TypeScript + React 18 + Vite 5로 만들었다. 경로는 저장소 `AIBE8-Project2-Team01` 루트 기준이다.

| 구분 | 경로 |
|---|---|
| 프로젝트 설정 | `dummy/payment/package.json`, `tsconfig.json`, `vite.config.ts`, `.gitignore` |
| 더미 화면 진입점 | `dummy/payment/index.html` → `src/main.tsx` → `src/App.tsx` |
| 화면 4개 | `src/pages/CreditPage.tsx`, `CheckoutPage.tsx`, `ResultPage.tsx`, `HistoryPage.tsx` |
| 공통 컴포넌트 | `src/components/` (잔액 카드, 모의 결제창, 데이터·API 패널, 더미 컨트롤 등) |
| API 계약·교체 지점 | `src/api/creditApi.ts` |
| mock API·mock 데이터 | `src/api/mockCreditApi.ts` |
| API 타입 | `src/types.ts` |
| 정책 상수 | `src/lib/policy.ts` |
| 훅 | `src/hooks/` (해시 라우팅, 중복 클릭 방지, mock 상태 구독, 비동기 조회) |
| 스타일 | `src/styles.css` |
| 와이어프레임 | `dummy/payment/wireframe.html` → `src/wireframe/Wireframe.tsx`, `src/wireframe/wireframe.css` |
| 이 문서 | `docs/payment-dummy.md` |

### TS·React가 아닌 파일

| 파일 | 형식 | 사용 이유 |
|---|---|---|
| `index.html`, `wireframe.html` | HTML | Vite가 페이지 진입점으로 요구하는 파일이다. `<div id="root">`와 TSX 진입 스크립트만 있고 화면 내용은 모두 React가 그린다. |
| `src/styles.css`, `src/wireframe/wireframe.css` | CSS | 화면 스타일이다. 별도 CSS 라이브러리를 추가하지 않았다. |

JavaScript 파일(`.js`)은 없다. 설정 파일도 `vite.config.ts`로 작성했다.

## 2. 실행 방법

Node.js 18 이상이 필요하다.

```bash
# 저장소 루트에서 실행
cd dummy/payment
npm ci          # 의존성 설치 (package-lock.json 기준)
npm run dev     # 개발 서버 http://localhost:5173
```

| 명령 | 용도 |
|---|---|
| `npm run dev` | 개발 서버 실행. 더미는 `http://localhost:5173/`, 와이어프레임은 `http://localhost:5173/wireframe.html` |
| `npm run typecheck` | TypeScript 타입 검사 |
| `npm run build` | 타입 검사 후 `dist/`에 빌드 |
| `npm run preview` | 빌드 결과 미리보기 |

- 더미 상단 띠의 "와이어프레임 보기"로 와이어프레임으로 이동할 수 있다.
- `node_modules/`와 `dist/`는 `dummy/payment/.gitignore`로 커밋에서 제외한다.
- 외부 요청은 Google Fonts 한 건뿐이다. 실패하면 시스템 글꼴로 표시한다.
- 실제 결제 API와 운영 DB는 호출하지 않는다.

## 3. 확인한 자료

| 자료 | 확인 결과 |
|---|---|
| 노션 프로젝트 소개 | 본문과 하위 문서(기획안, 전체 요구사항, 코드 컨벤션, 담당 파트 소개, 와이어프레임·화면 정의서, 프론트엔드 요청, 개발 결과물, 발표 자료, WBS, 테스트 및 이슈, 개인별 작업 기록, 1~8차 회의록, 멘토링) 확인. 프론트엔드 기술 스택을 정한 기록은 없고 "프론트엔드는 AI를 활용"(2차 회의)만 있다. TypeScript + React는 팀 결정으로 전달받은 내용이다. |
| 노션 8차 회의(11/02) | 2크레딧 통일, 3일 예약 유지, 예약 해제 시점, 보너스 크레딧 MVP 제외, 내역 페이지 조회, 프론트 반복 클릭 방지 확인 |
| 팀 결정(11/03) | SENT 견적 회수 시 예약 2크레딧 해제 |
| 노션 MVP 요구사항 명세서 | `크레딧·결제 · 지웅` 섹션 CR-01~CR-08과 MVP 범위 표 확인 |
| 노션 API 명세서 | credit 도메인 목록과 API-65~API-70 상세 페이지 확인. 모두 **명세 상태 '초안'** |
| 코드 `AIBE8-Project2-Team01` | `origin/dev` 최신(`c1375a0`)의 `domain/credit`, `ErrorCode`, `domain/estimate`, `domain/chat`, 테스트 `CreditTests` 확인 (11/03) |
| 코드 `efy` | 이전 저장소. 크레딧 코드 없음 |
| 기존 와이어프레임 `와이어프레임/html/11_크레딧.html` | 화면 톤(무채색·Noto Sans KR)과 헤더 구성을 참고 |

노션 문서는 웹 fetch로는 열리지 않아 로그인된 Chrome으로 열람했다.

## 4. 실제 코드에서 확인한 내용 (2026-11-03 기준)

### 구현된 것 (PR #72 `feat/36-credit-wallet`)

| 구분 | 코드 | 내용 |
|---|---|---|
| 지갑 | `credit/wallet/CreditWallet` | 회원당 1개(`member_id` 유일). `balance`(보유 크레딧) |
| 예약 | `credit/reservation/CreditReservation` | 견적서 1건당 예약 1건(`quote_id` 유일). 상태 `RESERVED`·`USED`·`RELEASED` |
| 내역 | `credit/history/CreditHistory` | 유형 `CHARGE`·`USE`·`REFUND`, `amount`, `balanceAfter`, `paymentId`, `quoteId`. (유형, 결제)·(유형, 견적) 조합 유일 제약으로 같은 결제·견적의 중복 지급·차감을 막는다 |
| 잔액 조회 API | `GET /api/members/me/credit` (`CreditWalletController`) | 응답 `balance`, `reservedCredit`, `creditPerQuote`, `availableQuoteCount`. 역할이 EXPERT가 아니면 403. 지갑이 없으면 모두 0(`creditPerQuote`만 2) |
| 예약 | `CreditReservationService.reserveForQuote(memberId, quoteId)` | 지갑을 잠그고 사용 가능(보유 − 예약)이 2 이상이면 2크레딧 예약. 같은 견적 중복 예약 거절 |
| 차감 | `chargeForConnection(quoteId)` | RESERVED 예약만 USED로 바꾸고 잔액 차감, USE 내역 기록 |
| 해제 | `releaseReservation(quoteId)`, `releaseReservations(quoteIds)` | RESERVED만 RELEASED로 변경. 여러 번 호출해도 안전 |
| 오류 코드 | `ErrorCode` ⑦ 크레딧·결제 | `INSUFFICIENT_CREDIT_EXCEPTION`(409 "사용 가능한 크레딧이 부족합니다."), `DUPLICATE_CREDIT_RESERVATION_EXCEPTION`(409), `CREDIT_RESERVATION_NOT_FOUND_EXCEPTION`(404), `INVALID_CREDIT_RESERVATION_STATUS_EXCEPTION`(409) |
| 테스트 | `CreditTests` 8건 | 지갑 없는 고수 0 응답, 예약 제외 발송 가능 수 계산, 권한 거절, 잔액 부족 예약 거절, 중복 예약 거절, 1회 차감·USE 내역, 해제 후 재사용, 동시 예약 |

- 사용 가능 크레딧 = `max(balance − reservedCredit, 0)`, `availableQuoteCount` = 사용 가능 ÷ 2(내림).
- 예약·차감 시 지갑과 예약 행을 비관적 잠금으로 잠가 동시 요청에도 잔액을 넘지 않게 한다.

### 아직 없는 것

- **충전 상품(API-65), 결제 주문·승인·실패(API-68~70), 내역 목록(API-67), 환불(CR-08)**: 컨트롤러·서비스가 없다. `credit/payment`에는 `docs/package-info.java`만 있고 `CreditHistoryRepository`에는 조회 메서드가 없다.
- 충전 상품 시드는 `data.sql`에 없다.
- **견적·채팅 연동**: `reserveForQuote`·`chargeForConnection`·`releaseReservation`을 호출하는 곳이 아직 없다. 태민님·영서님이 각 기능에서 호출하기로 했다(11/03).
  - `QuoteService.createQuote`는 견적 저장 시 크레딧을 예약하지 않는다.
  - 견적 회수(삭제) API와 3일 만료 처리(스케줄러)가 없다.
  - `domain/chat`에는 `docs`만 있어 의뢰 요청 → 차감 → 채팅방 생성 흐름이 없다.
- **프론트엔드 코드는 없다.** `dummy/payment/`는 백엔드 Gradle 빌드와 분리된 독립 실행 프로젝트다.

### 더미와 실제 코드의 차이

| 항목 | 실제 코드 | 더미(`src/types.ts`·`mockCreditApi.ts`) | 연동 시 조치 |
|---|---|---|---|
| 잔액 응답 필드 | `balance`, `reservedCredit`, `creditPerQuote`, `availableQuoteCount` | 위 4개 + `availableCredit` | `availableCredit` 필드는 응답에 없다. 지웅님이 처리 방식을 구현 중(11/03)이라 결과를 보고 맞춘다 |
| 비고수 접근 | 403 | 더미는 고수 화면만 있음 | 403을 받으면 크레딧 화면 대신 권한 안내 |
| 지갑 없음 | 0 응답 | 초기 잔액 16 | 신규 고수는 0·발송 가능 0건 상태를 보여 준다 |
| 오류 메시지 | 위 4개 정의 | "(mock 메시지)" | 잔액 부족 문구는 `INSUFFICIENT_CREDIT_EXCEPTION` 메시지를 사용 |

- 재사용한 공통 형식: 목록 응답 `PageResponse`(`content`·`pageNumber`(1부터)·`pageSize`·`totalElements`·`totalPages`·`lastPage`), 역할 `MemberRole`(`USER`·`EXPERT`·`ADMIN`)

## 5. 확인한 정책

출처: [MVP] = MVP 요구사항 명세서, [API] = API 명세서, [요청] = 이번 작업 요청

| 정책 | 출처 |
|---|---|
| 1크레딧 = 1,000원이고 연결 한 건의 비용은 2크레딧이다. | [MVP] CR-01 |
| 추가 지급 크레딧과 카테고리별 과금은 MVP에서 제외한다. | [MVP] 범위·CR-01, [요청] |
| 활성 상품만 가격 오름차순으로 제공한다. 상품이 없으면 빈 목록을 반환한다. | [MVP] CR-01 |
| 상품을 선택하면 서버가 금액을 정해 READY 주문을 만들고 `orderId`·`orderName`·`amount`를 반환한다. 클라이언트가 보낸 금액은 쓰지 않는다. | [MVP] CR-02, [API] API-68 |
| 토스 테스트 키만 사용하며 실제 결제는 발생하지 않는다. | [MVP] CR-02 |
| `paymentKey`·`orderId`·`amount`를 검증해 승인하면 DONE 처리 후 크레딧을 지급하고 CHARGE 내역을 남긴다. | [MVP] CR-03 |
| 같은 주문 승인이 반복돼도 한 번만 지급한다. 이미 DONE이면 재지급 없이 기존 결과를 반환한다. | [MVP] CR-03, [API] API-69 |
| 토스 승인 실패는 FAILED다. 응답 시간 초과는 성공 여부를 단정하지 않고 PENDING으로 둔 뒤 결제 조회로 재확인한다. | [MVP] CR-03 |
| 결제창 실패는 FAILED, 사용자 취소(`PAY_PROCESS_CANCELED`)는 CANCELED로 기록하고 지급하지 않는다. DONE 주문은 덮어쓰지 않는다. | [MVP] CR-04, [API] API-70 |
| 사용 가능 잔액은 예약을 제외해 계산하고, 잔액은 내역 합계와 일치해야 한다. | [MVP] CR-05 |
| 내역은 CHARGE·USE·REFUND 유형 필터와 최신순 페이지를 지원한다. 각 항목은 유형·변동량·변동 후 잔액·일시·관련 결제 또는 견적을 담는다. | [MVP] CR-06, [API] API-67 |
| 견적을 발송하면 2크레딧을 예약하고, 고객의 의뢰 요청으로 매칭될 때 실제로 차감한다. 첫 메시지를 과금 시점으로 쓰지 않는다. | [MVP] CR-07, [요청] |
| 예약 해제 조건: SENT 발송 후 3일 경과, 요청서 직접 취소, **SENT 견적 회수**. 예약 해제는 환불이 아니다. | [MVP] CR-07, 팀 결정(11/03) |
| 지갑이 없는 고수의 잔액은 0으로 응답한다. 사용 가능 = 보유 − 예약(0 미만이면 0). | 코드 `CreditWalletService` |
| 모든 크레딧·결제 API는 고수(EXPERT) 전용이다. 현재 구현된 잔액 조회는 EXPERT가 아니면 403이다. | [API] credit 목록, 코드 |
| 총잔액·예약 크레딧·사용 가능 크레딧을 구분해 보여 준다. | [요청] |
| 환불의 미확정 세부 조건은 화면에 안내하지 않는다. | [요청], [MVP] CR-08 |

### 최신 프론트엔드 요청과 비교 (2026-11-02)

노션 프론트엔드 요청의 "결제" 항목과 대조해 현재 크레딧 더미의 반영 범위를 정리했다.

| 프론트엔드 요청 | 이 더미의 반영 |
|---|---|
| 고수는 헤더와 마이페이지에서 크레딧 화면으로 이동한다 | 현재 더미는 헤더 진입만 제공하며 마이페이지 진입은 실제 화면 연동 시 추가한다 |
| 충전 상품, 보유·사용 가능 크레딧, 충전·사용·환불 내역 | 반영. 실제 API-66 응답에 `reservedCredit`이 생겼다. 사용 가능 크레딧은 지웅님이 구현 중(11/03) |
| 상품·잔액·내역의 정상·빈 결과·조회 실패를 구분 | 현재 더미는 정상·빈 결과를 제공하며 조회 실패 상태는 실제 API 연동 시 보완한다 |
| 예약 크레딧과 실제 차감을 혼동하지 않는다 | 예약은 별도 카드·표로 분리하고 내역(CHARGE·USE·REFUND)에 넣지 않음 |
| 보너스 크레딧 제외 | 반영(`bonusCredit` 0) |
| 실패·취소·처리 중을 충전 완료로 표시하지 않는다 | 반영. DONE만 잔액·내역에 반영 |
| mock 산출물 완료와 토스 테스트 결제 API 구현·연동 완료를 구분한다 | 이 더미는 mock 산출물이다. CR-01~06 서버 구현·연동 완료를 뜻하지 않는다 |
| 견적 작성 중 즉시 충전 모달은 후순위 | 넣지 않음. 견적 작성 화면(E-05)은 충전 화면 링크만 둔다 |
| 기본·최대 페이지 크기와 확정되지 않은 경로는 API 담당자와 확인 | 미확정 사항 7번으로 유지 |
| 크레딧 API는 EXPERT 전용, USER로 전환한 고수의 접근 범위는 Q-06 | 크레딧 진입을 EXPERT 상태에만 둔다 |

## 6. mock으로 만든 부분

| 항목 | mock 내용 | 근거·비고 |
|---|---|---|
| 충전 상품 구간 | 10개/10,000원, 30개/30,000원, 50개/50,000원, 100개/100,000원 | 화면에 "예시 데이터"로 표시. 100개/100,000원만 API-65 예시 값이다. 가격 = 크레딧 × 1,000원 규칙만 확정이다. |
| `bonusCredit` | 모든 상품 0 | API-65 예시는 10이지만 MVP에서 추가 크레딧을 제외한다. |
| 잔액 응답의 `availableCredit` | mock 응답에만 있음 | 실제 응답은 `reservedCredit`까지만 준다(코드 확인). 지웅님 구현 결과에 맞춰 교체 |
| 초기 데이터 | 총잔액 16, 예약 4(견적 #34·#35), 사용 가능 12. 내역 4건(충전 2, 사용 2) | 임의 값 |
| 예약 목록 | 견적 ID·예약 크레딧·발송 일시 | 엔티티(`CreditReservation`)는 있지만 조회 API 없음 |
| 모의 결제창 | 성공·실패·사용자 취소·승인 응답 지연 버튼 | 토스 결제창 자리. 결과 재현용 |
| `paymentKey`, `orderId` 형식 | `mock_pk_…`, `efy-mock-0003-…` | 형식은 서버에서 결정 |
| 실패 코드 | `MOCK_PAYMENT_FAILED` | 토스 실패 코드 목록은 확인하지 않았다. 취소 코드 `PAY_PROCESS_CANCELED`만 명세 값이다. |
| PENDING 응답과 재확인 | `{ orderId, paymentStatus: "PENDING" }`, 재확인 결과는 선택 상자로 재현 | 응답 형식과 조회 API 모두 미정 |
| 오류 메시지 | "(mock 메시지)"가 붙은 문구 | 예약·차감 관련 4개는 ErrorCode에 정의됨. 결제·상품·내역 오류는 미정의 |
| 내역 페이지 크기 | 10 | 기본값과 최대값은 미정 |
| 응답 지연 | 모든 mock 호출 0.5초 | 로딩·중복 클릭 확인용 |

## 7. 미확정 사항

이 항목들은 더미에서 임의로 확정하지 않았다. 화면에는 점선 박스나 "미정" 표시로 구분했다.

1. **사용 가능 크레딧 응답 필드** (지웅님 구현 중, 11/03)
   - 코드에 `reservedCredit`이 추가됐다(11/03 확인). 노션 API-66 초안은 아직 갱신 전인지 확인이 필요하다.
   - 사용 가능 크레딧(`balance − reservedCredit`)은 지웅님이 구현 중이다. 반영되면 응답 필드와 화면 계산 여부를 이 문서에 갱신한다.
2. **`availableQuoteCount`의 계산 범위**: 코드는 RESERVED 예약만 빼고 `사용 가능 ÷ 2`로 계산한다. 다만 3일 만료·회수·요청서 취소 시 해제를 호출하는 곳이 아직 없어, 연동 전까지는 만료된 견적의 예약도 계속 빠진 채로 계산된다. 만료 처리는 태민님이 구현 예정이다.
3. **상품 구간과 보너스 계산 방식**: Q-06 확정 전 초안이다.
4. **API 초안과 MVP 정책의 차이**
   - API-65 예시의 `bonusCredit: 10`과 API-69 예시의 `chargedCredit: 110`은 추가 크레딧이 들어간 값이다.
   - MVP 정책에서는 추가 크레딧을 지급하지 않는다. 명세 예시를 정리해야 한다.
5. **승인 응답 지연(PENDING)**
   - 응답 형식이 미정이다.
   - "결제 조회로 재확인"할 API의 경로와 응답도 없다.
6. **예약 내역의 표시 여부**
   - 내역 유형은 CHARGE·USE·REFUND뿐이고 예약 조회 API가 없다.
   - 기존 와이어프레임 `11_크레딧.html`에는 "예약"·"예약 해제" 행이 있어 정해진 기준과 다르다.
7. **내역 `size` 기본값·최대값**: Q-06에서 정한다.
8. **USER로 전환한 고수의 크레딧 API 접근 범위**: Q-06에서 정한다. 현재 잔액 조회 코드는 역할이 EXPERT가 아니면 403으로 막는다.
9. **환불(CR-08, API `POST /api/payments/{paymentId}/refund`)**
   - 대상과 범위는 8차 회의 확정 항목이다(Q-05).
   - 더미에는 환불 버튼과 안내 문구를 넣지 않았다. 내역 필터에 "환불" 유형만 있다.
10. **탈퇴 시 잔액 처리**: 8차 회의 확정 항목이다.
11. **README의 담당 범위 표기**
    - README의 김지웅 담당 범위에는 "채팅 시작 과금"이라고 적혀 있다.
    - MVP 정책은 의뢰 요청 시 차감하고 첫 메시지를 과금 시점으로 쓰지 않는다. README 표현 정리가 필요하다(이번에 수정하지 않음).
12. **결제·상품·내역 오류 코드**: 예약·차감 관련 4개만 정의됐다. 결제 실패·금액 불일치·상품 없음 등은 아직 없다.
13. **견적·채팅 연동** (호출 주체 확정, 11/03)
    - 크레딧 도메인은 예약·차감·해제 기능만 제공하고, 태민님(견적)과 영서님(의뢰 요청·채팅방)이 각자 기능에서 호출한다.
    - 견적 발송 → `reserveForQuote`, 의뢰 요청 → `chargeForConnection` 후 채팅방 생성, 요청서 취소·3일 만료·SENT 회수 → `releaseReservation`(`releaseReservations`).
    - 상황별 담당은 프론트엔드 요청의 담당 범위 기준이다(요청서·견적·회수: 태민님, 의뢰 요청·채팅방 생성: 영서님).
    - 견적 회수 API와 3일 만료 처리는 태민님이 구현 예정이다(11/03). 처리 방식(스케줄러 또는 조회 시 판정)은 구현되면 이 문서에 갱신한다.

## 8. 화면 흐름

```text
W1 잔액·상품 선택 ─다음→ W2 결제 정보 확인 ─결제하기(READY 주문 생성)→ W2-1 모의 결제창
   W2-1 ─성공→ W3-a DONE ─잔액·내역 확인→ W4 내역
   W2-1 ─실패→ W3-b FAILED ─다시 결제하기(새 주문)→ W2
   W2-1 ─사용자 취소→ W3-c CANCELED ─다시 결제하기(새 주문)→ W2
   W2-1 ─승인 응답 지연→ W3-d PENDING ─결과 다시 확인→ W3-a / W3-b / 유지
   W4 ─크레딧 충전하기→ W1
```

해시 경로: `#/credit` → `#/checkout?productId=` → `#/result?orderId=` → `#/history`

## 9. 화면별 구성·필요 데이터·API 연결 지점

API 연결 지점의 상태 표시:

- **구현됨**: 코드에 컨트롤러가 있다.
- **초안·미구현**: 노션에 명세 초안이 있지만 코드는 없다.
- **API 없음**: 대응하는 명세도 없어 결정이 필요하다.

각 화면의 하단 "이 화면의 데이터 · API 연결 지점"을 펼치면 같은 내용과 마지막 mock 응답을 볼 수 있다.

### W1 크레딧 잔액 확인·충전 상품 선택 (`#/credit`)

구성:

- 잔액 카드 3개: 총잔액, 견적에 예약됨, 사용 가능(발송 가능 견적 수 포함)
- 정책 안내: 견적 1건 = 2크레딧 예약, 매칭 시 차감, 1크레딧 = 1,000원
- 사용 가능 크레딧이 2보다 적으면 견적 발송 불가 안내
- 충전 상품 카드 단일 선택. 예시 데이터 표시
- [다음: 결제 정보 확인]은 상품을 선택하기 전에 비활성
- [충전·사용 내역]

| 필요 데이터 | API 연결 지점 | 상태 |
|---|---|---|
| `balance`, `reservedCredit`, `creditPerQuote`, `availableQuoteCount` | `GET /api/members/me/credit` (API-66) | **구현됨** (EXPERT 전용, 지갑 없으면 0) |
| 사용 가능 크레딧 | 지웅님 구현 중 | 구현 중 |
| `id`, `name`, `price`, `creditAmount`, `bonusCredit` | `GET /api/credit-products` (API-65) | 초안·미구현 |

### W2 결제 정보 확인·모의 결제 (`#/checkout`)

구성:

- 선택 상품, 충전 크레딧, 결제 금액
- 충전 후 예상 총잔액·사용 가능 크레딧
- 테스트 결제 안내
- [결제하기]를 누르면 주문을 생성하고 모의 결제창(W2-1)을 연다.
- [상품 다시 선택]

| 필요 데이터 | API 연결 지점 | 상태 |
|---|---|---|
| 요청 `creditProductId` → 응답 `paymentId`, `orderId`, `orderName`, `amount` | `POST /api/payments` (API-68) | 초안·미구현 |
| 결제창 호출 값 `orderId`, `orderName`, `amount` | 토스 테스트 결제창 SDK 호출. 더미는 모의 결제창으로 대체 | 연동 전 |
| 충전 후 예상 잔액 | W1의 잔액 응답과 상품 정보로 프론트에서 계산 | — |

### W3 결제 결과 (`#/result`)

| 결과 | 표시 | 버튼 | 잔액·내역 |
|---|---|---|---|
| W3-a 성공 DONE | 충전 크레딧, 충전 후 총잔액, 주문 정보 | [잔액·내역 확인], [크레딧 화면으로], [같은 주문 승인 재요청](더미 전용) | 1회 반영 |
| W3-b 실패 FAILED | 지급되지 않음 안내 | [다시 결제하기](새 주문), [상품 다시 선택] | 변화 없음 |
| W3-c 사용자 취소 CANCELED | 지급되지 않음 안내 | [다시 결제하기](새 주문), [상품 다시 선택] | 변화 없음 |
| W3-d 처리 확인 중 PENDING | 확인 전에는 지급되지 않음 안내 | 재확인 결과 선택 상자(더미 전용), [결과 다시 확인], [내역 보기] | 재확인 결과가 DONE일 때만 1회 반영 |

| 필요 데이터 | API 연결 지점 | 상태 |
|---|---|---|
| 요청 `paymentKey`, `orderId`, `amount` → 응답 `orderId`, `paymentStatus`, `chargedCredit`, `balance` | `POST /api/payments/confirm` (API-69). 토스 successUrl 처리 후 호출 | 초안·미구현 |
| 요청 `code`, `message` → 응답 `orderId`, `paymentStatus` | `PATCH /api/payments/{orderId}/fail` (API-70). 토스 failUrl 처리 후 호출 | 초안·미구현 |
| PENDING 응답 형식 | API-69 | 미확정 |
| 결제 결과 재확인 | 결제 조회 API | API 없음 |

### W4 충전·사용 내역 (`#/history`)

구성:

- 잔액 카드 3개(W1과 같음)
- 유형 필터: 전체·충전·사용·환불
- 내역 표: 일시, 유형, 관련(결제 #·견적 #), 변동, 변동 후 잔액
- 최신순, 이전·다음 페이지
- 방금 충전한 행 강조
- "견적에 예약된 크레딧" 표: 발송 일시, 견적, 예약 크레딧. mock이며 조회 API는 미정이다.
- [크레딧 충전하기]

| 필요 데이터 | API 연결 지점 | 상태 |
|---|---|---|
| 잔액 카드 데이터 | `GET /api/members/me/credit` (API-66) | 구현됨. 사용 가능 크레딧은 지웅님 구현 중 |
| 쿼리 `type`, `page`, `size` → `PageResponse<{ id, type, amount, balanceAfter, paymentId, quoteId, createdAt }>` | `GET /api/members/me/credit-histories` (API-67) | 초안·미구현 |
| 예약 중인 견적 목록 | 조회 API (`CreditReservation` 엔티티는 있음) | API 없음 |

## 10. 더미 동작 규칙

- **성공한 주문만 반영**: 잔액과 CHARGE 내역은 주문이 DONE이 될 때 한 번만 추가된다. 실패·취소·처리 중 주문은 반영하지 않는다.
- **중복 충전 방지**
  - 처리 중에는 화면과 결제창의 버튼을 모두 잠근다.
  - 결과가 나오면 해당 주문의 결제창을 다시 열 수 없다.
  - 다시 결제하면 새 주문을 만든다.
  - 같은 주문의 승인을 다시 요청해도 mock API가 DONE을 확인하고 재지급하지 않는다. 결과 화면의 [같은 주문 승인 재요청]으로 확인할 수 있다.
  - 뒤로 가기·앞으로 가기로 결과 화면을 다시 열어도 반영되지 않는다.
- **상태 전환 보호**: DONE 주문은 실패·취소 기록으로 덮어쓰지 않는다.
- **초기화**: 하단 "더미 컨트롤"의 [초기 상태로]를 누르면 mock 데이터를 초기값으로 되돌리고 W1로 이동한다. 새로고침해도 초기화된다(메모리 저장).
- **더미 컨트롤 패널**: mock 총잔액·예약·사용 가능 크레딧과 전체 주문의 상태를 보여 준다.
- **실서비스에서 뺄 요소**: 모의 결제창의 결과 선택 버튼, 재확인 결과 선택 상자, [같은 주문 승인 재요청], 더미 컨트롤, 데이터·API 패널

## 11. 실제 연동 시 교체 지점

- 화면은 `src/api/creditApi.ts`의 `CreditApi` 인터페이스만 사용한다. 같은 인터페이스를 HTTP로 구현한 뒤 `export const creditApi = mockCreditApi;`의 교체 지점만 바꾼다.
- 요청·응답 타입은 `src/types.ts`에 API 명세 초안 기준으로 정의했다. 명세가 확정되면 이 파일부터 맞춘다.
  - 잔액 조회는 `balance`·`reservedCredit`까지 구현됐고 사용 가능 크레딧은 지웅님이 구현 중이다. 반영되면 `MyCreditResponse`와 `BalanceCards.tsx`·`CreditPage.tsx`·`CheckoutPage.tsx`를 그 응답에 맞춘다.
- `src/components/MockPayModal.tsx`는 토스 테스트 결제창 호출로 교체한다. successUrl·failUrl을 받는 화면에서 각각 `confirmPayment`, `failPayment`를 호출하도록 연결한다.
- `DummyControls`(`confirmPaymentTimeout`·`recheckPayment`·`snapshot`·`reset`·`subscribe`)와 `getReservations`는 더미 전용이다. 화면 하단 패널은 `src/components/DevPanel.tsx`다. 대응 API가 확정되면 교체하거나 제거한다.
- 중복 클릭 방지는 `src/hooks/useSingleFlight.ts`를 그대로 사용할 수 있다. 서버 쪽 멱등 처리와는 별개다.
