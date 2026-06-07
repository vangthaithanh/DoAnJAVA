# MODEL CONTRACT V11

## Mục tiêu
Model luật kết hợp đa điểm đến cho gợi ý địa điểm, dịch vụ và tour. Mỗi destination là một model độc lập.

## Cấu trúc
`metadata_global.json`, `destinations.json`, và mỗi folder destination gồm:
`metadata.json`, `places.json`, `services.json`, `place_rules.json`,
`service_rules.json`, `place_to_service_stats.json`, `transactions_places.json`,
`transactions_services.json`, `tours.json`, `tour_recommendation_index.json`.

## Input
```json
{"destinationKey":"da_lat","selectedPlaces":[],"selectedServices":[],"topK":5}
```

## Output
```json
{"destination":{},"selectedPlaces":[],"selectedServices":[],"nextPlaces":{},"nextServices":{},"placeServices":{},"recommendedTours":{},"warnings":[],"modelReadiness":"ready"}
```

## Readiness
- `ready`: đủ dữ liệu để dùng mặc định.
- `limited`: có dữ liệu hữu ích nhưng chưa đạt ngưỡng ready.
- `not_ready`: dữ liệu quá ít; web nên hiện cảnh báo và không quảng bá gợi ý.

## Quy tắc tích hợp
- Không trộn destination.
- Ưu tiên strong rules, chỉ dùng reference rules khi thiếu kết quả.
- Rare places không hiển thị mặc định.
- Service rules chỉ dùng khi đủ dữ liệu; null nghĩa là unknown.
- Web Java chỉ đọc JSON artifact khi khởi động, không cần biết crawler hoặc CSV.
