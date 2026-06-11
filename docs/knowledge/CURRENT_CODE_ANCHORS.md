# WebDuLich - bản đồ code và dữ liệu neo

File này dùng như “phao cứu sinh” khi trình bày hoặc đưa cho đoạn chat mới. Nó ghi các line quan trọng hiện tại để lần theo code nhanh.

## 1. Endpoint và route chính

| Chức năng | Route | File | Line |
|---|---|---|---:|
| Trang tạo lịch trình | `GET /itinerary` | `TravelController.java` | 28 |
| Lưu lịch trình | `POST /itinerary` | `TravelController.java` | 42 |
| Sửa lịch trình | `POST /itinerary/{id}/update` | `TravelController.java` | 100 |
| Xóa lịch trình | `POST /itinerary/{id}/delete` | `TravelController.java` | 127 |
| API destinations | `GET /api/recommend/destinations` | `RecommendationController.java` | 23 |
| API places | `GET /api/recommend/places` | `RecommendationController.java` | 28 |
| API services | `GET /api/recommend/services` | `RecommendationController.java` | 35 |
| API full recommendation | `POST /api/recommend/full` | `RecommendationController.java` | 71 |
| Chi tiết tour | `GET /tours/{id}` | `PropertyController.java` | 67 |
| Gửi tư vấn tour | `POST /tours/{id}/inquiry` | `PropertyController.java` | 85 |
| Gửi liên hệ | `POST /contact` | `ContactController.java` | 33 |
| Dashboard tư vấn viên | `GET /consultant` | `ConsultantController.java` | 50 |
| Tư vấn lịch trình | `POST /consultant/requests/{id}/advise` | `ConsultantController.java` | 133 |
| Xử lý inquiry | `POST /consultant/inquiries/{id}/handle` | `ConsultantController.java` | 184 |
| Account khách hàng | `GET /account` | `AccountController.java` | 35 |
| Thêm giỏ | `POST /cart/add/{id}` | `CartController.java` | 54 |
| Thêm yêu thích | `POST /favorites/add/{tourId}` | `FavoriteTourController.java` | 19 |
| Xóa yêu thích | `POST /favorites/remove/{tourId}` | `FavoriteTourController.java` | 41 |
| Tạo thanh toán | `POST /payment/momo/create` | `MomoPaymentController.java` | 39 |
| Lưu đánh giá hóa đơn | `POST /account/payments/{orderId}/review` | `PaymentHistoryController.java` | 80 |

## 2. Các line code nên nhớ

### Recommendation

- Model path v11: `RecommendationService.java:32`.
- Load model một lần khi startup: `RecommendationService.java:48-62`.
- Load từng folder destination riêng: `RecommendationService.java:387-410`.
- Places mặc định chỉ core khi `includeRare=false`: `RecommendationService.java:68-73`.
- Next places: `RecommendationService.java:82-103`.
- Next services: `RecommendationService.java:105-123`.
- Place services stats: `RecommendationService.java:125-158`.
- Tour recommendation exact match: `RecommendationService.java:160-188`.
- `full` response gom toàn bộ khối: `RecommendationService.java:190-206`.
- Ưu tiên rule `manh`, rồi `tham_khao`, rồi mở rare nếu cần: `RecommendationService.java:209-218`.
- Enrich tour model bằng database: `RecommendationService.java:353-364`.
- Warning readiness: `RecommendationService.java:366-374`.
- Tour bookable/detailUrl/propertyId: `RecommendationService.java:682-696`.

### Form itinerary + model

- Check login trước khi mở `/itinerary`: `TravelController.java:30-34`.
- Nhận hidden model fields khi submit: `TravelController.java:48-53`.
- Gửi model fields vào service: `TravelController.java:73-78`.
- Hidden inputs trong template: `travel/itinerary.html:92-96`.
- Sticky summary tiêu chí model: `travel/itinerary.html:111`.
- JS đồng bộ hidden input: `recommendation.js:67-80`.
- JS load places/services theo destination: `recommendation.js:102-112`.
- JS gọi `/api/recommend/full`: `recommendation.js:232-240`.
- JS “Chọn tour này” gửi `/cart/add/{propertyId}`: `recommendation.js:193-195`.

### Lưu lịch trình

- `CustomItineraryService.create(...)`: `CustomItineraryService.java:33-45`.
- Validate user/destination/day/budget: `CustomItineraryService.java:47-69`.
- Kiểm tour được chọn còn tồn tại: `CustomItineraryService.java:71-80`.
- Set status `PENDING_REVIEW`: `CustomItineraryService.java:90`.
- Lưu model criteria vào lịch trình: `CustomItineraryService.java:92-97`.
- Gán agent id `3`: `CustomItineraryService.java:98`.
- Chỉ sửa/xóa khi lịch trình còn editable: `CustomItineraryService.java:152-164`.

### Tour detail và database tour model

- Tour detail có favorite/review/model highlights: `PropertyController.java:72-81`.
- Tách places/services JSON model để render: `PropertyController.java:170-172`.
- Template hiển thị “Các địa điểm sẽ đi”: `properties/detail.html:42-47`.
- Template hiển thị “Dịch vụ nổi bật”: `properties/detail.html:49-53`.
- Template thêm giỏ: `properties/detail.html:66-68`.
- Template yêu thích: `properties/detail.html:71-78`.
- Template tư vấn tour: `properties/detail.html:275-276`.

### Inquiry tour/liên hệ

- Tour inquiry endpoint: `PropertyController.java:85`.
- Tour inquiry yêu cầu login: `PropertyController.java:95-100`.
- Lưu tour inquiry: `InquiryService.java:37-49`.
- Contact inquiry: `InquiryService.java:51-68`.
- Contact bỏ qua agent id `3`: `InquiryService.java:62-65`.
- Xử lý inquiry, cập nhật tất cả assignment: `InquiryService.java:70-100`.
- Nếu một người xử lý, người khác thành `COLLEAGUE_HANDLED`: `InquiryService.java:85-89`.
- Tìm inquiry theo email để hiện cho khách hàng: `InquiryService.java:113-119`.
- Resolve agent theo property/destination: `InquiryService.java:143-170`.

### Consultant

- `ConsultantService.resolveAgentId` map user consultant sang agent bằng email: `ConsultantService.java:148-154`.
- Dashboard stats gồm cả itinerary và tour inquiries: `ConsultantService.java:61-73`.
- Agent id `3` xử lý lịch trình cá nhân.
- Agent id khác `3` xử lý inquiry tour/liên hệ.
- Tư vấn lịch trình đổi status sang `ADVISED`: `ConsultantService.java:75-92`.
- Sau tư vấn lịch trình tự mở conversation: `ConsultantService.java:90`.
- Phê duyệt lịch trình chỉ khi đã tư vấn: `ConsultantService.java:94-103`.
- Từ chối lịch trình: `ConsultantService.java:105-112`.
- Lấy inquiry assignment theo agent: `ConsultantService.java:127-135`.

### Account khách hàng

- Load lịch trình cá nhân: `AccountController.java:55`.
- Load yêu cầu tư vấn tour/liên hệ theo email: `AccountController.java:56`.
- Tổng yêu cầu tư vấn = lịch trình + inquiry: `AccountController.java:62`.
- Load tour yêu thích: `AccountController.java:63-64`.

### Cart/payment/review

- Session key giỏ hàng: `CartController.java:20`.
- Xem giỏ: `CartController.java:29`.
- Thêm tour: `CartController.java:54`.
- Xóa tour: `CartController.java:77`.
- Clear giỏ: `CartController.java:96`.
- Chống trùng tour trong giỏ: `CartController.java:135`.
- Tạo payment từ cart: `MomoPaymentController.java:39-57`.
- Demo pay thành công xóa cart: `MomoPaymentController.java:83-92`.
- MoMo return thành công xóa cart: `MomoPaymentController.java:150-166`.
- Lưu review sau thanh toán: `PaymentHistoryService.java:105-118`.
- Xóa review sau thanh toán: `PaymentHistoryService.java:127-135`.

## 3. Bảng database và cột quan trọng

### `properties`

Vai trò: bảng tour chính của toàn hệ thống. Tour gốc và tour model đều nằm chung bảng này.

Cột quan trọng:

- `id`: id tour dùng cho `/tours/{id}` và `/cart/add/{id}`.
- `title`, `price`, `image_url`, `description`: dữ liệu hiển thị tour.
- `type`, `city`, `location`: dùng lọc/hiển thị/phân công fallback.
- `agent_id`: tư vấn viên phụ trách.
- `model_ma_tour`: mã tour model, unique.
- `model_source`, `model_url`: nguồn dữ liệu model.
- `recommendation_enabled`: bật tour model cho recommendation.
- `model_destination_key`: destination key.
- `model_places`: JSON địa điểm trong tour.
- `model_services`: JSON dịch vụ trong tour.
- `model_version`: version model, hiện là `v11` với tour model mới.

### `custom_itineraries`

Vai trò: lịch trình cá nhân do người dùng tạo.

Cột quan trọng:

- `user_id`: tài khoản tạo lịch trình.
- `status`: `PENDING_REVIEW`, `ADVISED`, `APPROVED`, `REJECTED`.
- `assigned_agent_id`: hiện gán agent id `3`.
- `consultant_note`: ghi chú tư vấn.
- `model_destination_key`: destination được chọn từ model.
- `selected_places`: JSON địa điểm người dùng chọn.
- `selected_services`: JSON dịch vụ người dùng chọn.
- `selected_property_id`: nếu người dùng chọn tour gợi ý.
- `selected_model_ma_tour`: mã tour model được chọn.
- `selected_tour_title`: tên tour được chọn.

### `inquiries`

Vai trò: yêu cầu tư vấn tour hoặc liên hệ chung.

Cột quan trọng:

- `source`: `TOUR` hoặc `CONTACT`.
- `status`: `PENDING` hoặc `HANDLED`.
- `property_id`: có nếu source là tour.
- `assigned_agent_id`: agent chính nếu là tour inquiry.
- `handled_by_agent_id`: ai đã xử lý.
- `handled_at`: thời điểm xử lý.
- `consultant_note`: ghi chú xử lý.

### `inquiry_assignments`

Vai trò: phân phối một inquiry đến một hoặc nhiều tư vấn viên.

Cột quan trọng:

- `inquiry_id`
- `agent_id`
- `status`: `PENDING`, `HANDLED`, `COLLEAGUE_HANDLED`.
- `handled_at`

### `favorite_tours`

Vai trò: tour yêu thích của user.

Khóa chính kép:

- `user_id`
- `tour_id`

### `payment_orders`

Vai trò: hóa đơn/thanh toán.

Điểm quan trọng: cart được chụp thành danh sách tour ids trong order, sau khi thanh toán thành công thì người dùng có thể đánh giá tour trong hóa đơn.

### `tour_reviews`

Vai trò: đánh giá tour sau thanh toán.

Review gắn với:

- user
- payment order
- property/tour

## 4. Query nhanh khi cần kiểm tra demo

```sql
SELECT id, full_name, role, email
FROM agents
ORDER BY id;
```

```sql
SELECT id, email, full_name, role
FROM users
WHERE role IN ('ADMIN', 'CONSULTANT')
ORDER BY id;
```

```sql
SELECT COUNT(*) AS properties_total,
       SUM(recommendation_enabled = 1) AS model_tours,
       COUNT(DISTINCT model_ma_tour) AS distinct_model_ma_tour
FROM properties;
```

```sql
SELECT source, status, assigned_agent_id, COUNT(*) AS count
FROM inquiries
GROUP BY source, status, assigned_agent_id
ORDER BY source, assigned_agent_id;
```

```sql
SELECT ia.inquiry_id, ia.agent_id, ia.status, i.source, i.property_id
FROM inquiry_assignments ia
JOIN inquiries i ON i.id = ia.inquiry_id
ORDER BY ia.created_at DESC;
```

## 5. Các câu giải thích ngắn dễ dùng khi thuyết trình

- “Model không thay thế database tour; model chỉ đề xuất, còn tour có thể mua là tour đã được enrich sang bảng `properties` bằng `model_ma_tour`.”
- “Lịch trình cá nhân và yêu cầu tư vấn tour là hai nghiệp vụ khác nhau, nên tách bảng: `custom_itineraries` cho lịch trình, `inquiries` cho tour/liên hệ.”
- “Agent id 3 chỉ phụ trách lịch trình cá nhân. Các yêu cầu liên hệ chung được gửi cho 5 tư vấn viên tour còn lại.”
- “Tour recommendation hiện lọc exact match: tour phải có đủ địa điểm và dịch vụ đã chọn, nếu thiếu thì không hiển thị trong tour phù hợp.”
- “Khi một tư vấn viên xử lý yêu cầu liên hệ chung, các assignment của đồng nghiệp chuyển sang trạng thái `COLLEAGUE_HANDLED`, nên không bị xử lý trùng.”
- “Từ gợi ý lịch trình, người dùng có thể chọn tour để đưa thẳng vào giỏ hàng, thanh toán và đánh giá như tour thường.”
