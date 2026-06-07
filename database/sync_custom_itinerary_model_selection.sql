USE webdulich_db;

SET @schema_name = DATABASE();

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'model_destination_key'),
  'SELECT ''custom_itineraries.model_destination_key already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN model_destination_key VARCHAR(64) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'selected_places'),
  'SELECT ''custom_itineraries.selected_places already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN selected_places TEXT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'selected_services'),
  'SELECT ''custom_itineraries.selected_services already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN selected_services TEXT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'selected_property_id'),
  'SELECT ''custom_itineraries.selected_property_id already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN selected_property_id BIGINT NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'selected_model_ma_tour'),
  'SELECT ''custom_itineraries.selected_model_ma_tour already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN selected_model_ma_tour VARCHAR(64) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND column_name = 'selected_tour_title'),
  'SELECT ''custom_itineraries.selected_tour_title already exists'' AS info',
  'ALTER TABLE custom_itineraries ADD COLUMN selected_tour_title VARCHAR(255) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema = @schema_name AND table_name = 'custom_itineraries' AND index_name = 'idx_custom_itineraries_selected_property'),
  'SELECT ''idx_custom_itineraries_selected_property already exists'' AS info',
  'CREATE INDEX idx_custom_itineraries_selected_property ON custom_itineraries(selected_property_id)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SELECT
  COUNT(*) AS total_itineraries,
  SUM(selected_places IS NOT NULL AND selected_places <> '') AS itineraries_with_places,
  SUM(selected_services IS NOT NULL AND selected_services <> '') AS itineraries_with_services,
  SUM(selected_property_id IS NOT NULL) AS itineraries_with_selected_tour
FROM custom_itineraries;
