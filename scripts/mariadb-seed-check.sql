-- 시드 적용 직후 실행합니다. count 행은 기대값과 일치하고 violations 행은 모두 0이어야 합니다.
SELECT 'members' AS item, 50 AS expected, COUNT(*) AS actual FROM member
UNION ALL SELECT 'admins', 1, COUNT(*) FROM member WHERE role='ADMIN'
UNION ALL SELECT 'users_with_withdraw', 6, COUNT(*) FROM member WHERE role='USER' AND email NOT IN ('jian.choi@example.com', 'weekendwalk@example.com', 'dohyun.park@example.com', 'mocha.cup@example.com', 'sua.lee@example.com', 'clear.today@example.com', 'jimin.yoon@example.com', 'star.collector@example.com', 'hajun.jung@example.com', 'greencloud@example.com', 'seojin.han@example.com', 'slow.afternoon@example.com', 'dal.kong@example.com')
UNION ALL SELECT 'bookmark_demo_users', 13, COUNT(*) FROM member WHERE role='USER' AND email IN ('jian.choi@example.com', 'weekendwalk@example.com', 'dohyun.park@example.com', 'mocha.cup@example.com', 'sua.lee@example.com', 'clear.today@example.com', 'jimin.yoon@example.com', 'star.collector@example.com', 'hajun.jung@example.com', 'greencloud@example.com', 'seojin.han@example.com', 'slow.afternoon@example.com', 'dal.kong@example.com')
UNION ALL SELECT 'experts', 30, COUNT(*) FROM expert_profile
UNION ALL SELECT 'first_categories', 11, COUNT(*) FROM first_category
UNION ALL SELECT 'second_categories', 108, COUNT(*) FROM second_category
UNION ALL SELECT 'first_regions', 16, COUNT(*) FROM first_region
UNION ALL SELECT 'second_regions', 268, COUNT(*) FROM second_region
UNION ALL SELECT 'requests', 18, COUNT(*) FROM service_request
UNION ALL SELECT 'answers', 72, COUNT(*) FROM request_answer
UNION ALL SELECT 'quotes', 27, COUNT(*) FROM quote
UNION ALL SELECT 'reservations', 27, COUNT(*) FROM credit_reservation
UNION ALL SELECT 'used_credits', 7, COUNT(*) FROM credit_history
UNION ALL SELECT 'posts', 15, COUNT(*) FROM post
UNION ALL SELECT 'comments', 42, COUNT(*) FROM post_comment
UNION ALL SELECT 'bookmarks', 78, COUNT(*) FROM bookmark
UNION ALL SELECT 'rooms', 7, COUNT(*) FROM chat_room
UNION ALL SELECT 'messages', 30, COUNT(*) FROM chat_message;

SELECT 'post_like_count' AS violations, COUNT(*) AS actual FROM post p
WHERE p.like_count <> (SELECT COUNT(*) FROM post_like l WHERE l.post_id=p.id)
UNION ALL SELECT 'comment_like_count', COUNT(*) FROM post_comment c
WHERE c.like_count <> (SELECT COUNT(*) FROM post_comment_like l WHERE l.comment_id=c.id)
UNION ALL SELECT 'post_comment_count', COUNT(*) FROM post p
WHERE p.comment_count <> (SELECT COUNT(*) FROM post_comment c WHERE c.post_id=p.id AND c.deleted_at IS NULL)
UNION ALL SELECT 'cross_region_code', COUNT(*) FROM first_region f JOIN second_region s ON f.code=s.code
UNION ALL SELECT 'unnormalized_name_key', SUM(bad) FROM (
    SELECT COUNT(*) bad FROM first_category WHERE BINARY name_key<>BINARY LOWER(TRIM(name)) OR BINARY name<>BINARY TRIM(name)
    UNION ALL SELECT COUNT(*) FROM second_category WHERE BINARY name_key<>BINARY LOWER(TRIM(name)) OR BINARY name<>BINARY TRIM(name)
    UNION ALL SELECT COUNT(*) FROM first_region WHERE BINARY name_key<>BINARY LOWER(TRIM(name)) OR BINARY name<>BINARY TRIM(name)
    UNION ALL SELECT COUNT(*) FROM second_region WHERE BINARY name_key<>BINARY LOWER(TRIM(name)) OR BINARY name<>BINARY TRIM(name)
) keys_check
UNION ALL SELECT 'missing_primary_service', COUNT(*) FROM expert_profile ep
WHERE NOT EXISTS (SELECT 1 FROM expert_profile_services s WHERE s.expert_profile_id=ep.id AND s.services_id=ep.primary_category_id)
UNION ALL SELECT 'quote_service_mismatch', COUNT(*) FROM quote q JOIN service_request r ON r.id=q.service_request_id
WHERE NOT EXISTS (SELECT 1 FROM expert_profile_services s WHERE s.expert_profile_id=q.expert_id AND s.services_id=r.second_category_id)
UNION ALL SELECT 'quote_region_mismatch', COUNT(*) FROM quote q JOIN service_request r ON r.id=q.service_request_id
JOIN expert_profile ep ON ep.id=q.expert_id JOIN second_region sr ON sr.id=r.second_region_id
WHERE ep.is_nationwide=FALSE
AND NOT EXISTS (SELECT 1 FROM expert_profile_regions er WHERE er.expert_profile_id=ep.id AND er.regions_id=sr.id)
AND NOT EXISTS (SELECT 1 FROM expert_profile_whole_regions ew WHERE ew.expert_profile_id=ep.id AND ew.whole_regions_id=sr.first_region_id)
UNION ALL SELECT 'wallet_balance', COUNT(*) FROM credit_wallet w
JOIN member wm ON wm.id=w.member_id
WHERE w.balance <> (CASE WHEN wm.email = 'expert@example.com' THEN 10 WHEN wm.email IN ('hangyeol.move@example.com', 'sparkle.home@example.com', 'yoon.english@example.com', 'sori.music@example.com', 'codemate@example.com', 'bareun.fix@example.com', 'onjip@example.com', 'damda.snap@example.com', 'tidyday@example.com', 'kimcoach@example.com', 'doorknob@example.com', 'haeundae.move@example.com', 'career.note@example.com', 'forest.art@example.com', 'saegil@example.com', 'mindcomma@example.com', 'hanbit.car@example.com', 'cleargrout@example.com', 'haneul.wed@example.com', 'pilates.today@example.com') THEN 20 ELSE 50 END) + COALESCE((SELECT SUM(h.amount) FROM credit_history h WHERE h.credit_wallet_id=w.id),0)
OR w.balance < COALESCE((SELECT SUM(r.amount) FROM credit_reservation r WHERE r.credit_wallet_id=w.id AND r.reservation_status='RESERVED'),0)
UNION ALL SELECT 'quote_reservation', COUNT(*) FROM quote q
LEFT JOIN credit_reservation r ON r.quote_id=q.id
LEFT JOIN expert_profile ep ON ep.id=q.expert_id
LEFT JOIN credit_wallet w ON w.id=r.credit_wallet_id
WHERE r.id IS NULL OR r.amount<>2 OR w.member_id<>ep.member_id
OR r.reservation_status<>CASE WHEN q.quote_status='REQUESTED' THEN 'USED' ELSE 'RESERVED' END
UNION ALL SELECT 'requested_history', COUNT(*) FROM quote q
WHERE q.quote_status='REQUESTED' AND NOT EXISTS (
    SELECT 1 FROM credit_history h JOIN credit_wallet w ON w.id=h.credit_wallet_id
    JOIN expert_profile ep ON ep.member_id=w.member_id
    WHERE h.quote_id=q.id AND h.history_type='USE' AND h.amount=-2 AND ep.id=q.expert_id)
UNION ALL SELECT 'room_participants', COUNT(*) FROM chat_room cr
JOIN quote q ON q.id=cr.quote_id JOIN service_request sr ON sr.id=q.service_request_id
JOIN expert_profile ep ON ep.id=q.expert_id
WHERE q.quote_status<>'REQUESTED'
OR (SELECT COUNT(*) FROM chat_participant cp WHERE cp.chat_room_id=cr.id)<>2
OR NOT EXISTS (SELECT 1 FROM chat_participant cp WHERE cp.chat_room_id=cr.id AND cp.member_id=sr.member_id)
OR NOT EXISTS (SELECT 1 FROM chat_participant cp WHERE cp.chat_room_id=cr.id AND cp.member_id=ep.member_id)
UNION ALL SELECT 'message_sender', COUNT(*) FROM chat_message cm
WHERE NOT EXISTS (SELECT 1 FROM chat_participant cp WHERE cp.chat_room_id=cm.chat_room_id AND cp.member_id=cm.sender_id)
UNION ALL SELECT 'room_preview', COUNT(*) FROM chat_room cr
WHERE cr.last_message_preview<>(SELECT cm.content FROM chat_message cm WHERE cm.chat_room_id=cr.id ORDER BY cm.created_at DESC, cm.id DESC LIMIT 1)
OR cr.last_message_at<>(SELECT MAX(cm.created_at) FROM chat_message cm WHERE cm.chat_room_id=cr.id)
UNION ALL SELECT 'withdraw_activity', SUM(n) FROM (
    SELECT COUNT(*) n FROM service_request WHERE member_id=21
    UNION ALL SELECT COUNT(*) FROM expert_profile WHERE member_id=21
    UNION ALL SELECT COUNT(*) FROM post WHERE member_id=21
    UNION ALL SELECT COUNT(*) FROM post_comment WHERE member_id=21
    UNION ALL SELECT COUNT(*) FROM bookmark WHERE member_id=21
    UNION ALL SELECT COUNT(*) FROM chat_participant WHERE member_id=21
) withdraw_check;

-- 홈 예시 고수의 사진과 작업 사진 연결 검사
SELECT 'profile_photos' AS item, 20 AS expected, COUNT(*) AS actual FROM member m JOIN file f ON m.image_id=f.id WHERE f.storage_path LIKE 'seed/expert-portraits/%'
UNION ALL SELECT 'home_expert_work_images', 16, COUNT(*)
FROM work_image wi JOIN expert_profile ep ON wi.expert_profile_id=ep.id
JOIN member m ON m.id=ep.member_id
WHERE m.email REGEXP '^profile1(0[1-9]|1[0-9]|20)@example[.]com$';
SELECT 'image_owner' AS violations, COUNT(*) AS actual FROM work_image wi JOIN expert_profile ep ON wi.expert_profile_id=ep.id JOIN file f ON wi.file_id=f.id WHERE ep.member_id<>f.uploader_id;

SELECT 'all_profile_photos' AS item, 30 AS expected, COUNT(*) AS actual FROM expert_profile ep JOIN member m ON ep.member_id=m.id JOIN file f ON m.image_id=f.id AND f.uploader_id=m.id AND f.visibility='PUBLIC' AND f.deleted_at IS NULL;
SELECT 'all_work_images' AS item, 32 AS expected, COUNT(*) AS actual FROM work_image;
SELECT 'post_image_owner' AS violations, COUNT(*) AS actual FROM post_images pi JOIN post p ON pi.post_id=p.id JOIN file f ON pi.file_id=f.id WHERE p.member_id<>f.uploader_id OR f.visibility<>'PUBLIC' OR f.deleted_at IS NOT NULL;
