USE webdulich_db;

-- Expected snapshot total: 147 enabled v11 tours.
SELECT model_destination_key, COUNT(*) AS imported_tours
FROM properties
WHERE recommendation_enabled = 1 AND model_version = 'v11'
GROUP BY model_destination_key
ORDER BY model_destination_key;

-- Expected: 147.
SELECT COUNT(*) AS recommendation_enabled_v11_tour_count
FROM properties
WHERE recommendation_enabled = 1 AND model_version = 'v11';

-- Expected: no rows.
SELECT model_ma_tour, COUNT(*) AS duplicate_count
FROM properties
WHERE model_ma_tour IS NOT NULL
GROUP BY model_ma_tour
HAVING COUNT(*) > 1;

-- Samples include newly enriched tours from each destination.
SELECT
  id,
  model_ma_tour,
  model_destination_key,
  model_version,
  JSON_LENGTH(model_places) AS place_count,
  JSON_LENGTH(model_services) AS service_count
FROM properties
WHERE model_ma_tour IN (
  'de53bd06d241',
  '69d8ba8aaa8c',
  '1a881fb2ab33',
  'f6691ef7efab',
  'f927cf13209d',
  '0f1ec6a4a72b'
)
ORDER BY model_destination_key;
