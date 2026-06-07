CREATE DATABASE IF NOT EXISTS webdulich_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE webdulich_db;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS custom_itinerary_items;
DROP TABLE IF EXISTS custom_itinerary_days;
DROP TABLE IF EXISTS custom_itineraries;
DROP TABLE IF EXISTS favorite_tours;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS inquiries;
DROP TABLE IF EXISTS contact_messages;
DROP TABLE IF EXISTS blog_posts;
DROP TABLE IF EXISTS tour_destinations;
DROP TABLE IF EXISTS destinations;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS agents;
DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  phone VARCHAR(30),
  password_hash VARCHAR(255) NOT NULL,
  avatar_url VARCHAR(500),
  role VARCHAR(30) NOT NULL DEFAULT 'USER',
  status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE profiles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  preferred_budget DECIMAL(15,2),
  preferred_style VARCHAR(120),
  home_city VARCHAR(120),
  note TEXT,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE agents (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(120) NOT NULL,
  role VARCHAR(100),
  email VARCHAR(120),
  phone VARCHAR(30),
  avatar_url VARCHAR(500),
  bio TEXT,
  facebook_url VARCHAR(120),
  twitter_url VARCHAR(120),
  linkedin_url VARCHAR(120)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE properties (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  price DECIMAL(15,2) NOT NULL,
  image_url VARCHAR(500),
  gallery_image_one VARCHAR(500),
  gallery_image_two VARCHAR(500),
  gallery_image_three VARCHAR(500),
  location VARCHAR(255) NOT NULL,
  status VARCHAR(50),
  type VARCHAR(80),
  city VARCHAR(80),
  bedrooms INT,
  bathrooms INT,
  parking INT,
  area INT,
  year_built INT,
  description TEXT,
  featured BIT DEFAULT 0,
  agent_id BIGINT,
  FOREIGN KEY (agent_id) REFERENCES agents(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE destinations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(150) NOT NULL,
  region VARCHAR(80),
  summary TEXT,
  tags VARCHAR(255),
  image_url VARCHAR(500),
  status VARCHAR(30) DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE tour_destinations (
  tour_id BIGINT NOT NULL,
  destination_id BIGINT NOT NULL,
  PRIMARY KEY (tour_id, destination_id),
  FOREIGN KEY (tour_id) REFERENCES properties(id),
  FOREIGN KEY (destination_id) REFERENCES destinations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE blog_posts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(180) NOT NULL,
  category VARCHAR(80),
  image_url VARCHAR(500),
  author VARCHAR(100),
  published_date DATE,
  summary VARCHAR(300),
  content TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE contact_messages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL,
  phone VARCHAR(40),
  message TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE inquiries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(120) NOT NULL,
  phone VARCHAR(30),
  message TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  property_id BIGINT,
  FOREIGN KEY (property_id) REFERENCES properties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE reviews (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  tour_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (tour_id) REFERENCES properties(id),
  CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE favorite_tours (
  user_id BIGINT NOT NULL,
  tour_id BIGINT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, tour_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (tour_id) REFERENCES properties(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE custom_itineraries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  title VARCHAR(180) NOT NULL,
  destination_text VARCHAR(255),
  total_days INT NOT NULL,
  budget DECIMAL(15,2),
  travel_style VARCHAR(100),
  note TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE custom_itinerary_days (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  itinerary_id BIGINT NOT NULL,
  day_number INT NOT NULL,
  title VARCHAR(180),
  FOREIGN KEY (itinerary_id) REFERENCES custom_itineraries(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE TABLE custom_itinerary_items (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  day_id BIGINT NOT NULL,
  time_text VARCHAR(50),
  place_name VARCHAR(180),
  activity TEXT,
  estimated_cost DECIMAL(15,2),
  FOREIGN KEY (day_id) REFERENCES custom_itinerary_days(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO agents (full_name, role, email, phone, avatar_url, bio, facebook_url, twitter_url, linkedin_url) VALUES
  ('Nguyễn Hoàng Lan','Tư vấn tour gia đình','lan@webdulich.vn','0903 111 222','https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80','Chuyên tư vấn tour trong nước cho gia đình, nhóm bạn và khách đoàn nhỏ.','#','#','#'),
  ('Trần Minh Khang','Tư vấn tour miền Bắc - miền Trung','minh@webdulich.vn','0903 333 444','https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80','Có kinh nghiệm thiết kế hành trình Hà Nội, Hạ Long, Đà Nẵng, Huế và Hội An.','#','#','#'),
  ('Lê Thu Thảo','Tư vấn lịch trình tự tạo','thao@webdulich.vn','0903 555 666','https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=500&q=80','Hỗ trợ khách tự tạo lịch trình theo ngân sách, số ngày và phong cách du lịch.','#','#','#');

INSERT INTO destinations (name, region, summary, tags, image_url) VALUES
  ('Đà Nẵng','Miền Trung','Biển Mỹ Khê, Bà Nà Hills, Sơn Trà, Ngũ Hành Sơn và cầu Rồng.','biển,gia đình,checkin','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80'),
  ('Hội An','Miền Trung','Phố cổ, đèn lồng, sông Hoài, làng gốm và ẩm thực Quảng Nam.','di sản,văn hóa,ẩm thực','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=900&q=80'),
  ('Huế','Miền Trung','Đại Nội, chùa Thiên Mụ, lăng tẩm, sông Hương và ẩm thực cố đô.','di sản,văn hóa,tâm linh','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
  ('Quảng Bình','Miền Trung','Phong Nha, Thiên Đường, Nhật Lệ và hệ thống hang động nổi bật.','hang động,thiên nhiên,khám phá','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80'),
  ('Hà Nội','Miền Bắc','Phố cổ, hồ Hoàn Kiếm, Văn Miếu, ẩm thực và văn hóa thủ đô.','văn hóa,ẩm thực,lịch sử','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80'),
  ('Hạ Long','Miền Bắc','Vịnh Hạ Long, du thuyền, hang động và cảnh quan kỳ quan thiên nhiên.','du thuyền,biển,gia đình','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=900&q=80'),
  ('Ninh Bình','Miền Bắc','Tràng An, Bái Đính, Hang Múa, Tam Cốc và cảnh quan non nước.','thiên nhiên,tâm linh,checkin','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
  ('Sapa','Miền Bắc','Fansipan, bản Cát Cát, ruộng bậc thang và khí hậu vùng cao.','núi,rừng,khám phá','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80'),
  ('Hà Giang','Miền Bắc','Đồng Văn, Mã Pì Lèng, Quản Bạ và cung đường đá hùng vĩ.','phượt,núi,khám phá','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80'),
  ('Mộc Châu','Miền Bắc','Đồi chè, thác Dải Yếm, bản làng và khí hậu mát mẻ.','nghỉ dưỡng,checkin,núi','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=900&q=80'),
  ('Phú Quốc','Biển đảo','Grand World, VinWonders, Safari, Hòn Thơm và biển đảo nghỉ dưỡng.','biển,nghỉ dưỡng,gia đình','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
  ('Nha Trang','Biển đảo','Du ngoạn đảo, VinWonders, tắm biển và hải sản.','biển,vui chơi,ẩm thực','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80'),
  ('Quy Nhơn','Miền Trung','Kỳ Co, Eo Gió, biển xanh và cảnh quan hoang sơ.','biển,checkin,nghỉ dưỡng','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80'),
  ('Phú Yên','Miền Trung','Gành Đá Đĩa, Mũi Điện, Bãi Xép và thiên nhiên yên bình.','biển,checkin,thiên nhiên','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=900&q=80'),
  ('Đà Lạt','Tây Nguyên','Rừng thông, khí hậu mát mẻ, quán cà phê và điểm check-in.','nghỉ dưỡng,cặp đôi,checkin','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
  ('Buôn Ma Thuột','Tây Nguyên','Thác Dray Nur, Buôn Đôn, bảo tàng cà phê và văn hóa Tây Nguyên.','văn hóa,cà phê,thiên nhiên','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80'),
  ('Cần Thơ','Miền Nam','Chợ nổi Cái Răng, bến Ninh Kiều và sông nước miền Tây.','sông nước,ẩm thực,văn hóa','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80'),
  ('Châu Đốc','Miền Nam','Miếu Bà Chúa Xứ, rừng tràm Trà Sư và mùa nước nổi.','tâm linh,sông nước,thiên nhiên','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=900&q=80'),
  ('Cà Mau','Miền Nam','Đất Mũi, rừng ngập mặn và hành trình cực Nam Tổ quốc.','sông nước,thiên nhiên,khám phá','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
  ('Vũng Tàu','Biển đảo','Bãi Sau, tượng Chúa Kitô và tour cuối tuần gần TP.HCM.','biển,cuối tuần,gia đình','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80'),
  ('Phan Thiết','Biển đảo','Mũi Né, Bàu Trắng, đồi cát và resort biển.','biển,nghỉ dưỡng,checkin','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=900&q=80'),
  ('Côn Đảo','Biển đảo','Bãi Đầm Trầu, Hàng Dương, di tích lịch sử và biển trong xanh.','biển,tâm linh,lịch sử','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=900&q=80'),
  ('Tây Ninh','Miền Nam','Núi Bà Đen, Tòa Thánh Cao Đài và tour trong ngày.','tâm linh,leo núi,cuối tuần','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80'),
  ('Cần Giờ','Miền Nam','Rừng Sác, đảo khỉ, biển 30/4 và sinh thái gần TP.HCM.','sinh thái,cuối tuần,gia đình','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80');

INSERT INTO properties (title, price, image_url, gallery_image_one, gallery_image_two, gallery_image_three, location, status, type, city, bedrooms, bathrooms, parking, area, year_built, description, featured, agent_id) VALUES
  ('Đà Nẵng 3N2Đ | Bà Nà - Hội An - Sơn Trà',6399000,'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','Đà Nẵng - Bà Nà Hills - Bán đảo Sơn Trà - Ngũ Hành Sơn - Hội An','Đang mở bán','Tour miền Trung','Đà Nẵng',3,2,24,100,2026,'Cầu Vàng Bà Nà, biển Mỹ Khê, phố cổ Hội An về đêm. Tuyến: Đà Nẵng - Bà Nà Hills - Bán đảo Sơn Trà - Ngũ Hành Sơn - Hội An. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',1,1),
  ('Đà Nẵng 4N3Đ | Bà Nà - Hội An - Huế - Phong Nha',7799000,'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','Đà Nẵng - Hội An - Huế - Động Phong Nha','Đang mở bán','Tour miền Trung','Đà Nẵng',4,3,20,107,2026,'Khám phá miền Trung với di sản, biển xanh và hang động nổi bật. Tuyến: Đà Nẵng - Hội An - Huế - Động Phong Nha. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Đà Nẵng 5N4Đ | Bà Nà - Hội An - Huế - La Vang',8999000,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','Đà Nẵng - Bà Nà - Hội An - Huế - La Vang - Động Thiên Đường','Đang mở bán','Tour miền Trung','Đà Nẵng',5,4,18,114,2026,'Hành trình dài ngày cho gia đình yêu thích di sản miền Trung. Tuyến: Đà Nẵng - Bà Nà - Hội An - Huế - La Vang - Động Thiên Đường. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Huế 3N2Đ | Đại Nội - Thiên Mụ - Lăng Khải Định',4590000,'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','Huế - Đại Nội - Chùa Thiên Mụ - Lăng Khải Định - Sông Hương','Đang mở bán','Tour miền Trung','Huế',3,2,26,121,2026,'Trải nghiệm cố đô, ẩm thực Huế và du thuyền sông Hương. Tuyến: Huế - Đại Nội - Chùa Thiên Mụ - Lăng Khải Định - Sông Hương. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Quảng Bình 3N2Đ | Phong Nha - Thiên Đường - Nhật Lệ',5290000,'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','Đồng Hới - Động Phong Nha - Động Thiên Đường - Biển Nhật Lệ','Đang mở bán','Tour miền Trung','Quảng Bình',3,2,20,128,2026,'Khám phá hệ thống hang động và biển miền Trung. Tuyến: Đồng Hới - Động Phong Nha - Động Thiên Đường - Biển Nhật Lệ. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Hà Nội - Hạ Long - Ninh Bình - Tràng An 4N3Đ',6499000,'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','Hà Nội - Vịnh Hạ Long - Ninh Bình - Tràng An','Đang mở bán','Tour miền Bắc','Hà Nội',4,3,22,135,2026,'Thủ đô nghìn năm, kỳ quan Hạ Long và non nước Tràng An. Tuyến: Hà Nội - Vịnh Hạ Long - Ninh Bình - Tràng An. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',1,3),
  ('Hà Nội - Sapa - Fansipan 3N2Đ',6999000,'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','Hà Nội - Sapa - Bản Cát Cát - Fansipan','Đang mở bán','Tour miền Bắc','Sapa',3,2,18,142,2026,'Chinh phục nóc nhà Đông Dương và bản làng Tây Bắc. Tuyến: Hà Nội - Sapa - Bản Cát Cát - Fansipan. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe giường nằm/xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Hà Nội - Yên Tử - Hạ Long - Ninh Bình - Sapa 6N5Đ',11990000,'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','Hà Nội - Yên Tử - Hạ Long - Ninh Bình - Sapa - Fansipan','Đang mở bán','Tour miền Bắc','Hạ Long',6,5,16,149,2026,'Tour miền Bắc dài ngày kết hợp tâm linh, di sản và núi rừng. Tuyến: Hà Nội - Yên Tử - Hạ Long - Ninh Bình - Sapa - Fansipan. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Ninh Bình 2N1Đ | Tràng An - Bái Đính - Hang Múa',2490000,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','Ninh Bình - Tràng An - Bái Đính - Hang Múa','Đang mở bán','Tour miền Bắc','Ninh Bình',2,1,28,156,2026,'Lịch trình ngắn ngày phù hợp cuối tuần. Tuyến: Ninh Bình - Tràng An - Bái Đính - Hang Múa. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Hạ Long 2N1Đ | Du thuyền vịnh - Sun World',3290000,'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','Hạ Long - Du thuyền vịnh - Hang Sửng Sốt - Sun World','Đang mở bán','Tour miền Bắc','Hạ Long',2,1,20,163,2026,'Nghỉ đêm gần vịnh, tham quan kỳ quan thiên nhiên. Tuyến: Hạ Long - Du thuyền vịnh - Hang Sửng Sốt - Sun World. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Mộc Châu 2N1Đ | Đồi chè - Thác Dải Yếm',1990000,'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','Mộc Châu - Đồi chè Trái Tim - Thác Dải Yếm - Cầu kính','Đang mở bán','Tour miền Bắc','Mộc Châu',2,1,30,170,2026,'Khí hậu mát mẻ, đồi chè và cảnh núi Tây Bắc. Tuyến: Mộc Châu - Đồi chè Trái Tim - Thác Dải Yếm - Cầu kính. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Hà Giang 3N2Đ | Đồng Văn - Mã Pì Lèng',3690000,'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','Hà Giang - Quản Bạ - Đồng Văn - Mã Pì Lèng - Mèo Vạc','Đang mở bán','Tour miền Bắc','Hà Giang',3,2,18,177,2026,'Cung đường đá hùng vĩ dành cho người thích khám phá. Tuyến: Hà Giang - Quản Bạ - Đồng Văn - Mã Pì Lèng - Mèo Vạc. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch/limousine. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Phú Quốc 3N2Đ | Grand World - VinWonders - Safari',6990000,'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','Phú Quốc - Grand World - VinWonders - Safari - Cáp treo Hòn Thơm','Đang mở bán','Tour biển đảo','Phú Quốc',3,2,24,184,2026,'Nghỉ dưỡng đảo ngọc, vui chơi giải trí và khám phá biển đảo. Tuyến: Phú Quốc - Grand World - VinWonders - Safari - Cáp treo Hòn Thơm. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',1,1),
  ('Phú Quốc 4N3Đ | Nam đảo - Hòn Thơm - Sunset Town',8590000,'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','Phú Quốc - Nam Đảo - Hòn Thơm - Sunset Town - Dinh Cậu','Đang mở bán','Tour biển đảo','Phú Quốc',4,3,18,191,2026,'Biển xanh, cáp treo vượt biển và không gian nghỉ dưỡng. Tuyến: Phú Quốc - Nam Đảo - Hòn Thơm - Sunset Town - Dinh Cậu. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Nha Trang 3N3Đ | Du ngoạn 3 đảo - VinWonders',3286000,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','Nha Trang - Du ngoạn 3 đảo - VinWonders - Chợ Đầm','Đang mở bán','Tour biển đảo','Nha Trang',3,3,25,198,2026,'Biển đảo Nha Trang, vui chơi và ẩm thực hải sản. Tuyến: Nha Trang - Du ngoạn 3 đảo - VinWonders - Chợ Đầm. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch đời mới. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Nha Trang - Đà Lạt 5N4Đ | Biển xanh & cao nguyên',6290000,'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','Nha Trang - VinWonders - Đà Lạt - Mongoland - Đà Lạt View','Đang mở bán','Tour miền Trung','Nha Trang',5,4,20,205,2026,'Kết hợp biển Nha Trang và khí hậu se lạnh Đà Lạt. Tuyến: Nha Trang - VinWonders - Đà Lạt - Mongoland - Đà Lạt View. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Đà Lạt 3N2Đ | Langbiang - Mongo Land - Fresh Garden',3490000,'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','Đà Lạt - Langbiang - Fresh Garden - Mongo Land - Quảng trường Lâm Viên','Đang mở bán','Tour Tây Nguyên','Đà Lạt',3,2,28,212,2026,'Không khí mát lành, điểm check-in và ẩm thực cao nguyên. Tuyến: Đà Lạt - Langbiang - Fresh Garden - Mongo Land - Quảng trường Lâm Viên. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Buôn Ma Thuột 3N2Đ | Thác Dray Nur - Buôn Đôn',3890000,'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','Buôn Ma Thuột - Thác Dray Nur - Buôn Đôn - Bảo tàng cà phê','Đang mở bán','Tour Tây Nguyên','Buôn Ma Thuột',3,2,22,219,2026,'Khám phá văn hóa Tây Nguyên và hương vị cà phê. Tuyến: Buôn Ma Thuột - Thác Dray Nur - Buôn Đôn - Bảo tàng cà phê. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch/Máy bay. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Miền Tây 3N2Đ | Mỹ Tho - Bến Tre - Cần Thơ',1990000,'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','Mỹ Tho - Bến Tre - Cần Thơ - Chợ nổi Cái Răng','Đang mở bán','Tour miền Nam','Cần Thơ',3,2,30,226,2026,'Sông nước miền Tây, thuyền 3 lá, vườn trái cây và đờn ca tài tử. Tuyến: Mỹ Tho - Bến Tre - Cần Thơ - Chợ nổi Cái Răng. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',1,1),
  ('Miền Tây 4N3Đ | Sóc Trăng - Bạc Liêu - Cà Mau',3490000,'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','Cần Thơ - Sóc Trăng - Bạc Liêu - Cà Mau - Đất Mũi','Đang mở bán','Tour miền Nam','Cà Mau',4,3,24,233,2026,'Chạm mốc cực Nam, khám phá văn hóa Khmer và nhà Công tử Bạc Liêu. Tuyến: Cần Thơ - Sóc Trăng - Bạc Liêu - Cà Mau - Đất Mũi. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Châu Đốc - Rừng Tràm Trà Sư - Cần Thơ 3N2Đ',2350000,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','Châu Đốc - Miếu Bà Chúa Xứ - Rừng Tràm Trà Sư - Cần Thơ','Đang mở bán','Tour miền Nam','An Giang',3,2,24,240,2026,'Mùa nước nổi, rừng tràm và văn hóa miền Tây. Tuyến: Châu Đốc - Miếu Bà Chúa Xứ - Rừng Tràm Trà Sư - Cần Thơ. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Vũng Tàu 2N1Đ | Biển xanh cuối tuần',1700000,'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','TP.HCM - Vũng Tàu - Tượng Chúa Kitô - Bãi Sau','Đang mở bán','Tour biển đảo','Vũng Tàu',2,1,32,247,2026,'Tour ngắn ngày cuối tuần cho gia đình và nhóm bạn. Tuyến: TP.HCM - Vũng Tàu - Tượng Chúa Kitô - Bãi Sau. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Phan Thiết - Mũi Né 3N2Đ | Đồi cát - Bàu Trắng',2990000,'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','Phan Thiết - Mũi Né - Bàu Trắng - Lâu đài Rượu Vang','Đang mở bán','Tour biển đảo','Phan Thiết',3,2,26,254,2026,'Biển, đồi cát và resort nghỉ dưỡng gần TP.HCM. Tuyến: Phan Thiết - Mũi Né - Bàu Trắng - Lâu đài Rượu Vang. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Quy Nhơn - Phú Yên 4N3Đ | Kỳ Co - Eo Gió - Gành Đá Đĩa',6790000,'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','Quy Nhơn - Kỳ Co - Eo Gió - Phú Yên - Gành Đá Đĩa','Đang mở bán','Tour miền Trung','Quy Nhơn',4,3,20,261,2026,'Bờ biển miền Trung hoang sơ và nhiều điểm check-in đẹp. Tuyến: Quy Nhơn - Kỳ Co - Eo Gió - Phú Yên - Gành Đá Đĩa. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Côn Đảo 3N2Đ | Tâm linh & biển đảo',7290000,'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','Côn Đảo - Nghĩa trang Hàng Dương - Miếu Bà Phi Yến - Bãi Đầm Trầu','Đang mở bán','Tour biển đảo','Côn Đảo',3,2,16,268,2026,'Kết hợp nghỉ dưỡng biển và hành trình tri ân. Tuyến: Côn Đảo - Nghĩa trang Hàng Dương - Miếu Bà Phi Yến - Bãi Đầm Trầu. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Cao Bằng - Thác Bản Giốc - Hồ Ba Bể 3N2Đ',4290000,'https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','Cao Bằng - Thác Bản Giốc - Động Ngườm Ngao - Hồ Ba Bể','Đang mở bán','Tour miền Bắc','Cao Bằng',3,2,20,275,2026,'Thiên nhiên Đông Bắc, thác nước biên giới và hồ xanh. Tuyến: Cao Bằng - Thác Bản Giốc - Động Ngườm Ngao - Hồ Ba Bể. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Mai Châu - Pù Luông 3N2Đ | Bản làng & ruộng bậc thang',3290000,'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','Mai Châu - Bản Lác - Pù Luông - Kho Mường','Đang mở bán','Tour miền Bắc','Pù Luông',3,2,22,282,2026,'Nghỉ dưỡng sinh thái, bản làng và ruộng bậc thang. Tuyến: Mai Châu - Bản Lác - Pù Luông - Kho Mường. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3),
  ('Tây Ninh - Núi Bà Đen - Tòa Thánh 1N',990000,'https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','TP.HCM - Núi Bà Đen - Tòa Thánh Cao Đài','Đang mở bán','Tour miền Nam','Tây Ninh',1,0,35,289,2026,'Tour trong ngày phù hợp khách gia đình và nhóm nhỏ. Tuyến: TP.HCM - Núi Bà Đen - Tòa Thánh Cao Đài. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,1),
  ('Cần Giờ 1N | Rừng Sác - Đảo Khỉ - Biển 30/4',790000,'https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','TP.HCM - Cần Giờ - Đảo Khỉ - Rừng Sác - Biển 30/4','Đang mở bán','Tour miền Nam','Cần Giờ',1,0,35,296,2026,'Du lịch sinh thái gần thành phố, chi phí thấp. Tuyến: TP.HCM - Cần Giờ - Đảo Khỉ - Rừng Sác - Biển 30/4. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,2),
  ('Tự thiết kế lịch trình Việt Nam theo ngân sách',0,'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','Bạn chọn điểm đến - số ngày - ngân sách - phong cách, WebDuLich gợi ý lịch trình phù hợp','Theo yêu cầu','Lịch trình tự tạo','Việt Nam',3,2,99,303,2026,'Dành cho người dùng muốn tự tạo lịch trình thay vì mua tour cố định. Tuyến: Bạn chọn điểm đến - số ngày - ngân sách - phong cách, WebDuLich gợi ý lịch trình phù hợp. Khởi hành: Theo yêu cầu. Phương tiện: Tùy chọn. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.',0,3);

-- Gắn tour với điểm đến chính theo tên thành phố nếu tìm được
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (1, 1);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (2, 1);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (3, 1);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (4, 3);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (5, 4);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (6, 5);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (7, 8);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (8, 6);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (9, 7);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (10, 6);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (11, 10);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (12, 9);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (13, 11);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (14, 11);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (15, 12);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (16, 12);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (17, 15);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (18, 16);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (19, 17);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (20, 19);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (22, 20);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (23, 21);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (24, 13);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (25, 22);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (28, 23);
INSERT IGNORE INTO tour_destinations (tour_id, destination_id) VALUES (29, 24);

INSERT INTO blog_posts (title, category, image_url, author, published_date, summary, content) VALUES
  ('Cách chọn tour trong nước phù hợp ngân sách','Kinh nghiệm','https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80','WebDuLich Team','2026-05-02','Chuẩn bị ngân sách, thời gian và phong cách du lịch trước khi chọn tour giúp chuyến đi nhẹ nhàng hơn.','Khi chọn tour trong nước, bạn nên so sánh lịch trình, điểm tham quan chính, phương tiện, khách sạn, bữa ăn, chính sách trẻ em và ngày khởi hành.'),
  ('Gợi ý lịch trình Đà Nẵng - Hội An 3 ngày 2 đêm','Lịch trình','https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80','WebDuLich Team','2026-06-05','Một hành trình ngắn ngày có thể kết hợp biển Mỹ Khê, Bà Nà Hills và phố cổ Hội An.','Ngày đầu tham quan Sơn Trà và Mỹ Khê; ngày hai Bà Nà Hills và Cầu Vàng; buổi tối đi Hội An.'),
  ('Kinh nghiệm đi Phú Quốc cho gia đình','Điểm đến','https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80','WebDuLich Team','2026-07-08','Phú Quốc phù hợp nghỉ dưỡng, vui chơi VinWonders, Safari và trải nghiệm cáp treo Hòn Thơm.','Gia đình có trẻ em nên chọn khách sạn gần khu vui chơi, chuẩn bị kem chống nắng và chia lịch trình vừa phải.'),
  ('Miền Tây mùa nước nổi nên đi đâu?','Điểm đến','https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80','WebDuLich Team','2026-08-11','Châu Đốc, Trà Sư, Cần Thơ và Cà Mau là những điểm đến nổi bật cho hành trình miền Tây.','Các điểm nên có gồm chợ nổi Cái Răng, rừng tràm Trà Sư, nhà Công tử Bạc Liêu và Đất Mũi Cà Mau.'),
  ('Tự tạo lịch trình du lịch cần chuẩn bị gì?','Lịch trình','https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80','WebDuLich Team','2026-09-14','Một lịch trình tốt cần có điểm đến, thời gian, ngân sách, phương tiện và nhịp độ di chuyển hợp lý.','Bạn nên xác định số ngày, điểm đến chính, điểm phụ, chi phí dự kiến và hoạt động ưu tiên.'),
  ('Top điểm đến Việt Nam nên có trong kế hoạch du lịch','Cẩm nang','https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80','WebDuLich Team','2026-10-17','Hà Nội, Hạ Long, Ninh Bình, Đà Nẵng, Hội An, Huế, Đà Lạt, Phú Quốc và miền Tây là các lựa chọn nổi bật.','Các điểm đến này đại diện cho văn hóa, biển đảo, nghỉ dưỡng, di sản, núi rừng và sông nước.');

INSERT INTO users (full_name, email, phone, password_hash, role) VALUES ('Khách demo','khachdemo@webdulich.vn','0909000000','$2a$10$demo_hash','USER');
INSERT INTO profiles (user_id, preferred_budget, preferred_style, home_city, note) VALUES (1, 6000000, 'Gia đình, nghỉ dưỡng', 'TP.HCM', 'Tài khoản demo cho phần người dùng.');
INSERT INTO custom_itineraries (user_id, title, destination_text, total_days, budget, travel_style, note) VALUES (1, 'Lịch trình Đà Nẵng - Hội An 3N2Đ', 'Đà Nẵng, Hội An', 3, 6500000, 'Gia đình', 'Lịch trình mẫu cho chức năng tự tạo lịch trình.');
INSERT INTO custom_itinerary_days (itinerary_id, day_number, title) VALUES (1,1,'Sơn Trà - Mỹ Khê'),(1,2,'Bà Nà Hills - Hội An'),(1,3,'Mua đặc sản - về lại TP.HCM');
INSERT INTO custom_itinerary_items (day_id, time_text, place_name, activity, estimated_cost) VALUES (1,'08:00','Bán đảo Sơn Trà','Tham quan chùa Linh Ứng, ngắm biển',150000),(1,'15:00','Biển Mỹ Khê','Tắm biển và ăn hải sản',350000),(2,'08:00','Bà Nà Hills','Check-in Cầu Vàng, vui chơi trong ngày',950000),(2,'18:00','Phố cổ Hội An','Dạo phố cổ, ăn tối và thả hoa đăng',350000),(3,'09:00','Chợ Hàn','Mua đặc sản trước khi ra sân bay',250000);

CREATE INDEX idx_properties_type ON properties(type);
CREATE INDEX idx_properties_city ON properties(city);
CREATE INDEX idx_properties_price ON properties(price);
CREATE INDEX idx_destinations_region ON destinations(region);

INSERT INTO users (full_name, email, phone, password_hash, avatar_url, role, status, created_at)
VALUES (
           'Quản trị viên',
           'admin@webdulich.vn',
           '0909000001',
           '$2a$05$0gHxBCU/FElCKMR9EkWhIuwBMecHa5imYy3IgfposhbzDUGJjE8mK',
           NULL,
           'ADMIN',
           'ACTIVE',
           NOW()
       )
    ON DUPLICATE KEY UPDATE
                         full_name = VALUES(full_name),
                         phone = VALUES(phone),
                         password_hash = VALUES(password_hash),
                         role = 'ADMIN',
                         status = 'ACTIVE';