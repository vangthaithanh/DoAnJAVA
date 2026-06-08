USE webdulich_db;

SET @schema_name = DATABASE();

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'status'),
  'SELECT ''custom_itineraries.status already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT ''PENDING_REVIEW'''
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

UPDATE custom_itineraries
SET status = 'PENDING_REVIEW'
WHERE status IS NULL
   OR status = ''
   OR UPPER(status) = 'NEW'
   OR status = 'Đang xét duyệt'
   OR UPPER(status) = 'DANG_XET_DUYET';

UPDATE custom_itineraries
SET status = 'ADVISED'
WHERE status = 'Đã tư vấn'
   OR UPPER(status) = 'DA_TU_VAN';

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND index_name = 'idx_custom_itineraries_status'),
  'SELECT ''idx_custom_itineraries_status already exists'' AS info',
  'CREATE INDEX idx_custom_itineraries_status ON custom_itineraries(status)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT status, COUNT(*) AS total
FROM custom_itineraries
GROUP BY status
ORDER BY status;
