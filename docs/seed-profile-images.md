# 예시 고수 프로필 시드

홈 시안의 가상 고수 20명을 실제 프로필 데이터로 추가한다. 기존 계정과 ID는 유지하며 예시 ID 101~120을 DB 프로필 ID로 강제하지 않는다.

프로필 사진은 기존 예시 고수 101~120의 AI 인물 이미지다. 로컬 사본은 제거했으며 공개 URL은 `frontend/src/data/expertImages.json`에 기록한다. EFY의 기존 공개 Supabase 버킷에 업로드하고 원본 바이트와 일치하는지 확인했다. 키나 비밀값은 이 문서에 포함하지 않는다.

로컬 신규 계정의 비밀번호는 기존 시드와 같은 `1234`다. MariaDB 신규 계정은 배포 시드와 같은 `12341234`다. 닉네임이 같은 기존 고수는 계정을 추가하지 않고 해당 회원에 프로필을 연결한다.

| 고수 | 로컬 계정 | 서비스 ID | 활동 지역 |
| --- | --- | --- | --- |
| 한결이사 | hangyeol.move@example.com | 101, 102, 103 | 서울 강남구, 서울 서초구, 서울 송파구 |
| 반짝홈케어 | sparkle.home@example.com | 102, 101, 103, 104 | 서울 |
| 윤선생 영어 | yoon.english@example.com | 129, 127 | 서울 마포구, 서울 서대문구 |
| 소리공방 | sori.music@example.com | 165, 166 | 서울 마포구 |
| 코드메이트 | seed-dev@example.com | 139, 138 | 전국 |
| 바른설비 | seed-plumb@example.com | 109, 107, 108 | 부산 해운대구, 부산 수영구 |
| 온집 인테리어 | onjip@example.com | 113, 112, 114, 115 | 대구 |
| 담다 스냅 | damda.snap@example.com | 120, 118 | 전국 |
| 정리하는 하루 | tidyday@example.com | 103, 101 | 인천 연수구, 인천 남동구 |
| 김코치 피트니스 | kimcoach@example.com | 174 | 서울 강남구 |
| 문고리 수리 | doorknob@example.com | 108, 107, 109 | 서울 송파구, 서울 강동구 |
| 해운대 이사짐센터 | haeundae.move@example.com | 101, 102 | 부산 |
| 커리어노트 | career.note@example.com | 150, 151 | 대구 수성구 |
| 그림숲 아틀리에 | forest.art@example.com | 167, 165 | 인천 연수구 |
| 새길 노무 | saegil@example.com | 187, 186 | 전국 |
| 마음쉼표 상담 | mindcomma@example.com | 190 | 서울, 경기 |
| 한빛 카케어 | hanbit.car@example.com | 184 | 부산 해운대구, 부산 수영구 |
| 맑은창 줄눈 | cleargrout@example.com | 113, 112 | 서울 마포구, 서울 은평구 |
| 하늘 웨딩플래너 | haneul.wed@example.com | 118, 119, 120 | 부산 |
| 오늘의 요가 | pilates.today@example.com | 174 | 대구 수성구 |

예시의 작업 사진 수·최근 찜 증가 수는 실제 데이터처럼 저장하지 않는다. 프로필 이미지와 고수 작업 사진은 별개다. 커뮤니티 사진은 기존 Supabase 시드 파일을 재사용한다.

건웅님이 사용을 허락한 저장소: https://github.com/pgw2001/personal_AIBE8-Project2-Team01_FE . 현재 main에서 확인한 이미지 파일은 홈 배너 4개이며 고수 작업 사진 전용 파일은 확인되지 않았다.

기존 Supabase 커뮤니티 사진을 고수 작업 사진으로도 재사용했다. 이사·청소·설비·부분 인테리어·촬영·영어·개발 분야의 고수 10명에게 총 16장을 연결했으며 다른 회원 파일을 직접 연결하지 않도록 고수별 별도 저장 경로와 파일 소유자를 사용한다. 원본 게시글과 파일은 그대로 유지한다.

## 기존 테스트 계정 사진 보완

로컬의 기존 고수 12명과 MariaDB의 기존 고수 10명에게도 서비스에 맞는 프로필 사진·작업 사진을 연결한다. 같은 그림을 사용하더라도 파일 행의 소유자가 해당 고수가 되도록 별도 저장 경로를 사용한다. 기존 게시글 사진은 유지하고 사진이 없는 고수 작성 글만 보완한다.

## 로컬 계정 닉네임

이메일·비밀번호·ID·역할은 그대로 두고 아래 35개 계정의 닉네임만 변경한다. MariaDB 계정은 이미 자연스러운 이름이므로 유지한다. 탈퇴 전용 고객은 기존처럼 활동에 연결하지 않는다.

| 이메일 | 닉네임 |
| --- | --- |
| customer11@example.com | 다민고객 |
| customer12@example.com | 하린고객 |
| customer21@example.com | 서준고객 |
| customer22@example.com | 지우고객 |
| customer31@example.com | 유나고객 |
| customer32@example.com | 도윤고객 |
| customer41@example.com | 수아고객 |
| customer42@example.com | 시우고객 |
| customer51@example.com | 지안고객 |
| customer52@example.com | 하준고객 |
| expert11@example.com | 민서고수 |
| expert12@example.com | 지호고수 |
| expert21@example.com | 서연고수 |
| expert22@example.com | 이준고수 |
| expert31@example.com | 채원고수 |
| expert32@example.com | 도현고수 |
| expert41@example.com | 예린고수 |
| expert42@example.com | 은우고수 |
| expert51@example.com | 서아고수 |
| expert52@example.com | 수호고수 |
| admin11@example.com | 다온관리자 |
| admin12@example.com | 라온관리자 |
| admin21@example.com | 해솔관리자 |
| admin22@example.com | 이든관리자 |
| admin31@example.com | 가온관리자 |
| admin32@example.com | 나래관리자 |
| admin41@example.com | 유찬관리자 |
| admin42@example.com | 소율관리자 |
| admin51@example.com | 하람관리자 |
| admin52@example.com | 윤슬관리자 |
| customer19@example.com | 별하고객 |
| customer29@example.com | 로아고객 |
| customer39@example.com | 여름고객 |
| customer49@example.com | 온유고객 |
| customer59@example.com | 은솔고객 |

프론트 예시 사진은 고수별 원본 한 장만 참조한다. 큰 카드와 원형 아바타가 같은 공개 URL을 사용하며 원형 표시는 CSS로 처리한다. 이미 실제 회원의 사진에 연결된 Storage 파일은 삭제하지 않는다. 로컬 H2 시드의 고수 프로필 32개와 MariaDB의 프로필 30개 모두 회원 소유의 공개 프로필 사진을 갖는다.

MariaDB 예시 고수 `hangyeol.move@example.com`~`pilates.today@example.com`의 잘못된 해시 값을 기존 데모 계정과 같은 BCrypt 해시로 바로잡았다. 안내된 비밀번호 `12341234`를 사용하며 기존 17개 계정의 해시는 변경하지 않는다. 이 수정은 시드 파일에 반영한 것으로 기존 운영 DB에는 자동 적용하지 않는다.

## 발표용 이미지 재배정

로컬과 발표용 시드의 계정 수·시나리오는 별도로 유지한다. 발표용 고수 30명은 기존 원본 20장을 모두 활용하고 중복 연결된 데모 고수 10명에게만 확정된 새 원본을 배정했다. 확정한 구성과 이미지 요청은 [배정표](presentation-expert-image-plan.md)를 따른다. 추가 이미지 10장을 실제 업로드한 Storage 경로로 발표용 시드와 추가 적용 SQL에 반영했다. 운영 DB에는 아직 적용하지 않았다. 발표용 profile120만 필라테스로 조정하고 로컬의 요가 시나리오는 유지한다.
