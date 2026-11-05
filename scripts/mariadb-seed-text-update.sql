-- 이미 초기 시드가 들어간 EFY 발표 DB의 문구만 바꿉니다. 빈 DB에서는 실행하지 않습니다.
-- 새 DB에는 data-mariadb.sql만 적용합니다. 기존 시드 전체를 다시 실행하지 않습니다.
-- 한 연결에서 전체 실행하고 첫 오류에서 중단한 뒤 ROLLBACK 합니다.
-- 수동 수정·삭제된 문구는 덮어쓰지 않으며 계정 이메일과 기존 내용이 일치하는 행만 수정합니다.
SET NAMES utf8mb4;
START TRANSACTION;

UPDATE member t SET t.nickname='EFY운영자'
WHERE t.id=1 AND t.email='admin@example.com' AND BINARY t.nickname=BINARY 'EFY관리자';

UPDATE member t SET t.nickname='이하늘'
WHERE t.id=2 AND t.email='haneul123@example.com' AND BINARY t.nickname=BINARY '이사준비하늘';

UPDATE member t SET t.nickname='김지우'
WHERE t.id=3 AND t.email='jiuzzzang@example.com' AND BINARY t.nickname=BINARY '견적비교지우';

UPDATE member t SET t.nickname='박민서'
WHERE t.id=4 AND t.email='minseo.park@example.com' AND BINARY t.nickname=BINARY '상담중민서';

UPDATE member t SET t.nickname='정서연'
WHERE t.id=5 AND t.email='lovesaltbread@example.com' AND BINARY t.nickname=BINARY '생활도움서연';

UPDATE member t SET t.nickname='한유진'
WHERE t.id=6 AND t.email='customer@example.com' AND BINARY t.nickname=BINARY '새로운시작유진';

UPDATE member t SET t.nickname='김민준'
WHERE t.id=11 AND t.email='expert@example.com' AND BINARY t.nickname=BINARY '든든이사민준';

UPDATE member t SET t.nickname='이서준'
WHERE t.id=12 AND t.email='seojun.move@example.com' AND BINARY t.nickname=BINARY '꼼꼼이사서준';

UPDATE member t SET t.nickname='최도윤'
WHERE t.id=13 AND t.email='doyoon.move@example.com' AND BINARY t.nickname=BINARY '안심이사도윤';

UPDATE member t SET t.nickname='정수빈'
WHERE t.id=14 AND t.email='subin.clean@example.com' AND BINARY t.nickname=BINARY '맑은청소수빈';

UPDATE member t SET t.nickname='오지안'
WHERE t.id=15 AND t.email='jian.clean@example.com' AND BINARY t.nickname=BINARY '반짝청소지안';

UPDATE member t SET t.nickname='윤하윤'
WHERE t.id=16 AND t.email='hayoon.clean@example.com' AND BINARY t.nickname=BINARY '정성청소하윤';

UPDATE member t SET t.nickname='강지호'
WHERE t.id=17 AND t.email='jio.web@example.com' AND BINARY t.nickname=BINARY '웹개발지호';

UPDATE member t SET t.nickname='박현우'
WHERE t.id=18 AND t.email='hyunwoo.dev@example.com' AND BINARY t.nickname=BINARY '백엔드현우';

UPDATE member t SET t.nickname='임서아'
WHERE t.id=19 AND t.email='seoa.design@example.com' AND BINARY t.nickname=BINARY '디자인개발서아';

UPDATE member t SET t.nickname='장윤서'
WHERE t.id=20 AND t.email='yunseo.web@example.com' AND BINARY t.nickname=BINARY '서비스개발윤서';

UPDATE member t SET t.nickname='최다은'
WHERE t.id=21 AND t.email='daeun.choi@example.com' AND BINARY t.nickname=BINARY '탈퇴시연전용';

UPDATE expert_profile t SET t.short_intro='원룸 이사, 포장부터 가구 조립까지 맡겨 주세요.', t.description='안녕하세요. 서울에서 소형 이사를 맡고 있는 김민준입니다. 짐 사진과 이동할 곳을 보내 주시면 차량과 작업 인원부터 안내해 드려요. 침대와 책상은 구조를 확인한 뒤 분해·조립까지 도와드립니다.', t.career_description='원룸·오피스텔 이사 3년. 가구 분해와 재조립 작업을 함께 맡고 있습니다.'
WHERE t.id=1 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='expert@example.com') AND BINARY t.short_intro=BINARY '짐 정리부터 운반까지 안전하게 도와드립니다.' AND BINARY t.description=BINARY '짐 정리부터 운반까지 안전하게 도와드립니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 3년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='계단이 있는 집도 짐에 맞춰 안전하게 옮깁니다.', t.description='엘리베이터 없는 집은 계단 폭과 층수를 먼저 확인합니다. 박스에 담기 어려운 물건은 별도로 포장하고 바닥과 모서리를 보호한 뒤 옮겨요. 현장에서 비용이 달라질 수 있는 항목은 상담할 때 미리 말씀드립니다.', t.career_description='주택과 빌라의 소형 이사 4년. 계단 운반과 가전 포장 경험이 있습니다.'
WHERE t.id=2 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='seojun.move@example.com') AND BINARY t.short_intro=BINARY '원룸과 소형 이사를 꼼꼼하게 진행합니다.' AND BINARY t.description=BINARY '원룸과 소형 이사를 꼼꼼하게 진행합니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 4년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='큰 가구 하나부터 작은 집 이사까지 도와드려요.', t.description='서울 서남권에서 가구 운반과 원룸 이사를 하고 있습니다. 냉장고와 세탁기는 설치 상태를 먼저 확인하고 운반 가능 여부를 안내합니다. 혼자 옮기기 어려운 짐이 있다면 크기와 사진을 보내 주세요.', t.career_description='가전·가구 운반 5년. 소형 이사와 사무실 집기 이동을 진행했습니다.'
WHERE t.id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='doyoon.move@example.com') AND BINARY t.short_intro=BINARY '가구 보호 포장과 깔끔한 마무리를 약속합니다.' AND BINARY t.description=BINARY '가구 보호 포장과 깔끔한 마무리를 약속합니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 5년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='짐 들어오기 전, 빈집 구석구석 살펴드립니다.', t.description='입주 전에 창틀과 수납장 안쪽까지 청소합니다. 평수와 집 상태를 확인한 뒤 작업 인원과 예상 시간을 알려 드려요. 끝나면 함께 둘러보면서 부족한 곳이 없는지 확인합니다.', t.career_description='아파트·오피스텔 입주 청소 6년. 주방과 욕실의 오염 상태에 맞춰 작업합니다.'
WHERE t.id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='subin.clean@example.com') AND BINARY t.short_intro=BINARY '새집의 시작을 쾌적하게 준비합니다.' AND BINARY t.description=BINARY '새집의 시작을 쾌적하게 준비합니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 6년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='주방 기름때와 욕실 물때를 꼼꼼하게 정리해요.', t.description='눈에 잘 보이지 않는 후드 필터와 배수구까지 살핍니다. 소재를 확인하고 세제를 골라 작업하며 손상이 우려되는 부분은 먼저 설명드려요. 포함되는 작업과 별도 상담이 필요한 항목을 구분해서 안내합니다.', t.career_description='주거 공간 청소 7년. 주방 후드와 욕실의 묵은 오염을 다루는 작업을 맡아 왔습니다.'
WHERE t.id=5 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jian.clean@example.com') AND BINARY t.short_intro=BINARY '창틀과 욕실까지 놓치지 않는 입주 청소입니다.' AND BINARY t.description=BINARY '창틀과 욕실까지 놓치지 않는 입주 청소입니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 7년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='청소가 끝난 뒤 함께 확인하는 시간을 갖습니다.', t.description='상담할 때 가장 신경 쓰이는 공간부터 여쭤봅니다. 사진만으로 판단하기 어려운 오염은 현장에서 설명드리고 진행 여부를 정해요. 작업 후에는 창문과 수납장을 직접 열어 확인하실 수 있도록 안내합니다.', t.career_description='입주·이사 청소 8년. 구축 아파트와 장기 공실의 청소를 진행했습니다.'
WHERE t.id=6 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='hayoon.clean@example.com') AND BINARY t.short_intro=BINARY '작업 범위를 먼저 설명하는 정직한 청소입니다.' AND BINARY t.description=BINARY '작업 범위를 먼저 설명하는 정직한 청소입니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 8년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='필요한 기능부터 차근차근 웹으로 만듭니다.', t.description='작은 공방과 매장의 소개·예약 사이트를 개발합니다. 사용자가 어떤 순서로 예약하는지부터 정리하고 화면과 API를 함께 만들어요. 처음부터 기능을 늘리기보다 운영에 꼭 필요한 범위를 먼저 제안드립니다.', t.career_description='웹 서비스 개발 9년. 예약 사이트와 소규모 사업장의 관리 화면을 구축했습니다.'
WHERE t.id=7 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.short_intro=BINARY '작은 아이디어를 사용하기 쉬운 웹 서비스로 만듭니다.' AND BINARY t.description=BINARY '작은 아이디어를 사용하기 쉬운 웹 서비스로 만듭니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 9년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='회원·예약·결제 API를 안정적으로 연결합니다.', t.description='Java와 Spring Boot로 백엔드를 개발하는 박현우입니다. 기존 화면이 있다면 요청·응답 형식부터 맞추고 데이터 구조를 정리합니다. 운영 중 문제를 찾기 쉽도록 로그와 기본적인 테스트도 함께 준비해요.', t.career_description='백엔드 개발 10년. 회원 인증과 예약 관리 API의 설계·운영을 담당했습니다.'
WHERE t.id=8 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='hyunwoo.dev@example.com') AND BINARY t.short_intro=BINARY '안정적인 API와 데이터베이스를 설계합니다.' AND BINARY t.description=BINARY '안정적인 API와 데이터베이스를 설계합니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 10년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='휴대폰에서 쓰기 편한 화면을 고민합니다.', t.description='글자 크기와 버튼 위치처럼 사용자가 바로 느끼는 부분을 살펴봅니다. 소개 페이지부터 문의·예약 화면까지 모바일 흐름에 맞춰 개발해요. 완성 후 직접 수정하실 내용이 있다면 관리 방법도 설명드립니다.', t.career_description='웹 화면 개발 11년. 반응형 소개 페이지와 예약 화면을 제작했습니다.'
WHERE t.id=9 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='seoa.design@example.com') AND BINARY t.short_intro=BINARY '모바일에서도 편리한 화면을 개발합니다.' AND BINARY t.description=BINARY '모바일에서도 편리한 화면을 개발합니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 11년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE expert_profile t SET t.short_intro='멀리 계셔도 온라인으로 개발 과정을 공유합니다.', t.description='지역에 관계없이 온라인 상담으로 웹 개발을 진행합니다. 기능 목록과 일정을 합의한 뒤 작업 화면을 주기적으로 보여 드려요. 배포 후 확인할 항목과 운영에 필요한 계정도 함께 정리해 드립니다.', t.career_description='웹 개발 12년. 원격 협업으로 서비스 구축과 배포까지 진행했습니다.'
WHERE t.id=10 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='yunseo.web@example.com') AND BINARY t.short_intro=BINARY '기획부터 배포까지 함께하는 웹 개발입니다.' AND BINARY t.description=BINARY '기획부터 배포까지 함께하는 웹 개발입니다. 상담에서 작업 범위와 비용을 안내하고 진행 상황을 공유합니다.' AND BINARY t.career_description=BINARY '관련 서비스 경력 12년. 상담부터 마무리까지 직접 담당합니다.';

UPDATE request_answer t SET t.content='원룸 이사를 요청합니다.'
WHERE t.service_request_id=1 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '원룸 이사와 가구 운반을 부탁드립니다.';

UPDATE request_answer t SET t.content='11월 10일 오전을 희망합니다.'
WHERE t.service_request_id=1 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '다음 주 토요일 오전을 희망합니다.';

UPDATE request_answer t SET t.content='예산은 30만 원입니다.'
WHERE t.service_request_id=1 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '30만 원 안팎으로 생각하고 있습니다.';

UPDATE request_answer t SET t.content='침대와 책상 운반이 필요합니다.'
WHERE t.service_request_id=1 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '침대 프레임 분해와 조립이 필요합니다.';

UPDATE request_answer t SET t.content='20평 아파트 입주 청소가 필요합니다.'
WHERE t.service_request_id=2 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '이사 전 아파트 입주 청소를 요청합니다.';

UPDATE request_answer t SET t.content='11월 15일 오후를 희망합니다.'
WHERE t.service_request_id=2 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '입주 하루 전 평일 오후가 좋습니다.';

UPDATE request_answer t SET t.content='예산은 25만 원입니다.'
WHERE t.service_request_id=2 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '25만 원 안팎으로 상담하고 싶습니다.';

UPDATE request_answer t SET t.content='창틀과 화장실을 집중적으로 청소해 주세요.'
WHERE t.service_request_id=2 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '창틀과 주방 후드를 꼼꼼하게 청소해 주세요.';

UPDATE request_answer t SET t.content='Spring Boot 기반 REST API 개발이 필요합니다.'
WHERE t.service_request_id=3 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '예약 기능이 있는 소규모 웹사이트가 필요합니다.';

UPDATE request_answer t SET t.content='11월 말까지 완료하고 싶습니다.'
WHERE t.service_request_id=3 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '상담 후 3주 안에 진행하고 싶습니다.';

UPDATE request_answer t SET t.content='예산은 100만 원입니다.'
WHERE t.service_request_id=3 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '150만 원 안팎으로 범위를 협의하고 싶습니다.';

UPDATE request_answer t SET t.content='JPA와 MySQL을 사용해 주세요.'
WHERE t.service_request_id=3 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '모바일 화면과 관리자 페이지가 필요합니다.';

UPDATE request_answer t SET t.content='원룸 이사를 요청합니다.'
WHERE t.service_request_id=4 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '원룸 이사와 가구 운반을 부탁드립니다.';

UPDATE request_answer t SET t.content='11월 10일 오전을 희망합니다.'
WHERE t.service_request_id=4 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '다음 주 토요일 오전을 희망합니다.';

UPDATE request_answer t SET t.content='예산은 30만 원입니다.'
WHERE t.service_request_id=4 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '30만 원 안팎으로 생각하고 있습니다.';

UPDATE request_answer t SET t.content='침대와 책상 운반이 필요합니다.'
WHERE t.service_request_id=4 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '침대 프레임 분해와 조립이 필요합니다.';

UPDATE request_answer t SET t.content='20평 아파트 입주 청소가 필요합니다.'
WHERE t.service_request_id=5 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '이사 전 아파트 입주 청소를 요청합니다.';

UPDATE request_answer t SET t.content='11월 15일 오후를 희망합니다.'
WHERE t.service_request_id=5 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '입주 하루 전 평일 오후가 좋습니다.';

UPDATE request_answer t SET t.content='예산은 25만 원입니다.'
WHERE t.service_request_id=5 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '25만 원 안팎으로 상담하고 싶습니다.';

UPDATE request_answer t SET t.content='창틀과 화장실을 집중적으로 청소해 주세요.'
WHERE t.service_request_id=5 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '창틀과 주방 후드를 꼼꼼하게 청소해 주세요.';

UPDATE request_answer t SET t.content='Spring Boot 기반 REST API 개발이 필요합니다.'
WHERE t.service_request_id=6 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '예약 기능이 있는 소규모 웹사이트가 필요합니다.';

UPDATE request_answer t SET t.content='11월 말까지 완료하고 싶습니다.'
WHERE t.service_request_id=6 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '상담 후 3주 안에 진행하고 싶습니다.';

UPDATE request_answer t SET t.content='예산은 100만 원입니다.'
WHERE t.service_request_id=6 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '150만 원 안팎으로 범위를 협의하고 싶습니다.';

UPDATE request_answer t SET t.content='JPA와 MySQL을 사용해 주세요.'
WHERE t.service_request_id=6 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '모바일 화면과 관리자 페이지가 필요합니다.';

UPDATE request_answer t SET t.content='원룸 이사를 요청합니다.'
WHERE t.service_request_id=7 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '원룸 이사와 가구 운반을 부탁드립니다.';

UPDATE request_answer t SET t.content='11월 10일 오전을 희망합니다.'
WHERE t.service_request_id=7 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '다음 주 토요일 오전을 희망합니다.';

UPDATE request_answer t SET t.content='예산은 30만 원입니다.'
WHERE t.service_request_id=7 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '30만 원 안팎으로 생각하고 있습니다.';

UPDATE request_answer t SET t.content='침대와 책상 운반이 필요합니다.'
WHERE t.service_request_id=7 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '침대 프레임 분해와 조립이 필요합니다.';

UPDATE request_answer t SET t.content='20평 아파트 입주 청소가 필요합니다.'
WHERE t.service_request_id=8 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '이사 전 아파트 입주 청소를 요청합니다.';

UPDATE request_answer t SET t.content='11월 15일 오후를 희망합니다.'
WHERE t.service_request_id=8 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '입주 하루 전 평일 오후가 좋습니다.';

UPDATE request_answer t SET t.content='예산은 25만 원입니다.'
WHERE t.service_request_id=8 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '25만 원 안팎으로 상담하고 싶습니다.';

UPDATE request_answer t SET t.content='창틀과 화장실을 집중적으로 청소해 주세요.'
WHERE t.service_request_id=8 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '창틀과 주방 후드를 꼼꼼하게 청소해 주세요.';

UPDATE request_answer t SET t.content='Spring Boot 기반 REST API 개발이 필요합니다.'
WHERE t.service_request_id=9 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '예약 기능이 있는 소규모 웹사이트가 필요합니다.';

UPDATE request_answer t SET t.content='11월 말까지 완료하고 싶습니다.'
WHERE t.service_request_id=9 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '상담 후 3주 안에 진행하고 싶습니다.';

UPDATE request_answer t SET t.content='예산은 100만 원입니다.'
WHERE t.service_request_id=9 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '150만 원 안팎으로 범위를 협의하고 싶습니다.';

UPDATE request_answer t SET t.content='JPA와 MySQL을 사용해 주세요.'
WHERE t.service_request_id=9 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '모바일 화면과 관리자 페이지가 필요합니다.';

UPDATE request_answer t SET t.content='원룸 이사를 요청합니다.'
WHERE t.service_request_id=10 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '원룸 이사와 가구 운반을 부탁드립니다.';

UPDATE request_answer t SET t.content='11월 10일 오전을 희망합니다.'
WHERE t.service_request_id=10 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '다음 주 토요일 오전을 희망합니다.';

UPDATE request_answer t SET t.content='예산은 30만 원입니다.'
WHERE t.service_request_id=10 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '30만 원 안팎으로 생각하고 있습니다.';

UPDATE request_answer t SET t.content='침대와 책상 운반이 필요합니다.'
WHERE t.service_request_id=10 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '침대 프레임 분해와 조립이 필요합니다.';

UPDATE request_answer t SET t.content='20평 아파트 입주 청소가 필요합니다.'
WHERE t.service_request_id=11 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '이사 전 아파트 입주 청소를 요청합니다.';

UPDATE request_answer t SET t.content='11월 15일 오후를 희망합니다.'
WHERE t.service_request_id=11 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '입주 하루 전 평일 오후가 좋습니다.';

UPDATE request_answer t SET t.content='예산은 25만 원입니다.'
WHERE t.service_request_id=11 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '25만 원 안팎으로 상담하고 싶습니다.';

UPDATE request_answer t SET t.content='창틀과 화장실을 집중적으로 청소해 주세요.'
WHERE t.service_request_id=11 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '창틀과 주방 후드를 꼼꼼하게 청소해 주세요.';

UPDATE request_answer t SET t.content='Spring Boot 기반 REST API 개발이 필요합니다.'
WHERE t.service_request_id=12 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '예약 기능이 있는 소규모 웹사이트가 필요합니다.';

UPDATE request_answer t SET t.content='11월 말까지 완료하고 싶습니다.'
WHERE t.service_request_id=12 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '상담 후 3주 안에 진행하고 싶습니다.';

UPDATE request_answer t SET t.content='예산은 100만 원입니다.'
WHERE t.service_request_id=12 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '150만 원 안팎으로 범위를 협의하고 싶습니다.';

UPDATE request_answer t SET t.content='JPA와 MySQL을 사용해 주세요.'
WHERE t.service_request_id=12 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '모바일 화면과 관리자 페이지가 필요합니다.';

UPDATE request_answer t SET t.content='원룸 이사를 요청합니다.'
WHERE t.service_request_id=13 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '원룸 이사와 가구 운반을 부탁드립니다.';

UPDATE request_answer t SET t.content='11월 10일 오전을 희망합니다.'
WHERE t.service_request_id=13 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '다음 주 토요일 오전을 희망합니다.';

UPDATE request_answer t SET t.content='예산은 30만 원입니다.'
WHERE t.service_request_id=13 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '30만 원 안팎으로 생각하고 있습니다.';

UPDATE request_answer t SET t.content='침대와 책상 운반이 필요합니다.'
WHERE t.service_request_id=13 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '침대 프레임 분해와 조립이 필요합니다.';

UPDATE request_answer t SET t.content='20평 아파트 입주 청소가 필요합니다.'
WHERE t.service_request_id=14 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '이사 전 아파트 입주 청소를 요청합니다.';

UPDATE request_answer t SET t.content='11월 15일 오후를 희망합니다.'
WHERE t.service_request_id=14 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '입주 하루 전 평일 오후가 좋습니다.';

UPDATE request_answer t SET t.content='예산은 25만 원입니다.'
WHERE t.service_request_id=14 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '25만 원 안팎으로 상담하고 싶습니다.';

UPDATE request_answer t SET t.content='창틀과 화장실을 집중적으로 청소해 주세요.'
WHERE t.service_request_id=14 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '창틀과 주방 후드를 꼼꼼하게 청소해 주세요.';

UPDATE request_answer t SET t.content='Spring Boot 기반 REST API 개발이 필요합니다.'
WHERE t.service_request_id=15 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '예약 기능이 있는 소규모 웹사이트가 필요합니다.';

UPDATE request_answer t SET t.content='11월 말까지 완료하고 싶습니다.'
WHERE t.service_request_id=15 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '상담 후 3주 안에 진행하고 싶습니다.';

UPDATE request_answer t SET t.content='예산은 100만 원입니다.'
WHERE t.service_request_id=15 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '150만 원 안팎으로 범위를 협의하고 싶습니다.';

UPDATE request_answer t SET t.content='JPA와 MySQL을 사용해 주세요.'
WHERE t.service_request_id=15 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '모바일 화면과 관리자 페이지가 필요합니다.';

UPDATE request_answer t SET t.content='원룸 이사를 요청합니다.'
WHERE t.service_request_id=16 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '원룸 이사와 가구 운반을 부탁드립니다.';

UPDATE request_answer t SET t.content='11월 10일 오전을 희망합니다.'
WHERE t.service_request_id=16 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '다음 주 토요일 오전을 희망합니다.';

UPDATE request_answer t SET t.content='예산은 30만 원입니다.'
WHERE t.service_request_id=16 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '30만 원 안팎으로 생각하고 있습니다.';

UPDATE request_answer t SET t.content='침대와 책상 운반이 필요합니다.'
WHERE t.service_request_id=16 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '침대 프레임 분해와 조립이 필요합니다.';

UPDATE request_answer t SET t.content='20평 아파트 입주 청소가 필요합니다.'
WHERE t.service_request_id=17 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '이사 전 아파트 입주 청소를 요청합니다.';

UPDATE request_answer t SET t.content='11월 15일 오후를 희망합니다.'
WHERE t.service_request_id=17 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '입주 하루 전 평일 오후가 좋습니다.';

UPDATE request_answer t SET t.content='예산은 25만 원입니다.'
WHERE t.service_request_id=17 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '25만 원 안팎으로 상담하고 싶습니다.';

UPDATE request_answer t SET t.content='창틀과 화장실을 집중적으로 청소해 주세요.'
WHERE t.service_request_id=17 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '창틀과 주방 후드를 꼼꼼하게 청소해 주세요.';

UPDATE request_answer t SET t.content='Spring Boot 기반 REST API 개발이 필요합니다.'
WHERE t.service_request_id=18 AND t.question_id=1 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '예약 기능이 있는 소규모 웹사이트가 필요합니다.';

UPDATE request_answer t SET t.content='11월 말까지 완료하고 싶습니다.'
WHERE t.service_request_id=18 AND t.question_id=2 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '상담 후 3주 안에 진행하고 싶습니다.';

UPDATE request_answer t SET t.content='예산은 100만 원입니다.'
WHERE t.service_request_id=18 AND t.question_id=3 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '150만 원 안팎으로 범위를 협의하고 싶습니다.';

UPDATE request_answer t SET t.content='JPA와 MySQL을 사용해 주세요.'
WHERE t.service_request_id=18 AND t.question_id=4 AND EXISTS (SELECT 1 FROM service_request r JOIN member m ON m.id=r.member_id WHERE r.id=t.service_request_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '모바일 화면과 관리자 페이지가 필요합니다.';

UPDATE chat_message t SET t.content='안녕하세요. 요청서에 적은 REST API 개발 건으로 상담받고 싶어요.'
WHERE t.chat_room_id=1 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 견적 내용을 보고 상담을 요청드립니다.';

UPDATE chat_message t SET t.content='안녕하세요. 회원 기능과 예약 기능 중 어떤 부분부터 필요하신가요?'
WHERE t.chat_room_id=1 AND t.sender_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 요청하신 내용을 확인했습니다.';

UPDATE chat_message t SET t.content='우선 회원가입과 로그인부터 연결하고 싶어요. 화면은 어느 정도 준비돼 있습니다.'
WHERE t.chat_room_id=1 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안내해 주신 일정에 진행할 수 있을까요?';

UPDATE chat_message t SET t.content='그럼 기존 화면의 입력 항목부터 확인하겠습니다. 참고할 화면을 보내 주실 수 있을까요?'
WHERE t.chat_room_id=1 AND t.sender_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '네. 작업 범위를 확인하고 일정을 조율하겠습니다.';

UPDATE chat_message t SET t.content='네. 화면과 필요한 기능을 정리해서 보내 드릴게요.'
WHERE t.chat_room_id=1 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '감사합니다. 준비할 사항도 알려 주세요.';

UPDATE chat_message t SET t.content='자료를 보내 주시면 기능 범위와 예상 일정을 정리해 드릴게요.'
WHERE t.chat_room_id=1 AND t.sender_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.';

UPDATE chat_message t SET t.content='안녕하세요. 원룸 이사 견적 보고 연락드렸어요. 침대랑 책상도 옮겨야 해요.'
WHERE t.chat_room_id=2 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 견적 내용을 보고 상담을 요청드립니다.';

UPDATE chat_message t SET t.content='안녕하세요. 침대 프레임과 책상은 분해가 가능한 형태일까요?'
WHERE t.chat_room_id=2 AND t.sender_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 요청하신 내용을 확인했습니다.';

UPDATE chat_message t SET t.content='침대는 조립식이고 책상은 다리만 분리할 수 있어요.'
WHERE t.chat_room_id=2 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안내해 주신 일정에 진행할 수 있을까요?';

UPDATE chat_message t SET t.content='사진을 보면 작업 도구를 미리 준비할 수 있어요. 출발지에 엘리베이터도 있나요?'
WHERE t.chat_room_id=2 AND t.sender_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '네. 작업 범위를 확인하고 일정을 조율하겠습니다.';

UPDATE chat_message t SET t.content='네. 엘리베이터 있습니다. 가구 사진도 같이 보내 드릴게요.'
WHERE t.chat_room_id=2 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '감사합니다. 준비할 사항도 알려 주세요.';

UPDATE chat_message t SET t.content='사진 확인 후 포장과 분해·조립 범위를 안내해 드리겠습니다.'
WHERE t.chat_room_id=2 AND t.sender_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.';

UPDATE chat_message t SET t.content='안녕하세요. 입주 청소 견적을 받았는데 창틀도 포함되는지 궁금해요.'
WHERE t.chat_room_id=3 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 견적 내용을 보고 상담을 요청드립니다.';

UPDATE chat_message t SET t.content='네. 창틀 청소가 포함됩니다. 집 안에 짐이나 가구가 들어와 있을까요?'
WHERE t.chat_room_id=3 AND t.sender_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 요청하신 내용을 확인했습니다.';

UPDATE chat_message t SET t.content='아직 빈집이에요. 화장실 두 곳과 주방 후드가 특히 신경 쓰입니다.'
WHERE t.chat_room_id=3 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안내해 주신 일정에 진행할 수 있을까요?';

UPDATE chat_message t SET t.content='확인했습니다. 오염 상태를 볼 수 있도록 사진을 보내 주시면 작업 시간을 안내해 드릴게요.'
WHERE t.chat_room_id=3 AND t.sender_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '네. 작업 범위를 확인하고 일정을 조율하겠습니다.';

UPDATE chat_message t SET t.content='오늘 집에 들러서 사진 찍어 보낼게요.'
WHERE t.chat_room_id=3 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '감사합니다. 준비할 사항도 알려 주세요.';

UPDATE chat_message t SET t.content='사진을 보내 주시면 주방과 욕실의 작업 범위를 확인해 드릴게요.'
WHERE t.chat_room_id=3 AND t.sender_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.';

UPDATE chat_message t SET t.content='안녕하세요. 개발 견적 관련해서 데이터베이스 구성도 함께 상담할 수 있을까요?'
WHERE t.chat_room_id=4 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 견적 내용을 보고 상담을 요청드립니다.';

UPDATE chat_message t SET t.content='네. 현재 저장할 정보와 화면에서 필요한 기능부터 살펴보면 됩니다.'
WHERE t.chat_room_id=4 AND t.sender_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안녕하세요. 요청하신 내용을 확인했습니다.';

UPDATE chat_message t SET t.content='회원 정보랑 예약 날짜를 관리하려고 해요. 관리자가 예약을 확인하는 화면도 필요합니다.'
WHERE t.chat_room_id=4 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '안내해 주신 일정에 진행할 수 있을까요?';

UPDATE chat_message t SET t.content='예약 가능 시간과 취소 기준이 정해져 있으면 데이터 구조를 잡기 수월해요.'
WHERE t.chat_room_id=4 AND t.sender_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '네. 작업 범위를 확인하고 일정을 조율하겠습니다.';

UPDATE chat_message t SET t.content='운영 방식을 정리해서 공유드릴게요.'
WHERE t.chat_room_id=4 AND t.sender_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '감사합니다. 준비할 사항도 알려 주세요.';

UPDATE chat_message t SET t.content='운영 기준을 받으면 예약 데이터 구조부터 설명드리겠습니다.'
WHERE t.chat_room_id=4 AND t.sender_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.sender_id AND m.email LIKE '%@demo.efy.example') AND BINARY t.content=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.';

UPDATE post t SET t.title='첫 이사 전날, 뭘 먼저 챙기면 좋을까요?', t.content='처음 자취방을 옮기는데 어디서부터 짐을 싸야 할지 막막하네요. 옷이랑 책은 상자에 담고 있는데 이사 당일 쓸 물건도 같이 넣어 버릴까 봐 걱정돼요. 먼저 챙겨 두면 좋은 물건이 있을까요?'
WHERE t.id=1 AND t.member_id=2 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='haneul123@example.com') AND BINARY t.title=BINARY '이사 전날 어떤 준비가 필요할까요?' AND BINARY t.content=BINARY '다음 주 첫 이사를 앞두고 있습니다. 짐을 싸는 순서와 준비물을 알고 싶습니다.';

UPDATE post t SET t.title='원룸 이사 전에 챙길 다섯 가지', t.content='이사 현장에서 자주 빠뜨리는 준비를 적어 봅니다. ① 관리실에 차량 출입 시간을 알려 주세요. ② 냉장고 안은 미리 비워 주세요. ③ 상자에는 놓을 방을 적어 주세요. ④ 귀중품과 약은 따로 챙겨 주세요. ⑤ 도착한 집에서 쓸 충전기와 세면도구는 마지막까지 손에 두면 편합니다.'
WHERE t.id=2 AND t.member_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='expert@example.com') AND BINARY t.title=BINARY '원룸 이사 준비를 위한 다섯 가지 팁' AND BINARY t.content=BINARY '자주 쓰는 물건은 따로 담고 상자마다 방 이름을 적어 주세요. 귀중품은 직접 보관하면 좋습니다.';

UPDATE post t SET t.title='원룸·소형 이사 상담받고 있어요', t.content='서초·강남·마포 지역의 소형 이사를 진행하고 있습니다. 짐 사진과 출발지·도착지의 층수를 알려 주시면 필요한 차량과 인원을 안내해 드려요. 침대 조립이나 계단 운반이 있다면 처음 상담할 때 같이 말씀해 주세요.'
WHERE t.id=3 AND t.member_id=12 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='seojun.move@example.com') AND BINARY t.title=BINARY '소형 이사 일정 상담을 도와드립니다' AND BINARY t.content=BINARY '짐의 양과 이동 거리를 확인하고 필요한 인원과 차량을 안내합니다. 계단 작업 여부도 알려 주세요.';

UPDATE post t SET t.title='입주 청소와 가구 배송, 순서가 궁금해요', t.content='청소 날짜를 잡으려는데 침대 배송이 먼저 가능하다고 하네요. 가구가 없는 상태에서 청소하는 게 나을 것 같긴 한데 날짜를 바꾸기가 쉽지 않아요. 큰 가구가 한두 개 있어도 청소가 가능할까요?'
WHERE t.id=4 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.title=BINARY '입주 청소는 언제 예약하면 좋을까요?' AND BINARY t.content=BINARY '가구 배송과 청소 일정이 겹칠까 고민입니다. 입주 전 적절한 순서를 추천해 주세요.';

UPDATE post t SET t.title='입주 청소 때 놓치기 쉬운 공간', t.content='바닥만 깨끗해 보여도 창틀 홈과 수납장 안쪽에는 먼지가 남아 있을 수 있어요. 주방 후드 필터와 욕실 배수구도 같이 확인해 보세요. 상담할 때 사진을 보내 주시면 집 상태에 맞는 작업 범위를 설명드리기 좋습니다.'
WHERE t.id=5 AND t.member_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='subin.clean@example.com') AND BINARY t.title=BINARY '입주 청소 전 확인할 공간들' AND BINARY t.content=BINARY '창틀 배수구와 주방 수납장 안쪽을 확인해 보세요. 청소 범위를 미리 정하면 상담이 편합니다.';

UPDATE post t SET t.title='주방·욕실 청소, 범위부터 안내드립니다', t.content='후드에 기름때가 쌓였거나 욕실 물때 때문에 고민이라면 사진을 보내 주세요. 소재와 오염 정도를 보고 가능한 작업부터 말씀드립니다. 청소를 마친 뒤에는 함께 둘러보고 빠진 곳이 없는지 확인합니다.'
WHERE t.id=6 AND t.member_id=15 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jian.clean@example.com') AND BINARY t.title=BINARY '주방과 욕실을 꼼꼼하게 청소합니다' AND BINARY t.content=BINARY '공간별 오염 상태를 확인하고 작업 범위를 안내합니다. 완료 후에는 함께 상태를 확인합니다.';

UPDATE post t SET t.title='공방 예약 사이트를 함께 만들 고수 찾습니다', t.content='작은 도자기 공방을 운영하고 있어요. 지금은 메시지로 예약을 받는데 날짜가 겹치는 일이 생겨서 예약 사이트를 알아보고 있습니다. 휴대폰에서 날짜와 수업을 고를 수 있으면 좋겠어요. 제작 기간과 관리 방법도 상담받고 싶습니다.'
WHERE t.id=7 AND t.member_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='minseo.park@example.com') AND BINARY t.title=BINARY '예약 사이트를 만들어 주실 고수 구해요' AND BINARY t.content=BINARY '소규모 공방의 예약 사이트를 준비합니다. 모바일 예약과 일정 관리 기능을 상담하고 싶습니다.';

UPDATE post t SET t.title='개발 견적을 받기 전에 정리해 보세요', t.content='어떤 기능이 필요한지 말로 설명하기 어려울 때는 실제 사용하는 순서를 적어 보세요. 예를 들어 날짜 선택 → 인원 선택 → 연락처 입력처럼요. 참고 사이트에서 마음에 드는 부분과 필요 없는 부분을 나눠 주시면 견적 범위를 맞추기 훨씬 수월합니다.'
WHERE t.id=8 AND t.member_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.title=BINARY '웹사이트 의뢰 전에 정리할 내용' AND BINARY t.content=BINARY '서비스의 목적과 꼭 필요한 기능을 먼저 적어 주세요. 참고 화면이 있으면 범위를 정하기 쉽습니다.';

UPDATE post t SET t.title='기존 화면에 맞는 API 개발을 도와드립니다', t.content='화면은 준비됐는데 회원가입이나 예약 기능을 연결하지 못하고 계신가요? 현재 화면과 필요한 기능을 확인한 뒤 API와 데이터베이스를 구성해 드립니다. 작업 범위와 배포 후 확인할 항목은 상담에서 함께 정합니다.'
WHERE t.id=9 AND t.member_id=18 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='hyunwoo.dev@example.com') AND BINARY t.title=BINARY '웹 서비스 API 개발 상담 안내' AND BINARY t.content=BINARY '회원과 예약 기능을 포함한 API 개발을 도와드립니다. 데이터 구조와 배포 방법도 함께 설명합니다.';

UPDATE post t SET t.title='이삿날 비 예보가 있는데 괜찮을까요?', t.content='다음 주 이삿날에 비 예보가 떠서 걱정이에요. 책장이랑 컴퓨터가 젖을까 봐 비닐을 더 사 둬야 하나 고민 중입니다. 비 오는 날 이사해 보신 분들은 어떻게 준비하셨나요?'
WHERE t.id=10 AND t.member_id=5 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='lovesaltbread@example.com') AND BINARY t.title=BINARY '비 오는 날 이사는 어떻게 준비하나요?' AND BINARY t.content=BINARY '이사 당일 비 예보가 있어 걱정됩니다. 가구와 전자제품을 보호하는 방법이 궁금합니다.';

UPDATE post t SET t.title='큰 가구는 현관 폭도 확인해 주세요', t.content='책장이나 소파를 옮길 때는 가구 크기뿐 아니라 현관과 복도의 폭도 중요합니다. 엘리베이터가 있어도 문을 통과하지 못하는 경우가 있어요. 가구 전체 사진과 이동할 곳의 문 폭을 미리 알려 주시면 분해나 다른 운반 방법이 필요한지 확인해 드릴 수 있습니다.'
WHERE t.id=11 AND t.member_id=13 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='doyoon.move@example.com') AND BINARY t.title=BINARY '큰 가구를 옮기기 전 치수 확인하기' AND BINARY t.content=BINARY '현관과 엘리베이터의 폭을 먼저 확인해 주세요. 분해 가능한 가구인지 알려 주시면 준비에 도움이 됩니다.';

UPDATE post t SET t.title='강남 입주 청소 가능한 분 계실까요?', t.content='주말에 이사를 앞두고 있어서 금요일 오후에 청소를 맡기고 싶습니다. 주방과 거실 위주로 생각하고 있고 집은 비어 있어요. 창틀 청소도 포함되는지와 예상 작업 시간을 알고 싶습니다.'
WHERE t.id=12 AND t.member_id=2 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='haneul123@example.com') AND BINARY t.title=BINARY '주말 입주 청소 고수를 찾습니다' AND BINARY t.content=BINARY '이사 전에 거실과 주방 청소를 맡기고 싶습니다. 작업 가능 시간과 포함 범위를 알려 주세요.';

UPDATE post t SET t.title='휴대폰에서도 편한 가게 소개 페이지', t.content='가게 위치와 메뉴를 찾으려고 들어왔는데 화면이 작아서 불편했던 경험이 있나요? 휴대폰에서 읽기 편한 소개 페이지를 만듭니다. 전화·문의 버튼과 지도처럼 자주 쓰는 기능부터 함께 정해요.'
WHERE t.id=13 AND t.member_id=19 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='seoa.design@example.com') AND BINARY t.title=BINARY '사용하기 편한 모바일 웹을 만듭니다' AND BINARY t.content=BINARY '작은 화면에서도 예약과 문의가 편리하도록 구성합니다. 필요한 기능부터 차근차근 완성합니다.';

UPDATE post t SET t.title='개발 견적에서 유지보수는 따로 보나요?', t.content='사이트 제작 견적을 몇 군데 받았는데 유지보수 범위가 조금씩 다르네요. 문구 수정이나 오류 수정은 보통 어디까지 포함되는지 궁금합니다. 배포 비용도 처음 견적에서 같이 확인하는 게 좋을까요?'
WHERE t.id=14 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.title=BINARY '웹 개발 견적은 어떻게 비교하나요?' AND BINARY t.content=BINARY '유지보수와 배포 비용까지 비교하고 싶습니다. 견적서에서 확인할 항목을 알려 주세요.';

UPDATE post t SET t.title='작품을 소개할 공방 홈페이지가 필요해요', t.content='만든 작품을 사진과 설명으로 모아 둘 홈페이지를 준비하고 있어요. 주문 기능까지는 필요 없고 문의할 수 있는 창구가 있으면 좋겠습니다. 사진과 글은 제가 나중에 바꿀 수 있는 방식이면 더 좋겠어요.'
WHERE t.id=15 AND t.member_id=5 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='lovesaltbread@example.com') AND BINARY t.title=BINARY '공방 소개 페이지 개발을 의뢰합니다' AND BINARY t.content=BINARY '소개와 작품 목록과 문의 기능이 필요합니다. 먼저 작은 규모로 시작하고 싶습니다.';

UPDATE post_comment t SET t.content='이사 당일 쓸 충전기와 세면도구는 작은 가방에 따로 챙겨 두세요. 계약서와 신분증도 상자에 넣지 않고 직접 가지고 계시면 편합니다.'
WHERE t.id=1 AND t.member_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='expert@example.com') AND BINARY t.content=BINARY '작은 짐은 상자에 담고 큰 가구 목록을 미리 정리해 주세요.';

UPDATE post_comment t SET t.content='저도 첫 이사 때 상자에 가위를 넣어서 한참 찾았어요. 가위랑 테이프는 마지막까지 꺼내 두시는 걸 추천해요.'
WHERE t.id=2 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '감사합니다. 이동 동선과 가구 크기도 함께 확인하겠습니다.';

UPDATE post_comment t SET t.content='냉장고 비우는 걸 놓치는 분들이 많더라고요. 냉동식품은 전날까지 정리해 두면 당일 포장이 훨씬 수월합니다.'
WHERE t.id=3 AND t.member_id=12 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='seojun.move@example.com') AND BINARY t.content=BINARY '작은 짐은 상자에 담고 큰 가구 목록을 미리 정리해 주세요.';

UPDATE post_comment t SET t.content='상자마다 방 이름을 적는 건 생각 못 했네요. 도착해서 전부 다시 열어 볼 뻔했어요. 준비할 때 참고할게요.'
WHERE t.id=4 AND t.member_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '감사합니다. 이동 동선과 가구 크기도 함께 확인하겠습니다.';

UPDATE post_comment t SET t.content='계단 작업은 층수뿐 아니라 폭도 중요하죠. 상담할 때 계단 사진까지 받으면 현장 준비에 도움이 될 것 같아요.'
WHERE t.id=5 AND t.member_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='expert@example.com') AND BINARY t.content=BINARY '작은 짐은 상자에 담고 큰 가구 목록을 미리 정리해 주세요.';

UPDATE post_comment t SET t.content='큰 책상 하나만 옮기는 경우도 상담할 수 있나요? 분해가 필요한 책상이라 차량만 빌리기는 어려울 것 같아서요.'
WHERE t.id=6 AND t.member_id=5 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '감사합니다. 이동 동선과 가구 크기도 함께 확인하겠습니다.';

UPDATE post_comment t SET t.content='가능하면 청소를 먼저 하는 편이 좋지만 가구가 있어도 작업은 할 수 있어요. 다만 가구 뒤나 아래는 접근이 어려울 수 있어 위치를 미리 알려 주세요.'
WHERE t.id=7 AND t.member_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='subin.clean@example.com') AND BINARY t.content=BINARY '작업 공간을 비우고 집중적으로 청소할 곳을 알려 주시면 좋습니다.';

UPDATE post_comment t SET t.content='저는 배송을 하루 미뤘는데 청소 후 먼지가 가라앉을 시간도 있어서 괜찮았어요. 일정 조정이 된다면 청소 먼저 추천해요.'
WHERE t.id=8 AND t.member_id=2 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '청소 범위를 정리해서 상담드리겠습니다. 안내 감사합니다.';

UPDATE post_comment t SET t.content='수납장 안쪽은 문을 닫으면 잘 안 보이죠. 작업 후 확인하실 때 서랍도 한 번씩 열어 보시면 좋습니다.'
WHERE t.id=9 AND t.member_id=15 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jian.clean@example.com') AND BINARY t.content=BINARY '작업 공간을 비우고 집중적으로 청소할 곳을 알려 주시면 좋습니다.';

UPDATE post_comment t SET t.content='창틀은 눈에 보이는 부분만 생각했는데 홈까지 확인해야겠네요. 청소 상담할 때 사진부터 찍어 둬야겠어요.'
WHERE t.id=10 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '청소 범위를 정리해서 상담드리겠습니다. 안내 감사합니다.';

UPDATE post_comment t SET t.content='후드와 욕실은 소재에 따라 작업 방법이 달라지죠. 가능한 범위를 먼저 설명해 주신다는 점이 좋네요.'
WHERE t.id=11 AND t.member_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='subin.clean@example.com') AND BINARY t.content=BINARY '작업 공간을 비우고 집중적으로 청소할 곳을 알려 주시면 좋습니다.';

UPDATE post_comment t SET t.content='오래된 후드라 필터에 기름때가 꽤 있어요. 모델명과 사진을 같이 보내 드리면 상담에 도움이 될까요?'
WHERE t.id=12 AND t.member_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '청소 범위를 정리해서 상담드리겠습니다. 안내 감사합니다.';

UPDATE post_comment t SET t.content='같은 시간에 여러 분이 예약하는 경우도 있어서 중복 예약을 막는 방식이 중요해요. 수업별 정원과 취소 기준도 함께 알려 주시면 좋겠습니다.'
WHERE t.id=13 AND t.member_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.content=BINARY '꼭 필요한 기능과 원하는 일정을 적으면 견적 비교에 도움이 됩니다.';

UPDATE post_comment t SET t.content='저도 작은 가게를 운영하는데 예약 변경이 제일 번거롭더라고요. 손님이 직접 변경할 수 있는지도 상담해 보세요.'
WHERE t.id=14 AND t.member_id=5 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '우선순위를 정리한 뒤 구체적인 범위를 상담하겠습니다.';

UPDATE post_comment t SET t.content='참고 화면에 필요한 기능을 표시해 주시면 놓치는 부분이 줄어요. 관리자 화면에서 해야 할 일도 같이 정리하면 좋습니다.'
WHERE t.id=15 AND t.member_id=18 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='hyunwoo.dev@example.com') AND BINARY t.content=BINARY '꼭 필요한 기능과 원하는 일정을 적으면 견적 비교에 도움이 됩니다.';

UPDATE post_comment t SET t.content='기능 이름만 적으려니 어려웠는데 사용하는 순서로 쓰면 되겠네요. 예약받는 과정을 한번 정리해 볼게요.'
WHERE t.id=16 AND t.member_id=2 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '우선순위를 정리한 뒤 구체적인 범위를 상담하겠습니다.';

UPDATE post_comment t SET t.content='화면이 먼저 있다면 오류가 났을 때 보여 줄 안내도 같이 정해 두면 좋겠네요. 정상 동작만 맞추면 뒤에서 수정이 생기더라고요.'
WHERE t.id=17 AND t.member_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.content=BINARY '꼭 필요한 기능과 원하는 일정을 적으면 견적 비교에 도움이 됩니다.';

UPDATE post_comment t SET t.content='로그인 화면은 만들어 뒀는데 회원 정보가 저장되지 않아 막혀 있었어요. 이런 경우에도 기존 화면을 살려서 작업할 수 있을까요?'
WHERE t.id=18 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '우선순위를 정리한 뒤 구체적인 범위를 상담하겠습니다.';

UPDATE post_comment t SET t.content='컴퓨터는 전원을 분리하고 물이 닿지 않도록 포장해 주세요. 현장에서는 차량까지 이동하는 구간을 짧게 잡고 가구 덮개도 준비합니다.'
WHERE t.id=19 AND t.member_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='expert@example.com') AND BINARY t.content=BINARY '작은 짐은 상자에 담고 큰 가구 목록을 미리 정리해 주세요.';

UPDATE post_comment t SET t.content='비 오는 날 이사한 적 있는데 수건을 넉넉히 챙겨서 유용했어요. 현관 쪽 바닥에 물기가 생겨서 계속 닦게 되더라고요.'
WHERE t.id=20 AND t.member_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '감사합니다. 이동 동선과 가구 크기도 함께 확인하겠습니다.';

UPDATE post_comment t SET t.content='문 폭을 잴 때 손잡이나 돌출된 부분도 같이 봐야 해요. 사진 한 장이 있으면 설명만 듣는 것보다 판단하기 쉽습니다.'
WHERE t.id=21 AND t.member_id=11 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='expert@example.com') AND BINARY t.content=BINARY '작은 짐은 상자에 담고 큰 가구 목록을 미리 정리해 주세요.';

UPDATE post_comment t SET t.content='소파 길이만 재고 있었는데 복도에서 꺾이는 구간도 봐야겠네요. 이사 전에 치수부터 다시 확인해 볼게요.'
WHERE t.id=22 AND t.member_id=5 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='lovesaltbread@example.com') AND BINARY t.content=BINARY '감사합니다. 이동 동선과 가구 크기도 함께 확인하겠습니다.';

UPDATE post_comment t SET t.content='금요일 오후 일정을 확인해 드릴 수 있어요. 평수와 창문 개수를 알려 주시면 작업 시간과 창틀 청소 범위를 안내해 드리겠습니다.'
WHERE t.id=23 AND t.member_id=14 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='subin.clean@example.com') AND BINARY t.content=BINARY '작업 공간을 비우고 집중적으로 청소할 곳을 알려 주시면 좋습니다.';

UPDATE post_comment t SET t.content='입주 전에 청소 시간을 넉넉히 잡는 게 좋더라고요. 창문이 많으면 예상보다 오래 걸릴 수 있으니 꼭 물어보세요.'
WHERE t.id=24 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '청소 범위를 정리해서 상담드리겠습니다. 안내 감사합니다.';

UPDATE post_comment t SET t.content='가게 소개 페이지라면 영업시간과 휴무일도 눈에 잘 들어와야겠네요. 문의 버튼까지 가는 동선이 짧으면 더 편할 것 같아요.'
WHERE t.id=25 AND t.member_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.content=BINARY '꼭 필요한 기능과 원하는 일정을 적으면 견적 비교에 도움이 됩니다.';

UPDATE post_comment t SET t.content='가게 메뉴가 자주 바뀌는데 직접 수정할 수 있는지도 궁금해요. 개발 지식이 없어도 관리할 수 있는 방식이 있을까요?'
WHERE t.id=26 AND t.member_id=3 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jiuzzzang@example.com') AND BINARY t.content=BINARY '우선순위를 정리한 뒤 구체적인 범위를 상담하겠습니다.';

UPDATE post_comment t SET t.content='오류 수정과 새 기능 추가는 범위를 나눠서 확인하는 편이 좋아요. 서버·도메인 비용과 지원 기간도 견적서에 적어 달라고 요청해 보세요.'
WHERE t.id=27 AND t.member_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.content=BINARY '꼭 필요한 기능과 원하는 일정을 적으면 견적 비교에 도움이 됩니다.';

UPDATE post_comment t SET t.content='저도 견적 받을 때 호스팅 비용을 놓쳤었어요. 매달 나가는 금액이 있는지 먼저 물어보시면 비교하기 편해요.'
WHERE t.id=28 AND t.member_id=4 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='minseo.park@example.com') AND BINARY t.content=BINARY '우선순위를 정리한 뒤 구체적인 범위를 상담하겠습니다.';

UPDATE post_comment t SET t.content='사진과 글을 직접 바꾸시려면 간단한 관리 화면이 있으면 좋겠어요. 작품 분류가 필요한지도 먼저 정해 보세요.'
WHERE t.id=29 AND t.member_id=17 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='jio.web@example.com') AND BINARY t.content=BINARY '꼭 필요한 기능과 원하는 일정을 적으면 견적 비교에 도움이 됩니다.';

UPDATE post_comment t SET t.content='저는 사진을 한꺼번에 올리면 느려질까 걱정되더라고요. 작품 사진 용량이나 권장 크기도 같이 안내받으면 좋을 것 같아요.'
WHERE t.id=30 AND t.member_id=2 AND EXISTS (SELECT 1 FROM member m WHERE m.id=t.member_id AND m.email='haneul123@example.com') AND BINARY t.content=BINARY '우선순위를 정리한 뒤 구체적인 범위를 상담하겠습니다.';

UPDATE chat_room t SET t.last_message_preview='자료를 보내 주시면 기능 범위와 예상 일정을 정리해 드릴게요.'
WHERE t.id=1 AND t.quote_id=16 AND EXISTS (SELECT 1 FROM quote q JOIN service_request r ON r.id=q.service_request_id JOIN member m ON m.id=r.member_id WHERE q.id=t.quote_id AND m.email='minseo.park@example.com') AND BINARY t.last_message_preview=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.' AND (SELECT cm.content FROM chat_message cm WHERE cm.chat_room_id=t.id ORDER BY cm.created_at DESC, cm.id DESC LIMIT 1)='자료를 보내 주시면 기능 범위와 예상 일정을 정리해 드릴게요.';

UPDATE chat_room t SET t.last_message_preview='사진 확인 후 포장과 분해·조립 범위를 안내해 드리겠습니다.'
WHERE t.id=2 AND t.quote_id=19 AND EXISTS (SELECT 1 FROM quote q JOIN service_request r ON r.id=q.service_request_id JOIN member m ON m.id=r.member_id WHERE q.id=t.quote_id AND m.email='minseo.park@example.com') AND BINARY t.last_message_preview=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.' AND (SELECT cm.content FROM chat_message cm WHERE cm.chat_room_id=t.id ORDER BY cm.created_at DESC, cm.id DESC LIMIT 1)='사진 확인 후 포장과 분해·조립 범위를 안내해 드리겠습니다.';

UPDATE chat_room t SET t.last_message_preview='사진을 보내 주시면 주방과 욕실의 작업 범위를 확인해 드릴게요.'
WHERE t.id=3 AND t.quote_id=22 AND EXISTS (SELECT 1 FROM quote q JOIN service_request r ON r.id=q.service_request_id JOIN member m ON m.id=r.member_id WHERE q.id=t.quote_id AND m.email='minseo.park@example.com') AND BINARY t.last_message_preview=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.' AND (SELECT cm.content FROM chat_message cm WHERE cm.chat_room_id=t.id ORDER BY cm.created_at DESC, cm.id DESC LIMIT 1)='사진을 보내 주시면 주방과 욕실의 작업 범위를 확인해 드릴게요.';

UPDATE chat_room t SET t.last_message_preview='운영 기준을 받으면 예약 데이터 구조부터 설명드리겠습니다.'
WHERE t.id=4 AND t.quote_id=25 AND EXISTS (SELECT 1 FROM quote q JOIN service_request r ON r.id=q.service_request_id JOIN member m ON m.id=r.member_id WHERE q.id=t.quote_id AND m.email='minseo.park@example.com') AND BINARY t.last_message_preview=BINARY '상담 감사합니다. 준비 사항을 정리해 드리겠습니다.' AND (SELECT cm.content FROM chat_message cm WHERE cm.chat_room_id=t.id ORDER BY cm.created_at DESC, cm.id DESC LIMIT 1)='운영 기준을 받으면 예약 데이터 구조부터 설명드리겠습니다.';

-- 고수별 견적 메시지. 기존 공통 문구가 남아 있는 시드 견적만 수정합니다.
UPDATE quote t SET t.description='안녕하세요. 김민준입니다. 원룸 이사와 침대·책상 운반을 함께 도와드릴 수 있어요. 가구 사진을 보내 주시면 분해·조립이 필요한지 확인하겠습니다.'
WHERE t.id=1 AND t.expert_id=1 AND t.service_request_id=4
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='expert@example.com');

UPDATE quote t SET t.description='이서준입니다. 출발지와 도착지의 층수, 엘리베이터 유무를 알려 주세요. 침대와 책상을 안전하게 옮길 수 있도록 운반 동선부터 살펴보겠습니다.'
WHERE t.id=2 AND t.expert_id=2 AND t.service_request_id=4
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='seojun.move@example.com');

UPDATE quote t SET t.description='안녕하세요. 최도윤입니다. 큰 가구는 모서리를 보호해 포장하고 이동합니다. 책상 크기와 침대 프레임 사진을 보고 필요한 인원을 안내해 드릴게요.'
WHERE t.id=3 AND t.expert_id=3 AND t.service_request_id=4
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='doyoon.move@example.com');

UPDATE quote t SET t.description='안녕하세요. 정수빈입니다. 20평 아파트 입주 청소 가능하며 창틀과 화장실도 포함해서 살펴봅니다. 집 안이 비어 있는지 알려 주시면 작업 시간을 안내해 드릴게요.'
WHERE t.id=4 AND t.expert_id=4 AND t.service_request_id=5
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='subin.clean@example.com');

UPDATE quote t SET t.description='오지안입니다. 요청하신 창틀과 화장실을 중심으로 청소 범위를 잡아 봤어요. 물때와 곰팡이 상태를 사진으로 확인하면 작업 방법을 더 구체적으로 말씀드릴 수 있습니다.'
WHERE t.id=5 AND t.expert_id=5 AND t.service_request_id=5
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='jian.clean@example.com');

UPDATE quote t SET t.description='안녕하세요. 윤하윤입니다. 입주 전에 주방과 수납장 안쪽까지 확인하고 청소합니다. 특히 신경 쓰이는 공간을 알려 주시면 마무리 점검 때 함께 살펴볼게요.'
WHERE t.id=6 AND t.expert_id=6 AND t.service_request_id=5
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='hayoon.clean@example.com');

UPDATE quote t SET t.description='안녕하세요. 강지호입니다. Spring Boot 기반 REST API 개발을 도와드릴 수 있어요. 이번 달 안에 필요한 기능을 우선순위로 정리해서 작업 범위를 맞춰 보겠습니다.'
WHERE t.id=7 AND t.expert_id=7 AND t.service_request_id=6
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='jio.web@example.com');

UPDATE quote t SET t.description='박현우입니다. JPA와 MySQL을 사용하는 백엔드 개발 요청 확인했습니다. 기존 화면이나 API 명세가 있다면 보내 주세요. 기능별로 필요한 작업과 일정을 정리해 드리겠습니다.'
WHERE t.id=8 AND t.expert_id=8 AND t.service_request_id=6
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='hyunwoo.dev@example.com');

UPDATE quote t SET t.description='안녕하세요. 임서아입니다. 준비된 화면에 맞춰 API를 연결하는 작업도 가능합니다. 이번 견적은 기본 기능을 기준으로 잡았으며 화면 수와 상세 기능을 확인한 뒤 범위를 협의하고 싶습니다.'
WHERE t.id=9 AND t.expert_id=9 AND t.service_request_id=6
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='seoa.design@example.com');

UPDATE quote t SET t.description='김민준입니다. 침대와 책상을 포함한 원룸 짐 운반을 기준으로 견적 드렸어요. 이사 당일 바로 쓰실 물건은 별도로 챙겨 두시면 좋습니다. 가구 조립 범위는 사진을 보고 안내할게요.'
WHERE t.id=10 AND t.expert_id=1 AND t.service_request_id=7
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='expert@example.com');

UPDATE quote t SET t.description='안녕하세요. 이서준입니다. 이사 차량이 건물 가까이 주차할 수 있는지 먼저 확인 부탁드려요. 계단 운반이 필요하면 층수와 계단 폭에 맞춰 작업 인원을 준비하겠습니다.'
WHERE t.id=11 AND t.expert_id=2 AND t.service_request_id=7
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='seojun.move@example.com');

UPDATE quote t SET t.description='최도윤입니다. 침대와 책상 외에 냉장고나 세탁기도 옮기실 예정인가요? 짐 목록을 보내 주시면 운반 가능한 크기와 포장 방법을 확인해 드리겠습니다.'
WHERE t.id=12 AND t.expert_id=3 AND t.service_request_id=7
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='doyoon.move@example.com');

UPDATE quote t SET t.description='정수빈입니다. 요청하신 입주 청소에서 창틀 홈과 욕실 배수구를 중점적으로 확인하겠습니다. 가구가 먼저 들어오는 일정이라면 위치를 알려 주세요. 접근 가능한 범위를 설명드릴게요.'
WHERE t.id=13 AND t.expert_id=4 AND t.service_request_id=8
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='subin.clean@example.com');

UPDATE quote t SET t.description='안녕하세요. 오지안입니다. 화장실의 타일과 수전 상태를 먼저 보고 소재에 맞게 작업합니다. 창틀 사진도 함께 보내 주시면 추가 상담이 필요한 부분을 미리 안내해 드릴게요.'
WHERE t.id=14 AND t.expert_id=5 AND t.service_request_id=8
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='jian.clean@example.com');

UPDATE quote t SET t.description='윤하윤입니다. 빈집 입주 청소를 기준으로 제안드립니다. 작업을 마친 뒤 창문과 수납장을 열어 함께 확인하는 시간을 갖고 있어요. 입주 일정에 맞춰 상담해 보겠습니다.'
WHERE t.id=15 AND t.expert_id=6 AND t.service_request_id=8
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='hayoon.clean@example.com');

UPDATE quote t SET t.description='강지호입니다. REST API 개발은 회원 기능부터 나눠 진행하는 방식으로 제안드려요. 우선 필요한 입력 항목과 화면 흐름을 확인하고 구현 순서와 이번 달 일정을 함께 정하겠습니다.'
WHERE t.id=16 AND t.expert_id=7 AND t.service_request_id=9
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='jio.web@example.com');

UPDATE quote t SET t.description='안녕하세요. 박현우입니다. 회원 인증과 데이터 저장 API를 기준으로 작업 범위를 확인하고 싶습니다. 현재 코드가 있다면 JPA 엔티티와 MySQL 테이블 구조부터 함께 살펴보겠습니다.'
WHERE t.id=17 AND t.expert_id=8 AND t.service_request_id=9
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='hyunwoo.dev@example.com');

UPDATE quote t SET t.description='임서아입니다. API가 연결될 화면을 보내 주시면 요청·응답에 필요한 항목을 정리해 드릴게요. 오류가 났을 때의 안내까지 포함할 수 있도록 화면별 동작도 함께 확인하고 싶습니다.'
WHERE t.id=18 AND t.expert_id=9 AND t.service_request_id=9
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='seoa.design@example.com');

UPDATE quote t SET t.description='안녕하세요. 김민준입니다. 원룸 이사 요청 확인했습니다. 침대 프레임과 책상은 분해 가능한 구조인지 먼저 살펴볼게요. 양쪽 집의 엘리베이터 유무와 가구 사진을 부탁드립니다.'
WHERE t.id=19 AND t.expert_id=1 AND t.service_request_id=10
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='expert@example.com');

UPDATE quote t SET t.description='이서준입니다. 짐을 싣는 곳부터 내려놓을 방까지 이동 경로를 확인해 준비하겠습니다. 엘리베이터 사용 예약이 필요한 건물이라면 관리실에 가능한 시간도 확인해 주세요.'
WHERE t.id=20 AND t.expert_id=2 AND t.service_request_id=10
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='seojun.move@example.com');

UPDATE quote t SET t.description='안녕하세요. 최도윤입니다. 침대와 책상이 현관을 통과하는지 치수를 먼저 확인하면 좋겠습니다. 큰 짐은 보호 포장한 뒤 옮기고 도착한 곳에서 배치까지 도와드려요.'
WHERE t.id=21 AND t.expert_id=3 AND t.service_request_id=10
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='doyoon.move@example.com');

UPDATE quote t SET t.description='정수빈입니다. 20평 입주 청소 요청에 맞춰 견적 보내 드립니다. 창틀과 화장실을 꼼꼼하게 살펴보고 주방 후드 상태도 확인하겠습니다. 집 상태를 볼 수 있는 사진이 있으면 부탁드려요.'
WHERE t.id=22 AND t.expert_id=4 AND t.service_request_id=11
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='subin.clean@example.com');

UPDATE quote t SET t.description='안녕하세요. 오지안입니다. 창틀과 욕실을 집중적으로 청소하는 범위로 제안드립니다. 오래된 물때나 실리콘 변색처럼 청소만으로 달라지기 어려운 부분은 시작 전에 설명드리겠습니다.'
WHERE t.id=23 AND t.expert_id=5 AND t.service_request_id=11
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='jian.clean@example.com');

UPDATE quote t SET t.description='윤하윤입니다. 입주 전에 집 안이 비어 있는지와 출입 가능한 시간을 알려 주세요. 작업 후 직접 확인하시기 편하도록 마무리 시간을 미리 안내해 드리겠습니다.'
WHERE t.id=24 AND t.expert_id=6 AND t.service_request_id=11
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='hayoon.clean@example.com');

UPDATE quote t SET t.description='안녕하세요. 강지호입니다. 요청하신 REST API와 데이터베이스 구성을 함께 상담할 수 있어요. 예약 흐름과 관리자에게 필요한 기능부터 정리해서 구현 범위를 제안드리겠습니다.'
WHERE t.id=25 AND t.expert_id=7 AND t.service_request_id=12
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='jio.web@example.com');

UPDATE quote t SET t.description='박현우입니다. Spring Boot·JPA·MySQL 구성으로 개발 가능하며 이번 달 완료를 목표로 범위를 협의하고 싶습니다. 필수 기능과 나중에 추가할 기능을 나눠 알려 주시면 일정 산정에 도움이 됩니다.'
WHERE t.id=26 AND t.expert_id=8 AND t.service_request_id=12
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='hyunwoo.dev@example.com');

UPDATE quote t SET t.description='안녕하세요. 임서아입니다. 사용자가 입력하는 화면과 관리자가 확인하는 화면을 함께 보면 API 범위를 정하기 좋아요. 참고 화면과 필요한 기능 목록을 보내 주시면 연결 작업을 구체적으로 안내하겠습니다.'
WHERE t.id=27 AND t.expert_id=9 AND t.service_request_id=12
AND BINARY t.description=BINARY '요청하신 범위를 확인했습니다. 사전 상담 후 세부 일정과 비용을 확정하겠습니다.'
AND EXISTS (SELECT 1 FROM expert_profile ep JOIN member m ON m.id=ep.member_id WHERE ep.id=t.expert_id AND m.email='seoa.design@example.com');

COMMIT;
