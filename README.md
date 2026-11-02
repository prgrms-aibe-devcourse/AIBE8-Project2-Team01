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
팀은 백엔드 구현에 집중하며, 데스크톱·모바일 프론트엔드는 AI를 활용해 제작합니다.

## 기술 스택

| 구분           | 기술                          |
|----------------|-------------------------------|
| 언어           | Java 25                       |
| 프레임워크     | Spring Boot 4.1.2, Spring MVC |
| 인증·인가      | Spring Security, JWT          |
| 데이터 접근    | Spring Data JPA               |
| 데이터베이스   | H2(로컬), MariaDB(배포 설정)  |
| API 문서       | springdoc-openapi, Swagger UI |
| 빌드·개발 도구 | Gradle, Lombok, MapStruct     |

## 로컬 실행

1. `src/main/resources/application-secret.properties.example`을 복사해서 `application-secret.properties`로 이름을 바꾼다.
2. 팀 페이지에서 공유된 비밀값을 채운다.
3. `./gradlew bootRun` 또는 IntelliJ에서 `EfyApplication`을 실행한다.

| 주소                                          | 용도                                     |
|-----------------------------------------------|------------------------------------------|
| <http://localhost:8080/swagger-ui/index.html> | API 문서                                 |
| <http://localhost:8080/h2-console>            | H2 콘솔 (JDBC URL: `jdbc:h2:mem:gosuDB`) |

## 초기 구현 범위

- 회원가입·이메일 로그인·JWT 인증·토큰 재발급·로그아웃
- 회원 조회·정보 수정·논리 삭제 및 관리자 회원 관리
- 카테고리·지역 공개 조회 및 관리자 등록·수정·삭제
- 카테고리·지역 기준 데이터와 테스트 회원 시드
- 공통 예외 처리 및 Swagger API 문서

고수 프로필·견적·채팅 등 후속 도메인은 팀별 담당 범위에 따라 개발합니다.

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
| 김지웅 | 크레딧·결제           | 토스 테스트 결제, 충전·차감·내역, 채팅 시작 과금              |

## 협업 방식

- `main`: 배포용 브랜치
- `dev`: 기능 통합 브랜치
- 기능 브랜치: `dev`에서 생성하며 `<type>/<이슈번호>-<기능명>` 형식 사용
- 작업 완료 후 `dev` 대상으로 PR을 작성하고 Squash and merge로 병합
- PR 본문에 `Closes #이슈번호`를 작성해 이슈 연결

[코드 컨벤션](https://app.notion.com/p/d7942d9a1d1883e9b3d7816f1343598c) · [테스트 및 이슈](https://app.notion.com/p/0fb42d9a1d1883d09800019f804d1c65)
