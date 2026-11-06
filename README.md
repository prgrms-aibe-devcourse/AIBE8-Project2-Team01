<!--suppress HtmlDeprecatedAttribute -->
<div align="center">

<img src="docs/images/efy-logo.png" width="420" alt="EFY 로고" />

# EFY

### Experts for you

**필요한 일상 서비스와 나에게 맞는 고수를 연결합니다.**

일반 사용자와 고수가 견적 요청과 제안을 통해 만나는 일상 서비스 매칭 플랫폼

2026.10.26 – 2026.11.06 · 백엔드 개발자 5인 팀 프로젝트

<br />

<table align="center">
  <tr>
    <th align="center" width="20%">이현정 · 팀장</th>
    <th align="center" width="20%">김태민</th>
    <th align="center" width="20%">김지웅</th>
    <th align="center" width="20%">박건웅</th>
    <th align="center" width="20%">김영서</th>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/bam090"><img src="https://github.com/bam090.png" width="100" alt="이현정 GitHub 프로필" /></a></td>
    <td align="center"><a href="https://github.com/qkdtkd77"><img src="https://github.com/qkdtkd77.png" width="100" alt="김태민 GitHub 프로필" /></a></td>
    <td align="center"><a href="https://github.com/j-woong"><img src="https://github.com/j-woong.png" width="100" alt="김지웅 GitHub 프로필" /></a></td>
    <td align="center"><a href="https://github.com/pgw2001"><img src="https://github.com/pgw2001.png" width="100" alt="박건웅 GitHub 프로필" /></a></td>
    <td align="center"><a href="https://github.com/Luvys99"><img src="https://github.com/Luvys99.png" width="100" alt="김영서 GitHub 프로필" /></a></td>
  </tr>
  <tr>
    <td align="center"><a href="https://github.com/bam090">@bam090</a></td>
    <td align="center"><a href="https://github.com/qkdtkd77">@qkdtkd77</a></td>
    <td align="center"><a href="https://github.com/j-woong">@j-woong</a></td>
    <td align="center"><a href="https://github.com/pgw2001">@pgw2001</a></td>
    <td align="center"><a href="https://github.com/Luvys99">@Luvys99</a></td>
  </tr>
</table>

<br />

[배포 사이트](https://port-0-efy-api-mw1zsbylfe8cd41c.sel3.cloudtype.app) ·
[프로젝트 문서](https://app.notion.com/p/3f942d9a1d1880dbaa98e9d0c85e377f) ·
[요구사항 명세서](https://app.notion.com/p/de442d9a1d1882ac85d901a68f007fa6) ·
[API 명세서](https://app.notion.com/p/8d342d9a1d188283955b81b70bfc58af) ·
[ERD](https://app.notion.com/p/40042d9a1d18807a96fcc32f2d425820)

</div>

---

## 프로젝트 소개

EFY는 이사·과외 등 일상에 필요한 서비스를 제공하는 전문가와 사용자를 연결하는 플랫폼입니다.<br />
사용자는 견적을 요청하고, 제안받은 견적을 비교해 자신에게 맞는 전문가를 선택할 수 있습니다.<br />
전문가는 커뮤니티에서 노하우를 공유하고 자신의 서비스를 홍보할 수 있습니다.<br /><br />
팀은 백엔드 구현에 집중하며, 데스크톱·모바일 프론트엔드는 AI를 활용해 제작했습니다.

## 기술 스택

| 구분           | 기술                          |
|----------------|-------------------------------|
| 언어           | Java 25                       |
| 프레임워크     | Spring Boot 4.1.2, Spring MVC |
| 인증·인가      | Spring Security, JWT          |
| 데이터 접근    | Spring Data JPA               |
| 데이터베이스   | H2(로컬), MariaDB(배포)       |
| API 문서       | springdoc-openapi, Swagger UI |
| 빌드·개발 도구 | Gradle, Lombok, MapStruct     |
| 실시간 통신    | WebSocket, STOMP              |
| 외부 연동      | Toss Payments(테스트), Supabase Storage |
| 프론트엔드     | React 18, TypeScript, Vite (AI 활용 제작) |
| 배포           | Docker, CloudType             |

## 로컬 실행

1. `src/main/resources/application-secret.properties.example`을 복사해서 `application-secret.properties`로 이름을 바꾼다.
2. 팀 페이지에서 공유된 비밀값(`jwt.secret`, `toss.secret-key`, `supabase.*`)을 채운다.
3. `./gradlew bootRun` 또는 IntelliJ에서 `EfyApplication`을 실행한다.

| 주소                                          | 용도                                     |
|-----------------------------------------------|------------------------------------------|
| <http://localhost:8080/swagger-ui/index.html> | API 문서                                 |
| <http://localhost:8080/h2-console>            | H2 콘솔 (JDBC URL: `jdbc:h2:mem:gosuDB`) |

### 프론트엔드

1. `frontend/.env.example`을 복사해 `frontend/.env.local`을 만들고 `VITE_TOSS_CLIENT_KEY`(토스 테스트 클라이언트 키)를 채운다.
2. 백엔드를 먼저 실행한 뒤 `frontend` 폴더에서 `npm install`, `npm run dev`를 실행한다.
3. <http://localhost:5173>에 접속한다. `/api`, `/ws` 요청은 개발 서버가 8080으로 넘긴다.

## 로컬 테스트 계정

기존 계정 6개를 유지하고 팀원별 일반 계정 30개, 탈퇴 테스트용 고객 5개, 홈 화면 노출용 고수 샘플 계정 18개(`profile101`~`profile120@example.com` 중 18개)를 추가해 총 **59개**를 구성한다. 공통 비밀번호는 **1234**이며 DB에는 BCrypt 해시로 저장한다. 로컬 H2 테스트 전용이며 배포용 계정으로 사용하지 않는다.

아래 번호는 customer·expert·admin에 동일하게 사용한다. 예를 들어 11번은 customer11@example.com·expert11@example.com·admin11@example.com이다. 팀원당 일반 계정은 고객 2개·고수 2개·관리자 2개이며 탈퇴용 고객 1개를 별도로 사용한다.

| 사용 구분 | 고객·고수·관리자 공통 번호 | 탈퇴 테스트 전용 고객 |
| --- | --- | --- |
| 팀원 1 · 10번대 | 11·12 | customer19@example.com |
| 팀원 2 · 20번대 | 21·22 | customer29@example.com |
| 팀원 3 · 30번대 | 31·32 | customer39@example.com |
| 팀원 4 · 40번대 | 41·42 | customer49@example.com |
| 팀원 5 · 50번대 | 51·52 | customer59@example.com |

탈퇴용 계정은 처음에는 정상 USER 상태이다. 회원 탈퇴 검증에 사용하고 일반 화면 테스트용 계정과 구분한다. 초기 역할별 수는 고객 17개·고수 31개(샘플 18개 포함)·관리자 11개이다. 고수 샘플 계정은 각각 20크레딧을 가진다.

| 기존 계정 | 테스트 조건 |
| --- | --- |
| customer1@example.com | 기존 요청서·견적 연결 확인 |
| customer2@example.com | 기존 만료 요청서·견적 조건 확인 |
| admin1@example.com | 기존 관리자 API 확인 |
| expert1@example.com | 이사·입주 청소 / 대표 이사 / 강남구·서초구. 초기 10크레딧 중 기존 견적 2건에 4크레딧 예약 |
| expert2@example.com | 개발 외주·이사 / 대표 개발 외주 / 서초구. 초기 2크레딧 전액 예약으로 추가 견적 발송 시 크레딧 부족 확인 |
| expert3@example.com | 프로필 없는 예외 검증 전용. 본인 프로필·받은 요청 조회 404 예상 |

신규 고수의 제공 서비스는 끝자리 1번이 이사·입주 청소(대표 이사), 2번이 이사·개발 외주(대표 개발 외주)이다. 1번은 표의 개별 시군구, 2번은 해당 시도 전체에서 활동한다.

| 팀원 | 1번 고수 활동 지역 | 2번 고수 활동 지역 | 1번 고수 견적 | 2번 고수 견적 |
| --- | --- | --- | --- | --- |
| 1 · 11/12 | 서울 강남구 | 서울 전체 | 미발송 | SENT 1건 |
| 2 · 21/22 | 부산 중구 | 부산 전체 | SENT 1건 | REQUESTED 1건 |
| 3 · 31/32 | 대구 중구 | 대구 전체 | SENT 2건 | SENT 1건·REQUESTED 1건 |
| 4 · 41/42 | 인천 제물포구 | 인천 전체 | SENT 1건 | 미발송 |
| 5 · 51/52 | 대전 동구 | 대전 전체 | SENT 2건 | REQUESTED 1건 |

팀원별 고객 1번에는 이사 OPEN·개발 외주 CLOSED 요청서, 고객 2번에는 입주 청소 OPEN·개발 외주 요청서를 저장한다. 고객 2번의 개발 외주는 2·3·5팀에서 MATCHED이고 나머지는 OPEN이다. 요청서마다 질문 답변을 포함한다. 신규 고수는 지역·서비스 조건에 맞는 요청만 받아 팀원별 목록이 구분된다. 종료 요청서는 받은 요청·내 요청 목록에서 제외된다.

신규 고수의 초기 지급 기준은 20크레딧이다. SENT는 건당 2크레딧 예약, REQUESTED는 건당 2크레딧 차감과 USE 내역·채팅방·참여자까지 연결한다. 미발송은 견적 행이 없는 상태이며 별도의 견적 상태 코드는 아니다. 탈퇴 전용 계정에는 요청서와 견적을 넣지 않는다.

기존 회원 6명의 ID·역할과 요청서·견적·크레딧 연결은 유지한다. 신규 계정은 시드 마지막에 추가한다. 서버를 재시작하면 H2 데이터와 테스트 중 변경한 잔액·역할·탈퇴 상태도 초기화된다. 위 예약액은 초기 로딩 기준이며 만료 처리나 테스트 진행 후 달라질 수 있다.

## 주요 기능

- **회원·인증**: 회원가입·로그인, JWT 인증·재발급·로그아웃, USER·EXPERT 역할 전환, 관리자 회원 관리
- **기준 데이터**: 카테고리·지역 공개 조회 및 관리자 등록·수정·삭제
- **고수 프로필**: 등록·수정·공개 조회, 활동 지역·제공 서비스, 작업 사진
- **견적**: 요청서 작성·취소, 조건에 맞는 고수에게 노출, 견적 발송·회수·만료, 의뢰 요청
- **채팅**: WebSocket(STOMP) 실시간 메시지·사진, 안 읽음 수, 읽음 처리, 나가기
- **커뮤니티**: 고수 노하우·고수에게 묻다 게시글·댓글·답글, 좋아요·찜, 관리자 공개 여부 관리
- **크레딧·결제**: 토스 테스트 결제 충전, 견적 발송 시 크레딧 예약·의뢰 요청 시 차감·만료 시 해제, 충전·사용 내역

## 담당 범위

아래는 팀원별 개발 담당 범위이며, 전체 기능의 구현 완료를 의미하지 않습니다.

| 담당   | 영역                  | 구현 범위                                                     |
|--------|-----------------------|---------------------------------------------------------------|
| 이현정 | 회원·인증·기준 데이터 | 회원 관리, JWT, 카테고리·지역, 공통 설정                      |
| 이현정 | 고수 프로필           | 고수 등록·조회·수정, 활동 지역·제공 서비스 연결               |
| 김태민 | 견적 요청·매칭        | 고객 요청, 조건에 맞는 고수 조회, 견적서 발송·선택, 완료 처리 |
| 김영서 | 채팅                  | WebSocket 연결, 채팅방·참여자·메시지, 대화 조회               |
| 박건웅 | 커뮤니티              | 고수 노하우·고수에게 묻다, 게시글·댓글·답글                   |
| 박건웅 | 리뷰·반응             | 완료 서비스 리뷰·별점, 찜·좋아요                              |
| 김지웅 | 크레딧·결제           | 토스 테스트 결제(주문·승인·실패 기록), 충전 상품, 견적 크레딧 예약·차감·해제, 잔액·사용 가능 크레딧, 충전·사용 내역 |

## 협업 방식

- `main`: 배포용 브랜치
- `dev`: 기능 통합 브랜치
- 기능 브랜치: `dev`에서 생성하며 `<type>/<이슈번호>-<기능명>` 형식 사용
- 작업 완료 후 `dev` 대상으로 PR을 작성하고 Squash and merge로 병합
- PR 본문에 `Closes #이슈번호`를 작성해 이슈 연결

[코드 컨벤션](https://app.notion.com/p/d7942d9a1d1883e9b3d7816f1343598c) · [테스트 및 이슈](https://app.notion.com/p/0fb42d9a1d1883d09800019f804d1c65)
