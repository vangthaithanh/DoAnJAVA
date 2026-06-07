USE webdulich_db;

-- Expected after running import_model_tours_v10.sql: 31 imported model tours.
SELECT COUNT(*) AS recommendation_enabled_tour_count
FROM properties
WHERE recommendation_enabled = 1;

-- Expected: no rows.
SELECT model_ma_tour, COUNT(*) AS duplicate_count
FROM properties
WHERE model_ma_tour IS NOT NULL
GROUP BY model_ma_tour
HAVING COUNT(*) > 1;

-- Expected: one row for each sample model code below.
SELECT id, model_ma_tour, title, model_source, recommendation_enabled
FROM properties
WHERE model_ma_tour IN (
  '1af2ccf64ad3',
  '3d291bbbe7a1',
  'faee8260fa6d'
)
ORDER BY model_ma_tour;
