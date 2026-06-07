USE webdulich_db;

CREATE TABLE IF NOT EXISTS custom_itineraries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title VARCHAR(180) NOT NULL,
  destination_text VARCHAR(255),
  total_days INT NOT NULL,
  budget DECIMAL(15,2),
  travel_style VARCHAR(100),
  note TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET @schema_name = DATABASE();

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'status'),
  'SELECT ''custom_itineraries.status already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT ''NEW'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'assigned_agent_id'),
  'SELECT ''custom_itineraries.assigned_agent_id already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN assigned_agent_id BIGINT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'admin_note'),
  'SELECT ''custom_itineraries.admin_note already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN admin_note TEXT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'updated_at'),
  'SELECT ''custom_itineraries.updated_at already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN updated_at DATETIME NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE custom_itineraries
SET status = COALESCE(NULLIF(status, ''), 'NEW'),
    updated_at = COALESCE(updated_at, created_at, NOW())
WHERE status IS NULL
   OR status = ''
   OR updated_at IS NULL;

CREATE TABLE IF NOT EXISTS custom_itinerary_days (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  itinerary_id BIGINT NOT NULL,
  day_number INT NOT NULL,
  title VARCHAR(180),
  FOREIGN KEY (itinerary_id) REFERENCES custom_itineraries(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS custom_itinerary_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  day_id BIGINT NOT NULL,
  time_text VARCHAR(50),
  place_name VARCHAR(180),
  activity TEXT,
  estimated_cost DECIMAL(15,2),
  FOREIGN KEY (day_id) REFERENCES custom_itinerary_days(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SELECT status, COUNT(*) AS total
FROM custom_itineraries
GROUP BY status
ORDER BY status;
