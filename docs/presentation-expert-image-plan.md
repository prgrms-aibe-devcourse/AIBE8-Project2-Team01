# 발표용 고수 구성과 이미지 배정표

2026-11-05 기준이다. 로컬 H2와 발표용 MariaDB 시드는 분리한다. 로컬 계정 수와 시나리오를 발표용 37계정·30프로필에 맞추지 않는다.

발표용 고수 30명과 기존 원본 이미지 20장을 유지한다. 아래 기존 고수 10명에 배정된 재사용 이미지의 중복을 해소하기 위해 새 이미지 10장만 추가한다. 인물 증명사진을 강제하지 않으며 작업 모습·전신·옆모습·매장·스튜디오·도구를 활용한다. 얼굴이 없는 이미지도 허용한다. 얼굴 없는 작업·공간 사진에는 얼굴 확대 규칙을 적용하지 않으며 최종 이미지 연결 시 표시 설정도 함께 확인한다.

계정 이메일을 영구 식별 기준으로 사용한다. 표의 회원·프로필 ID는 빈 DB에 현재 시드를 적용했을 때의 값이며 기존 Cloudtype DB의 ID를 강제로 변경하지 않는다. 추가 이미지 10장의 원본 확인과 Supabase 업로드를 완료했다. 최종 연결은 아래 표를 따른다. DB 파일 ID는 각 DB에서 생성한 실제 값을 사용한다.

## 추가 이미지 10장

| 고수 | 계정 | 회원/프로필 ID | 전문 분야 | 변경 전 원본 | 추가 이미지 방향 |
| --- | --- | --- | --- | --- | --- |
| 김민준 | expert1@demo.efy.example | 11/1 | 이사 | [기존 101](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/160ca0178ba218aa5430172258e81061c7543a82f6e9b241215f393846800ccd.jpg) | 이삿짐 포장 중인 작업자의 전신·사선 구도 |
| 이서준 | expert2@demo.efy.example | 12/2 | 이사 | [기존 101](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/999816323094679630b9f1439e5882cff7aed0361628a7ed318937b2d6cc9541.jpg) | 이사 차량 옆 운반 도구와 정리된 박스 |
| 최도윤 | expert3@demo.efy.example | 13/3 | 이사 | [기존 101](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/941ef02196ec8967be5c2be1652afc998146c22dc7489dc7e9d7962f8a52c558.jpg) | 가구를 보호 포장하는 손과 작업 현장 |
| 정수빈 | expert4@demo.efy.example | 14/4 | 입주·집 청소 | [기존 102](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/5319fc6d71e5f917095557135d5a256a27d8467c3b0ef69105493ef542f184f1.jpg) | 주방 수납장 청소 중인 옆모습 |
| 오지안 | expert5@demo.efy.example | 15/5 | 입주·집 청소 | [기존 102](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/07de837d4dceed8e21851ac042bf070d40d826326d200f7617e6fed02375e153.jpg) | 욕실 청소 도구와 청결한 작업 공간 |
| 윤하윤 | expert6@demo.efy.example | 16/6 | 입주·집 청소 | [기존 102](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/4e1412568928287ee5e01c30aa16453a8aa0b2cc5845d72c61963d37bdcb44a9.jpg) | 창틀을 닦는 작업자의 전신·뒷모습 |
| 강지호 | expert7@demo.efy.example | 17/7 | 개발 외주 | [기존 105](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/b0173a8b0100f6e484d7c4585364b573260256dd5c02cb8ddc8cd9c6586d058a.jpg) | 노트북과 모니터가 있는 개발 작업실 |
| 박현우 | expert8@demo.efy.example | 18/8 | 개발 외주 | [기존 105](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/baeb12b874aa93d437ed7180c985344c805497274ed66088ede8b8bdf3eb11ca.jpg) | 코드 검토 중인 개발자의 옆모습 |
| 임서아 | expert9@demo.efy.example | 19/9 | 개발 외주 | [기존 105](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/2b796d43b228aa1b13ce8861b9371ef74324cb2c97bb473f1fa546085d069325.jpg) | 화이트보드와 노트북을 사용하는 작업 모습 |
| 장윤서 | expert10@demo.efy.example | 20/10 | 개발 외주 | [기존 105](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/accounts/a62d50b22e7026e476a23be3bc1fb6fc4f15c01b9dfe7461a1685e0b9e73c19a.jpg) | 정돈된 개발 스튜디오와 장비 |

## 기존 원본 20장 유지

| 고수 | 계정 | 회원/프로필 ID | 전문 분야 | 사용할 기존 원본 | 추가 이미지 |
| --- | --- | --- | --- | --- | --- |
| 한결이사 | profile101@example.com | 22/11 | 이사 | [기존 101](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/fac79094f403daa49b653ca7165425239106e33de7c46cbf9943e0afb06594f2.jpg) | 불필요 |
| 반짝홈케어 | profile102@example.com | 23/12 | 입주·집 청소 | [기존 102](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/3a3b36b5e571b7df3f0cf9f556eeeb547c68adde6d77b47b79f3cdb8ec47b31b.jpg) | 불필요 |
| 윤선생 영어 | profile103@example.com | 24/13 | 영어 | [기존 103](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/2536fa08e550e96a256fe8288f3d09b17d055949df054a541ef09de97fc31421.jpg) | 불필요 |
| 소리공방 | profile104@example.com | 25/14 | 악기 | [기존 104](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/d01c2618fe2ab4a4b3e5a4788a830ea6cd49651d876de7fea68e776e75397b43.jpg) | 불필요 |
| 코드메이트 | profile105@example.com | 26/15 | 개발 외주 | [기존 105](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/8706e1fa2982c383584d4f8f45cc67a854e272943b2fdbbb4a1948b697389102.jpg) | 불필요 |
| 바른설비 | profile106@example.com | 27/16 | 수도·보일러·전기 | [기존 106](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/eedb73b924cddac497cf0106675414f0b5b0e659b6da32a9501cab29e2977a31.jpg) | 불필요 |
| 온집 인테리어 | profile107@example.com | 28/17 | 부분 인테리어 | [기존 107](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/91d49f183011359c7be3278ef88d06a0e2b85ae4f75d28985065fad7c180abfe.jpg) | 불필요 |
| 담다 스냅 | profile108@example.com | 29/18 | 촬영·편집 | [기존 108](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/bf61066aa0ba76c96797534f2ac306cfdc8e32a7c14b7adc4e052be7744cee4b.jpg) | 불필요 |
| 정리하는 하루 | profile109@example.com | 30/19 | 가전·가구 청소 | [기존 109](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/2c6b30f4271e6fe0de0876b2c58147e797c16951f31db4eb2497a6d68ee8efb6.jpg) | 불필요 |
| 김코치 피트니스 | profile110@example.com | 31/20 | 피트니스·PT | [기존 110](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/9e0a013410c73d2600f30f7764042c664597f65dc75fec784602f011101dc86b.jpg) | 불필요 |
| 문고리 수리 | profile111@example.com | 32/21 | 문·창문 수리 | [기존 111](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/730c36a224c75ad6716687b5e3412b10018f5ac5fc95c388df95f7d52852bf55.jpg) | 불필요 |
| 해운대 이사짐센터 | profile112@example.com | 33/22 | 이사 | [기존 112](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/8236bea6ade5759f4a467ae33aad1dfb8b0a8497fa0925396e2bea41ef4c56a7.jpg) | 불필요 |
| 커리어노트 | profile113@example.com | 34/23 | 취업 준비 | [기존 113](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/3dbb32e8d3411c87a6e6a0b69329bfd6beb96b11cc057a9e906c73418b43fa09.jpg) | 불필요 |
| 그림숲 아틀리에 | profile114@example.com | 35/24 | 미술·드로잉 | [기존 114](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/cd044537eb5b2ec44fa95ecd26f478034191e1add421270a26e57fb771200d50.jpg) | 불필요 |
| 새길 노무 | profile115@example.com | 36/25 | 노무·법률 | [기존 115](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/61a80e03ceb23a12806b88a229bb551f8049954dbca07e60d4deee5dac2123d2.jpg) | 불필요 |
| 마음쉼표 상담 | profile116@example.com | 37/26 | 심리 상담 | [기존 116](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/6a49ba904e011b7af641619aff3198364b1274e647ab0068c9396331f6ec88ab.jpg) | 불필요 |
| 한빛 카케어 | profile117@example.com | 38/27 | 차량 관리 | [기존 117](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/7e4aaf88726a0226a2685864377b24821ee4b03de50f4279e7e22745f2ea4a46.jpg) | 불필요 |
| 맑은창 줄눈 | profile118@example.com | 39/28 | 부분 인테리어·줄눈 | [기존 118](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/51d1c0e5c1940e4304e24b58465dbfc1defd5480104064407d108fd84c73b6d1.jpg) | 불필요 |
| 하늘 웨딩플래너 | profile119@example.com | 40/29 | 웨딩 | [기존 119](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/23fc54320fddad75b686b37b70e15606ce25e3bed139a01d0765ba581b96465d.jpg) | 불필요 |
| 오늘의 필라테스 | profile120@example.com | 41/30 | 필라테스 | [기존 120](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/expert-portraits/f3f7685df4a9d41458c4e1aac7d34397e6ba4bb301d6d63a6527c02968caeb9b.jpg) | 불필요 |

## 필라테스 조정

발표용에는 필라테스 전용 고수가 없어 `profile120@example.com`의 ‘오늘의 요가’를 ‘오늘의 필라테스’로 조정한다. 기존 피트니스 소분류 174를 대표·제공 서비스로 유지하고 소개·경력 문구를 필라테스 수업에 맞춘다. 계정·ID·활동 지역·지갑은 유지한다. 이 계정에 연결된 요청서·견적·게시글이 있는지도 검증 SQL로 확인한다. 로컬의 요가 테스트 계정은 별도 시나리오로 유지한다.

## 이미지 생성 컨텍스트에 전달할 조건

- 추가 이미지 10장만 위 계정별 방향에 맞춰 생성하거나 선정한다. 기존 20장은 모두 활용한다.
- 인물 사진만 만들지 않는다. 작은 원형과 큰 프로필에서 모두 쓸 수 있도록 피사체와 작업 도구가 중앙에서 지나치게 잘리지 않는 원본을 준비한다.
- 얼굴이 있는 사진도 별도 얼굴 크롭 파일을 DB용으로 만들지 않는다. 표시 방식은 프론트에서 처리한다.
- 완료 시 계정 이메일·원본 파일 절대 경로·내용 설명·생성/외부 출처 구분을 표로 보고한다. 외부 이미지라면 원문 URL·제작자·라이선스·이용 조건 확인 날짜를 함께 제공한다.
- 무료 이미지 출처 기록 페이지: https://app.notion.com/p/40f42d9a1d1880e7a435f87cbdd42e27
- 새 URL·파일 ID를 가정하지 않는다. 시드·DB·Supabase·커밋·푸시는 수정하지 않는다.
- 이미지가 준비되면 사용자에게 알리고 ‘구현’ 컨텍스트에 완료 표와 파일 위치를 다시 전달하도록 요청한다. 사용자가 이 컨텍스트에 대한 회신을 승인하면 스레드 01a17e16-2e8c-7c03-a510-a809367f0a40에 전달한다.

## 연결 완료 조건

원본을 확인한 뒤 Supabase Storage에 저장한다. DB에는 실제 파일 메타데이터와 회원·작업 사진 연결만 저장한다. 발표용 고수 30명의 최종 이미지 원본이 중복되지 않는지 확인한다. 기존 Cloudtype DB에는 전체 시드를 재실행하지 않고 기존 데이터를 보존하는 추가 적용 SQL과 별도 검증 SQL만 제공한다. 실제 운영 적용은 사용자가 진행한다.

현재 상태는 신규 원본 10장 업로드와 발표용 초기 시드·추가 적용 SQL 연결 완료다. 기존 운영 DB에는 적용하지 않았다.

## 이번 확인 결과

- 최신 발표용 시드를 별도 MariaDB 11.2에 로딩하고 37계정·30프로필 및 기존 수량·정합성 검사 통과를 확인했다.
- 신규 고수 20개 계정의 BCrypt 해시가 `12341234`와 일치하는 것을 자동 테스트로 확인했다. 관련 시드 테스트 12개가 통과했다.
- 기존 MariaDB·수동 변경을 포함한 DB·이전 로컬 데이터 복제 DB에 추가 SQL을 적용해 기존 연결 보존과 두 번째 적용 시 결과 유지를 확인했다. 빈 DB에서는 적용이 차단됐다.
- 프론트 TypeScript 검사와 빌드가 통과했다.
- 이후 최종 이미지 연결을 완료했다. 전체 빌드와 자동 테스트 314개가 통과했고 임시 서버에서 37계정 로그인·본인 정보와 공개 프로필 30개의 사진·작업 사진32개 연결을 확인했다. 브라우저에서는 큰 프로필 원본과 찜 목록의 작은 원형 사진 표시를 확인했다. 이번에는 모바일 화면을 다시 검증하지 않았으며 운영 DB 적용은 실행하지 않았다.

## 확정된 추가 이미지 연결

10장 모두 AI 생성 원본 PNG다. 외부 무료 이미지를 사용하지 않아 외부 사이트 라이선스 항목은 해당하지 않는다. 생성 원본과 프롬프트 기록은 이미지 생성 컨텍스트의 `output/presentation-expert-images`에 보관한다. 기존 Supabase 파일은 삭제하거나 덮어쓰지 않았다. 로컬 H2 시드에는 이 발표용 데모 계정을 추가하지 않는다.

| 계정 | 고수 | 실제 원본 | 내용 | 원본 SHA-256 |
| --- | --- | --- | --- | --- |
| expert1@demo.efy.example | 김민준 | [expert01.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/d09431ee697649ae6a059995d9b03eb0069d86a3cd788b5e83c28ac8be9c1cee.png) | 전신·사선 구도의 이삿짐 포장 작업 | `d09431ee697649ae6a059995d9b03eb0069d86a3cd788b5e83c28ac8be9c1cee` |
| expert2@demo.efy.example | 이서준 | [expert02.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/816310cf78c8685bc578edcce6dbfa93006ca553ec39927d11adc18f3329e688.png) | 이사 차량 옆 운반 도구와 정리된 박스 | `816310cf78c8685bc578edcce6dbfa93006ca553ec39927d11adc18f3329e688` |
| expert3@demo.efy.example | 최도윤 | [expert03.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/c002942b04c1c93a16833549018ac701f37ab755b019724cc8939833b041f29c.png) | 가구 보호 포장과 스트랩을 다루는 손 | `c002942b04c1c93a16833549018ac701f37ab755b019724cc8939833b041f29c` |
| expert4@demo.efy.example | 정수빈 | [expert04.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/3a55a6d8ce7cd546f333debd38cd2c8b406bf5293b134b08011246c756c56a02.png) | 주방 수납장 내부를 닦는 옆모습 | `3a55a6d8ce7cd546f333debd38cd2c8b406bf5293b134b08011246c756c56a02` |
| expert5@demo.efy.example | 오지안 | [expert05.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/4446b1ab54cd891731dd0f5b2b3f52296ba34bb249d1da53b5568d48e780340e.png) | 욕실 청소 도구와 청결한 작업 공간 | `4446b1ab54cd891731dd0f5b2b3f52296ba34bb249d1da53b5568d48e780340e` |
| expert6@demo.efy.example | 윤하윤 | [expert06.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/e550aedb1375b19f2a9a3adf6ac87333c3368d11938456a6ac2932837773be23.png) | 창틀을 닦는 작업자의 전신·뒷모습 | `e550aedb1375b19f2a9a3adf6ac87333c3368d11938456a6ac2932837773be23` |
| expert7@demo.efy.example | 강지호 | [expert07.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/ac87b492952a1e806cfd07c8b34d28036c3eaac9882f10ed109eb4c1ee12cd4e.png) | 노트북과 모니터가 있는 따뜻한 개발 작업실 | `ac87b492952a1e806cfd07c8b34d28036c3eaac9882f10ed109eb4c1ee12cd4e` |
| expert8@demo.efy.example | 박현우 | [expert08.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/eb98873dad0c9294e1e20be39ad20bfe1221e64fb07bab9d478455f71f412833.png) | 코드를 검토하는 개발자의 옆모습 | `eb98873dad0c9294e1e20be39ad20bfe1221e64fb07bab9d478455f71f412833` |
| expert9@demo.efy.example | 임서아 | [expert09.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/ba4360ddbbd45220d6b76ae0bf036a745fc9e5209880534584c5b2733d112b68.png) | 화이트보드와 노트북으로 설계하는 개발자 | `ba4360ddbbd45220d6b76ae0bf036a745fc9e5209880534584c5b2733d112b68` |
| expert10@demo.efy.example | 장윤서 | [expert10.png](https://navdkqoenvuhjnoqjegi.supabase.co/storage/v1/object/public/efy-public/seed/presentation-profiles/710d56a3e6311981449bab84ec340139a77e7a6d6813c2d00282c72e2a65c032.png) | 정돈된 개발 스튜디오와 장비 | `710d56a3e6311981449bab84ec340139a77e7a6d6813c2d00282c72e2a65c032` |

기존 원본 20장과 추가 원본 10장의 공개 다운로드를 확인하고 실제 바이트의 SHA-256 값이 30개 모두 다른 것을 확인했다. 새 작업·공간 사진은 작은 아바타에서도 얼굴 확대 처리를 적용하지 않는다. 큰 프로필은 원본을 사용한다.

## 배포 검토 후 추가 확인

17계정 구버전 전환과 혼합 게시글 사진 보존 검증을 보완했다. 사전 검사·누락 계정 추가·사진 보완·사후 검증의 파일별 순서와 실제 결과는 [배포용 시드 안내](mariadb-seed.md#195-배포-검토-보완과-적용-순서)에 기록했다. 390×844 모바일에서 프로필·작업 사진·팁 목록·게시글 상세 사진 로딩과 가로 넘침 없음을 추가 확인했다. 실제 Cloudtype DB에는 적용하지 않았다.
