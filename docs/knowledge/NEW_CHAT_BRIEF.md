# WebDuLich - Brief cho chat mới

Tài liệu này là bản đọc nhanh để một đoạn chat mới nắm đúng trạng thái dự án trước khi sửa code.

## Cách đọc bộ tri thức

Đọc theo thứ tự:

1. `docs/knowledge/PROJECT_KNOWLEDGE.md` - tổng quan cấu trúc, luồng code, route, cách chạy.
2. `docs/knowledge/FEATURE_STATUS.md` - phần nào đã làm thật, phần nào còn demo/chưa hoàn thiện.
3. `docs/knowledge/DATA_AND_RECOMMENDATION_MODEL.md` - database, dữ liệu model v11, import tour, API recommendation.

## Tóm tắt rất ngắn

Đây là web du lịch Spring Boot/Thymeleaf đang dùng bảng/entity `Property` như tour. Không refactor `Property` thành `Tour` nếu user không yêu cầu rõ, vì nhiều controller/template/service đang phụ thuộc tên cũ.

Stack chính:

- Java 17
- Spring Boot 3.3.5
- Thymeleaf
- Spring Web
- Spring Data JPA
- MySQL
- Static CSS/JS trong `src/main/resources/static/assets`

App chạy port `8080`. Cấu hình MySQL hiện tại trong `src/main/resources/application.properties`:

- DB: `webdulich_db`
- user: `root`
- password: `bonganhhung1`
- `spring.jpa.hibernate.ddl-auto=update`

Maven có thể không nằm trong PATH. Nếu cần dùng Maven trên máy này, dùng:

```powershell
C:\Users\PC\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd
```

## Trạng thái chức năng

Đã làm thật:

- Danh sách tour: `/tours`, alias `/properties`.
- Chi tiết tour: `/tours/{id}`, alias `/properties/{id}`.
- Form tư vấn/đặt tour: `POST /tours/{id}/inquiry`, lưu bảng `inquiries`, không cần đăng nhập.
- Blog list/detail.
- Agent list/detail.
- Recommendation v11 ở `/itinerary` và API `/api/recommend/...`.
- Tour recommendation đã enrich qua database: nếu `model_ma_tour` có trong `properties` thì trả `propertyId`, `detailUrl=/tours/{id}`, `bookable=true`.

Còn demo/chưa hoàn thiện:

- `/login`, `/register`: chỉ có GET/template, chưa có Spring Security, chưa có POST xử lý tài khoản.
- Bảng `users`, `profiles`, `reviews`, `favorite_tours`, `custom_itineraries` có trong SQL nền nhưng chưa được map/triển khai đầy đủ trong Java.
- Phần form tự tạo lịch trình phía trên trang `/itinerary` là demo UI, chưa lưu database.
- Favorite/heart chỉ là hiệu ứng JS, chưa lưu.
- Newsletter chỉ flash message, chưa lưu.
- Admin, thanh toán, booking phức tạp chưa có.

## Recommendation hiện tại

Model đang dùng: `src/main/resources/recommendation_model_v11/`.

Destination:

- `da_lat`: ready, 75 tour sạch, 63 places, 9 services, 316 place rules, 17 service rules.
- `phan_thiet`: ready, 32 tour sạch, 21 places, 9 services, 71 place rules, 34 service rules.
- `vung_tau`: ready, 40 tour sạch, 39 places, 9 services, 93 place rules, 26 service rules.

Luật hiển thị:

- `recommendation_level = manh` -> nhãn xanh `Nên chọn`.
- `recommendation_level = tham_khao` -> nhãn vàng `Tham khảo`.
- Không dùng `place_level` để quyết định nhãn luật. `place_level` chỉ là phân loại catalog `core/rare`.

Tour phù hợp:

- Chỉ trả tour exact match với toàn bộ `selectedPlaces` và `selectedServices`.
- Nếu không có tour đủ tiêu chí thì danh sách tour phải rỗng/`no_exact_match`, không giữ tour không phù hợp.

## Các file cần nhớ

- `src/main/java/com/example/webdulich/recommendation/RecommendationService.java`
- `src/main/resources/static/assets/js/recommendation.js`
- `src/main/java/com/example/webdulich/entity/Property.java`
- `src/main/java/com/example/webdulich/controller/PropertyController.java`
- `database/import_model_tours_v11.sql`
- `database/generate_import_model_tours_v11.ps1`

## Lưu ý lớn

Base SQL `database/webdulich_mysql.sql` là dữ liệu nền cũ và chưa phản ánh toàn bộ cột model v11. Các cột v11 nằm ở entity `Property.java` và script import `database/import_model_tours_v11.sql`.

Template `src/main/resources/templates/contact/index.html` hiện có dấu hiệu lỗi ở `th:object="${contactNội dung cần tư vấn}"`; nếu cần dùng trang liên hệ thật, nên sửa thành `${contactMessage}` và sửa text nút.
