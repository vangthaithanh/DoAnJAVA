# webdulich - Website tour du lịch Việt Nam

Project này được chỉnh từ bản giao diện villa/tour trước đó sang hướng **website du lịch trong nước Việt Nam**.

## Hướng hiện tại

Phần người dùng hiện tập trung vào 2 chức năng chính:

1. **Bán tour du lịch trong nước**
   - Xem danh sách tour.
   - Lọc tour theo vùng miền, điểm đến, ngân sách.
   - Xem chi tiết tour.
   - Gửi yêu cầu tư vấn/đặt tour.

2. **Tự tạo lịch trình du lịch**
   - Giao diện nhập điểm đến, số ngày, ngân sách, phong cách du lịch.
   - Database đã có sẵn bảng mẫu cho lịch trình tự tạo.
   - Phần lưu lịch trình vào tài khoản cá nhân sẽ làm tiếp khi triển khai đăng nhập thật.

Ngoài ra có phần **xem điểm đến Việt Nam** để mô phỏng chức năng địa điểm/check-in như các web cộng đồng du lịch.

## Các route chính

```text
/                       Trang chủ
/tours                  Danh sách tour + gợi ý tour đã gộp chung
/tours/{id}             Chi tiết tour
/itinerary              Tạo lịch trình du lịch
/destinations           Xem điểm đến Việt Nam
/agents                 Tư vấn viên
/blog                   Cẩm nang du lịch
/contact                Liên hệ
/login                  Giao diện đăng nhập
/register               Giao diện đăng ký
```

Các route quản lý như thêm/sửa/xóa tour đã được gỡ khỏi Controller người dùng. Phần quản lý sẽ tách riêng sau.

## Database MySQL

Project mặc định kết nối MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/webdulich_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
```

File SQL chính:

```text
database/webdulich_mysql.sql
```

File này có:

```text
- users
- profiles
- agents
- properties              // hiện được dùng như bảng tours để giảm rủi ro refactor
- destinations
- tour_destinations
- blog_posts
- contact_messages
- inquiries
- reviews
- favorite_tours
- custom_itineraries
- custom_itinerary_days
- custom_itinerary_items
```

Dữ liệu mẫu gồm:

```text
- 30 tour trong nước Việt Nam
- 24 điểm đến Việt Nam
- 3 tư vấn viên
- 6 bài viết cẩm nang
- 1 tài khoản demo
- 1 lịch trình tự tạo mẫu
```

## Cách chạy database

Mở MySQL Workbench hoặc terminal MySQL rồi chạy:

```sql
source đường_dẫn_tới_file/database/webdulich_mysql.sql;
```

Hoặc copy toàn bộ nội dung file `webdulich_mysql.sql` và chạy trong MySQL Workbench.

## Cách chạy project

```bash
mvn spring-boot:run
```

Sau đó mở:

```text
http://localhost:8080
```

## Lưu ý kỹ thuật

Để giữ giao diện ổn và tránh lỗi refactor quá nhiều, backend hiện vẫn dùng tên cũ:

```text
Property
PropertyController
PropertyService
PropertyRepository
```

Nhưng trên giao diện và database nghiệp vụ, chúng đang được hiểu là **Tour**.

Bước refactor sạch sau này:

```text
Property            -> Tour
properties          -> tours
PropertyController  -> TourController
PropertyService     -> TourService
PropertyRepository  -> TourRepository
```

## Phần quản lý

Phần hiện tại là hướng **người dùng**. Những chức năng quản lý như thêm/sửa/xóa tour, quản lý đơn đặt tour, quản lý địa điểm, duyệt đánh giá, quản lý tài khoản admin sẽ tách riêng sau ở nhóm route `/admin`.
