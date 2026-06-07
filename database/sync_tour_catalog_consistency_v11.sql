USE webdulich_db;

START TRANSACTION;

-- Dong bo nhom tour de filter va thong ke giao dien dung chung mot cau truc.
-- Script nay khong dung toi JSON recommendation/model logic; chi chuan hoa catalog trong bang properties.

UPDATE properties
SET type = 'Tour Tây Nguyên',
    city = COALESCE(NULLIF(city, ''), 'Đà Lạt'),
    status = COALESCE(NULLIF(status, ''), 'Đang mở bán')
WHERE model_version = 'v11'
  AND model_destination_key = 'da_lat';

UPDATE properties
SET type = 'Tour miền Nam',
    city = COALESCE(NULLIF(city, ''), 'Phan Thiết / Mũi Né'),
    status = COALESCE(NULLIF(status, ''), 'Đang mở bán')
WHERE model_version = 'v11'
  AND model_destination_key = 'phan_thiet';

UPDATE properties
SET type = 'Tour miền Nam',
    city = COALESCE(NULLIF(city, ''), 'Vũng Tàu'),
    status = COALESCE(NULLIF(status, ''), 'Đang mở bán')
WHERE model_version = 'v11'
  AND model_destination_key = 'vung_tau';

UPDATE properties
SET type = 'Tour Tây Nguyên'
WHERE city IN ('Đà Lạt', 'Buôn Ma Thuột')
  AND (model_version IS NULL OR model_version <> 'v11');

UPDATE properties
SET type = 'Tour miền Nam'
WHERE city IN ('Vũng Tàu', 'Phan Thiết', 'Phan Thiết / Mũi Né', 'Cần Thơ', 'Cà Mau', 'An Giang', 'Tây Ninh', 'Cần Giờ')
  AND (model_version IS NULL OR model_version <> 'v11');

UPDATE properties
SET type = 'Tour miền Bắc'
WHERE city IN ('Hà Nội', 'Sapa', 'Hạ Long', 'Ninh Bình', 'Mộc Châu', 'Hà Giang', 'Cao Bằng', 'Pù Luông')
  AND (model_version IS NULL OR model_version <> 'v11');

UPDATE properties
SET type = 'Tour miền Trung'
WHERE city IN ('Đà Nẵng', 'Huế', 'Quảng Bình', 'Quy Nhơn')
  AND (model_version IS NULL OR model_version <> 'v11');

UPDATE properties
SET type = 'Tour biển đảo'
WHERE city IN ('Phú Quốc', 'Nha Trang', 'Côn Đảo')
  AND (model_version IS NULL OR model_version <> 'v11');

UPDATE properties
SET status = 'Đang mở bán'
WHERE status IS NULL OR status = '';

UPDATE properties
SET parking = COALESCE(parking, 20),
    area = COALESCE(area, 100),
    bedrooms = COALESCE(bedrooms, 1),
    bathrooms = COALESCE(bathrooms, 0),
    year_built = COALESCE(year_built, 2026)
WHERE type <> 'Lịch trình tự tạo';

SELECT type, COUNT(*) AS total
FROM properties
GROUP BY type
ORDER BY total DESC, type;

SELECT COUNT(*) AS invalid_tours
FROM properties
WHERE type IS NULL OR type = ''
   OR status IS NULL OR status = ''
   OR city IS NULL OR city = ''
   OR location IS NULL OR location = ''
   OR agent_id IS NULL;

COMMIT;
