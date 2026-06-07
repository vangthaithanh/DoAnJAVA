# WebDuLich - Tri thức dữ liệu và recommendation model

Tài liệu này mô tả database, dữ liệu tour import, model recommendation v10/v11 và logic hiện tại của API/giao diện.

## Database

Database mặc định:

- Tên DB: `webdulich_db`
- Charset nên dùng: `utf8mb4`
- User cấu hình trong app: `root`
- Password cấu hình trong app: `bonganhhung1`

File cấu hình:

- `src/main/resources/application.properties`

File SQL nền:

- `database/webdulich_mysql.sql`

Script import model:

- `database/import_model_tours_v10.sql`
- `database/import_model_tours_v11.sql`

Script tạo import v11:

- `database/generate_import_model_tours_v11.ps1`

Script kiểm tra:

- `database/test_model_tour_import_check.sql`
- `database/test_model_tour_import_v11_check.sql`

## Schema nền trong `webdulich_mysql.sql`

Script nền có các bảng:

- `users`
- `profiles`
- `agents`
- `properties`
- `destinations`
- `tour_destinations`
- `blog_posts`
- `contact_messages`
- `inquiries`
- `reviews`
- `favorite_tours`
- `custom_itineraries`
- `custom_itinerary_days`
- `custom_itinerary_items`

Không phải bảng nào cũng đã có code Java tương ứng. Bảng đang được Java dùng thật:

- `properties`
- `agents`
- `blog_posts`
- `contact_messages`
- `inquiries`

Bảng có trong SQL nhưng chưa có triển khai Java đầy đủ:

- `users`
- `profiles`
- `destinations`
- `tour_destinations`
- `reviews`
- `favorite_tours`
- `custom_itineraries`
- `custom_itinerary_days`
- `custom_itinerary_items`

## Bảng `properties`

`properties` là bảng tour chính.

Cột nền:

- `id`
- `title`
- `price`
- `image_url`
- `gallery_image_one`
- `gallery_image_two`
- `gallery_image_three`
- `location`
- `status`
- `type`
- `city`
- `bedrooms`
- `bathrooms`
- `parking`
- `area`
- `year_built`
- `description`
- `featured`
- `agent_id`

Cột model đã thêm:

- `model_ma_tour`
- `model_source`
- `model_url`
- `recommendation_enabled`
- `model_destination_key`
- `model_places`
- `model_services`
- `model_version`

Mapping tour model sang `properties`:

- `model_ma_tour` = `ma_tour`
- `title` = `tieu_de`
- `price` = `gia_tu`
- `location/city` = destination name
- `bedrooms` = `so_ngay`
- `bathrooms` = `so_dem`
- `description` = mô tả ngắn tạo từ tour model
- `model_source` = `nguon`
- `model_url` = `url`
- `model_destination_key` = `destination_key`
- `model_version` = `v11`
- `model_places` = JSON array các địa điểm
- `model_services` = JSON array dịch vụ
- `recommendation_enabled` = true

`model_ma_tour` nên unique để chống trùng tour model.

## Dữ liệu hiện tại đã xác nhận

Trạng thái đã xác nhận trong phiên làm việc trước:

- Tổng `properties`: 178.
- Tour model v11 bật recommendation: 147.
- Tất cả 147 tour v11 có `model_services` là JSON array hợp lệ.
- Phân bổ v11:
  - `da_lat`: 75.
  - `phan_thiet`: 32.
  - `vung_tau`: 40.
- `agents`: 3.
- `blog_posts`: 6.
- `contact_messages`: 0 tại thời điểm kiểm tra.
- `inquiries`: 3 tại thời điểm kiểm tra.

Lưu ý: số liệu DB có thể thay đổi sau khi người dùng thao tác thêm. Nếu cần chắc chắn, chạy SQL kiểm tra lại.

## Model artifacts

### v10

Đường dẫn:

- `src/main/resources/recommendation_model/`

File:

- `metadata.json`
- `places.json`
- `place_rules.json`
- `transactions.json`
- `services.json`
- `tours.json`
- `tour_recommendation_index.json`
- `model_schema.md`

Trạng thái:

- Vẫn còn trong project để tham chiếu lịch sử.
- Code hiện tại không dùng v10 làm model chính.

### v11 active

Đường dẫn:

- `src/main/resources/recommendation_model_v11/`

File/folder cấp gốc:

- `metadata_global.json`
- `destinations.json`
- `MODEL_CONTRACT_V11.md`
- `da_lat/`
- `phan_thiet/`
- `vung_tau/`

Mỗi folder destination có:

- `metadata.json`
- `places.json`
- `place_rules.json`
- `place_to_service_stats.json`
- `services.json`
- `service_rules.json`
- `tours.json`
- `tour_recommendation_index.json`
- `transactions_places.json`
- `transactions_services.json`

Metadata global hiện tại:

- `model_version`: `v11`
- `generated_at_utc`: `2026-06-01T20:10:52.986125+00:00`
- `destination_count`: 3
- `ready_destination_count`: 3
- `limited_destination_count`: 0
- `not_ready_destination_count`: 0
- `total_clean_tour_count`: 147
- `total_place_rule_count`: 480
- `total_service_rule_count`: 77

Nguồn dữ liệu model:

- `dalattrip`
- `intour`
- `ivivu`
- `phusitravel`
- `saigontourist`
- `saigontravel`
- `vietfuntravel`
- `vietnambooking`

Known limitations của model:

- Dữ liệu lấy từ tour công khai, không phải hành vi người dùng.
- Catalog công khai có thể chứa biến thể tour gần nhau.
- Không trộn luật giữa các destination.

## Destination v11

| destination_key | Tên | Readiness | Tour sạch | Places | Services | Place rules | Service rules |
| --- | --- | --- | ---: | ---: | ---: | ---: | ---: |
| `da_lat` | Đà Lạt | ready | 75 | 63 | 9 | 316 | 17 |
| `phan_thiet` | Phan Thiết / Mũi Né | ready | 32 | 21 | 9 | 71 | 34 |
| `vung_tau` | Vũng Tàu | ready | 40 | 39 | 9 | 93 | 26 |

Trước đây Phan Thiết/Vũng Tàu từng `not_ready`, nhưng bộ dữ liệu đã được làm giàu; hiện metadata v11 trong project đang là `ready` cho cả 3 destination.

## Recommendation API

Controller:

- `src/main/java/com/example/webdulich/recommendation/RecommendationController.java`

Service:

- `src/main/java/com/example/webdulich/recommendation/RecommendationService.java`

Request:

- `src/main/java/com/example/webdulich/recommendation/RecommendationRequest.java`

Default request:

- `destinationKey`: nếu null/blank thì `da_lat`.
- `selectedPlaces`: nếu null thì list rỗng.
- `selectedServices`: nếu null thì list rỗng.
- `topK`: nếu null thì 5.
- `topK` bị giới hạn bởi `MAX_TOP_K = 20`.

Endpoints:

### `GET /api/recommend/destinations`

Trả danh sách destination từ `destinations.json`.

### `GET /api/recommend/places?destinationKey=da_lat&includeRare=false`

Trả places của destination.

Nếu `includeRare=false`, chỉ trả core places. Nếu destination không có core phù hợp trong một số logic đề xuất, service có thể fallback rare trong kết quả recommendation.

### `GET /api/recommend/services?destinationKey=da_lat`

Trả danh sách services của destination.

### `POST /api/recommend/next-places`

Body mẫu:

```json
{
  "destinationKey": "da_lat",
  "selectedPlaces": ["Hồ Tuyền Lâm"],
  "topK": 5
}
```

### `POST /api/recommend/next-services`

Body mẫu:

```json
{
  "destinationKey": "da_lat",
  "selectedServices": ["hotel"],
  "topK": 5
}
```

### `POST /api/recommend/place-services`

Body mẫu:

```json
{
  "destinationKey": "da_lat",
  "selectedPlaces": ["Hồ Tuyền Lâm"],
  "selectedServices": [],
  "topK": 5
}
```

### `POST /api/recommend/tours`

Body mẫu:

```json
{
  "destinationKey": "da_lat",
  "selectedPlaces": ["Hồ Tuyền Lâm"],
  "selectedServices": ["hotel"],
  "topK": 5
}
```

### `POST /api/recommend/full`

Body mẫu:

```json
{
  "destinationKey": "da_lat",
  "selectedPlaces": ["Hồ Tuyền Lâm"],
  "selectedServices": ["hotel"],
  "topK": 5
}
```

Response full gồm:

- `destination`
- `selectedPlaces`
- `selectedServices`
- `nextPlaces`
- `nextServices`
- `placeServices`
- `recommendedTours`
- `warnings`
- `status`
- `modelReadiness`

## Logic gợi ý địa điểm

Hàm:

- `recommendNextPlaces(destinationKey, selectedPlaces, topK)`

Nếu có đúng 1 selected place đã biết:

1. Dùng `place_rules.json`.
2. Append rule `manh` trước.
3. Append rule `tham_khao` sau.
4. Ưu tiên core candidate.
5. Nếu không có core phù hợp thì mới mở rare candidate.
6. Nếu không có rule thì fallback transaction.

Nếu có nhiều selected places:

1. Dùng `transactions_places.json`.
2. Tìm transaction chứa toàn bộ selected places.
3. Ưu tiên `day_level` nếu có.
4. Nếu không có `day_level`, dùng `tour_level`.
5. Đếm candidate còn lại.
6. Core candidate hiện trước.
7. Nếu không có core candidate thì rare candidate vẫn được hiện.

Luôn loại bỏ địa điểm đã chọn khỏi gợi ý.

## Logic nhãn địa điểm tiếp theo

API trả thêm:

- `recommendation_level`

Quy ước:

- `recommendation_level = manh` -> UI nhãn xanh `Nên chọn`.
- `recommendation_level = tham_khao` -> UI nhãn vàng `Tham khảo`.

Với rule trực tiếp:

- `recommendation_level` lấy từ `rule_level` trong JSON.

Với fallback transaction:

- core candidate -> `recommendation_level = manh`.
- rare candidate -> `recommendation_level = tham_khao`.

Không dùng `place_level` để quyết định nhãn luật. `place_level` chỉ nói địa điểm thuộc nhóm `core` hay `rare` trong catalog.

Lý do: Có trường hợp một địa điểm core nhưng rule từ antecedent hiện tại là `tham_khao`; UI phải hiện `Tham khảo`, không được hiện `Nên chọn`.

## Logic gợi ý dịch vụ tiếp theo

Hàm:

- `recommendNextServices(destinationKey, selectedServices, topK)`

Dữ liệu:

- `service_rules.json`

Input:

- `selectedServices`

Logic:

- Chỉ xét services trong destination hiện tại.
- Dùng service rules theo selected services.
- Append `manh` trước, `tham_khao` sau.
- Không đề xuất lại dịch vụ đã chọn.
- Nếu không đủ rule thì status `insufficient_rules`.

UI cũng dùng `recommendation_level`:

- `manh` -> `Nên chọn`.
- `tham_khao` -> `Tham khảo`.

## Logic dịch vụ theo địa điểm

Hàm:

- `recommendPlaceServices(destinationKey, selectedPlaces, selectedServices, topK)`

Dữ liệu:

- `place_to_service_stats.json`

Input:

- `selectedPlaces`
- `selectedServices`

Logic:

- Lấy stats theo các selected places đã biết.
- Aggregate theo service.
- Không đề xuất lại dịch vụ đã chọn.
- Sort theo:
  - rank confidence level
  - service confidence
  - service coverage rate
  - service order

Response item có các trường:

- `service_key`
- `service_label`
- `matched_tour_count`
- `service_available_count`
- `service_known_count`
- `service_coverage_rate`
- `service_confidence`
- `confidence_level`

## Logic gợi ý tour

Hàm:

- `recommendTours(destinationKey, selectedPlaces, selectedServices, topK)`

Dữ liệu:

- `tours.json`
- `tour_recommendation_index.json`
- `transactions_services.json`
- Bảng DB `properties` để enrich bookable tour

Input:

- destination
- selected places
- selected services

Logic hiện tại:

1. Chỉ xét tour trong destination hiện tại.
2. Tạo recommendation cho từng tour:
   - `matchedPlaces`
   - `missingPlaces`
   - `matchedServices`
   - `missingServices`
   - `tourScore`
   - `recommendationReason`
   - `serviceDataQuality`
3. Chỉ giữ tour exact match:
   - không thiếu selected place nào
   - không thiếu selected service nào
4. Sort theo score giảm dần.
5. Limit theo topK.
6. Enrich với DB bằng `PropertyRepository.findByModelMaTourIn`.

Nếu có property trong DB:

- `propertyId`: id bảng `properties`
- `detailUrl`: `/tours/{propertyId}`
- `bookable`: true
- title/price/duration/source có thể lấy từ DB

Nếu chưa có property trong DB:

- `bookable`: false
- `detailUrl`: URL nguồn nếu có

Yêu cầu đã chốt:

- Tour phù hợp bên phải phải biến mất nếu tour không chứa đủ tiêu chí đã chọn.
- Không giữ tour partial match trong UI khi user đang lọc bằng tiêu chí.

## Frontend `/itinerary`

File JS:

- `src/main/resources/static/assets/js/recommendation.js`

Các vùng UI chính:

- Dropdown destination.
- Chip chọn địa điểm.
- Chip chọn dịch vụ.
- Nút tạo gợi ý.
- Cột `Địa điểm tiếp theo`.
- Cột `Dịch vụ tiếp theo`.
- Cột `Dịch vụ theo địa điểm`.
- Cột `Tour phù hợp`.

Hành vi:

- Đổi destination thì gọi lại places/services và clear kết quả cũ.
- Click chip chọn/bỏ chọn tiêu chí.
- Click gợi ý trong các cột kết quả sẽ thêm gợi ý đó vào tiêu chí và gọi lại API.
- Nếu `bookable=true`, link tour là `Xem tour` tới `/tours/{id}`.
- Nếu `bookable=false`, link là `Xem nguồn tham khảo` tới URL nguồn.

## Import tour v11 vào DB

Script chính:

- `database/import_model_tours_v11.sql`

Script này:

- Thêm cột model nếu thiếu.
- Chống trùng bằng `model_ma_tour`.
- Insert tour thiếu từ v11.
- Không xóa tour cũ.
- Không ghi đè dữ liệu cũ.

Nếu có bộ model mới nhưng vẫn là v11:

1. Copy artifact mới vào `src/main/resources/recommendation_model_v11/`.
2. Không sửa nội dung JSON artifact.
3. Chạy/tạo lại `database/import_model_tours_v11.sql` bằng `generate_import_model_tours_v11.ps1` nếu cần.
4. Chạy SQL import vào MySQL.
5. Restart app để `RecommendationService` load JSON mới.
6. Test API và UI cho cả 3 destination.

Không bắt buộc import nếu chỉ test model JSON; nhưng để `Tour phù hợp` có `bookable=true` và link `/tours/{id}`, cần có row tương ứng trong `properties`.

## Kiểm tra SQL gợi ý

Đếm tour model v11:

```sql
SELECT model_destination_key, COUNT(*) AS total
FROM properties
WHERE recommendation_enabled = 1
  AND model_version = 'v11'
GROUP BY model_destination_key;
```

Kiểm tra trùng `model_ma_tour`:

```sql
SELECT model_ma_tour, COUNT(*) AS total
FROM properties
WHERE model_ma_tour IS NOT NULL
GROUP BY model_ma_tour
HAVING COUNT(*) > 1;
```

Kiểm tra JSON services:

```sql
SELECT COUNT(*) AS invalid_services
FROM properties
WHERE recommendation_enabled = 1
  AND model_version = 'v11'
  AND (model_services IS NULL OR JSON_TYPE(CAST(model_services AS JSON)) <> 'ARRAY');
```

Kiểm tra một mã tour:

```sql
SELECT id, title, model_ma_tour, model_destination_key, recommendation_enabled
FROM properties
WHERE model_ma_tour IN ('VF182', 'VF367');
```

## Test API nên chạy sau khi sửa recommendation

Destinations:

```http
GET http://localhost:8080/api/recommend/destinations
```

Places:

```http
GET http://localhost:8080/api/recommend/places?destinationKey=da_lat
GET http://localhost:8080/api/recommend/places?destinationKey=phan_thiet
GET http://localhost:8080/api/recommend/places?destinationKey=vung_tau
```

Services:

```http
GET http://localhost:8080/api/recommend/services?destinationKey=da_lat
GET http://localhost:8080/api/recommend/services?destinationKey=phan_thiet
GET http://localhost:8080/api/recommend/services?destinationKey=vung_tau
```

Full examples:

```json
{
  "destinationKey": "da_lat",
  "selectedPlaces": ["Hồ Tuyền Lâm"],
  "selectedServices": ["hotel"],
  "topK": 5
}
```

```json
{
  "destinationKey": "phan_thiet",
  "selectedPlaces": ["Biển Mũi Né"],
  "selectedServices": ["transport"],
  "topK": 5
}
```

```json
{
  "destinationKey": "vung_tau",
  "selectedPlaces": ["Ngọn Hải Đăng Vũng Tàu"],
  "selectedServices": ["hotel"],
  "topK": 5
}
```

Điều cần kiểm:

- Response có `modelReadiness`.
- Gợi ý không chứa địa điểm/dịch vụ đã chọn.
- `nextPlaces.recommendations[*].recommendation_level` đúng với luật.
- UI không hiện chữ `Luật mạnh` hoặc `Luật tham khảo`.
- UI nhãn `Nên chọn`/`Tham khảo` đúng.
- `recommendedTours.tours` chỉ gồm tour đủ selected places/services.
- Tour DB có `propertyId`, `detailUrl`, `bookable=true`.

## Test UI nên chạy

URL:

- `http://localhost:8080/itinerary`

Kịch bản:

1. Chọn Đà Lạt, chọn địa điểm/dịch vụ, bấm gợi ý.
2. Click một địa điểm gợi ý, kiểm tra nó được thêm vào tiêu chí và không còn lặp trong gợi ý.
3. Click một dịch vụ gợi ý, kiểm tra nó được thêm vào tiêu chí và không còn lặp.
4. Chọn nhiều tiêu chí đến khi không có tour đủ điều kiện, kiểm tra `Tour phù hợp` rỗng.
5. Chọn Phan Thiết, lặp lại với `Biển Mũi Né`.
6. Chọn Vũng Tàu, lặp lại với `Ngọn Hải Đăng Vũng Tàu`.
7. Bấm `Xem tour`, vào `/tours/{id}`.
8. Gửi form tư vấn và kiểm tra có row trong `inquiries`.

## Những lỗi đã từng xuất hiện và cách tránh

### Dùng `place_level` thay cho rule level

Sai:

- Core place được hiện `Nên chọn` dù rule đang là `tham_khao`.

Đúng:

- UI dùng `recommendation_level`.
- Direct rule: `recommendation_level = rule_level`.
- Transaction fallback: core -> `manh`, rare -> `tham_khao`.

### Giữ tour partial match

Sai:

- User chọn nhiều điểm/dịch vụ nhưng UI vẫn hiện tour thiếu tiêu chí.

Đúng:

- API chỉ trả exact match trong `recommendedTours.tours`.
- Nếu không có tour đủ tiêu chí, status `no_exact_match` và danh sách rỗng.

### Dịch vụ đã chọn vẫn xuất hiện trong gợi ý

Sai:

- `placeServices` hoặc `nextServices` đề xuất lại service đã tick.

Đúng:

- Service phải bị loại nếu nằm trong `selectedServices`.

### Destination có ít/core thiếu thì không hiện rare

Sai:

- Nếu không có core candidate, gợi ý trống dù vẫn có rare candidate hữu ích.

Đúng:

- Ưu tiên core.
- Nếu không có core candidate phù hợp, cho phép rare candidate hiện với nhãn `Tham khảo`.
