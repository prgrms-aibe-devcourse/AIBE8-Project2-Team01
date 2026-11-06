# Docker 배포 준비

Node 22에서 프론트를 빌드하고 결과물을 Spring Boot의 정적 파일로 포함한다. Java 25 JDK 이미지에서 Gradle로 실행 JAR을 만들며 실행 이미지에는 Java 25 JRE와 통합 JAR만 둔다.

- `Dockerfile`은 프론트 빌드, JAR 패키징, 실행 이미지의 세 단계로 구성한다. 포트는 8080이며 기본 프로필은 `prod`다.
- `.dockerignore`는 프론트 소스와 공개 파일, Gradle 설정과 백엔드 소스를 빌드에 전달한다. 비밀 설정, 환경 파일, 로컬 빌드 결과와 수동 시드는 제외한다.

시드는 제외한다.

시드는 [MariaDB 시드 안내](mariadb-seed.md)에 따라 DB에 수동 적용한다. 이미지에 시드 파일을 넣거나 시작할 때 실행하지 않는다. `prod`의 `spring.sql.init.mode=never`와 상속되는 `ddl-auto=update`는 현재 설정대로 유지한다.

## 빌드

저장소 루트에서 실행한다. 빌드에는 실제 DB 비밀번호나 외부 서비스 키가 필요하지 않다.

```bash
cd /Users/goonbam/study/grepp/project2/AIBE8-Project2-Team01
./gradlew test
docker build -t efy:local .
```

이미지 빌드는 `npm ci`, 프론트 타입 검사와 Vite 빌드, Gradle `bootJar`를 실행한다. 테스트는 위처럼 별도로 실행한다. 첫 빌드는 JDK 이미지와 Gradle·의존성 다운로드에 시간이 걸릴 수 있다.

프론트는 해시 경로를 사용한다. `/#/mypage` 같은 화면을 새로고침해도 서버는 루트의 HTML을 제공한다. API와 WebSocket은 화면과 같은 주소를 사용한다.

토스 테스트 결제창을 포함하려면 Docker 빌드 인자 `VITE_TOSS_CLIENT_KEY`에 브라우저 공개용 테스트 클라이언트 키를 설정한다. 서버 전용 `TOSS_SECRET_KEY`는 실행 환경변수로만 설정한다.

## 클라우드타입 설정안

아래는 아직 실행하지 않은 배포 설정안이다. 커밋·푸시·PR과 실제 배포는 사용자 승인 범위에서 별도로 진행한다.

| 항목 | 설정 |
| --- | --- |
| 스페이스 / 프로젝트 / 배포환경 | `ezzange` / `efy` / `main` |
| 템플릿 | Dockerfile |
| 서브 디렉토리 | 비워 두기. 저장소 루트의 Dockerfile 사용 |
| 서비스 이름 | `efy-api` |
| 대상 포트 | `8080` (HTTP) |
| 리소스 | 프리티어, 최소 CPU, 메모리 512MB |
| 레플리카 | 1개. 지역 코드 동시성은 단일 JVM 기준 |
| 업데이트 방식 | Recreate. 1GB를 DB와 앱이 512MB씩 쓰므로 추가 인스턴스가 필요한 배포 방식은 피한다. 교체 중 일시 중단은 허용한다. |
| 시작 명령 | 별도 지정 없이 Dockerfile의 ENTRYPOINT 사용 |
| Health Check 경로 | `/api/public/first-categories` |
| 영구 디스크 | 백엔드에는 추가하지 않음. 업로드 파일은 기존 Supabase Storage 사용 |

MariaDB와 같은 배포환경에 배치한다. 로컬에서 사용하는 외부 접속 주소 `svc.sel3.cloudtype.app:32605` 대신 내부 주소 `efy-db:3306`을 쓴다. 실제 배포 브랜치는 배포 직전 이슈와 병합 상태를 확인하여 선택한다.

## 실행 환경변수

비밀값은 Dockerfile·빌드 인자·Git에 넣지 않고 클라우드타입 서비스의 실행 환경변수 또는 시크릿으로 설정한다.

| 이름 | 값 또는 준비할 내용 |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `prod` (이미지 기본값에도 지정) |
| `DB_HOST` | `efy-db` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `efy` |
| `DB_USERNAME` | 현재 DB 연결 검증에 사용한 `root` |
| `DB_PASSWORD` | 클라우드타입 MariaDB 생성 시 정한 비밀번호. 시드 계정 비밀번호와 다름 |
| `JWT_SECRET` | 현재 사용한 서명 키. 32바이트 이상이며 서버 재배포 시에도 같은 값 사용 |
| `FRONTEND_URL` | 통합 서비스의 공개 주소. 로컬 Docker 확인은 `http://localhost:18080` |
| `SUPABASE_URL` | 기존 팀 Supabase 프로젝트 URL |
| `SUPABASE_SERVICE_KEY` | 서버 전용 service_role 키 |
| `SUPABASE_BUCKET_PUBLIC` | 프로젝트에 설정된 공개 버킷명 |
| `SUPABASE_BUCKET_PRIVATE` | 프로젝트에 설정된 비공개 버킷명 |
| `TOSS_SECRET_KEY` | 팀 결제 담당자의 토스 테스트 시크릿 키 |

`JAVA_TOOL_OPTIONS=-XX:MaxRAMPercentage=40.0 -XX:+UseSerialGC -XX:ActiveProcessorCount=2`과 `TZ=Asia/Seoul`은 이미지 기본값이다. 최대 Java 힙을 컨테이너 메모리의 40%로 제한하고 나머지는 JVM·스레드·네이티브 메모리에 남긴다. 최종 메모리는 힙 이외에도 사용되므로 실행 후 워크로드를 확인한다.

## 확인한 결과

2026-11-04에 로컬 Docker에서 검증했다.

- 백엔드 자동 테스트: 310개 통과. 실패·오류·건너뜀 0개
- Docker 이미지 실제 빌드 성공. 로컬 검증 아키텍처 arm64, 이미지 약 170MiB
- 실제 빌드 컨텍스트 265개 파일 확인. 비밀 설정과 예제·환경 파일·프론트·테스트·로컬 빌드·시드 제외
- 실행 이미지에서 꺼낸 JAR에도 위 비밀 설정·예제·환경 파일·시드 미포함 확인
- UID/GID 1000, 메모리 512MiB·스왑 없음, 별도 포트 18074로 실행
- 실제 MariaDB 11.2.6을 별도 컨테이너에 띄우고 내부 이름 `efy-db:3306`으로 연결 성공
- 빈 DB의 첫 실행에서 카테고리·지역 공개 API가 빈 배열 반환. 시드 자동 삽입 없음
- 임시 DB에 시드를 적용한 뒤 앱 컨테이너를 교체해도 기존 기준 데이터 수량 유지
- 최종 이미지 실제 HTTP 12개 요청 통과: 관리자 로그인·목록, 기준 데이터·게시판·고수 조회, Swagger·API 문서·H2 콘솔 404, 허용 Origin의 CORS, Refresh 쿠키의 Secure·HttpOnly 속성
- 초기 힙 상한 60%에서 관측 메모리 약 473MiB로 여유가 적어 50%로 조정. 같은 기본 API 검증 후 약 423MiB, OOM 없음

검증용 앱·MariaDB 컨테이너와 네트워크는 종료·정리한다. 사용자 IntelliJ 8080 서버와 실제 클라우드 MariaDB는 변경하지 않았다. 검증 이미지는 로컬 `efy:174-check`로 남긴다.

위 결과는 적은 수의 기본 요청 확인이다. 동시 접속·채팅·최대 파일 업로드의 부하 테스트가 아니며 512MB에서 모든 기능이 안정적이라고 보장하는 결과는 아니다. amd64 환경의 클라우드 빌드와 실제 HTTPS 프론트·로그인/재발급·WebSocket·파일·토스 테스트 결제는 배포 후 확인한다. 외부 저장소·결제 키는 로컬 Docker 검증에서 실제 값 대신 테스트 값을 사용했으므로 해당 서비스와의 연동은 확인하지 않았다.

공식 참고: [클라우드타입 Dockerfile 배포](https://docs.cloudtype.dev/ko/developers/dockerfile), [Docker 빌드 컨텍스트와 제외 규칙](https://docs.docker.com/build/concepts/context/).
