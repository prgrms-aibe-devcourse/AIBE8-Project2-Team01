-- 배포 전에 기존 DB에 NULL 허용 컬럼을 추가한다.
ALTER TABLE expert_profile ADD COLUMN IF NOT EXISTS created_at DATETIME(6) NULL;

-- 기존 프로필의 빈 등록일만 회원 가입일로 채운다.
UPDATE expert_profile ep
JOIN member m ON m.id = ep.member_id
SET ep.created_at = m.created_at
WHERE ep.created_at IS NULL;

SELECT COUNT(*) AS missing_created_at FROM expert_profile WHERE created_at IS NULL;
