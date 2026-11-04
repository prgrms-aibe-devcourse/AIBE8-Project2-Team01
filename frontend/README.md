# EFY 프론트엔드

이슈 #92 회원·고수 프로필 화면이에요.
로그인 · 마이페이지 · 내 정보 수정 · 고수 전환 안내 · 고수 프로필 등록/수정 · 내 고수 프로필 · 공개 고수 프로필을 포함해요.

## 실행

```bash
cd frontend
npm install
npm run dev   # http://localhost:5173
```

- 개발 서버는 `/api` 요청을 `http://localhost:8080`(로컬 백엔드)으로 넘겨요.
- 백엔드의 `app.frontend-url`이 `http://localhost:5173`이어야 리프레시 쿠키가 동작해요.

## 빌드

```bash
npm run build   # 타입 검사 후 dist/ 생성
```

배포 시 API 주소가 다르면 `.env.example`을 참고해 `VITE_API_BASE_URL`을 지정해요.

## 화면 경로

| 경로 | 화면 | 로그인 |
| --- | --- | --- |
| `#/login` | 로그인 | - |
| `#/mypage` | 마이페이지 | 필요 |
| `#/mypage/info` | 내 정보 수정 | 필요 |
| `#/expert/intro` | 고수 전환 안내 | 필요 |
| `#/expert/register` | 고수 프로필 등록 | 필요 |
| `#/expert/me` | 내 고수 프로필 | 필요 |
| `#/expert/me/edit` | 고수 프로필 수정 | 필요 |
| `#/experts/{id}` | 공개 고수 프로필 | - |

## 인증

- 로그인 응답의 액세스 토큰을 `localStorage`(`efy.accessToken`)에 저장해요.
- 401을 받으면 기존 토큰과 리프레시 쿠키로 한 번 재발급한 뒤 다시 요청해요.
- 재발급에 실패하면 토큰을 지우고 로그인 화면으로 보내요.
- 역할(고객/고수)은 항상 `GET /api/members/me` 응답 기준이에요.
