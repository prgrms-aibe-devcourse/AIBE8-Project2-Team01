-- 읽기 전용 사전 검사. 실제 Cloudtype 상태를 조회한 뒤 필요한 파일만 선택합니다.
-- seed_base_10과 missing_sample_*는 계정 수 전체가 아니라 지정한 발표용 계정만 집계합니다.
SELECT 'all_members' item,COUNT(*) actual FROM member
UNION ALL SELECT 'all_profiles',COUNT(*) FROM expert_profile
UNION ALL SELECT 'seed_base_10',COUNT(*) FROM member m JOIN expert_profile ep ON ep.member_id=m.id WHERE m.email REGEXP '^expert([1-9]|10)@demo[.]efy[.]example$'
UNION ALL SELECT 'sample_members',COUNT(*) FROM member WHERE email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$';
WITH targets AS (
SELECT 'profile101@example.com' email,'한결이사' nickname,'01080000101' phone
UNION ALL SELECT 'profile102@example.com' email,'반짝홈케어' nickname,'01080000102' phone
UNION ALL SELECT 'profile103@example.com' email,'윤선생 영어' nickname,'01080000103' phone
UNION ALL SELECT 'profile104@example.com' email,'소리공방' nickname,'01080000104' phone
UNION ALL SELECT 'profile105@example.com' email,'코드메이트' nickname,'01080000105' phone
UNION ALL SELECT 'profile106@example.com' email,'바른설비' nickname,'01080000106' phone
UNION ALL SELECT 'profile107@example.com' email,'온집 인테리어' nickname,'01080000107' phone
UNION ALL SELECT 'profile108@example.com' email,'담다 스냅' nickname,'01080000108' phone
UNION ALL SELECT 'profile109@example.com' email,'정리하는 하루' nickname,'01080000109' phone
UNION ALL SELECT 'profile110@example.com' email,'김코치 피트니스' nickname,'01080000110' phone
UNION ALL SELECT 'profile111@example.com' email,'문고리 수리' nickname,'01080000111' phone
UNION ALL SELECT 'profile112@example.com' email,'해운대 이사짐센터' nickname,'01080000112' phone
UNION ALL SELECT 'profile113@example.com' email,'커리어노트' nickname,'01080000113' phone
UNION ALL SELECT 'profile114@example.com' email,'그림숲 아틀리에' nickname,'01080000114' phone
UNION ALL SELECT 'profile115@example.com' email,'새길 노무' nickname,'01080000115' phone
UNION ALL SELECT 'profile116@example.com' email,'마음쉼표 상담' nickname,'01080000116' phone
UNION ALL SELECT 'profile117@example.com' email,'한빛 카케어' nickname,'01080000117' phone
UNION ALL SELECT 'profile118@example.com' email,'맑은창 줄눈' nickname,'01080000118' phone
UNION ALL SELECT 'profile119@example.com' email,'하늘 웨딩플래너' nickname,'01080000119' phone
UNION ALL SELECT 'profile120@example.com' email,'오늘의 필라테스' nickname,'01080000120' phone
)
SELECT d.email,CASE WHEN m.id IS NULL AND collision.id IS NULL THEN '추가 가능'
 WHEN m.id IS NULL THEN '중단: 닉네임·전화번호 충돌'
 WHEN m.deleted_at IS NOT NULL THEN '중단: 탈퇴 계정'
 WHEN m.role NOT IN ('USER','EXPERT') OR m.phone IS NULL OR BINARY m.phone<>BINARY d.phone OR ep.id IS NULL THEN '중단: 기존 이메일의 식별 정보·역할·프로필 확인 필요'
 ELSE '기존 계정 보존' END AS action,m.id,ep.id AS profile_id,m.deleted_at
FROM targets d LEFT JOIN member m ON m.email=d.email LEFT JOIN expert_profile ep ON ep.member_id=m.id
LEFT JOIN member collision ON m.id IS NULL AND collision.email<>d.email AND (collision.nickname=d.nickname OR collision.phone=d.phone)
ORDER BY d.email;
-- 추가 가능 항목이 있을 때만 add-experts.sql을 먼저 실행합니다. 중단 항목은 사용자 확인 전까지 진행하지 않습니다.
-- 20개가 모두 있어도 profile·사진·지갑의 수동 변경은 덮어쓰지 않고 check.sql 결과로 확인합니다.
