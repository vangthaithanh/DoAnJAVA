# WebDuLich - Tri thức cấu trúc dự án

Tài liệu này mô tả cấu trúc web du lịch hiện tại để một chat mới có thể đọc và tiếp tục làm việc mà không phải lần lại toàn bộ lịch sử.

## Tổng quan

WebDuLich là project Spring Boot dùng Thymeleaf, MySQL và JPA. Ban đầu project đi theo template bất động sản nên nhiều tên miền kỹ thuật vẫn là `Property`, `properties`, `Agent`; hiện tại giao diện và dữ liệu đã được chuyển sang ngữ cảnh bán tour du lịch.

Quy ước quan trọng:

- `Property` trong code hiện đại diện cho một tour.
- Bảng `properties` hiện là bảng tour chính.
- Không đổi tên `Property` thành `Tour` nếu user không yêu cầu rõ, vì thay đổi đó sẽ kéo theo entity, repository, controller, template, SQL và dữ liệu import.

## Stack kỹ thuật

- Java 17
- Spring Boot 3.3.5
- Spring MVC / Web
- Spring Data JPA
- Bean Validation
- Thymeleaf
- MySQL Connector/J
- H2 runtime có trong dependency nhưng cấu hình mặc định đang dùng MySQL
- Frontend: HTML Thymeleaf, CSS thuần, JS thuần

File cấu hình chính: `src/main/resources/application.properties`.

Thông tin cấu hình hiện tại:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/webdulich_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=bonganhhung1
spring.jpa.hibernate.ddl-auto=update
spring.thymeleaf.cache=false
```

`ddl-auto=update` giúp Hibernate tự thêm/cập nhật bảng theo entity hiện tại khi chạy app. Khi làm production thật nên dùng migration hoặc `validate`.

## Cấu trúc thư mục

```text
webdulich/
  pom.xml
  README.md
  database/
    webdulich_mysql.sql
    import_model_tours_v10.sql
    import_model_tours_v11.sql
    generate_import_model_tours_v11.ps1
    test_model_tour_import_check.sql
    test_model_tour_import_v11_check.sql
    DATA_SOURCES.md
  docs/
    knowledge/
      NEW_CHAT_BRIEF.md
      PROJECT_KNOWLEDGE.md
      FEATURE_STATUS.md
      DATA_AND_RECOMMENDATION_MODEL.md
  src/main/java/com/example/webdulich/
    WebDuLichApplication.java
    config/
    controller/
    entity/
    repository/
    service/
    recommendation/
  src/main/resources/
    application.properties
    templates/
    static/assets/
    recommendation_model/
    recommendation_model_v11/
```

## Package Java

### Entry point

`src/main/java/com/example/webdulich/WebDuLichApplication.java`

Class khởi động Spring Boot.

### Config

`src/main/java/com/example/webdulich/config/DataSeeder.java`

Seeder chạy lúc app start. Nếu bảng tương ứng đang trống, seeder tạo dữ liệu mẫu:

- `agents`: 3 tư vấn viên.
- `properties`: các tour mẫu nền.
- `blog_posts`: bài viết mẫu.

Nếu DB đã có dữ liệu, seeder không ghi đè. Sau khi đã import tour model v11, bảng `properties` không trống nên seeder sẽ không tạo lại tour nền.

### Entity

`src/main/java/com/example/webdulich/entity/Property.java`

Entity chính cho tour, map vào bảng `properties`.

Nhóm field nền:

- `id`
- `title`
- `price`
- `imageUrl`
- `galleryImageOne`
- `galleryImageTwo`
- `galleryImageThree`
- `location`
- `status`
- `type`
- `city`
- `bedrooms`
- `bathrooms`
- `parking`
- `area`
- `yearBuilt`
- `description`
- `featured`
- `agent`

Cách dùng hiện tại trong ngữ cảnh tour:

- `title`: tên tour.
- `price`: giá từ.
- `location/city`: điểm đến.
- `bedrooms`: số ngày.
- `bathrooms`: số đêm.
- `parking`: số chỗ còn hoặc số lượng quan tâm tùy dữ liệu mẫu.
- `type`: loại tour.

Nhóm field model/recommendation:

- `modelMaTour`: mã tour từ model, cột `model_ma_tour`.
- `modelSource`: nguồn tour, cột `model_source`.
- `modelUrl`: URL nguồn, cột `model_url`.
- `recommendationEnabled`: bật dùng trong recommendation/import, cột `recommendation_enabled`.
- `modelDestinationKey`: destination của model, cột `model_destination_key`.
- `modelVersion`: ví dụ `v11`, cột `model_version`.
- `modelPlaces`: JSON array địa điểm trong tour, cột `model_places`.
- `modelServices`: JSON array dịch vụ trong tour, cột `model_services`.

`src/main/java/com/example/webdulich/entity/Inquiry.java`

Yêu cầu tư vấn tour. Map bảng `inquiries`.

- `fullName`
- `email`
- `phone`
- `message`
- `createdAt`
- `property`

`@PrePersist` tự set `createdAt`.

`src/main/java/com/example/webdulich/entity/ContactMessage.java`

Tin nhắn liên hệ chung. Map bảng `contact_messages`.

`src/main/java/com/example/webdulich/entity/Agent.java`

Tư vấn viên/nhân sự hiển thị ở trang agents và liên kết với `Property`.

`src/main/java/com/example/webdulich/entity/BlogPost.java`

Bài viết blog.

### Repository

`PropertyRepository.java`

Repository chính cho tour:

- `findFirstByFeaturedTrueOrderByIdDesc`
- `findTop6ByOrderByIdDesc`
- `findTop3ByOrderByIdDesc`
- `findByAgentIdOrderByIdDesc`
- `findByModelMaTour`
- `findByModelMaTourIn`
- `searchProperties`
- `countByTypeIgnoreCase`
- `countByCityIgnoreCase`

Hai method model quan trọng:

- `findByModelMaTour(String modelMaTour)`
- `findByModelMaTourIn(Collection<String> modelMaTours)`

Các repository khác:

- `InquiryRepository`
- `ContactMessageRepository`
- `AgentRepository`
- `BlogPostRepository`

### Service

`PropertyService.java`

Service đọc/search tour từ `PropertyRepository`.

`InquiryService.java`

Service lưu yêu cầu tư vấn tour.

`ContactService.java`

Service lưu contact message.

`AgentService.java`, `BlogService.java`

Service đọc agents/blog.

`recommendation/RecommendationService.java`

Service recommendation v11, đọc JSON classpath khi app start và enrich tour từ database.

### Controller

`HomeController.java`

Route trang chủ và about.

`PropertyController.java`

Controller tour chính, route kép:

- `/tours`
- `/properties`

Luồng:

- `GET /tours`: danh sách tour.
- `GET /properties`: alias danh sách tour.
- `GET /tours/{id}`: chi tiết tour.
- `GET /properties/{id}`: alias chi tiết tour.
- `POST /tours/{id}/inquiry`: lưu yêu cầu tư vấn tour.
- `POST /properties/{id}/inquiry`: alias do mapping controller kép.

Khi mở detail, controller đọc `modelPlaces` và `modelServices` từ JSON string để render điểm nhấn tour.

`TravelController.java`

Route trang du lịch:

- `/itinerary`
- `/destinations`

`/itinerary` chứa cả form demo tạo lịch trình và khối recommendation v11.

`RecommendationController.java`

REST API recommendation:

- `GET /api/recommend/destinations`
- `GET /api/recommend/places`
- `GET /api/recommend/services`
- `POST /api/recommend/next-places`
- `POST /api/recommend/next-services`
- `POST /api/recommend/place-services`
- `POST /api/recommend/tours`
- `POST /api/recommend/full`

`AuthController.java`

Chỉ có:

- `GET /login`
- `GET /register`

Chưa có auth thật.

`ContactController.java`

Route:

- `GET /contact`
- `POST /contact`
- `POST /newsletter`

Lưu ý: controller contact có luồng lưu thật, nhưng template contact hiện có dấu hiệu sai `th:object`; xem `FEATURE_STATUS.md`.

`AgentController.java`

Route danh sách/chi tiết tư vấn viên.

`BlogController.java`

Route danh sách/chi tiết blog.

## Templates

`src/main/resources/templates/fragments/layout.html`

Layout fragment dùng chung:

- head
- header
- banner
- flash
- footer
- scripts

`src/main/resources/templates/index.html`

Trang chủ.

`src/main/resources/templates/about.html`

Trang giới thiệu.

`src/main/resources/templates/properties/list.html`

Danh sách tour. Template vẫn nằm trong thư mục `properties` vì giữ cấu trúc cũ.

`src/main/resources/templates/properties/detail.html`

Chi tiết tour. Có:

- Gallery/ảnh.
- Giá, số ngày, số đêm.
- Điểm nhấn hành trình.
- Block địa điểm/dịch vụ nổi bật từ model.
- Form tư vấn/đặt tour.

`src/main/resources/templates/travel/itinerary.html`

Trang lịch trình. Gồm:

- Phần form demo tạo lịch trình.
- Khối recommendation thật: chọn destination, chọn địa điểm, chọn dịch vụ, nhận gợi ý.

`src/main/resources/templates/travel/destinations.html`

Trang điểm đến dạng thông tin/static.

`src/main/resources/templates/auth/login.html`, `auth/register.html`

Demo UI đăng nhập/đăng ký.

`src/main/resources/templates/contact/index.html`

Trang liên hệ. Cần kiểm tra/sửa `th:object` nếu muốn dùng thật.

## Static assets

`src/main/resources/static/assets/css/style.css`

CSS toàn site, có cả style cho recommendation block.

`src/main/resources/static/assets/js/main.js`

JS chung của layout.

`src/main/resources/static/assets/js/recommendation.js`

JS cho `/itinerary`:

- Load destinations.
- Load places/services theo destination.
- Render chip checkbox địa điểm/dịch vụ.
- Gửi `POST /api/recommend/full`.
- Render 4 cột kết quả.
- Cho phép click gợi ý để thêm vào tiêu chí.
- Render tour phù hợp và link `/tours/{id}` nếu `bookable=true`.

## Route map

| Route | Loại | Trạng thái | Ghi chú |
| --- | --- | --- | --- |
| `/` | MVC | Đang dùng | Trang chủ |
| `/about` | MVC | Đang dùng | Giới thiệu |
| `/tours` | MVC | Đang dùng | List tour từ DB |
| `/properties` | MVC | Alias | Alias của `/tours` |
| `/tours/{id}` | MVC | Đang dùng | Detail tour |
| `/properties/{id}` | MVC | Alias | Alias detail |
| `/tours/{id}/inquiry` | POST | Đang dùng | Lưu `inquiries` |
| `/itinerary` | MVC | Đang dùng một phần | Form trên demo, recommendation thật |
| `/destinations` | MVC | Static/demo | Chưa có CRUD destination |
| `/agents` | MVC | Đang dùng | List agents |
| `/agents/{id}` | MVC | Đang dùng | Detail agent |
| `/blog` | MVC | Đang dùng | List blog |
| `/blog/{id}` | MVC | Đang dùng | Detail blog |
| `/contact` | MVC/POST | Cần kiểm tra template | Controller lưu thật, template có lỗi `th:object` |
| `/newsletter` | POST | Demo | Flash message, chưa lưu DB |
| `/login` | MVC | Demo | Không auth thật |
| `/register` | MVC | Demo | Không tạo user thật |
| `/api/recommend/...` | REST | Đang dùng | Recommendation v11 |

## Luồng tour detail và inquiry

1. User vào `/itinerary`.
2. User chọn destination, địa điểm, dịch vụ.
3. JS gọi `/api/recommend/full`.
4. API trả `recommendedTours.tours`.
5. Nếu tour đã import vào DB, object tour có:
   - `propertyId`
   - `detailUrl=/tours/{propertyId}`
   - `bookable=true`
6. User click `Xem tour`.
7. Mở `/tours/{id}`.
8. Form tư vấn gửi `POST /tours/{id}/inquiry`.
9. `PropertyController.sendInquiry` set `inquiry.property = property`.
10. `InquiryService.save` lưu vào bảng `inquiries`.

## Cách chạy và test nhanh

Từ thư mục project:

```powershell
cd F:\webdulich\webdulich
C:\Users\PC\.m2\wrapper\dists\apache-maven-3.9.15-bin\4rlcemksed9vjmkvgss0jpc4po\apache-maven-3.9.15\bin\mvn.cmd spring-boot:run
```

Nếu Maven có trong PATH thì có thể dùng:

```powershell
mvn spring-boot:run
```

URL test:

- `http://localhost:8080/`
- `http://localhost:8080/tours`
- `http://localhost:8080/itinerary`
- `http://localhost:8080/api/recommend/destinations`
- `http://localhost:8080/api/recommend/places?destinationKey=da_lat`
- `http://localhost:8080/api/recommend/services?destinationKey=da_lat`

Ví dụ body test full:

```json
{
  "destinationKey": "vung_tau",
  "selectedPlaces": ["Ngọn Hải Đăng Vũng Tàu"],
  "selectedServices": ["hotel"],
  "topK": 5
}
```

## Nguyên tắc khi sửa tiếp

- Đọc file liên quan trước khi sửa.
- Không refactor lớn nếu user chỉ yêu cầu sửa một logic cụ thể.
- Không thay `Property` bằng `Tour` nếu không có yêu cầu riêng.
- Không thay model v11 bằng v10.
- Không sửa `/tours` để lấy trực tiếp từ JSON; tour phù hợp trên web nên là dữ liệu database khi `bookable=true`.
- Recommendation không được crawl, không đọc CSV, không build model mới trong project web.
- Khi sửa recommendation, test cả 3 destination: `da_lat`, `phan_thiet`, `vung_tau`.
- Với gợi ý tiếp theo, không được đề xuất lại tiêu chí đã chọn.
- Với tour phù hợp, không được giữ tour thiếu địa điểm/dịch vụ đã chọn.
- Với nhãn gợi ý luật, dùng `recommendation_level`, không dùng nhãn thô `rule_level` hoặc `place_level` trên UI.
