-- 기존 Cloudtype DB 추가 적용 전후에 실행하는 읽기 전용 검사입니다.
-- 전체 계정 수를 37로 강제하지 않습니다. 사용자 생성 데이터가 추가될 수 있습니다.
-- 실행 전 결과를 저장해 기존 계정·요청·견적·지갑 수와 상태가 유지되는지 비교합니다.
SELECT 'members' AS item,COUNT(*) AS actual FROM member
UNION ALL SELECT 'profiles',COUNT(*) FROM expert_profile
UNION ALL SELECT 'requests',COUNT(*) FROM service_request
UNION ALL SELECT 'quotes',COUNT(*) FROM quote
UNION ALL SELECT 'wallets',COUNT(*) FROM credit_wallet;
SELECT request_status,COUNT(*) AS actual FROM service_request GROUP BY request_status;
SELECT quote_status,COUNT(*) AS actual FROM quote GROUP BY quote_status;

-- 발표용 고수 30명. 최종 배정 30개의 실제 Storage 경로와 대조합니다.
SELECT m.id AS member_id,m.email,m.nickname,ep.id AS profile_id,c.name AS primary_service,
       m.image_id,f.original_name,f.storage_path,f.size,f.uploader_id,f.visibility,f.deleted_at
FROM member m JOIN expert_profile ep ON ep.member_id=m.id
JOIN second_category c ON c.id=ep.primary_category_id LEFT JOIN file f ON f.id=m.image_id
WHERE m.email REGEXP '^expert([1-9]|10)@demo[.]efy[.]example$'
   OR m.email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$'
ORDER BY ep.id;

-- 결과가 있으면 누락·소유권·공개 상태를 확인합니다.
SELECT m.email,'missing_or_invalid_profile_photo' AS violation
FROM member m JOIN expert_profile ep ON ep.member_id=m.id LEFT JOIN file f ON f.id=m.image_id
WHERE m.deleted_at IS NULL
  AND (m.email REGEXP '^expert([1-9]|10)@demo[.]efy[.]example$' OR m.email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$')
  AND (f.id IS NULL OR f.uploader_id<>m.id OR f.visibility<>'PUBLIC' OR f.deleted_at IS NOT NULL);

-- 파일 이름·크기가 같은 사진의 중복 후보입니다. 실제 원본 중복은 파일 SHA-256으로 별도 확인합니다.
SELECT f.original_name,f.size,COUNT(*) AS linked_profiles,GROUP_CONCAT(m.email ORDER BY m.email) AS accounts
FROM expert_profile ep JOIN member m ON m.id=ep.member_id JOIN file f ON f.id=m.image_id
WHERE m.deleted_at IS NULL GROUP BY f.original_name,f.size HAVING COUNT(*)>1;

-- SQL에서는 BCrypt 형식만 확인합니다. 비밀번호 12341234 일치는 자동 테스트와 로그인으로 검증합니다.
SELECT email FROM member WHERE email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$'
AND (CHAR_LENGTH(password)<>60 OR password NOT REGEXP '^[$]2[aby][$][0-9]{2}[$][./A-Za-z0-9]{53}$');
SELECT COUNT(*) AS sample_experts_present FROM member
WHERE email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$' AND deleted_at IS NULL;

SELECT m.email,m.nickname,ep.short_intro,ep.description,ep.career_description,
       ep.primary_category_id,GROUP_CONCAT(s.services_id) AS services
FROM member m JOIN expert_profile ep ON ep.member_id=m.id
LEFT JOIN expert_profile_services s ON s.expert_profile_id=ep.id
WHERE m.email='profile120@example.com' GROUP BY m.id,ep.id;
-- 프로필120은 서비스174를 유지하므로 기존 매칭 참조를 변경하지 않습니다.
SELECT 'pilates_quotes' AS item,COUNT(*) AS actual FROM quote q JOIN expert_profile ep ON q.expert_id=ep.id
JOIN member m ON m.id=ep.member_id WHERE m.email='profile120@example.com';

-- 최종 배정과 다른 사진은 수동 변경일 수도 있으므로 결과를 직접 확인합니다.
SELECT d.email,m.id,f.storage_path AS actual_path,d.path AS expected_path
FROM (
SELECT 'expert1@demo.efy.example' AS email,'seed/presentation-profiles/d09431ee697649ae6a059995d9b03eb0069d86a3cd788b5e83c28ac8be9c1cee.png' AS path
UNION ALL SELECT 'expert2@demo.efy.example' AS email,'seed/presentation-profiles/816310cf78c8685bc578edcce6dbfa93006ca553ec39927d11adc18f3329e688.png' AS path
UNION ALL SELECT 'expert3@demo.efy.example' AS email,'seed/presentation-profiles/c002942b04c1c93a16833549018ac701f37ab755b019724cc8939833b041f29c.png' AS path
UNION ALL SELECT 'expert4@demo.efy.example' AS email,'seed/presentation-profiles/3a55a6d8ce7cd546f333debd38cd2c8b406bf5293b134b08011246c756c56a02.png' AS path
UNION ALL SELECT 'expert5@demo.efy.example' AS email,'seed/presentation-profiles/4446b1ab54cd891731dd0f5b2b3f52296ba34bb249d1da53b5568d48e780340e.png' AS path
UNION ALL SELECT 'expert6@demo.efy.example' AS email,'seed/presentation-profiles/e550aedb1375b19f2a9a3adf6ac87333c3368d11938456a6ac2932837773be23.png' AS path
UNION ALL SELECT 'expert7@demo.efy.example' AS email,'seed/presentation-profiles/ac87b492952a1e806cfd07c8b34d28036c3eaac9882f10ed109eb4c1ee12cd4e.png' AS path
UNION ALL SELECT 'expert8@demo.efy.example' AS email,'seed/presentation-profiles/eb98873dad0c9294e1e20be39ad20bfe1221e64fb07bab9d478455f71f412833.png' AS path
UNION ALL SELECT 'expert9@demo.efy.example' AS email,'seed/presentation-profiles/ba4360ddbbd45220d6b76ae0bf036a745fc9e5209880534584c5b2733d112b68.png' AS path
UNION ALL SELECT 'expert10@demo.efy.example' AS email,'seed/presentation-profiles/710d56a3e6311981449bab84ec340139a77e7a6d6813c2d00282c72e2a65c032.png' AS path
UNION ALL SELECT 'profile101@example.com' AS email,'seed/expert-portraits/fac79094f403daa49b653ca7165425239106e33de7c46cbf9943e0afb06594f2.jpg' AS path
UNION ALL SELECT 'profile102@example.com' AS email,'seed/expert-portraits/3a3b36b5e571b7df3f0cf9f556eeeb547c68adde6d77b47b79f3cdb8ec47b31b.jpg' AS path
UNION ALL SELECT 'profile103@example.com' AS email,'seed/expert-portraits/2536fa08e550e96a256fe8288f3d09b17d055949df054a541ef09de97fc31421.jpg' AS path
UNION ALL SELECT 'profile104@example.com' AS email,'seed/expert-portraits/d01c2618fe2ab4a4b3e5a4788a830ea6cd49651d876de7fea68e776e75397b43.jpg' AS path
UNION ALL SELECT 'profile105@example.com' AS email,'seed/expert-portraits/8706e1fa2982c383584d4f8f45cc67a854e272943b2fdbbb4a1948b697389102.jpg' AS path
UNION ALL SELECT 'profile106@example.com' AS email,'seed/expert-portraits/eedb73b924cddac497cf0106675414f0b5b0e659b6da32a9501cab29e2977a31.jpg' AS path
UNION ALL SELECT 'profile107@example.com' AS email,'seed/expert-portraits/91d49f183011359c7be3278ef88d06a0e2b85ae4f75d28985065fad7c180abfe.jpg' AS path
UNION ALL SELECT 'profile108@example.com' AS email,'seed/expert-portraits/bf61066aa0ba76c96797534f2ac306cfdc8e32a7c14b7adc4e052be7744cee4b.jpg' AS path
UNION ALL SELECT 'profile109@example.com' AS email,'seed/expert-portraits/2c6b30f4271e6fe0de0876b2c58147e797c16951f31db4eb2497a6d68ee8efb6.jpg' AS path
UNION ALL SELECT 'profile110@example.com' AS email,'seed/expert-portraits/9e0a013410c73d2600f30f7764042c664597f65dc75fec784602f011101dc86b.jpg' AS path
UNION ALL SELECT 'profile111@example.com' AS email,'seed/expert-portraits/730c36a224c75ad6716687b5e3412b10018f5ac5fc95c388df95f7d52852bf55.jpg' AS path
UNION ALL SELECT 'profile112@example.com' AS email,'seed/expert-portraits/8236bea6ade5759f4a467ae33aad1dfb8b0a8497fa0925396e2bea41ef4c56a7.jpg' AS path
UNION ALL SELECT 'profile113@example.com' AS email,'seed/expert-portraits/3dbb32e8d3411c87a6e6a0b69329bfd6beb96b11cc057a9e906c73418b43fa09.jpg' AS path
UNION ALL SELECT 'profile114@example.com' AS email,'seed/expert-portraits/cd044537eb5b2ec44fa95ecd26f478034191e1add421270a26e57fb771200d50.jpg' AS path
UNION ALL SELECT 'profile115@example.com' AS email,'seed/expert-portraits/61a80e03ceb23a12806b88a229bb551f8049954dbca07e60d4deee5dac2123d2.jpg' AS path
UNION ALL SELECT 'profile116@example.com' AS email,'seed/expert-portraits/6a49ba904e011b7af641619aff3198364b1274e647ab0068c9396331f6ec88ab.jpg' AS path
UNION ALL SELECT 'profile117@example.com' AS email,'seed/expert-portraits/7e4aaf88726a0226a2685864377b24821ee4b03de50f4279e7e22745f2ea4a46.jpg' AS path
UNION ALL SELECT 'profile118@example.com' AS email,'seed/expert-portraits/51d1c0e5c1940e4304e24b58465dbfc1defd5480104064407d108fd84c73b6d1.jpg' AS path
UNION ALL SELECT 'profile119@example.com' AS email,'seed/expert-portraits/23fc54320fddad75b686b37b70e15606ce25e3bed139a01d0765ba581b96465d.jpg' AS path
UNION ALL SELECT 'profile120@example.com' AS email,'seed/expert-portraits/f3f7685df4a9d41458c4e1aac7d34397e6ba4bb301d6d63a6527c02968caeb9b.jpg' AS path
) d LEFT JOIN member m ON m.email=d.email AND m.deleted_at IS NULL
LEFT JOIN file f ON f.id=m.image_id
WHERE m.id IS NULL OR f.id IS NULL OR BINARY f.storage_path<>BINARY d.path;
