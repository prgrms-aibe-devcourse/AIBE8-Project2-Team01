-- #195 구버전 발표용 DB에 없는 예시 고수만 추가합니다. 운영 DB에는 아직 실행하지 않았습니다.
-- 읽기 전용 mariadb-seed-precheck.sql로 상태·충돌을 확인한 뒤 실행합니다.
-- 기존 회원은 수정하지 않습니다. 탈퇴 계정·역할·전화번호 충돌·프로필 없는 기존 이메일은 차단합니다.
-- 쓰기 요청을 멈추고 백업 후 한 연결에서 전체 실행합니다. 첫 오류 중단·ROLLBACK, --force 금지.
SET NAMES utf8mb4;
CREATE TEMPORARY TABLE seed195_add_guard (ok INT CHECK(ok=1)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TEMPORARY TABLE seed195_add_profiles (email VARCHAR(255) PRIMARY KEY,password VARCHAR(255),nickname VARCHAR(255),phone VARCHAR(30),age_days INT,short_intro VARCHAR(255),description TEXT,career_years INT,career_description TEXT,primary_category_id BIGINT,is_nationwide BOOLEAN) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TEMPORARY TABLE seed195_add_pending (email VARCHAR(255) PRIMARY KEY) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TEMPORARY TABLE seed195_add_services (email VARCHAR(255),category_id BIGINT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TEMPORARY TABLE seed195_add_categories (id BIGINT PRIMARY KEY,name VARCHAR(255)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TEMPORARY TABLE seed195_add_regions (email VARCHAR(255),first_id BIGINT,name VARCHAR(255)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

CREATE TEMPORARY TABLE seed195_add_whole (email VARCHAR(255),first_id BIGINT) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

INSERT INTO seed195_add_profiles VALUES
('hangyeol.move@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','한결이사','01080000101',41,'원룸부터 쓰리룸까지 포장이사 전문','원룸부터 쓰리룸까지 포장이사 전문. 작업 범위와 일정은 상담 후 안내해 드립니다.',9,'관련 서비스 경력 9년입니다.',101,0),
('sparkle.home@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','반짝홈케어','01080000102',20,'입주 청소와 이사 청소를 하루에 끝내 드려요','입주 청소와 이사 청소를 하루에 끝내 드려요. 작업 범위와 일정은 상담 후 안내해 드립니다.',6,'관련 서비스 경력 6년입니다.',102,0),
('yoon.english@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','윤선생 영어','01080000103',120,'중고등 내신과 수능 영어를 1:1로 봐 드립니다','중고등 내신과 수능 영어를 1:1로 봐 드립니다. 작업 범위와 일정은 상담 후 안내해 드립니다.',11,'관련 서비스 경력 11년입니다.',129,0),
('sori.music@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','소리공방','01080000104',5,'취미 피아노와 기타를 처음부터 차근차근','취미 피아노와 기타를 처음부터 차근차근. 작업 범위와 일정은 상담 후 안내해 드립니다.',7,'관련 서비스 경력 7년입니다.',165,0),
('codemate@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','코드메이트','01080000105',2,'쇼핑몰과 예약 서비스 웹을 기획부터 배포까지','쇼핑몰과 예약 서비스 웹을 기획부터 배포까지. 작업 범위와 일정은 상담 후 안내해 드립니다.',8,'관련 서비스 경력 8년입니다.',139,1),
('bareun.fix@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','바른설비','01080000106',210,'누수 탐지와 보일러 교체를 당일에 확인합니다','누수 탐지와 보일러 교체를 당일에 확인합니다. 작업 범위와 일정은 상담 후 안내해 드립니다.',15,'관련 서비스 경력 15년입니다.',109,0),
('onjip@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','온집 인테리어','01080000107',8,'욕실과 주방 부분 시공을 깔끔하게','욕실과 주방 부분 시공을 깔끔하게. 작업 범위와 일정은 상담 후 안내해 드립니다.',12,'관련 서비스 경력 12년입니다.',113,0),
('damda.snap@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','담다 스냅','01080000108',1,'웨딩 스냅과 가족 사진을 자연광으로 담아요','웨딩 스냅과 가족 사진을 자연광으로 담아요. 작업 범위와 일정은 상담 후 안내해 드립니다.',5,'관련 서비스 경력 5년입니다.',120,1),
('tidyday@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','정리하는 하루','01080000109',6,'가전 분해 세척과 가구 청소를 함께 맡겨 주세요','가전 분해 세척과 가구 청소를 함께 맡겨 주세요. 작업 범위와 일정은 상담 후 안내해 드립니다.',4,'관련 서비스 경력 4년입니다.',103,0),
('kimcoach@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','김코치 피트니스','01080000110',60,'체형 교정 중심의 1:1 퍼스널 트레이닝','체형 교정 중심의 1:1 퍼스널 트레이닝. 작업 범위와 일정은 상담 후 안내해 드립니다.',10,'관련 서비스 경력 10년입니다.',174,0),
('doorknob@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','문고리 수리','01080000111',4,'도어락 교체와 방충망, 창문 수리','도어락 교체와 방충망, 창문 수리. 작업 범위와 일정은 상담 후 안내해 드립니다.',8,'관련 서비스 경력 8년입니다.',108,0),
('haeundae.move@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','해운대 이사짐센터','01080000112',300,'부산 전 지역 소형 이사와 용달','부산 전 지역 소형 이사와 용달. 작업 범위와 일정은 상담 후 안내해 드립니다.',18,'관련 서비스 경력 18년입니다.',101,0),
('career.note@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','커리어노트','01080000113',90,'자소서 첨삭과 직무 면접 준비를 1:1로','자소서 첨삭과 직무 면접 준비를 1:1로. 작업 범위와 일정은 상담 후 안내해 드립니다.',9,'관련 서비스 경력 9년입니다.',150,0),
('forest.art@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','그림숲 아틀리에','01080000114',9,'성인 취미 드로잉과 수채화 클래스','성인 취미 드로잉과 수채화 클래스. 작업 범위와 일정은 상담 후 안내해 드립니다.',6,'관련 서비스 경력 6년입니다.',167,0),
('saegil@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','새길 노무','01080000115',2,'임금 체불과 근로계약 상담을 쉽게 풀어 드려요','임금 체불과 근로계약 상담을 쉽게 풀어 드려요. 작업 범위와 일정은 상담 후 안내해 드립니다.',13,'관련 서비스 경력 13년입니다.',187,1),
('mindcomma@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','마음쉼표 상담','01080000116',30,'직장인 스트레스와 관계 고민 상담','직장인 스트레스와 관계 고민 상담. 작업 범위와 일정은 상담 후 안내해 드립니다.',10,'관련 서비스 경력 10년입니다.',190,0),
('hanbit.car@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','한빛 카케어','01080000117',150,'출장 세차와 실내 클리닝, 소모품 교체','출장 세차와 실내 클리닝, 소모품 교체. 작업 범위와 일정은 상담 후 안내해 드립니다.',16,'관련 서비스 경력 16년입니다.',184,0),
('cleargrout@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','맑은창 줄눈','01080000118',75,'욕실 줄눈과 실리콘 재시공을 반나절에','욕실 줄눈과 실리콘 재시공을 반나절에. 작업 범위와 일정은 상담 후 안내해 드립니다.',9,'관련 서비스 경력 9년입니다.',113,0),
('haneul.wed@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','하늘 웨딩플래너','01080000119',200,'스드메 견적 비교부터 본식 진행까지','스드메 견적 비교부터 본식 진행까지. 작업 범위와 일정은 상담 후 안내해 드립니다.',8,'관련 서비스 경력 8년입니다.',118,0),
('pilates.today@example.com','$2a$10$m12OHqj53WgOH6fIa2QQeOimqaR0t3t9.WiMtzy8KfBvS3UZRVLmu','오늘의 필라테스','01080000120',45,'기구 필라테스와 1:1 자세 교정 수업','기구 필라테스와 1:1 자세 교정 수업. 작업 범위와 일정은 상담 후 안내해 드립니다.',6,'필라테스 개인·소그룹 수업 경력 6년입니다.',174,0);

INSERT INTO seed195_add_services VALUES
('hangyeol.move@example.com',101),
('hangyeol.move@example.com',102),
('hangyeol.move@example.com',103),
('sparkle.home@example.com',102),
('sparkle.home@example.com',101),
('sparkle.home@example.com',103),
('sparkle.home@example.com',104),
('yoon.english@example.com',129),
('yoon.english@example.com',127),
('sori.music@example.com',165),
('sori.music@example.com',166),
('codemate@example.com',139),
('codemate@example.com',138),
('bareun.fix@example.com',109),
('bareun.fix@example.com',107),
('bareun.fix@example.com',108),
('onjip@example.com',113),
('onjip@example.com',112),
('onjip@example.com',114),
('onjip@example.com',115),
('damda.snap@example.com',120),
('damda.snap@example.com',118),
('tidyday@example.com',103),
('tidyday@example.com',101),
('kimcoach@example.com',174),
('doorknob@example.com',108),
('doorknob@example.com',107),
('doorknob@example.com',109),
('haeundae.move@example.com',101),
('haeundae.move@example.com',102),
('career.note@example.com',150),
('career.note@example.com',151),
('forest.art@example.com',167),
('forest.art@example.com',165),
('saegil@example.com',187),
('saegil@example.com',186),
('mindcomma@example.com',190),
('hanbit.car@example.com',184),
('cleargrout@example.com',113),
('cleargrout@example.com',112),
('haneul.wed@example.com',118),
('haneul.wed@example.com',119),
('haneul.wed@example.com',120),
('pilates.today@example.com',174);

INSERT INTO seed195_add_categories VALUES
(101,'이사'),
(102,'입주/집 청소'),
(103,'가전/가구 청소'),
(104,'사업장 청소'),
(107,'가전제품'),
(108,'문/창문'),
(109,'수도/보일러/전기'),
(112,'종합 인테리어'),
(113,'부분 인테리어'),
(114,'벽/천장 시공'),
(115,'바닥 시공'),
(118,'웨딩'),
(119,'뷰티'),
(120,'촬영 및 편집'),
(127,'국내 입시'),
(129,'영어'),
(138,'디자인 외주'),
(139,'개발 외주'),
(150,'취업 준비'),
(151,'창업 준비'),
(165,'악기'),
(166,'음악이론/보컬'),
(167,'미술/드로잉'),
(174,'피트니스'),
(184,'차량 관리'),
(186,'금융'),
(187,'법률'),
(190,'심리');

INSERT INTO seed195_add_regions VALUES
('hangyeol.move@example.com',1,'강남구'),
('hangyeol.move@example.com',1,'서초구'),
('hangyeol.move@example.com',1,'송파구'),
('yoon.english@example.com',1,'마포구'),
('yoon.english@example.com',1,'서대문구'),
('sori.music@example.com',1,'마포구'),
('bareun.fix@example.com',3,'해운대구'),
('bareun.fix@example.com',3,'수영구'),
('tidyday@example.com',5,'연수구'),
('tidyday@example.com',5,'남동구'),
('kimcoach@example.com',1,'강남구'),
('doorknob@example.com',1,'송파구'),
('doorknob@example.com',1,'강동구'),
('career.note@example.com',4,'수성구'),
('forest.art@example.com',5,'연수구'),
('hanbit.car@example.com',3,'해운대구'),
('hanbit.car@example.com',3,'수영구'),
('cleargrout@example.com',1,'마포구'),
('cleargrout@example.com',1,'은평구'),
('pilates.today@example.com',4,'수성구');

INSERT INTO seed195_add_whole VALUES
('sparkle.home@example.com',1),
('onjip@example.com',4),
('haeundae.move@example.com',3),
('mindcomma@example.com',1),
('mindcomma@example.com',9),
('haneul.wed@example.com',3);

CREATE TEMPORARY TABLE seed195_add_first (id BIGINT PRIMARY KEY,name VARCHAR(255)) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
INSERT INTO seed195_add_first VALUES
(1,'서울특별시'),
(3,'부산광역시'),
(4,'대구광역시'),
(5,'인천광역시'),
(9,'경기도');
START TRANSACTION;
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END FROM seed195_add_first d LEFT JOIN first_region fr ON fr.id=d.id WHERE fr.id IS NULL OR BINARY fr.name<>BINARY d.name;
-- 발표용 기존 고수 10명의 이메일과 프로필이 모두 있는 DB에서만 시작합니다. 수동 가입 계정 수는 강제하지 않습니다.
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=10 THEN 1 ELSE 0 END
FROM member m JOIN expert_profile ep ON ep.member_id=m.id
WHERE m.email REGEXP '^expert([1-9]|10)@demo[.]efy[.]example$';
-- 기존 이메일이 탈퇴했거나 사용자 계정·식별 정보 충돌이면 추가하지 않고 전체 중단합니다.
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_add_profiles d JOIN member m ON m.email=d.email LEFT JOIN expert_profile ep ON ep.member_id=m.id
WHERE m.deleted_at IS NOT NULL OR m.role NOT IN ('USER','EXPERT') OR m.phone IS NULL OR BINARY m.phone<>BINARY d.phone OR ep.id IS NULL;
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_add_profiles d LEFT JOIN member same ON same.email=d.email
JOIN member conflict ON conflict.email<>d.email AND (conflict.nickname=d.nickname OR conflict.phone=d.phone)
WHERE same.id IS NULL;
-- IDだけで別のサービス・地域を結び付けないよう実在とカテゴリ名を確認します。
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_add_categories d LEFT JOIN second_category c ON c.id=d.id
WHERE c.id IS NULL OR BINARY c.name<>BINARY d.name;
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_add_regions d WHERE (SELECT COUNT(*) FROM second_region sr WHERE sr.first_region_id=d.first_id AND BINARY sr.name=BINARY d.name)<>1;
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_add_whole d LEFT JOIN first_region fr ON fr.id=d.first_id WHERE fr.id IS NULL;
INSERT INTO seed195_add_pending SELECT d.email FROM seed195_add_profiles d WHERE NOT EXISTS (SELECT 1 FROM member m WHERE m.email=d.email);
INSERT INTO member (email,password,nickname,phone,role,created_at,token_version)
SELECT d.email,d.password,d.nickname,d.phone,'EXPERT',DATE_SUB(CURRENT_TIMESTAMP,INTERVAL d.age_days DAY),0
FROM seed195_add_profiles d JOIN seed195_add_pending n ON n.email=d.email;
SELECT 'members_added' AS item,ROW_COUNT() AS affected;
INSERT INTO expert_profile (member_id,short_intro,description,career_years,career_description,primary_category_id,is_nationwide)
SELECT m.id,d.short_intro,d.description,d.career_years,d.career_description,d.primary_category_id,d.is_nationwide
FROM seed195_add_profiles d JOIN seed195_add_pending n ON n.email=d.email JOIN member m ON m.email=d.email;
INSERT INTO expert_profile_services (expert_profile_id,services_id)
SELECT ep.id,d.category_id FROM seed195_add_services d JOIN seed195_add_pending n ON n.email=d.email JOIN member m ON m.email=d.email JOIN expert_profile ep ON ep.member_id=m.id;
INSERT INTO expert_profile_regions (expert_profile_id,regions_id)
SELECT ep.id,sr.id FROM seed195_add_regions d JOIN seed195_add_pending n ON n.email=d.email JOIN member m ON m.email=d.email
JOIN expert_profile ep ON ep.member_id=m.id JOIN second_region sr ON sr.first_region_id=d.first_id AND BINARY sr.name=BINARY d.name;
INSERT INTO expert_profile_whole_regions (expert_profile_id,whole_regions_id)
SELECT ep.id,d.first_id FROM seed195_add_whole d JOIN seed195_add_pending n ON n.email=d.email JOIN member m ON m.email=d.email JOIN expert_profile ep ON ep.member_id=m.id;
INSERT INTO credit_wallet (member_id,balance,created_at,updated_at)
SELECT m.id,20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP FROM seed195_add_pending n JOIN member m ON m.email=n.email;
-- 이번에 추가한 대상의 프로필·대표 서비스·지역·지갑을 COMMIT 전에 검사합니다.
INSERT INTO seed195_add_guard SELECT CASE WHEN COUNT(*)=0 THEN 1 ELSE 0 END
FROM seed195_add_pending n JOIN member m ON m.email=n.email LEFT JOIN expert_profile ep ON ep.member_id=m.id
LEFT JOIN credit_wallet w ON w.member_id=m.id
WHERE ep.id IS NULL OR w.id IS NULL OR NOT EXISTS (SELECT 1 FROM expert_profile_services s WHERE s.expert_profile_id=ep.id AND s.services_id=ep.primary_category_id)
 OR (ep.is_nationwide=FALSE AND NOT EXISTS (SELECT 1 FROM expert_profile_regions rg WHERE rg.expert_profile_id=ep.id) AND NOT EXISTS (SELECT 1 FROM expert_profile_whole_regions wr WHERE wr.expert_profile_id=ep.id));
COMMIT;
-- 새 계정만 추가한 상태입니다. 이어서 account-photo-update.sql로 확인된 사진을 연결합니다.
SELECT COUNT(*) AS added_accounts FROM seed195_add_pending;
DROP TEMPORARY TABLE seed195_add_guard,seed195_add_profiles,seed195_add_pending,seed195_add_services,seed195_add_categories,seed195_add_regions,seed195_add_whole,seed195_add_first;
