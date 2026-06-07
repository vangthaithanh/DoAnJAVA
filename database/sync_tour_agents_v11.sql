USE webdulich_db;

START TRANSACTION;

-- Dong bo tu van vien cho cac tour import tu model va tour con thieu agent_id.
-- Script nay co the chay lai nhieu lan: khong insert trung agent neu email da ton tai.

INSERT INTO agents (full_name, role, email, phone, avatar_url, bio, facebook_url, twitter_url, linkedin_url)
SELECT
  'Phạm Bảo Ngọc',
  'Tư vấn tour Đà Lạt - Tây Nguyên',
  'dalat@webdulich.vn',
  '0903 777 101',
  'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80',
  'Phụ trách tư vấn các tour Đà Lạt, lịch trình nghỉ dưỡng, tham quan thắng cảnh và trải nghiệm nông trại.',
  '#',
  '#',
  '#'
WHERE NOT EXISTS (
  SELECT 1 FROM agents WHERE email = 'dalat@webdulich.vn'
);

INSERT INTO agents (full_name, role, email, phone, avatar_url, bio, facebook_url, twitter_url, linkedin_url)
SELECT
  'Võ Minh Hải',
  'Tư vấn tour Phan Thiết - Mũi Né',
  'phanthiet@webdulich.vn',
  '0903 777 202',
  'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80',
  'Phụ trách tư vấn các tour biển Phan Thiết, Mũi Né, nghỉ dưỡng resort và lịch trình gia đình.',
  '#',
  '#',
  '#'
WHERE NOT EXISTS (
  SELECT 1 FROM agents WHERE email = 'phanthiet@webdulich.vn'
);

INSERT INTO agents (full_name, role, email, phone, avatar_url, bio, facebook_url, twitter_url, linkedin_url)
SELECT
  'Đặng Gia Hân',
  'Tư vấn tour Vũng Tàu - Hồ Tràm',
  'vungtau@webdulich.vn',
  '0903 777 303',
  'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=500&q=80',
  'Phụ trách tư vấn các tour Vũng Tàu, Hồ Tràm, Bình Châu, nghỉ dưỡng biển và tour ngắn ngày.',
  '#',
  '#',
  '#'
WHERE NOT EXISTS (
  SELECT 1 FROM agents WHERE email = 'vungtau@webdulich.vn'
);

SET @agent_da_lat = (
  SELECT id FROM agents WHERE email = 'dalat@webdulich.vn' ORDER BY id LIMIT 1
);
SET @agent_phan_thiet = (
  SELECT id FROM agents WHERE email = 'phanthiet@webdulich.vn' ORDER BY id LIMIT 1
);
SET @agent_vung_tau = (
  SELECT id FROM agents WHERE email = 'vungtau@webdulich.vn' ORDER BY id LIMIT 1
);
SET @agent_general = COALESCE(
  (SELECT id FROM agents WHERE email = 'lan@webdulich.vn' ORDER BY id LIMIT 1),
  @agent_da_lat,
  @agent_phan_thiet,
  @agent_vung_tau
);

UPDATE properties
SET agent_id = CASE model_destination_key
  WHEN 'da_lat' THEN @agent_da_lat
  WHEN 'phan_thiet' THEN @agent_phan_thiet
  WHEN 'vung_tau' THEN @agent_vung_tau
  ELSE @agent_general
END
WHERE agent_id IS NULL
  AND recommendation_enabled = 1
  AND model_version = 'v11';

UPDATE properties
SET agent_id = @agent_vung_tau
WHERE agent_id IS NULL
  AND (
    LOWER(COALESCE(city, '')) LIKE '%vũng tàu%'
    OR LOWER(COALESCE(location, '')) LIKE '%vũng tàu%'
    OR LOWER(COALESCE(title, '')) LIKE '%vũng tàu%'
    OR LOWER(COALESCE(location, '')) LIKE '%hồ tràm%'
    OR LOWER(COALESCE(title, '')) LIKE '%hồ tràm%'
  );

UPDATE properties
SET agent_id = @agent_general
WHERE agent_id IS NULL;

SELECT COUNT(*) AS tours_without_agent
FROM properties
WHERE agent_id IS NULL;

SELECT a.id, a.full_name, COUNT(p.id) AS assigned_tours
FROM agents a
LEFT JOIN properties p ON p.agent_id = a.id
GROUP BY a.id, a.full_name
ORDER BY a.id;

COMMIT;
