USE webdulich_db;

-- Add model integration columns only when they are absent.
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

UPDATE properties
SET recommendation_enabled = 0
WHERE recommendation_enabled IS NULL;

ALTER TABLE properties
  MODIFY COLUMN recommendation_enabled TINYINT(1) NOT NULL DEFAULT 0;

-- Add the unique index only when it is absent and existing data has no duplicate model code.
SET @sql = IF(
  EXISTS(SELECT 1 FROM information_schema.statistics WHERE table_schema = @schema_name AND table_name = 'properties' AND index_name = 'uk_properties_model_ma_tour')
  OR EXISTS(SELECT model_ma_tour FROM properties WHERE model_ma_tour IS NOT NULL GROUP BY model_ma_tour HAVING COUNT(*) > 1),
  'SELECT ''Skipped unique index: it already exists or duplicate model_ma_tour values were found'' AS info',
  'ALTER TABLE properties ADD UNIQUE INDEX uk_properties_model_ma_tour (model_ma_tour)'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Existing UI uses bedrooms/bathrooms as tour days/nights. Missing prices remain visible as 0.
INSERT INTO properties (
  title, price, image_url, gallery_image_one, gallery_image_two, gallery_image_three,
  location, status, type, city, bedrooms, bathrooms, description, featured,
  model_ma_tour, model_source, model_url, recommendation_enabled
)
SELECT
  model_tours.title,
  model_tours.price,
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80',
  'Đà Lạt',
  'Đang mở bán',
  'Tour Đà Lạt',
  'Đà Lạt',
  model_tours.bedrooms,
  model_tours.bathrooms,
  model_tours.description,
  0,
  model_tours.model_ma_tour,
  model_tours.model_source,
  model_tours.model_url,
  1
FROM (  SELECT '2145b06b59fc' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-xe-dap-da-lat' AS model_url, 'TOUR XE ĐẠP ĐÀ LẠT DẤU CHÂN YERSIN' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR XE ĐẠP ĐÀ LẠT DẤU CHÂN YERSIN. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Địa điểm đón.' AS description
  UNION ALL SELECT '23e3ee949e36' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-leo-nui-langbiang-da-lat' AS model_url, 'TOUR LEO NÚI LANGBIANG ĐÀ LẠT' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR LEO NÚI LANGBIANG ĐÀ LẠT. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Đón & trả khách: Tại địa điểm khách yêu cầu trong trung tâm thành phố Đà Lạt.' AS description
  UNION ALL SELECT '311eedfc2bac' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-ngoai-thanh-da-lat' AS model_url, 'TOUR NGOẠI THÀNH ĐÀ LẠT TRONG NGÀY' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR NGOẠI THÀNH ĐÀ LẠT TRONG NGÀY. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Từ 08:00 đến 08:30.' AS description
  UNION ALL SELECT '3954355044af' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-da-lat-thu-nho' AS model_url, 'TOUR MỘT THOÁNG ĐÀ LẠT THU NHỎ' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR MỘT THOÁNG ĐÀ LẠT THU NHỎ. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Từ 08:00 đến 08:30.' AS description
  UNION ALL SELECT '3ba34eea4a80' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-cong-chieng-da-lat' AS model_url, 'TOUR CỒNG CHIÊNG ĐÀ LẠT HÀNG ĐÊM' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR CỒNG CHIÊNG ĐÀ LẠT HÀNG ĐÊM. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Đón & trả khách: Tại địa điểm khách yêu cầu trong trung tâm thành phố Đà Lạt..' AS description
  UNION ALL SELECT '683ada3345d5' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-lang-nghe' AS model_url, 'TOUR LÀNG NGHỀ ĐÀ LẠT NHẪN BẠC CHURU' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR LÀNG NGHỀ ĐÀ LẠT NHẪN BẠC CHURU. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Từ 08:00 đến 08:30.' AS description
  UNION ALL SELECT '7ba145b35d22' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-tham-quan-da-lat' AS model_url, 'TOUR THAM QUAN THÀNH PHỐ ĐÀ LẠT' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR THAM QUAN THÀNH PHỐ ĐÀ LẠT. Thời lượng: 1 ngày, 0 đêm. Khởi hành: ►Thời gian đón khách: Từ 08:00 đến 08:30 sáng.' AS description
  UNION ALL SELECT '7fe58e0ee0b3' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-thac-nuoc-da-lat' AS model_url, 'TOUR KHÁM PHÁ CÁC THÁC NƯỚC ĐÀ LẠT' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR KHÁM PHÁ CÁC THÁC NƯỚC ĐÀ LẠT. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Khách tự quyết định, mặc định là 08:00 sáng..' AS description
  UNION ALL SELECT 'b83b45d63e85' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-da-lat-2-ngay-1-dem' AS model_url, 'TOUR ĐÀ LẠT 2 NGÀY 1 ĐÊM TIẾT KIỆM' AS title, 850000.00 AS price, 2 AS bedrooms, 1 AS bathrooms, 'Tour model v10: TOUR ĐÀ LẠT 2 NGÀY 1 ĐÊM TIẾT KIỆM. Thời lượng: 2 ngày, 1 đêm. Khởi hành: ►Thời gian & địa điểm đón khách: Trong thành phố Đà Lạt..' AS description
  UNION ALL SELECT 'cfd8d5d356b5' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-van-hoa-da-lat' AS model_url, 'TOUR VĂN HÓA VÀ KIẾN TRÚC ĐÀ LẠT' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR VĂN HÓA VÀ KIẾN TRÚC ĐÀ LẠT. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Từ 08:00 đến 08:30.' AS description
  UNION ALL SELECT 'de53bd06d241' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-trang-mat-da-lat' AS model_url, 'TOUR DU LỊCH TRĂNG MẬT ĐÀ LẠT' AS title, 0.00 AS price, 3 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR DU LỊCH TRĂNG MẬT ĐÀ LẠT. Thời lượng: 3 ngày, 0 đêm. Khởi hành: ►Thời gian đón khách: Theo yêu cầu của khách.' AS description
  UNION ALL SELECT 'e413384c7524' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-tron-goi-da-lat' AS model_url, 'TOUR ĐÀ LẠT 4 NGÀY 3 ĐÊM TRỌN GÓI' AS title, 0.00 AS price, 4 AS bedrooms, 3 AS bathrooms, 'Tour model v10: TOUR ĐÀ LẠT 4 NGÀY 3 ĐÊM TRỌN GÓI. Thời lượng: 4 ngày, 3 đêm. Khởi hành: ►Thời gian đón khách: Do khách yêu cầu..' AS description
  UNION ALL SELECT 'f6668e11d42b' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-thien-nhien-da-lat' AS model_url, 'TOUR THẮNG CẢNH THIÊN NHIÊN ĐÀ LẠT' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR THẮNG CẢNH THIÊN NHIÊN ĐÀ LẠT. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Từ 08:00 đến 08:30.' AS description
  UNION ALL SELECT 'f9b3e022820d' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-nong-trai-va-nha-vuon-da-lat' AS model_url, 'TOUR NÔNG TRẠI VÀ NHÀ VƯỜN ĐÀ LẠT' AS title, 0.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: TOUR NÔNG TRẠI VÀ NHÀ VƯỜN ĐÀ LẠT. Thời lượng: 1 ngày, 0 đêm. Khởi hành: Thời gian đón khách: Khách tự quyết định, mặc định là 08:00 sáng..' AS description
  UNION ALL SELECT 'faee8260fa6d' AS model_ma_tour, 'dalattrip' AS model_source, 'https://www.dalattrip.com/dulich/tour-da-lat-3-ngay-2-dem' AS model_url, 'TOUR ĐÀ LẠT 3 NGÀY 2 ĐÊM TRỌN VẸN' AS title, 1400000.00 AS price, 3 AS bedrooms, 2 AS bathrooms, 'Tour model v10: TOUR ĐÀ LẠT 3 NGÀY 2 ĐÊM TRỌN VẸN. Thời lượng: 3 ngày, 2 đêm. Khởi hành: ►Thời gian đón khách: Trong thành phố Đà Lạt..' AS description
  UNION ALL SELECT '1af2ccf64ad3' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-3n2d-don-tu-nha-trang-kham-pha-xu-so-ngan-hoa/4440' AS model_url, 'Tour Đà Lạt 3N2Đ (Đón Từ Đà Lạt): Khám Phá Xứ Sở Ngàn Hoa' AS title, 4175000.00 AS price, 3 AS bedrooms, 2 AS bathrooms, 'Tour model v10: Tour Đà Lạt 3N2Đ (Đón Từ Đà Lạt): Khám Phá Xứ Sở Ngàn Hoa. Thời lượng: 3 ngày, 2 đêm. Khởi hành: Đà Lạt.' AS description
  UNION ALL SELECT '1bae7a51f8cf' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-1n-vuot-thac-datanla-da-lat/1278' AS model_url, 'Tour Đà Lạt Nửa Ngày: Thác Datanla - Đường Hầm Đất Sét - Crazy House' AS title, 482000.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: Tour Đà Lạt Nửa Ngày: Thác Datanla - Đường Hầm Đất Sét - Crazy House. Thời lượng: 0.5 ngày, 0 đêm. Khởi hành: chưa công bố.' AS description
  UNION ALL SELECT '3d291bbbe7a1' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-2n1d-thanh-pho-ngan-hoa/3075' AS model_url, 'Tour Đà Lạt 2N1Đ (Đón Từ Đà Lạt): Thành Phố Ngàn Hoa' AS title, 2810000.00 AS price, 2 AS bedrooms, 1 AS bathrooms, 'Tour model v10: Tour Đà Lạt 2N1Đ (Đón Từ Đà Lạt): Thành Phố Ngàn Hoa. Thời lượng: 2 ngày, 1 đêm. Khởi hành: Đà Lạt.' AS description
  UNION ALL SELECT '5603d5ab707a' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-3n3d-hcm-thi-tran-iyashi-langbiang-land-ga-nuong-dap-lu/2249' AS model_url, 'Tour Đà Lạt 3N3Đ: HCM - Thị Trấn Iyashi - Langbiang Land - Gà Nướng Đập Lu' AS title, 3250000.00 AS price, 3 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Tour Đà Lạt 3N3Đ: HCM - Thị Trấn Iyashi - Langbiang Land - Gà Nướng Đập Lu. Thời lượng: 3 ngày, 3 đêm. Khởi hành: chưa công bố.' AS description
  UNION ALL SELECT '6325e6ad1fdb' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-1n-song-ao-cac-dia-diem-hot/1322' AS model_url, 'Tour Đà Lạt Trong Ngày: Checkin Các Điểm Nổi Bật' AS title, 747000.00 AS price, 1 AS bedrooms, 0 AS bathrooms, 'Tour model v10: Tour Đà Lạt Trong Ngày: Checkin Các Điểm Nổi Bật. Thời lượng: 1 ngày, 0 đêm. Khởi hành: chưa công bố.' AS description
  UNION ALL SELECT '85424eece132' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-3n3d-hcm-thi-tran-iyashi-vuon-chau-au-lac-hu-co-tran-fresh-garden/122' AS model_url, 'Tour Đà Lạt 3N3Đ: HCM - Thị Trấn Iyashi - Vườn Châu Âu - Lạc Hư Cổ Trấn - Fresh Garden' AS title, 3750000.00 AS price, 3 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Tour Đà Lạt 3N3Đ: HCM - Thị Trấn Iyashi - Vườn Châu Âu - Lạc Hư Cổ Trấn - Fresh Garden. Thời lượng: 3 ngày, 3 đêm. Khởi hành: chưa công bố.' AS description
  UNION ALL SELECT 'b3960517d9e9' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-3n2d-hcm-puppy-farm-doi-che-phuoc-lac-free-day/601' AS model_url, 'Tour Lễ Đà Lạt 3N2Đ: HCM - Puppy Farm - Đồi Chè Phước Lạc - Free Day' AS title, 2570000.00 AS price, 3 AS bedrooms, 2 AS bathrooms, 'Tour model v10: Tour Lễ Đà Lạt 3N2Đ: HCM - Puppy Farm - Đồi Chè Phước Lạc - Free Day. Thời lượng: 3 ngày, 2 đêm. Khởi hành: Hồ Chí Minh.' AS description
  UNION ALL SELECT 'b46e8cf6266e' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-3n3d-hcm-samten-hills-langbiang-land-trai-mat-vuon-dia-dang/452' AS model_url, 'Tour Đà Lạt 3N3Đ: HCM - Samten Hills - LangBiang Land - Trại Mát - Vườn Địa Đàng' AS title, 2570000.00 AS price, 3 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Tour Đà Lạt 3N3Đ: HCM - Samten Hills - LangBiang Land - Trại Mát - Vườn Địa Đàng. Thời lượng: 3 ngày, 3 đêm. Khởi hành: Hồ Chí Minh.' AS description
  UNION ALL SELECT 'bbc024de2a2a' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-tet-da-lat-3n3d-hcm-happy-hill-kdl-mongo-land-trai-mat-tu-vien-bat-nha/296' AS model_url, 'Tour Đà Lạt 3N3Đ: HCM - Happy Hill - KDL Mongo land - Trại Mát - Tu Viện Bát Nhã' AS title, 2570000.00 AS price, 3 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Tour Đà Lạt 3N3Đ: HCM - Happy Hill - KDL Mongo land - Trại Mát - Tu Viện Bát Nhã. Thời lượng: 3 ngày, 3 đêm. Khởi hành: Hồ Chí Minh.' AS description
  UNION ALL SELECT 'cb96e649a3da' AS model_ma_tour, 'ivivu' AS model_source, 'https://www.ivivu.com/du-lich/tour-da-lat-3n3d-hcm-langbiang-land-fresh-garden-thien-duong-san-may/1896' AS model_url, 'Tour Đà Lạt 3N3Đ: HCM - Langbiang Land - Fresh Garden - Thiên Đường Săn Mây' AS title, 2786000.00 AS price, 3 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Tour Đà Lạt 3N3Đ: HCM - Langbiang Land - Fresh Garden - Thiên Đường Săn Mây. Thời lượng: 3 ngày, 3 đêm. Khởi hành: Hồ Chí Minh.' AS description
  UNION ALL SELECT '469a52c42070' AS model_ma_tour, 'saigontourist' AS model_source, 'https://saigontourist.net/tour/dinh-bao-dai-lang-art-cafe-thien-vien-truc-lam-buffet-rau-leguda-nha-tho-domain-de-marie-vuon-dau' AS model_url, 'Dinh Bảo Đại – Buffet Rau Banchou – Nông Trại Cổ Tích - Lặng Art Café – Gallery La Chocotea - Vườn Dâu Đà Lạt' AS title, 1790000.00 AS price, 3 AS bedrooms, 2 AS bathrooms, 'Tour model v10: Dinh Bảo Đại – Buffet Rau Banchou – Nông Trại Cổ Tích - Lặng Art Café – Gallery La Chocotea - Vườn Dâu Đà Lạt. Thời lượng: 3 ngày, 2 đêm. Khởi hành: TP Hồ Chí Minh.' AS description
  UNION ALL SELECT '898180eb5691' AS model_ma_tour, 'saigontourist' AS model_source, 'https://saigontourist.net/tour/thac-bobla-langbiang-land-buffet-rau-banchou-novadreams-royal-garden-samten-hills' AS model_url, 'Thác Bobla – Langbiang Land - Buffet Rau Banchou - Novadreams Royal Garden - Samten Hills' AS title, 2290000.00 AS price, 4 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Thác Bobla – Langbiang Land - Buffet Rau Banchou - Novadreams Royal Garden - Samten Hills. Thời lượng: 4 ngày, 3 đêm. Khởi hành: TP Hồ Chí Minh.' AS description
  UNION ALL SELECT '8aa721af2943' AS model_ma_tour, 'saigontourist' AS model_source, 'https://saigontourist.net/tour/da-lat--2478' AS model_url, 'Đà Lạt' AS title, 1995000.00 AS price, 4 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Đà Lạt. Thời lượng: 4 ngày, 3 đêm. Khởi hành: Cần Thơ.' AS description
  UNION ALL SELECT 'b1ed1d2ec409' AS model_ma_tour, 'saigontourist' AS model_source, 'https://saigontourist.net/tour/bao-tang-madame-de-da-lat-doi-che-cau-dat-chua-linh-phuoc-domain-de-maire-thac-datanla-2581' AS model_url, 'Bảo Tàng Madame De Da Lat - Đồi Chè Cầu Đất - Chùa Linh Phước - Domain De Maire - Thác Datanla' AS title, 0.00 AS price, 4 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Bảo Tàng Madame De Da Lat - Đồi Chè Cầu Đất - Chùa Linh Phước - Domain De Maire - Thác Datanla. Thời lượng: 4 ngày, 3 đêm. Khởi hành: TP Hồ Chí Minh.' AS description
  UNION ALL SELECT 'c589176b2272' AS model_ma_tour, 'saigontourist' AS model_source, 'https://saigontourist.net/tour/thac-datanla-nong-trai-co-tich-buffet-rau-banchou-doi-che-cau-dat-chua-linh-phuoc' AS model_url, 'Bảo Tàng Madame De – Nông Trại Cổ Tích Đồi Chè Cầu Đất - Chùa Linh Phước - Nhà Thờ Domain De Maire - Gallery La Chocotea' AS title, 2140000.00 AS price, 4 AS bedrooms, 3 AS bathrooms, 'Tour model v10: Bảo Tàng Madame De – Nông Trại Cổ Tích Đồi Chè Cầu Đất - Chùa Linh Phước - Nhà Thờ Domain De Maire - Gallery La Chocotea. Thời lượng: 4 ngày, 3 đêm. Khởi hành: TP Hồ Chí Minh.' AS description
  UNION ALL SELECT 'c8afdd98270a' AS model_ma_tour, 'saigontourist' AS model_source, 'https://saigontourist.net/tour/-duong-sinh-dat-lanh-da-lat-mong-mo-3007' AS model_url, 'Dưỡng Sinh Đất Lành – Đà Lạt Mộng Mơ' AS title, 0.00 AS price, 3 AS bedrooms, 2 AS bathrooms, 'Tour model v10: Dưỡng Sinh Đất Lành – Đà Lạt Mộng Mơ. Thời lượng: 3 ngày, 2 đêm. Khởi hành: TP Hồ Chí Minh.' AS description
) AS model_tours
LEFT JOIN properties existing ON existing.model_ma_tour = model_tours.model_ma_tour
WHERE existing.id IS NULL;
