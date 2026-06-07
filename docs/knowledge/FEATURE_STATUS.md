# WebDuLich - Trạng thái chức năng

Tài liệu này phân loại phần đã làm thật, phần đang demo, phần có dữ liệu nhưng chưa có code hoàn chỉnh. Mục tiêu là tránh để chat mới hiểu nhầm rằng mọi bảng trong SQL đều đã có chức năng tương ứng.

## Đã làm thật / có thể dùng

### Trang chủ và giới thiệu

Route:

- `/`
- `/about`

Trạng thái:

- Render bằng Thymeleaf.
- Dùng dữ liệu từ service/repository cho một số block tour/agent/blog nếu có.
- Chủ yếu là landing page.

### Danh sách tour

Route:

- `/tours`
- `/properties`

Controller:

- `PropertyController.list`

Service/repository:

- `PropertyService.search`
- `PropertyRepository.searchProperties`

Trạng thái:

- Lấy dữ liệu từ bảng `properties`.
- Hỗ trợ filter query param:
  - `keyword`
  - `type`
  - `location`
  - `status`
  - `minPrice`
  - `maxPrice`
- Template vẫn nằm ở `templates/properties/list.html`.

Ghi chú:

- Đây là trang tour thật của web, không lấy trực tiếp từ JSON model.
- Các tour import từ model v11 cũng nằm trong bảng `properties` nên có thể xuất hiện ở đây.

### Chi tiết tour

Route:

- `/tours/{id}`
- `/properties/{id}`

Controller:

- `PropertyController.detail`

Template:

- `templates/properties/detail.html`

Trạng thái:

- Lấy tour theo `Property.id`.
- Hiển thị title, giá, ảnh, vị trí, số ngày/số đêm, mô tả.
- Nếu tour là tour import từ model, hiển thị thêm:
  - các địa điểm sẽ đi từ `model_places`
  - dịch vụ nổi bật từ `model_services`
- Có form tư vấn/đặt tour.

### Form tư vấn/đặt tour

Route:

- `POST /tours/{id}/inquiry`
- `POST /properties/{id}/inquiry`

Controller:

- `PropertyController.sendInquiry`

Entity/service:

- `Inquiry`
- `InquiryService`
- `InquiryRepository`

DB:

- Bảng `inquiries`

Trạng thái:

- Có validation cho họ tên và email.
- Không cần đăng nhập.
- Không cần thanh toán.
- Không tạo booking phức tạp.
- Gửi thành công thì lưu `property_id` của tour hiện tại và flash message thành công.

Field form:

- Họ tên
- Email
- Số điện thoại
- Nội dung/yêu cầu tư vấn

Ghi chú:

- Tour import từ model vẫn gửi inquiry được nếu có row trong bảng `properties`.

### Recommendation itinerary v11

Route MVC:

- `/itinerary`

REST API:

- `/api/recommend/destinations`
- `/api/recommend/places`
- `/api/recommend/services`
- `/api/recommend/next-places`
- `/api/recommend/next-services`
- `/api/recommend/place-services`
- `/api/recommend/tours`
- `/api/recommend/full`

Files:

- `RecommendationController.java`
- `RecommendationRequest.java`
- `RecommendationService.java`
- `templates/travel/itinerary.html`
- `static/assets/js/recommendation.js`
- `static/assets/css/style.css`

Trạng thái:

- Chọn destination.
- Load địa điểm theo destination.
- Load dịch vụ theo destination.
- Chọn địa điểm/dịch vụ bằng chip checkbox.
- Gợi ý địa điểm tiếp theo.
- Gợi ý dịch vụ tiếp theo.
- Gợi ý dịch vụ thường đi kèm địa điểm.
- Gợi ý tour phù hợp.
- Click gợi ý để thêm vào tiêu chí.
- Tour phù hợp có link `Xem tour` nếu đã import vào database.

Luồng dữ liệu:

- Model JSON v11 đọc từ classpath.
- Tour recommendation được enrich bằng bảng `properties`.
- Không đọc CSV.
- Không gọi crawler.
- Không trộn dữ liệu giữa destinations.

### Blog

Route:

- `/blog`
- `/blog/{id}`

Files:

- `BlogController`
- `BlogService`
- `BlogPost`
- `BlogPostRepository`
- `templates/blog/list.html`
- `templates/blog/detail.html`

Trạng thái:

- Đọc blog từ DB.
- DataSeeder tạo dữ liệu mẫu nếu bảng trống.

### Agents

Route:

- `/agents`
- `/agents/{id}`

Files:

- `AgentController`
- `AgentService`
- `Agent`
- `AgentRepository`
- `templates/agents/list.html`
- `templates/agents/detail.html`

Trạng thái:

- Đọc agent từ DB.
- DataSeeder tạo 3 agent nếu bảng trống.
- Agent có thể liên kết với tour qua `Property.agent`.

### Contact service

Controller/service/entity có:

- `ContactController`
- `ContactService`
- `ContactMessage`
- `ContactMessageRepository`

DB có:

- `contact_messages`

Trạng thái code backend:

- `GET /contact` tạo model attribute `contactMessage`.
- `POST /contact` validate và gọi `ContactService.save`.

Vấn đề template:

- `templates/contact/index.html` hiện đang có `th:object="${contactNội dung cần tư vấn}"`.
- Đúng ra phải là `th:object="${contactMessage}"`.
- Nút hiện ghi `Send Nội dung cần tư vấn`, nên sửa thành text tiếng Việt rõ ràng nếu dùng trang này thật.

Kết luận:

- Backend contact đã có.
- UI contact cần sửa trước khi coi là ổn định.

## Demo / chưa hoàn thiện

### Đăng nhập

Route:

- `GET /login`

Files:

- `AuthController.login`
- `templates/auth/login.html`

Trạng thái:

- Chỉ render template.
- Chưa có Spring Security.
- Chưa có `POST /login` tự xử lý.
- Chưa map entity `User`.
- Chưa có session/auth role.

### Đăng ký

Route:

- `GET /register`

Files:

- `AuthController.register`
- `templates/auth/register.html`

Trạng thái:

- Chỉ render template.
- Chưa có `POST /register`.
- Chưa lưu user.
- Chưa hash password trong Java.

### Users/profiles trong SQL

Trong `database/webdulich_mysql.sql` có:

- `users`
- `profiles`

Trạng thái:

- Có bảng và dữ liệu demo trong SQL nền.
- Chưa có Java entity/repository/service/controller tương ứng.
- Chưa liên kết login/register với bảng này.

### Form tạo lịch trình ở `/itinerary`

Trang `/itinerary` có form phía trên để nhập điểm đến, số ngày, ngân sách, phong cách.

Trạng thái:

- Demo UI.
- Chưa lưu `custom_itineraries`.
- Chưa sinh lịch trình theo ngày.
- Phần recommendation bên dưới mới là chức năng thật.

### Custom itineraries trong SQL

Trong SQL nền có:

- `custom_itineraries`
- `custom_itinerary_days`
- `custom_itinerary_items`

Trạng thái:

- Có schema và dữ liệu mẫu.
- Chưa có entity/repository/service/controller Java.
- Chưa có CRUD lịch trình thật.

### Reviews

Trong SQL có:

- `reviews`

Trạng thái:

- Chưa có entity/repository/service/controller.
- Chưa có UI submit review.
- Chưa liên kết với auth/user.

### Favorites

Trong SQL có:

- `favorite_tours`

Trạng thái:

- Chưa có entity/repository/service/controller.
- Nút trái tim/favorite trên UI chỉ là hiệu ứng JS nếu có.
- Chưa lưu DB.

### Newsletter

Route:

- `POST /newsletter`

Trạng thái:

- Chỉ nhận email và flash message.
- Chưa lưu database.
- Chưa gửi email.

### Destinations page

Route:

- `/destinations`

Trạng thái:

- Trang thông tin/static.
- Không phải CRUD destination.
- Không dùng bảng `destinations` trong SQL theo logic đầy đủ.

### Admin

Trạng thái:

- Chưa có dashboard admin.
- Chưa có CRUD tour/blog/agent/user.
- Chưa có phân quyền.

### Thanh toán / booking

Trạng thái:

- Chưa có booking entity riêng.
- Chưa có thanh toán.
- Luồng hiện tại chỉ là gửi yêu cầu tư vấn.

## Cần cẩn thận khi sửa

### Không hiểu nhầm `Property`

`Property` là tên cũ từ template bất động sản. Hiện nó là tour. Các field như `bedrooms/bathrooms/parking/area/yearBuilt` đang được tận dụng cho tour.

Nếu cần làm sạch domain model sau này, nên làm thành một task refactor riêng có migration rõ ràng. Không làm lẫn trong task recommendation hoặc UI nhỏ.

### Không hiểu nhầm base SQL là DB hiện tại

`database/webdulich_mysql.sql` là script nền. Nó chưa chắc khớp hoàn toàn DB hiện tại sau khi Hibernate update và import model v11.

Các cột model v11 nằm trong:

- `Property.java`
- `database/import_model_tours_v11.sql`

### Không thay tour recommendation về JSON-only

Yêu cầu hiện tại của user: tour phù hợp bên phải phải là dữ liệu database nếu có thể book/xem detail. Vì vậy recommendation trả tour model nhưng enrich bằng `properties`.

### Không để tour không phù hợp còn hiển thị

Logic hiện tại nên giữ:

- Nếu user đã chọn địa điểm/dịch vụ, tour phải chứa đủ tiêu chí đó.
- Tour thiếu tiêu chí không được hiện trong `Tour phù hợp`.

### Không đề xuất lại tiêu chí đã chọn

Các block sau phải loại bỏ item đã chọn:

- Địa điểm tiếp theo.
- Dịch vụ tiếp theo.
- Dịch vụ theo địa điểm.

### Nhãn luật

UI không hiển thị text kỹ thuật như `Luật mạnh`, `Luật tham khảo`.

Quy ước:

- `recommendation_level=manh` -> nhãn `Nên chọn`, màu xanh lá.
- `recommendation_level=tham_khao` -> nhãn `Tham khảo`, màu vàng.

Không dùng `place_level=core/rare` làm nhãn luật. `place_level` là phân loại dữ liệu địa điểm, không phải độ mạnh của luật.

## Known issue cần ghi nhớ

`templates/contact/index.html` có lỗi rõ ở `th:object`. Nếu người dùng yêu cầu hoàn thiện contact, sửa nhẹ file này trước rồi test `GET /contact` và `POST /contact`.

## Những thứ đã từng được test trong các phiên trước

Các phần đã được kiểm tra khi nâng model:

- Import 147 tour v11 vào `properties`.
- Không trùng `model_ma_tour`.
- `model_services` là JSON array hợp lệ.
- Recommendation full cho `da_lat`, `phan_thiet`, `vung_tau`.
- Tour recommendation chỉ trả exact match.
- Gợi ý không trả item đã chọn.
- Nhãn `Nên chọn`/`Tham khảo` dựa trên `recommendation_level`.
- Form inquiry tại `/tours/{id}` lưu được vào `inquiries` trong luồng tour import.

Nếu tiếp tục sửa, vẫn nên test lại phần bị ảnh hưởng thay vì chỉ tin vào kết quả cũ.
