param(
    [string]$ModelRoot = (Join-Path $PSScriptRoot '..\src\main\resources\recommendation_model_v11'),
    [string]$OutputPath = (Join-Path $PSScriptRoot 'import_model_tours_v11.sql')
)

$ErrorActionPreference = 'Stop'
$invariant = [System.Globalization.CultureInfo]::InvariantCulture
$destinations = @('da_lat', 'phan_thiet', 'vung_tau')
$destinationNames = @{
    da_lat = 'Đà Lạt'
    phan_thiet = 'Phan Thiết / Mũi Né'
    vung_tau = 'Vũng Tàu'
}

function ConvertTo-SqlText([object]$Value) {
    if ($null -eq $Value) {
        return 'NULL'
    }
    $text = [string]$Value
    $text = $text.Replace("'", "''").Replace("`r", ' ').Replace("`n", ' ')
    return "'$text'"
}

function ConvertTo-SqlJson([object]$Value) {
    return ConvertTo-SqlText (ConvertTo-Json -InputObject $Value -Compress -Depth 20)
}

function ConvertTo-SqlPrice([object]$Value) {
    if ($null -eq $Value) {
        return '0.00'
    }
    return ([decimal]$Value).ToString('0.00', $invariant)
}

function ConvertTo-SqlDuration([object]$Value) {
    if ($null -eq $Value) {
        return '0'
    }
    return [string][int][Math]::Ceiling([double]$Value)
}

$rows = [System.Collections.Generic.List[string]]::new()
foreach ($destinationKey in $destinations) {
    $destinationPath = Join-Path $ModelRoot $destinationKey
    $serviceCatalog = @{}
    foreach ($service in @(Get-Content (Join-Path $destinationPath 'services.json') -Raw | ConvertFrom-Json)) {
        $serviceCatalog[$service.service_key] = $service.service_label
    }

    $servicesByTour = @{}
    $serviceTransactions = Get-Content (Join-Path $destinationPath 'transactions_services.json') -Raw |
        ConvertFrom-Json -AsHashtable
    foreach ($transaction in $serviceTransactions.Values) {
        $services = [System.Collections.Generic.List[object]]::new()
        foreach ($serviceKey in $transaction.services.Keys) {
            if ($null -ne $transaction.services[$serviceKey] -and [double]$transaction.services[$serviceKey] -gt 0) {
                $services.Add([ordered]@{
                    key = $serviceKey
                    label = $serviceCatalog[$serviceKey]
                })
            }
        }
        $servicesByTour[$transaction.ma_tour] = @($services)
    }

    foreach ($tour in @(Get-Content (Join-Path $destinationPath 'tours.json') -Raw | ConvertFrom-Json)) {
        $departure = if ($null -eq $tour.noi_khoi_hanh -or [string]::IsNullOrWhiteSpace($tour.noi_khoi_hanh)) {
            'chưa công bố'
        } else {
            $tour.noi_khoi_hanh
        }
        $days = ConvertTo-SqlDuration $tour.so_ngay
        $nights = ConvertTo-SqlDuration $tour.so_dem
        $description = "Tour model v11: $($tour.tieu_de). Thời lượng: $days ngày, $nights đêm. Khởi hành: $departure."
        $services = if ($servicesByTour.ContainsKey($tour.ma_tour)) {
            $servicesByTour[$tour.ma_tour]
        } else {
            @()
        }
        $rows.Add("  ($(ConvertTo-SqlText $tour.ma_tour), $(ConvertTo-SqlText $tour.nguon), $(ConvertTo-SqlText $tour.url), $(ConvertTo-SqlText $tour.tieu_de), $(ConvertTo-SqlText $destinationKey), $(ConvertTo-SqlText $destinationNames[$destinationKey]), $(ConvertTo-SqlPrice $tour.gia_tu), $days, $nights, $(ConvertTo-SqlText $departure), $(ConvertTo-SqlJson @($tour.places)), $(ConvertTo-SqlJson @($services)), $(ConvertTo-SqlText $description))")
    }
}

$values = $rows -join ",`n"
$sql = @"
USE webdulich_db;

-- Generated from recommendation_model_v11. Preserve editorial fields and insert only missing tours.
SET @schema_name = DATABASE();

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_ma_tour'),
  'SELECT ''properties.model_ma_tour already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_ma_tour VARCHAR(64) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_source'),
  'SELECT ''properties.model_source already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_source VARCHAR(80) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_url'),
  'SELECT ''properties.model_url already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_url VARCHAR(500) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'recommendation_enabled'),
  'SELECT ''properties.recommendation_enabled already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN recommendation_enabled TINYINT(1) NOT NULL DEFAULT 0'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_destination_key'),
  'SELECT ''properties.model_destination_key already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_destination_key VARCHAR(64) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_version'),
  'SELECT ''properties.model_version already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_version VARCHAR(20) NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_places'),
  'SELECT ''properties.model_places already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_places JSON NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema = @schema_name AND table_name = 'properties' AND column_name = 'model_services'),
  'SELECT ''properties.model_services already exists'' AS info',
  'ALTER TABLE properties ADD COLUMN model_services JSON NULL'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema = @schema_name AND table_name = 'properties' AND index_name = 'uk_properties_model_ma_tour'),
  'SELECT ''uk_properties_model_ma_tour already exists'' AS info',
  IF(
    EXISTS(SELECT model_ma_tour FROM properties WHERE model_ma_tour IS NOT NULL GROUP BY model_ma_tour HAVING COUNT(*) > 1),
    'SELECT ''Skip unique index: duplicate model_ma_tour values exist'' AS warning',
    'CREATE UNIQUE INDEX uk_properties_model_ma_tour ON properties(model_ma_tour)'
  )
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

DROP TEMPORARY TABLE IF EXISTS model_tours_v11;
CREATE TEMPORARY TABLE model_tours_v11 (
  model_ma_tour VARCHAR(64) PRIMARY KEY,
  model_source VARCHAR(80),
  model_url VARCHAR(500),
  title TEXT NOT NULL,
  destination_key VARCHAR(64),
  destination_name VARCHAR(120),
  price DECIMAL(15,2) NOT NULL,
  days_count INT,
  nights_count INT,
  departure TEXT,
  places JSON,
  services JSON,
  description TEXT
);

INSERT INTO model_tours_v11 (
  model_ma_tour, model_source, model_url, title, destination_key, destination_name,
  price, days_count, nights_count, departure, places, services, description
) VALUES
$values
;

-- Keep old rows for history, but do not recommend codes absent from this snapshot.
UPDATE properties existing
LEFT JOIN model_tours_v11 model ON model.model_ma_tour = existing.model_ma_tour
SET existing.recommendation_enabled = 0
WHERE existing.model_version = 'v11'
  AND existing.model_ma_tour IS NOT NULL
  AND model.model_ma_tour IS NULL;

-- Refresh model-owned metadata without replacing editorial title, price or images.
UPDATE properties existing
JOIN model_tours_v11 model ON model.model_ma_tour = existing.model_ma_tour
SET existing.model_source = model.model_source,
    existing.model_url = model.model_url,
    existing.model_destination_key = model.destination_key,
    existing.model_version = 'v11',
    existing.model_places = model.places,
    existing.model_services = model.services,
    existing.description = IF(existing.description LIKE 'Tour model v10:%' OR existing.description LIKE 'Tour model v11:%', model.description, existing.description),
    existing.year_built = COALESCE(existing.year_built, 2026),
    existing.recommendation_enabled = 1;

-- Existing UI uses bedrooms/bathrooms as tour days/nights.
INSERT INTO properties (
  title, price, image_url, gallery_image_one, gallery_image_two, gallery_image_three,
  location, status, type, city, bedrooms, bathrooms, year_built, description, featured,
  model_ma_tour, model_source, model_url, recommendation_enabled,
  model_destination_key, model_version, model_places, model_services
)
SELECT
  LEFT(model.title, 150),
  model.price,
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  model.destination_name,
  'Đang mở bán',
  CONCAT('Tour ', model.destination_name),
  model.destination_name,
  model.days_count,
  model.nights_count,
  2026,
  model.description,
  0,
  model.model_ma_tour,
  model.model_source,
  model.model_url,
  1,
  model.destination_key,
  'v11',
  model.places,
  model.services
FROM model_tours_v11 model
LEFT JOIN properties existing ON existing.model_ma_tour = model.model_ma_tour
WHERE existing.id IS NULL;

SELECT model_destination_key, COUNT(*) AS imported_tours
FROM properties
WHERE recommendation_enabled = 1 AND model_version = 'v11'
GROUP BY model_destination_key
ORDER BY model_destination_key;

DROP TEMPORARY TABLE model_tours_v11;
"@

Set-Content -LiteralPath $OutputPath -Value $sql -Encoding utf8
Write-Output "Generated $OutputPath with $($rows.Count) tours."
