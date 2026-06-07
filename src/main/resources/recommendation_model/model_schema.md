# Model artifacts v10

## Mục tiêu

Các file JSON là đầu vào độc lập cho recommendation engine và có thể được đọc
khi Spring Boot service khởi động. Engine dự đoán không đọc CSV.

## Artifact

| File | Nội dung |
| --- | --- |
| `metadata.json` | Phiên bản model, nguồn dữ liệu, filter, ngưỡng luật và giới hạn đã biết |
| `places.json` | Địa điểm, `tour_count`, `transaction_count`, phân loại `core` hoặc `rare` |
| `place_rules.json` | Map `antecedent -> tối đa 10 luật`, strong đứng trước reference |
| `transactions.json` | Map `transaction_id -> {transaction_type, ma_tour, ngay_tour, items}` |
| `services.json` | Nhãn dịch vụ, cột flag và dữ liệu theo tour |
| `tours.json` | Tour sạch, place list, missing fields và quality score |
| `tour_recommendation_index.json` | Chỉ mục place/service -> tour và quality theo tour |

## Quy tắc sử dụng

- `tour_level` bao phủ toàn bộ lịch trình tour.
- `day_level` ưu tiên cho luật gần thực tế hơn khi lịch trình ngày tồn tại.
- `place_level=core` khi `tour_count >= 3`; `rare` được giữ nhưng mặc định ẩn.
- Flag dịch vụ null nghĩa là nguồn không đủ dữ liệu để kết luận.
- Không diễn giải luật kết hợp này là dữ liệu hành vi người dùng.
