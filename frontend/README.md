# EFY 프론트엔드

EFY 웹·모바일 반응형 프론트엔드이다 (React + Vite).
메인 화면은 새로 설계했다.
나머지 화면은 기존 화면 코드(#186·#187)의 기능을 그대로 쓰고 모양을 EFY 디자인 체계로 바꿨다.
디자인 규칙은 `DESIGN.md`, 제품 방향은 `PRODUCT.md`에 정리했다.

## 실행

```bash
cd frontend
npm install
npm run dev
```

브라우저에서 `http://localhost:5173`을 연다 (사용 중이면 Vite가 다른 포트를 고른다).
빌드는 `npm run build`로 확인한다. 타입 검사 후 `dist/`를 만든다.

## 백엔드 연결

- `.env`의 `VITE_API_BASE_URL`은 비워 두고 같은 출처의 `/api`, `/ws`로 호출한다.
- 개발 서버의 Vite 프록시가 `EFY_PROXY_TARGET`으로 요청을 넘긴다. 비우면 로컬 백엔드 `http://localhost:8080`이다.
- 배포된 백엔드로 붙여 보려면 `.env.local`에 `EFY_PROXY_TARGET=<배포 주소>`를 둔다.
- 백엔드의 `app.frontend-url`이 `http://localhost:5173`이어야 리프레시 쿠키가 동작한다.
- 백엔드 CORS는 배포 프론트 주소만 허용하므로 프록시가 `Origin` 헤더를 지운다.
- Access 토큰은 로그인 응답 본문으로 받아 `localStorage`(`efy.accessToken`)에 둔다. Refresh 토큰은 HttpOnly 쿠키이고 401이 오면 한 번 재발급한 뒤 다시 요청한다 (`src/api/client.ts`).
- 크레딧 충전 결제창을 쓰려면 `.env.local`에 토스페이먼츠 테스트 클라이언트 키 `VITE_TOSS_CLIENT_KEY`를 넣는다.
- README의 테스트 계정(`customer1@example.com` 등)은 로컬 H2 전용이다. 배포 서버에서는 배포용 계정을 쓴다.

## 구조

| 위치 | 내용 | 바탕 |
| --- | --- | --- |
| `src/App.tsx` | 해시 주소(`#/...`) 라우팅, 접근 권한, 로그인 만료 안내 | 기존 코드 (메인 화면 연결과 관리자 경로 추가) |
| `src/api/`, `src/auth/`, `src/hooks/`, `src/lib/*.ts` | API 호출, 세션, 조회 훅, 문구 규칙, STOMP, 토스 결제 | 기존 코드 그대로 |
| `src/pages/*.tsx` | 메인을 뺀 모든 화면 | 기존 코드 (분야 아이콘, 예시 고수 화면, 관리자 화면 추가) |
| `src/components/ui.tsx`, `common.tsx`, `toast.tsx`, `ProfileView.tsx`, `BookmarkButton.tsx` | 공통 조각 | 기존 코드 (프로필 사진 크기, 작업 사진 추가) |
| `src/pages/HomePage.jsx`와 `src/components/*.jsx` | 메인 화면, 상단 바, 하단 탭, 푸터 | 새 디자인 |
| `src/styles/app.css` | 기능 화면 클래스(`btn primary`, `card`, `req-card` 등)를 EFY 토큰으로 다시 그린 스타일 | 새 디자인 |
| `src/styles/tokens.css` | 색·글자·간격·모서리 토큰과 다크 모드 값 | 새 디자인 |

기능 화면 스타일은 `main.page`와 모달(`.dim`) 안으로 범위를 좁혀서 메인 화면의 같은 이름 클래스와 섞이지 않는다.

## 화면 경로

| 경로 | 화면 | 로그인 |
| --- | --- | --- |
| `#/` | 메인 (서비스 찾기, 요청서 작성 시트) | - |
| `#/login`, `#/signup` | 로그인, 회원가입 | - |
| `#/community`, `#/community/{id}` | 커뮤니티 목록과 글 상세 | - |
| `#/community/write`, `#/community/mine` | 글쓰기, 내 글 | 필요 |
| `#/experts/{id}` | 고수 공개 프로필 | - |
| `#/experts/sample/{id}` | 메인의 예시 고수 (AI로 만든 가상 인물) | - |
| `#/mypage`, `#/mypage/info` | 마이페이지, 내 정보 수정 | 필요 |
| `#/expert/intro`, `#/expert/register` | 고수 등록 안내, 고수 프로필 등록 | 고객 |
| `#/expert/me`, `#/expert/me/edit` | 내 고수 프로필, 수정 | 필요 |
| `#/requests/new`, `#/requests` | 요청서 작성, 내 요청서와 받은 견적 | 고객 |
| `#/received`, `#/quotes` | 받은 요청서와 견적 보내기, 보낸 견적 | 고수 |
| `#/credit`, `#/credit/history`, `#/credit/result` | 크레딧 충전, 충전·사용 내역, 결제 결과 | 고수 |
| `#/chats`, `#/chats/{roomId}` | 채팅 목록과 채팅방 | 고객·고수 |
| `#/bookmarks` | 찜한 고수 (고수 상태에서는 메뉴와 버튼을 숨긴다) | 고객 |
| `#/admin/members`, `#/admin/categories`, `#/admin/regions` | 회원·카테고리·지역 관리 | 관리자 |
| `#/admin/posts` | 게시글 공개·숨김과 변경 이력 | 관리자 |
| `#/admin/requests`, `#/admin/quotes` | 요청서·견적 현황 (상태 필터·정렬) | 관리자 |

고수 전용 가입은 없다.
순서는 일반 회원가입 → 로그인 → 고수 프로필 등록이고 등록을 마치면 서버가 고수로 전환한다.

## 이번에 더한 기능

기존 화면이 범위 밖으로 두었던 백엔드 기능을 화면에 연결했다.

| 기능 | API | 화면 |
| --- | --- | --- |
| 고수 프로필 작업 사진 (최대 10장·설명·순서) | `POST/PATCH /api/members/me/expert-profile`의 `workImages` | 고수 프로필 등록·수정, 공개 프로필 |
| 고수 등록 화면의 프로필 사진 | `POST /api/files`, `PUT/DELETE /api/members/me/image` | 고수 프로필 등록 맨 위 |
| 회원 탈퇴 | `DELETE /api/members/me` | 내 정보 수정 아래 |
| 커뮤니티 분야·지역 필터 | `GET /api/public/posts`의 `firstCategoryId`, `firstRegionId` | 커뮤니티 목록 |
| 게시글 공개 관리 | `GET /api/admin/posts`, `PATCH .../visibility`, `GET .../visibility-history` | 관리자 게시글 관리 |
| 요청서·견적 현황 | `GET /api/admin/requests`, `GET /api/admin/quotes` | 관리자 요청서·견적 현황 |

## 메인 화면

| 순서 | 섹션 | 데이터 근거 |
| --- | --- | --- |
| 1 | 히어로 검색창과 흐름 예시 모션그래픽 | 서비스·지역은 실제 기준 데이터. 견적·채팅 내용은 예시 |
| 2 | 어떤 일을 맡기고 싶으세요? (입체 카테고리 11개와 소분류 펼치기) | 실제 (`data.sql` 대분류 11개·소분류 108개) |
| 3 | 상황에 맞춰 한 번에 찾기 | 소분류는 실제. 묶음은 편집 기준 |
| 4 | 우리 동네에서 활동하는 고수 | 예시 고수. 지역 이름은 실제 시군구 |
| 5 | 최근 2주 사이 새로 합류한 고수 | 예시. 등록일 필드 없음 |
| 6 | 이번 주 찜이 빠르게 늘고 있는 고수 | 예시. 기간별 찜 집계 없음 |
| 7 | 고수들의 작업 소개 | 예시. 사진은 가상 인물의 작업 장면. 여러 고수의 작업 사진을 모아 보는 공개 API 없음 |
| 8 | 커뮤니티 놀러가기 | 글 내용은 예시 |
| 9 | 고수 활동 안내 | 실제 규칙 |
| - | 요청서 작성 시트 | 실제 전송 (`POST /api/requests`) |

- 요청서 시트는 실제로 전송한다. 로그인하지 않았으면 작성한 내용을 `sessionStorage`에 잠시 보관하고 로그인 화면으로 보낸 뒤 돌아오면 다시 연다.
- 질문 번호는 `GET /api/questions` 응답을 표시 순서대로 맞춰 쓴다.
- 예시를 쓰는 섹션에는 회색 `예시 데이터` 표기를 붙였다. 예시 고수 카드는 `#/experts/sample/{id}`로 열려서 실제 고수 번호와 섞이지 않는다.
- 고수 프로필 사진은 AI로 만든 가상 인물이며 푸터와 예시 고수 화면에서 안내한다.

## 디자인

- 행동 버튼은 파랑 `#1F6AE6` 하나만 쓰고 청록은 거의 쓰지 않는다. 기존 화면의 청록 표시(매칭 배지 등)도 파랑 계열로 바꿨다.
- 바탕은 메인과 기능 화면 모두 거의 흰색인 무채색 `#FAFAFA`(`--canvas`)이고 헤더·카드·푸터는 흰 면(`--surface`)이다. 다크 모드에서도 면이 바탕보다 밝다.
- 요청서·받은 요청·팁 게시글에는 메인과 같은 입체 분야 아이콘을 붙여 화면이 이어지게 했다 (`src/components/CategoryIcon.jsx`).
- 서체는 나눔스퀘어 네오이고 히어로 제목만 Black Han Sans이다 (팀원 저장소 personal_AIBE8-Project2-Team01_FE 기준).
- 라이트·다크 모드: 처음에는 시스템 설정을 따르고 상단 바의 해·달 버튼으로 바꾼 선택은 저장된다.
- 모바일에서는 기존 화면 규칙대로 하단 탭을 쓰고 채팅방은 헤더 없이 화면 전체를 쓴다.

## 실제 데이터로 바꾸려면 필요한 API와 집계

| 화면 요소 | 지금 백엔드 상태 | 필요한 것 |
| --- | --- | --- |
| 우리 동네 고수 | 고수 공개 목록 API 없음 | `GET /api/public/expert-profiles?secondRegionId=&secondCategoryId=&page=` 같은 공개 목록·검색 API |
| 새로 합류한 고수 | `ExpertProfile`에 등록일 없음 | `ExpertProfile.createdAt`과 최신순 목록 |
| 찜이 늘고 있는 고수 | 공개 집계 없음 | 최근 7일 찜 수 집계 API |
| 고수 카드 찜 표시 | 찜 여부 조회 API 없음 (지금은 내 찜 목록을 넘겨 보며 판별) | 목록·상세 응답에 로그인 회원의 찜 여부 필드 |
| 견적 카드의 고수 사진 | 견적 응답에 고수 사진이 없음 | `RequestQuote`에 고수 프로필 이미지 URL |
| 평점·후기 | 리뷰 엔티티 없음 | 리뷰·별점 도메인 |

## 폴더

- `src/data/baseData.js`: 백엔드 `data.sql`에서 추출한 카테고리·지역 (실제)
- `src/data/questions.js`: 요청서 질문 (실제, 표시 순서 기준)
- `src/data/sampleData.js`: 메인의 고수·게시글 예시 (시안용)
- `src/data/categoryMeta.js`: 분야별 입체 아이콘, 상황별 묶음, 서버 응답 이름으로 아이콘 찾기
- `public/brand/`: 팀 로고 투명 PNG와 파비콘
- `public/efy-assets/`: 입체 아이콘(`icons/`, 화면용 `icons-256/`)과 가상 인물 프로필 사진(`portraits/`, 작업 장면 v2)과 작은 아바타용 얼굴 사본(`faces/`)
