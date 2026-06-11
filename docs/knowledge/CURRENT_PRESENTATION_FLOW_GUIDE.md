# WebDuLich - luồng hiện tại để trình bày

Tài liệu này không tổng hợp các thay đổi giao diện nhỏ. Nội dung tập trung vào các luồng nghiệp vụ đã được nối thật trong project: model gợi ý, tour model đưa vào database, giỏ hàng, yêu cầu tư vấn, lịch trình cá nhân, phân công tư vấn viên, tài khoản khách hàng, thanh toán và đánh giá.

## 1. Điểm nhìn tổng quan

Project hiện là web Java Spring Boot + Thymeleaf + MySQL.

Các khối đã nối với nhau:

- Người dùng đăng nhập mới được thao tác với các luồng có dữ liệu cá nhân: tạo lịch trình, thêm giỏ hàng, gửi tư vấn tour, yêu thích.
- Model recommendation v11 chỉ dùng artifact JSON trong classpath, không gọi crawler, không đọc CSV, không phụ thuộc hoàn toàn database.
- Tour model đã được đưa vào bảng `properties`, nên tour gợi ý có thể trở thành tour thật: xem chi tiết, thêm giỏ hàng, thanh toán, đánh giá.
- Lịch trình cá nhân và yêu cầu tư vấn tour/liên hệ là hai luồng riêng.
- Lịch trình cá nhân đi về tư vấn viên id `3`.
- Tư vấn tour từ chi tiết tour đi về nhân viên phụ trách khu vực của tour.
- Liên hệ chung được phát cho 5 tư vấn viên tour, không gửi cho tư vấn viên lịch trình id `3`.
- Khi một tư vấn viên xử lý yêu cầu liên hệ/tour, các assignment còn lại chuyển trạng thái “đồng nghiệp đã xử lý”.

## 2. ID quan trọng trong database

Tư vấn viên trong bảng `agents` hiện tại:

| agent id | Họ tên | Vai trò nghiệp vụ | Email |
|---:|---|---|---|
| 1 | Nguyễn Hoàng Lan | Tư vấn tour miền Trung - miền Nam | `lan@webdulich.vn` |
| 2 | Trần Minh Khang | Tư vấn tour miền Bắc | `khang@webdulich.vn` |
| 3 | Lê Thu Thảo | Tư vấn lịch trình cá nhân | `thao@webdulich.vn` |
| 4 | Phạm Bảo Ngọc | Tư vấn tour Đà Lạt - Tây Nguyên | `dalat@webdulich.vn` |
| 5 | Võ Minh Hải | Tư vấn tour Phan Thiết - Mũi Né | `phanthiet@webdulich.vn` |
| 6 | Đặng Gia Hân | Tư vấn tour Vũng Tàu - Hồ Tràm | `vungtau@webdulich.vn` |

Tài khoản consultant tương ứng trong bảng `users`:

| user id | Email | Tên |
|---:|---|---|
| 14 | `lan@webdulich.vn` | Nguyễn Hoàng Lan |
| 15 | `khang@webdulich.vn` | Trần Minh Khang |
| 16 | `thao@webdulich.vn` | Lê Thu Thảo |
| 17 | `dalat@webdulich.vn` | Phạm Bảo Ngọc |
| 18 | `phanthiet@webdulich.vn` | Võ Minh Hải |
| 19 | `vungtau@webdulich.vn` | Đặng Gia Hân |

Tài khoản admin:

| user id | Email | Tên |
|---:|---|---|
| 3 | `admin@webdulich.vn` | Quản trị viên |

Snapshot dữ liệu tour hiện tại:

- Tổng số tour trong `properties`: `178`.
- Tour có `recommendation_enabled = 1`: `147`.
- Cột khóa nối tour model: `properties.model_ma_tour`.
- Unique index hiện có: `uk_properties_model_ma_tour`.
- File dump database duy nhất hiện nằm ở `database/webdulich_db.sql`.

## 3. Luồng đăng nhập và ràng buộc tài khoản

Các thao tác cần tài khoản đều dùng session `currentUserId`.

Luồng chung:

1. Nếu chưa đăng nhập, controller set `afterLoginRedirect`.
2. Người dùng bị chuyển về `/login`.
3. Đăng nhập thành công thì `AuthController` đọc `afterLoginRedirect` và đưa người dùng quay lại đúng nơi cần thao tác.

Điểm code neo:

- `AuthController.handleLogin`: `src/main/java/com/example/webdulich/controller/AuthController.java`, dòng 31-55.
- `AuthController.consumeSafeRedirect`: dòng 99-111.
- `AuthController.defaultRedirectByRole`: dòng 115-123.
- `/itinerary` chặn chưa đăng nhập: `TravelController`, dòng 28-34.
- `/cart` chặn chưa đăng nhập: `CartController`, dòng 29-33 và dòng 54-63.
- Gửi tư vấn tour chặn chưa đăng nhập: `PropertyController`, dòng 85-100.

Điểm trình bày tốt: ràng buộc tài khoản không chỉ là ẩn nút ở giao diện, mà có kiểm ở controller trước khi ghi dữ liệu.

## 4. Luồng model recommendation v11

### 4.1. Nguồn dữ liệu model

Model được đọc từ classpath:

```text
src/main/resources/recommendation_model_v11/
```

Các destination chính:

- `da_lat`
- `phan_thiet`
- `vung_tau`

Service không đọc CSV, không gọi crawler. Dữ liệu được load một lần khi service khởi động:

- `RecommendationService.MODEL_PATH`: dòng 32.
- `RecommendationService.loadModel()`: dòng 48-62.
- Mỗi destination được load riêng trong `loadDestinationModel`: dòng 387-410.

Artifact quan trọng trong mỗi destination:

- `metadata.json`: readiness.
- `places.json`: địa điểm và `place_level`.
- `services.json`: dịch vụ.
- `place_rules.json`: luật địa điểm tiếp theo.
- `service_rules.json`: luật dịch vụ tiếp theo.
- `place_to_service_stats.json`: dịch vụ thường đi kèm địa điểm.
- `transactions_places.json`: giao tour/transaction theo địa điểm.
- `transactions_services.json`: dịch vụ theo tour.
- `tours.json`: tour model.

### 4.2. API recommendation

Controller chính:

```text
src/main/java/com/example/webdulich/recommendation/RecommendationController.java
```

Endpoint:

| Endpoint | Ý nghĩa | Line |
|---|---|---:|
| `GET /api/recommend/destinations` | danh sách destination + readiness | 23 |
| `GET /api/recommend/places` | địa điểm theo destination, mặc định core | 28 |
| `GET /api/recommend/services` | dịch vụ theo destination | 35 |
| `POST /api/recommend/next-places` | gợi ý địa điểm tiếp theo | 41 |
| `POST /api/recommend/next-services` | gợi ý dịch vụ tiếp theo | 47 |
| `POST /api/recommend/place-services` | dịch vụ thường đi kèm địa điểm | 53 |
| `POST /api/recommend/tours` | tour phù hợp theo địa điểm + dịch vụ | 62 |
| `POST /api/recommend/full` | trả toàn bộ gợi ý cho giao diện | 71 |

DTO request:

```text
src/main/java/com/example/webdulich/recommendation/RecommendationRequest.java
```

Điểm quan trọng:

- `destinationKey` mặc định `da_lat`: dòng 7 và dòng 12-14.
- `selectedPlaces` mặc định `List.of()`: dòng 8 và dòng 20-22.
- `selectedServices` mặc định `List.of()`: dòng 9 và dòng 28-30.
- `topK` mặc định `5`: dòng 10 và dòng 36-38.

### 4.3. Logic gợi ý địa điểm

Hàm chính:

```text
RecommendationService.recommendNextPlaces(...)
```

Line neo: dòng 82-103.

Luồng:

1. Chuẩn hóa `selectedPlaces`.
2. Tách địa điểm đã biết và chưa biết trong model.
3. Nếu chọn đúng 1 địa điểm, ưu tiên `place_rules`.
4. Nếu không có luật, hoặc chọn nhiều địa điểm, dùng transaction chứa toàn bộ địa điểm đã chọn.
5. Không trả lại địa điểm đã chọn.

Ưu tiên rule:

- `recommendNextPlacesFromRules`: dòng 209-218.
- Gọi `appendPlaceRules(..., "manh", false, ...)` trước.
- Sau đó mới gọi `appendPlaceRules(..., "tham_khao", false, ...)`.
- Nếu không có kết quả core thì mới mở rare: dòng 214-217.

Ý nghĩa trình bày: hệ thống không gợi ý mù. Nó ưu tiên luật mạnh, rồi luật tham khảo, rồi fallback bằng giao các tour/transaction.

### 4.4. Logic gợi ý dịch vụ

Hàm chính:

```text
RecommendationService.recommendNextServices(...)
```

Line neo: dòng 105-123.

Luồng:

1. Input là `selectedServices`.
2. Dùng `service_rules.json`.
3. Ưu tiên `manh`, sau đó `tham_khao`: dòng 113-115.
4. Nếu không có rule phù hợp thì response status là `insufficient_rules`: dòng 118.

### 4.5. Dịch vụ thường đi kèm địa điểm

Hàm chính:

```text
RecommendationService.recommendPlaceServices(...)
```

Line neo: dòng 125-158.

Luồng:

1. Input là `selectedPlaces` và `selectedServices`.
2. Đọc `place_to_service_stats.json`.
3. Chỉ xét stat của các địa điểm đang chọn.
4. Loại các service đã chọn rồi: dòng 135.
5. Sort theo độ tin cậy, coverage và thứ tự service: dòng 141-147.

Các chỉ số trả về quan trọng:

- `matched_tour_count`
- `service_available_count`
- `service_known_count`
- `service_coverage_rate`
- `service_confidence`
- `confidence_level`

Điểm trình bày tốt: phần này cho biết “địa điểm này thường đi kèm dịch vụ nào”, khác với `service_rules` là “dịch vụ này thường kéo theo dịch vụ nào”.

### 4.6. Logic tour phù hợp

Hàm chính:

```text
RecommendationService.recommendTours(...)
```

Line neo: dòng 160-188.

Điểm rất quan trọng hiện tại:

- Tour chỉ được đưa vào danh sách nếu là exact match:
  - `filter(TourRecommendation::isExactMatch)`: dòng 172.
  - `isExactMatch()` nghĩa là `missingPlaces.isEmpty() && missingServices.isEmpty()`: dòng 663-665.
- Vì vậy nếu người dùng chọn địa điểm/dịch vụ mà tour không có đủ, tour đó bị loại khỏi kết quả.
- Response status là `no_exact_match` nếu không có tour nào khớp đủ: dòng 181.

Cách tính score:

- `placeCoverage`: trọng số lớn nhất.
- `serviceCoverage`: có trọng số riêng.
- `tour_quality_score`, `service_data_quality`, và việc có giá/thời lượng cũng ảnh hưởng.
- Công thức score nằm ở dòng 338-343.

### 4.7. Enrich tour model bằng database

Tour model được nối sang tour thật bằng:

```text
properties.model_ma_tour = tours.json.ma_tour
```

Code:

- `PropertyRepository.findByModelMaTourIn(...)`: `src/main/java/com/example/webdulich/repository/PropertyRepository.java`, dòng 25.
- `RecommendationService.findPropertiesByModelTour(...)`: dòng 353-364.
- Khi có `Property`, response có:
  - `propertyId`
  - `detailUrl = /tours/{propertyId}`
  - `bookable = true`
- Code map bookable: dòng 682-696.

Điểm trình bày tốt: model không đứng riêng nữa. Nó gợi ý tour và nếu tour đó đã được import vào database thì người dùng có thể mua như tour thường.

### 4.8. Readiness destination

Nếu destination chưa đủ dữ liệu:

- `readinessWarnings(...)`: dòng 366-374.
- `not_ready` trả warning: “Dữ liệu điểm đến này còn ít, kết quả chỉ mang tính tham khảo.”
- Giao diện vẫn cho test, nhưng không quảng bá là gợi ý mạnh.

## 5. Luồng `/itinerary`: gộp tạo lịch trình và model

Template:

```text
src/main/resources/templates/travel/itinerary.html
```

JavaScript:

```text
src/main/resources/static/assets/js/recommendation.js
```

Controller:

```text
src/main/java/com/example/webdulich/controller/TravelController.java
```

### 5.1. Người dùng mở trang

- `GET /itinerary`: `TravelController`, dòng 28-40.
- Nếu chưa đăng nhập thì redirect login: dòng 30-34.
- Nếu đăng nhập, trả template `travel/itinerary`: dòng 39.

### 5.2. Form lịch trình lưu thông tin thủ công và tiêu chí model

Input thủ công trong template:

- `destinationText`: `travel/itinerary.html`, dòng 71.
- `totalDays`: dòng 74.
- `budget`: dòng 77.
- `note`: dòng 90.

Hidden input để lưu tiêu chí model:

- `modelDestinationKey`: dòng 92.
- `selectedPlaces`: dòng 93.
- `selectedServices`: dòng 94.
- `selectedPropertyId`: dòng 95.
- `selectedModelMaTour`: dòng 96.

### 5.3. Giao diện gọi model

`recommendation.js` làm các việc chính:

- Load destination từ `/api/recommend/destinations`: dòng 123.
- Khi đổi destination, gọi song song:
  - `/api/recommend/places?destinationKey=...`: dòng 103.
  - `/api/recommend/services?destinationKey=...`: dòng 104.
- Khi bấm gợi ý, gọi `/api/recommend/full`: dòng 232.
- Body gửi:
  - `destinationKey`: dòng 236.
  - `selectedPlaces`: dòng 237.
  - `selectedServices`: dòng 238.
- Kết quả render:
  - next places: dòng 141.
  - next services: dòng 158.
  - place services: dòng 173-177.
  - recommended tours: dòng 193-195.

### 5.4. Chọn tour này từ gợi ý

Trong danh sách tour phù hợp, JS render form:

```text
POST /cart/add/{propertyId}
```

Line neo:

- `recommendation.js`, dòng 193-195.

Ý nghĩa:

- “Xem tour” để xem chi tiết.
- “Chọn tour này” đưa tour vào giỏ hàng, đi vào flow thanh toán như tour thường.

### 5.5. Lưu lịch trình vào tài khoản

POST form:

- `TravelController.createItinerary`: dòng 42-98.
- Các field model được truyền vào service: dòng 73-78.
- Thành công redirect `/account`: dòng 82.

Service lưu:

```text
CustomItineraryService.create(...)
```

Line neo:

- Nhận đầy đủ thông tin: dòng 33-45.
- Validate user, điểm đến, số ngày, budget: dòng 47-69.
- Nếu có `selectedPropertyId`, kiểm tra tour còn tồn tại: dòng 71-80.
- Tạo `CustomItinerary`: dòng 83-98.
- Status mặc định `PENDING_REVIEW`: dòng 90.
- Lưu selected places/services/tour: dòng 92-97.
- Gán tư vấn viên id `3`: dòng 98.
- Save: dòng 100.

Điểm trình bày tốt: người dùng có thể chọn tour phù hợp để mua; nếu không chọn tour thì vẫn lưu lịch trình tự tạo để tư vấn viên xử lý tiếp.

### 5.6. Trạng thái lịch trình

Entity:

```text
src/main/java/com/example/webdulich/entity/CustomItinerary.java
```

Status:

- `PENDING_REVIEW`: đang xét duyệt.
- `ADVISED`: đã tư vấn.
- `APPROVED`: đã phê duyệt.
- `REJECTED`: từ chối.

Line neo:

- Hằng status: dòng 15-18.
- Display tiếng Việt: dòng 134-137.
- Người dùng chỉ sửa/xóa khi còn editable: `CustomItineraryService.findUserEditableItinerary`, dòng 152-164.

## 6. Tour model trở thành tour thật trong database

Bảng chính:

```text
properties
```

Cột quan trọng:

- `model_ma_tour`: mã tour từ artifact.
- `model_source`: nguồn tour.
- `model_url`: URL nguồn tham khảo.
- `recommendation_enabled`: tour được bật cho recommendation.
- `model_destination_key`: destination của model.
- `model_places`: JSON danh sách địa điểm.
- `model_services`: JSON danh sách dịch vụ.
- `model_version`: ví dụ `v11`.
- `agent_id`: tư vấn viên phụ trách tour.

Schema hiện có thể xem trong:

```text
database/webdulich_db.sql
```

Chi tiết tour:

```text
src/main/resources/templates/properties/detail.html
```

Điểm hiển thị có giá trị trình bày:

- “Các địa điểm sẽ đi”: dòng 42-47.
- “Dịch vụ nổi bật trong tour”: dòng 49-53.
- Nút thêm giỏ: dòng 66-68.
- Nút yêu thích: dòng 71-78.
- Form tư vấn/đặt tour: dòng 275-276.

Controller chi tiết tour:

```text
src/main/java/com/example/webdulich/controller/PropertyController.java
```

Line neo:

- Gán dữ liệu tour detail, favorite, review, highlight model: dòng 72-81.
- `addModelTourHighlights(...)`: dòng 170-172.
- Form tư vấn tour: dòng 85-117.

## 7. Luồng yêu cầu tư vấn: tách lịch trình và tour/liên hệ

Đây là phần rất quan trọng để trình bày vì nó thể hiện logic nghiệp vụ không bị gộp sai.

### 7.1. Bảng dữ liệu

Lịch trình cá nhân:

```text
custom_itineraries
```

Yêu cầu tư vấn tour/liên hệ:

```text
inquiries
inquiry_assignments
```

Ý nghĩa:

- `custom_itineraries`: người dùng tự tạo lịch trình, có tiêu chí model, được agent id `3` xử lý.
- `inquiries`: yêu cầu từ chi tiết tour hoặc form liên hệ.
- `inquiry_assignments`: một inquiry có thể được giao cho một hoặc nhiều tư vấn viên.

### 7.2. Tư vấn tour từ trang chi tiết tour

Luồng:

1. Người dùng ở `/tours/{id}` gửi form tư vấn.
2. `PropertyController` nhận `POST /tours/{id}/inquiry`.
3. Nếu chưa đăng nhập thì yêu cầu login.
4. Gọi `InquiryService.createTourInquiry(inquiry, property)`.
5. Service xác định agent phụ trách tour.
6. Lưu `Inquiry`.
7. Tạo một `InquiryAssignment` cho agent đó.

Line neo:

- Controller endpoint: `PropertyController`, dòng 85.
- Kiểm login: dòng 95-100.
- Gọi service: dòng 115.
- `InquiryService.createTourInquiry`: dòng 37-49.
- Gán source `TOUR`: dòng 42.
- Gán status `PENDING`: dòng 43.
- Gán agent phụ trách: dòng 44.
- Tạo assignment: dòng 47.

### 7.3. Cách chọn tư vấn viên cho tour

Hàm:

```text
InquiryService.resolveAgentForProperty(...)
```

Line neo: dòng 143-170.

Ưu tiên:

1. Nếu `property.agent` đã có thì dùng luôn: dòng 147-149.
2. Nếu không có thì fallback theo `modelDestinationKey`, `city`, `type`.

Mapping fallback:

- `da_lat` hoặc Đà Lạt/Tây Nguyên → agent `4`.
- `phan_thiet` hoặc Phan Thiết/Mũi Né → agent `5`.
- `vung_tau` hoặc Vũng Tàu/Hồ Tràm → agent `6`.
- miền Bắc → agent `2`.
- miền Trung, miền Nam, biển đảo → agent `1`.

### 7.4. Liên hệ chung

Controller:

```text
src/main/java/com/example/webdulich/controller/ContactController.java
```

Luồng:

1. Người dùng gửi form `/contact`.
2. Lưu `ContactMessage`.
3. Đồng thời tạo `Inquiry` source `CONTACT`.
4. Gửi assignment đến 5 tư vấn viên tour.
5. Không gửi cho agent id `3`, vì id `3` chỉ xử lý lịch trình cá nhân.

Line neo:

- `ContactController.POST /contact`: dòng 33.
- Gọi `inquiryService.createContactInquiry(contactMessage)`: dòng 45.
- `InquiryService.createContactInquiry`: dòng 51-68.
- Bỏ qua agent id `3`: dòng 62-65.

### 7.5. Khi tư vấn viên xử lý inquiry

Luồng:

1. Tư vấn viên bấm xử lý inquiry.
2. `ConsultantController` gọi `ConsultantService.handleInquiry`.
3. `ConsultantService` chuyển user consultant sang agent id.
4. `InquiryService.markHandled` cập nhật inquiry và tất cả assignment.

Line neo:

- `ConsultantController.POST /consultant/inquiries/{id}/handle`: dòng 184-193.
- `ConsultantService.handleInquiry`: dòng 137-140.
- `InquiryService.markHandled`: dòng 70-100.
- Assignment của người xử lý thành `HANDLED`: dòng 87.
- Assignment của người khác thành `COLLEAGUE_HANDLED`: dòng 87.

Điểm trình bày tốt: cùng một inquiry có thể được gửi đến nhiều nhân viên, nhưng hệ thống vẫn biết ai xử lý và ai chỉ nhận thông báo rằng đồng nghiệp đã xử lý.

## 8. Dashboard tư vấn viên

Controller:

```text
src/main/java/com/example/webdulich/controller/ConsultantController.java
```

Service:

```text
src/main/java/com/example/webdulich/service/ConsultantService.java
```

Điểm phân luồng:

- Tài khoản consultant được map sang agent bằng email.
- `ConsultantService.resolveAgentId`: dòng 148-154.
- Nếu agent id là `3`, dashboard hiển thị lịch trình cá nhân.
- Nếu agent id khác `3`, dashboard hiển thị tour/liên hệ.

Line neo trong controller:

- Resolve agent id: `ConsultantController`, dòng 56.
- `itineraryConsultant = agentId == 3`: dòng 57.
- Nếu là id `3`, load pending/my itineraries: dòng 60-63.
- Nếu không phải id `3`, load pending/my inquiry assignments: dòng 65-66.
- Add model cho template: dòng 81-86.

Line neo trong service:

- Thống kê dashboard: `ConsultantService.getDashboardStats`, dòng 61-73.
- Tư vấn lịch trình: dòng 75-92.
- Khi tư vấn lịch trình thì tự mở conversation: dòng 90.
- Phê duyệt lịch trình: dòng 94-103.
- Từ chối lịch trình: dòng 105-112.
- Lấy inquiry assignment theo agent: dòng 127-135.

## 9. Account khách hàng

Controller:

```text
src/main/java/com/example/webdulich/controller/AccountController.java
```

Luồng:

1. Người dùng vào `/account`.
2. Controller lấy current user từ session.
3. Load lịch trình cá nhân từ `custom_itineraries`.
4. Load yêu cầu tư vấn tour/liên hệ từ `inquiries` theo email.
5. Load yêu thích, hóa đơn, đánh giá.

Line neo:

- `GET /account`: dòng 35.
- Load itineraries: dòng 55.
- Load customer inquiries: dòng 56.
- `consultationRequestCount = itineraries.size() + inquiryCount`: dòng 62.
- Load favorite tours: dòng 63.
- Load favorite count: dòng 64.

Điểm trình bày tốt: phía khách hàng xem được cả hai loại yêu cầu tư vấn, nhưng database vẫn tách đúng hai nghiệp vụ.

## 10. Giỏ hàng, thanh toán, đánh giá

### 10.1. Giỏ hàng

Controller:

```text
src/main/java/com/example/webdulich/controller/CartController.java
```

Điểm chính:

- Giỏ hàng hiện lưu trong session key `cartTourIds`: dòng 20.
- Xem giỏ: `GET /cart`, dòng 29.
- Thêm tour: `POST /cart/add/{id}`, dòng 54.
- Xóa tour: `POST /cart/remove/{id}`, dòng 77.
- Clear giỏ: `POST /cart/clear`, dòng 96.
- Chống trùng tour trong giỏ bằng `LinkedHashSet`: dòng 135.

Liên kết từ model:

- `recommendation.js` render form `POST /cart/add/{propertyId}`: dòng 193-195.
- Vì recommendation đã enrich `propertyId`, tour model đi thẳng được vào cart.

### 10.2. Thanh toán

Controller:

```text
src/main/java/com/example/webdulich/controller/MomoPaymentController.java
```

Điểm chính:

- Dùng cùng session key `cartTourIds`: dòng 28.
- Tạo thanh toán: `POST /payment/momo/create`, dòng 39.
- Demo payment: `GET /payment/momo/demo`, dòng 65.
- Demo pay success: `POST /payment/momo/demo/pay`, dòng 83.
- Khi thanh toán demo thành công thì xóa giỏ: dòng 92.
- MoMo return thật: `GET /payment/momo/return`, dòng 150.
- Nếu result code thành công thì xóa giỏ: dòng 166.

### 10.3. Đánh giá sau thanh toán

Controller:

```text
src/main/java/com/example/webdulich/controller/PaymentHistoryController.java
```

Service:

```text
src/main/java/com/example/webdulich/service/PaymentHistoryService.java
```

Điểm chính:

- Chỉ lấy hóa đơn đã thanh toán cho user.
- Đánh giá gắn với `payment_order_id`, `order_id`, `property_id`, `user_id`.
- Lưu review: `PaymentHistoryService`, dòng 105-118.
- Xóa review: dòng 127-135.
- Tour detail hiển thị review: `PropertyController.addTourReviewData`, dòng 121-145.

## 11. Yêu thích tour

Bảng:

```text
favorite_tours
```

Khóa chính:

- `user_id`
- `tour_id`

Controller:

```text
src/main/java/com/example/webdulich/controller/FavoriteTourController.java
```

Service:

```text
src/main/java/com/example/webdulich/service/FavoriteTourService.java
```

Line neo:

- Thêm yêu thích: `POST /favorites/add/{tourId}`, controller dòng 19.
- Xóa yêu thích: `POST /favorites/remove/{tourId}`, controller dòng 41.
- Không lưu trùng: `FavoriteTourService.add`, dòng 24-30.
- Đếm yêu thích: dòng 46.
- Lấy danh sách tour yêu thích: dòng 51.
- Tour detail hiển thị nút theo trạng thái yêu thích: `properties/detail.html`, dòng 71-78.

## 12. File nên mở khi cần giải thích từng luồng

Recommendation:

- `src/main/java/com/example/webdulich/recommendation/RecommendationController.java`
- `src/main/java/com/example/webdulich/recommendation/RecommendationService.java`
- `src/main/java/com/example/webdulich/recommendation/RecommendationRequest.java`
- `src/main/resources/static/assets/js/recommendation.js`

Lịch trình:

- `src/main/java/com/example/webdulich/controller/TravelController.java`
- `src/main/java/com/example/webdulich/service/CustomItineraryService.java`
- `src/main/java/com/example/webdulich/entity/CustomItinerary.java`
- `src/main/resources/templates/travel/itinerary.html`

Tour và tư vấn:

- `src/main/java/com/example/webdulich/controller/PropertyController.java`
- `src/main/java/com/example/webdulich/service/InquiryService.java`
- `src/main/java/com/example/webdulich/entity/Inquiry.java`
- `src/main/java/com/example/webdulich/entity/InquiryAssignment.java`
- `src/main/resources/templates/properties/detail.html`

Tư vấn viên:

- `src/main/java/com/example/webdulich/controller/ConsultantController.java`
- `src/main/java/com/example/webdulich/service/ConsultantService.java`
- `src/main/resources/templates/consultant/dashboard.html`

Khách hàng:

- `src/main/java/com/example/webdulich/controller/AccountController.java`
- `src/main/resources/templates/account/index.html`

Giỏ hàng, thanh toán, đánh giá:

- `src/main/java/com/example/webdulich/controller/CartController.java`
- `src/main/java/com/example/webdulich/controller/MomoPaymentController.java`
- `src/main/java/com/example/webdulich/controller/PaymentHistoryController.java`
- `src/main/java/com/example/webdulich/service/PaymentHistoryService.java`

Database:

- `database/webdulich_db.sql`

