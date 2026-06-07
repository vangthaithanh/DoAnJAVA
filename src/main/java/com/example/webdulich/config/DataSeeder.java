
package com.example.webdulich.config;

import com.example.webdulich.entity.Agent;
import com.example.webdulich.entity.BlogPost;
import com.example.webdulich.entity.Property;
import com.example.webdulich.repository.AgentRepository;
import com.example.webdulich.repository.BlogPostRepository;
import com.example.webdulich.repository.PropertyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(AgentRepository agentRepository,
                               PropertyRepository propertyRepository,
                               BlogPostRepository blogPostRepository) {
        return args -> {
            if (agentRepository.count() == 0) {
                agentRepository.saveAll(List.of(
                        new Agent(
                                "Nguyễn Hoàng Lan",
                                "Tư vấn tour gia đình",
                                "lan@webdulich.vn",
                                "0903 111 222",
                                "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=500&q=80",
                                "Chuyên tư vấn tour trong nước cho gia đình, nhóm bạn và khách đoàn nhỏ.",
                                "#", "#", "#"
                        ),
                        new Agent(
                                "Trần Minh Khang",
                                "Tư vấn tour miền Bắc - miền Trung",
                                "minh@webdulich.vn",
                                "0903 333 444",
                                "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=500&q=80",
                                "Có kinh nghiệm thiết kế hành trình Hà Nội, Hạ Long, Đà Nẵng, Huế và Hội An.",
                                "#", "#", "#"
                        ),
                        new Agent(
                                "Lê Thu Thảo",
                                "Tư vấn lịch trình tự tạo",
                                "thao@webdulich.vn",
                                "0903 555 666",
                                "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=500&q=80",
                                "Hỗ trợ khách tự tạo lịch trình theo ngân sách, số ngày và phong cách du lịch.",
                                "#", "#", "#"
                        )
                ));
            }

            if (propertyRepository.count() == 0) {
                Agent lan = agentRepository.findAll().get(0);
                Agent minh = agentRepository.findAll().get(1);
                Agent thao = agentRepository.findAll().get(2);

                propertyRepository.save(new Property(
                        "Đà Nẵng 3N2Đ | Bà Nà - Hội An - Sơn Trà",
                        new BigDecimal("6399000"),
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "Đà Nẵng - Bà Nà Hills - Bán đảo Sơn Trà - Ngũ Hành Sơn - Hội An",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Đà Nẵng",
                        3,
                        2,
                        24,
                        100,
                        2026,
                        "Cầu Vàng Bà Nà, biển Mỹ Khê, phố cổ Hội An về đêm. Tuyến: Đà Nẵng - Bà Nà Hills - Bán đảo Sơn Trà - Ngũ Hành Sơn - Hội An. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        true,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Đà Nẵng 4N3Đ | Bà Nà - Hội An - Huế - Phong Nha",
                        new BigDecimal("7799000"),
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "Đà Nẵng - Hội An - Huế - Động Phong Nha",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Đà Nẵng",
                        4,
                        3,
                        20,
                        107,
                        2026,
                        "Khám phá miền Trung với di sản, biển xanh và hang động nổi bật. Tuyến: Đà Nẵng - Hội An - Huế - Động Phong Nha. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Đà Nẵng 5N4Đ | Bà Nà - Hội An - Huế - La Vang",
                        new BigDecimal("8999000"),
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "Đà Nẵng - Bà Nà - Hội An - Huế - La Vang - Động Thiên Đường",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Đà Nẵng",
                        5,
                        4,
                        18,
                        114,
                        2026,
                        "Hành trình dài ngày cho gia đình yêu thích di sản miền Trung. Tuyến: Đà Nẵng - Bà Nà - Hội An - Huế - La Vang - Động Thiên Đường. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Huế 3N2Đ | Đại Nội - Thiên Mụ - Lăng Khải Định",
                        new BigDecimal("4590000"),
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "Huế - Đại Nội - Chùa Thiên Mụ - Lăng Khải Định - Sông Hương",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Huế",
                        3,
                        2,
                        26,
                        121,
                        2026,
                        "Trải nghiệm cố đô, ẩm thực Huế và du thuyền sông Hương. Tuyến: Huế - Đại Nội - Chùa Thiên Mụ - Lăng Khải Định - Sông Hương. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Quảng Bình 3N2Đ | Phong Nha - Thiên Đường - Nhật Lệ",
                        new BigDecimal("5290000"),
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "Đồng Hới - Động Phong Nha - Động Thiên Đường - Biển Nhật Lệ",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Quảng Bình",
                        3,
                        2,
                        20,
                        128,
                        2026,
                        "Khám phá hệ thống hang động và biển miền Trung. Tuyến: Đồng Hới - Động Phong Nha - Động Thiên Đường - Biển Nhật Lệ. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Hà Nội - Hạ Long - Ninh Bình - Tràng An 4N3Đ",
                        new BigDecimal("6499000"),
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "Hà Nội - Vịnh Hạ Long - Ninh Bình - Tràng An",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Hà Nội",
                        4,
                        3,
                        22,
                        135,
                        2026,
                        "Thủ đô nghìn năm, kỳ quan Hạ Long và non nước Tràng An. Tuyến: Hà Nội - Vịnh Hạ Long - Ninh Bình - Tràng An. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        true,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Hà Nội - Sapa - Fansipan 3N2Đ",
                        new BigDecimal("6999000"),
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "Hà Nội - Sapa - Bản Cát Cát - Fansipan",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Sapa",
                        3,
                        2,
                        18,
                        142,
                        2026,
                        "Chinh phục nóc nhà Đông Dương và bản làng Tây Bắc. Tuyến: Hà Nội - Sapa - Bản Cát Cát - Fansipan. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe giường nằm/xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Hà Nội - Yên Tử - Hạ Long - Ninh Bình - Sapa 6N5Đ",
                        new BigDecimal("11990000"),
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "Hà Nội - Yên Tử - Hạ Long - Ninh Bình - Sapa - Fansipan",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Hạ Long",
                        6,
                        5,
                        16,
                        149,
                        2026,
                        "Tour miền Bắc dài ngày kết hợp tâm linh, di sản và núi rừng. Tuyến: Hà Nội - Yên Tử - Hạ Long - Ninh Bình - Sapa - Fansipan. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay khứ hồi & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Ninh Bình 2N1Đ | Tràng An - Bái Đính - Hang Múa",
                        new BigDecimal("2490000"),
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "Ninh Bình - Tràng An - Bái Đính - Hang Múa",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Ninh Bình",
                        2,
                        1,
                        28,
                        156,
                        2026,
                        "Lịch trình ngắn ngày phù hợp cuối tuần. Tuyến: Ninh Bình - Tràng An - Bái Đính - Hang Múa. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Hạ Long 2N1Đ | Du thuyền vịnh - Sun World",
                        new BigDecimal("3290000"),
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "Hạ Long - Du thuyền vịnh - Hang Sửng Sốt - Sun World",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Hạ Long",
                        2,
                        1,
                        20,
                        163,
                        2026,
                        "Nghỉ đêm gần vịnh, tham quan kỳ quan thiên nhiên. Tuyến: Hạ Long - Du thuyền vịnh - Hang Sửng Sốt - Sun World. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Mộc Châu 2N1Đ | Đồi chè - Thác Dải Yếm",
                        new BigDecimal("1990000"),
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "Mộc Châu - Đồi chè Trái Tim - Thác Dải Yếm - Cầu kính",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Mộc Châu",
                        2,
                        1,
                        30,
                        170,
                        2026,
                        "Khí hậu mát mẻ, đồi chè và cảnh núi Tây Bắc. Tuyến: Mộc Châu - Đồi chè Trái Tim - Thác Dải Yếm - Cầu kính. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Hà Giang 3N2Đ | Đồng Văn - Mã Pì Lèng",
                        new BigDecimal("3690000"),
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "Hà Giang - Quản Bạ - Đồng Văn - Mã Pì Lèng - Mèo Vạc",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Hà Giang",
                        3,
                        2,
                        18,
                        177,
                        2026,
                        "Cung đường đá hùng vĩ dành cho người thích khám phá. Tuyến: Hà Giang - Quản Bạ - Đồng Văn - Mã Pì Lèng - Mèo Vạc. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch/limousine. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Phú Quốc 3N2Đ | Grand World - VinWonders - Safari",
                        new BigDecimal("6990000"),
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "Phú Quốc - Grand World - VinWonders - Safari - Cáp treo Hòn Thơm",
                        "Đang mở bán",
                        "Tour biển đảo",
                        "Phú Quốc",
                        3,
                        2,
                        24,
                        184,
                        2026,
                        "Nghỉ dưỡng đảo ngọc, vui chơi giải trí và khám phá biển đảo. Tuyến: Phú Quốc - Grand World - VinWonders - Safari - Cáp treo Hòn Thơm. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        true,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Phú Quốc 4N3Đ | Nam đảo - Hòn Thơm - Sunset Town",
                        new BigDecimal("8590000"),
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "Phú Quốc - Nam Đảo - Hòn Thơm - Sunset Town - Dinh Cậu",
                        "Đang mở bán",
                        "Tour biển đảo",
                        "Phú Quốc",
                        4,
                        3,
                        18,
                        191,
                        2026,
                        "Biển xanh, cáp treo vượt biển và không gian nghỉ dưỡng. Tuyến: Phú Quốc - Nam Đảo - Hòn Thơm - Sunset Town - Dinh Cậu. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Nha Trang 3N3Đ | Du ngoạn 3 đảo - VinWonders",
                        new BigDecimal("3286000"),
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "Nha Trang - Du ngoạn 3 đảo - VinWonders - Chợ Đầm",
                        "Đang mở bán",
                        "Tour biển đảo",
                        "Nha Trang",
                        3,
                        3,
                        25,
                        198,
                        2026,
                        "Biển đảo Nha Trang, vui chơi và ẩm thực hải sản. Tuyến: Nha Trang - Du ngoạn 3 đảo - VinWonders - Chợ Đầm. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch đời mới. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Nha Trang - Đà Lạt 5N4Đ | Biển xanh & cao nguyên",
                        new BigDecimal("6290000"),
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "Nha Trang - VinWonders - Đà Lạt - Mongoland - Đà Lạt View",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Nha Trang",
                        5,
                        4,
                        20,
                        205,
                        2026,
                        "Kết hợp biển Nha Trang và khí hậu se lạnh Đà Lạt. Tuyến: Nha Trang - VinWonders - Đà Lạt - Mongoland - Đà Lạt View. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Đà Lạt 3N2Đ | Langbiang - Mongo Land - Fresh Garden",
                        new BigDecimal("3490000"),
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "Đà Lạt - Langbiang - Fresh Garden - Mongo Land - Quảng trường Lâm Viên",
                        "Đang mở bán",
                        "Tour Tây Nguyên",
                        "Đà Lạt",
                        3,
                        2,
                        28,
                        212,
                        2026,
                        "Không khí mát lành, điểm check-in và ẩm thực cao nguyên. Tuyến: Đà Lạt - Langbiang - Fresh Garden - Mongo Land - Quảng trường Lâm Viên. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Buôn Ma Thuột 3N2Đ | Thác Dray Nur - Buôn Đôn",
                        new BigDecimal("3890000"),
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "Buôn Ma Thuột - Thác Dray Nur - Buôn Đôn - Bảo tàng cà phê",
                        "Đang mở bán",
                        "Tour Tây Nguyên",
                        "Buôn Ma Thuột",
                        3,
                        2,
                        22,
                        219,
                        2026,
                        "Khám phá văn hóa Tây Nguyên và hương vị cà phê. Tuyến: Buôn Ma Thuột - Thác Dray Nur - Buôn Đôn - Bảo tàng cà phê. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch/Máy bay. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Miền Tây 3N2Đ | Mỹ Tho - Bến Tre - Cần Thơ",
                        new BigDecimal("1990000"),
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "Mỹ Tho - Bến Tre - Cần Thơ - Chợ nổi Cái Răng",
                        "Đang mở bán",
                        "Tour miền Nam",
                        "Cần Thơ",
                        3,
                        2,
                        30,
                        226,
                        2026,
                        "Sông nước miền Tây, thuyền 3 lá, vườn trái cây và đờn ca tài tử. Tuyến: Mỹ Tho - Bến Tre - Cần Thơ - Chợ nổi Cái Răng. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        true,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Miền Tây 4N3Đ | Sóc Trăng - Bạc Liêu - Cà Mau",
                        new BigDecimal("3490000"),
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "Cần Thơ - Sóc Trăng - Bạc Liêu - Cà Mau - Đất Mũi",
                        "Đang mở bán",
                        "Tour miền Nam",
                        "Cà Mau",
                        4,
                        3,
                        24,
                        233,
                        2026,
                        "Chạm mốc cực Nam, khám phá văn hóa Khmer và nhà Công tử Bạc Liêu. Tuyến: Cần Thơ - Sóc Trăng - Bạc Liêu - Cà Mau - Đất Mũi. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Châu Đốc - Rừng Tràm Trà Sư - Cần Thơ 3N2Đ",
                        new BigDecimal("2350000"),
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "Châu Đốc - Miếu Bà Chúa Xứ - Rừng Tràm Trà Sư - Cần Thơ",
                        "Đang mở bán",
                        "Tour miền Nam",
                        "An Giang",
                        3,
                        2,
                        24,
                        240,
                        2026,
                        "Mùa nước nổi, rừng tràm và văn hóa miền Tây. Tuyến: Châu Đốc - Miếu Bà Chúa Xứ - Rừng Tràm Trà Sư - Cần Thơ. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Vũng Tàu 2N1Đ | Biển xanh cuối tuần",
                        new BigDecimal("1700000"),
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "TP.HCM - Vũng Tàu - Tượng Chúa Kitô - Bãi Sau",
                        "Đang mở bán",
                        "Tour biển đảo",
                        "Vũng Tàu",
                        2,
                        1,
                        32,
                        247,
                        2026,
                        "Tour ngắn ngày cuối tuần cho gia đình và nhóm bạn. Tuyến: TP.HCM - Vũng Tàu - Tượng Chúa Kitô - Bãi Sau. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Phan Thiết - Mũi Né 3N2Đ | Đồi cát - Bàu Trắng",
                        new BigDecimal("2990000"),
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "Phan Thiết - Mũi Né - Bàu Trắng - Lâu đài Rượu Vang",
                        "Đang mở bán",
                        "Tour biển đảo",
                        "Phan Thiết",
                        3,
                        2,
                        26,
                        254,
                        2026,
                        "Biển, đồi cát và resort nghỉ dưỡng gần TP.HCM. Tuyến: Phan Thiết - Mũi Né - Bàu Trắng - Lâu đài Rượu Vang. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Quy Nhơn - Phú Yên 4N3Đ | Kỳ Co - Eo Gió - Gành Đá Đĩa",
                        new BigDecimal("6790000"),
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "Quy Nhơn - Kỳ Co - Eo Gió - Phú Yên - Gành Đá Đĩa",
                        "Đang mở bán",
                        "Tour miền Trung",
                        "Quy Nhơn",
                        4,
                        3,
                        20,
                        261,
                        2026,
                        "Bờ biển miền Trung hoang sơ và nhiều điểm check-in đẹp. Tuyến: Quy Nhơn - Kỳ Co - Eo Gió - Phú Yên - Gành Đá Đĩa. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Côn Đảo 3N2Đ | Tâm linh & biển đảo",
                        new BigDecimal("7290000"),
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "Côn Đảo - Nghĩa trang Hàng Dương - Miếu Bà Phi Yến - Bãi Đầm Trầu",
                        "Đang mở bán",
                        "Tour biển đảo",
                        "Côn Đảo",
                        3,
                        2,
                        16,
                        268,
                        2026,
                        "Kết hợp nghỉ dưỡng biển và hành trình tri ân. Tuyến: Côn Đảo - Nghĩa trang Hàng Dương - Miếu Bà Phi Yến - Bãi Đầm Trầu. Khởi hành: Từ TP.HCM. Phương tiện: Máy bay & xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Cao Bằng - Thác Bản Giốc - Hồ Ba Bể 3N2Đ",
                        new BigDecimal("4290000"),
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "Cao Bằng - Thác Bản Giốc - Động Ngườm Ngao - Hồ Ba Bể",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Cao Bằng",
                        3,
                        2,
                        20,
                        275,
                        2026,
                        "Thiên nhiên Đông Bắc, thác nước biên giới và hồ xanh. Tuyến: Cao Bằng - Thác Bản Giốc - Động Ngườm Ngao - Hồ Ba Bể. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Mai Châu - Pù Luông 3N2Đ | Bản làng & ruộng bậc thang",
                        new BigDecimal("3290000"),
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "Mai Châu - Bản Lác - Pù Luông - Kho Mường",
                        "Đang mở bán",
                        "Tour miền Bắc",
                        "Pù Luông",
                        3,
                        2,
                        22,
                        282,
                        2026,
                        "Nghỉ dưỡng sinh thái, bản làng và ruộng bậc thang. Tuyến: Mai Châu - Bản Lác - Pù Luông - Kho Mường. Khởi hành: Từ Hà Nội. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
                propertyRepository.save(new Property(
                        "Tây Ninh - Núi Bà Đen - Tòa Thánh 1N",
                        new BigDecimal("990000"),
                        "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "TP.HCM - Núi Bà Đen - Tòa Thánh Cao Đài",
                        "Đang mở bán",
                        "Tour miền Nam",
                        "Tây Ninh",
                        1,
                        0,
                        35,
                        289,
                        2026,
                        "Tour trong ngày phù hợp khách gia đình và nhóm nhỏ. Tuyến: TP.HCM - Núi Bà Đen - Tòa Thánh Cao Đài. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        lan
                ));
                propertyRepository.save(new Property(
                        "Cần Giờ 1N | Rừng Sác - Đảo Khỉ - Biển 30/4",
                        new BigDecimal("790000"),
                        "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "TP.HCM - Cần Giờ - Đảo Khỉ - Rừng Sác - Biển 30/4",
                        "Đang mở bán",
                        "Tour miền Nam",
                        "Cần Giờ",
                        1,
                        0,
                        35,
                        296,
                        2026,
                        "Du lịch sinh thái gần thành phố, chi phí thấp. Tuyến: TP.HCM - Cần Giờ - Đảo Khỉ - Rừng Sác - Biển 30/4. Khởi hành: Từ TP.HCM. Phương tiện: Xe du lịch. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        minh
                ));
                propertyRepository.save(new Property(
                        "Tự thiết kế lịch trình Việt Nam theo ngân sách",
                        new BigDecimal("0"),
                        "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                        "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                        "Bạn chọn điểm đến - số ngày - ngân sách - phong cách, WebDuLich gợi ý lịch trình phù hợp",
                        "Theo yêu cầu",
                        "Lịch trình tự tạo",
                        "Việt Nam",
                        3,
                        2,
                        99,
                        303,
                        2026,
                        "Dành cho người dùng muốn tự tạo lịch trình thay vì mua tour cố định. Tuyến: Bạn chọn điểm đến - số ngày - ngân sách - phong cách, WebDuLich gợi ý lịch trình phù hợp. Khởi hành: Theo yêu cầu. Phương tiện: Tùy chọn. Dữ liệu tour mẫu phục vụ giao diện bán tour trong nước Việt Nam.",
                        false,
                        thao
                ));
            }

            if (blogPostRepository.count() == 0) {
                blogPostRepository.saveAll(List.of(
                        new BlogPost(
                                "Cách chọn tour trong nước phù hợp ngân sách",
                                "Kinh nghiệm",
                                "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1100&q=80",
                                "WebDuLich Team",
                                LocalDate.of(2026, 5, 2),
                                "Chuẩn bị ngân sách, thời gian và phong cách du lịch trước khi chọn tour giúp chuyến đi nhẹ nhàng hơn.",
                                "Khi chọn tour trong nước, bạn nên so sánh lịch trình, điểm tham quan chính, phương tiện, khách sạn, bữa ăn, chính sách trẻ em và ngày khởi hành. Với gia đình, nên ưu tiên lịch trình vừa phải; với nhóm trẻ, có thể chọn tour khám phá nhiều điểm check-in."
                        ),
                        new BlogPost(
                                "Gợi ý lịch trình Đà Nẵng - Hội An 3 ngày 2 đêm",
                                "Lịch trình",
                                "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=1100&q=80",
                                "WebDuLich Team",
                                LocalDate.of(2026, 6, 5),
                                "Một hành trình ngắn ngày có thể kết hợp biển Mỹ Khê, Bà Nà Hills và phố cổ Hội An.",
                                "Ngày đầu nên tham quan Sơn Trà và tắm biển Mỹ Khê. Ngày thứ hai dành cho Bà Nà Hills, Cầu Vàng. Buổi tối có thể đi Hội An. Ngày cuối mua đặc sản và nghỉ ngơi trước khi về."
                        ),
                        new BlogPost(
                                "Kinh nghiệm đi Phú Quốc cho gia đình",
                                "Điểm đến",
                                "https://images.unsplash.com/photo-1548013146-72479768bada?auto=format&fit=crop&w=1100&q=80",
                                "WebDuLich Team",
                                LocalDate.of(2026, 7, 8),
                                "Phú Quốc phù hợp nghỉ dưỡng, vui chơi VinWonders, Safari và trải nghiệm cáp treo Hòn Thơm.",
                                "Gia đình có trẻ em nên chọn khách sạn gần trung tâm hoặc khu vui chơi, chuẩn bị kem chống nắng, đặt vé tham quan sớm và chia lịch trình vừa phải để có thời gian nghỉ dưỡng."
                        ),
                        new BlogPost(
                                "Miền Tây mùa nước nổi nên đi đâu?",
                                "Điểm đến",
                                "https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1100&q=80",
                                "WebDuLich Team",
                                LocalDate.of(2026, 8, 11),
                                "Châu Đốc, Trà Sư, Cần Thơ và Cà Mau là những điểm đến nổi bật cho hành trình miền Tây.",
                                "Miền Tây phù hợp với người thích sông nước, ẩm thực địa phương và trải nghiệm văn hóa. Các điểm nên có gồm chợ nổi Cái Răng, rừng tràm Trà Sư, nhà Công tử Bạc Liêu và Đất Mũi Cà Mau."
                        ),
                        new BlogPost(
                                "Tự tạo lịch trình du lịch cần chuẩn bị gì?",
                                "Lịch trình",
                                "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=1100&q=80",
                                "WebDuLich Team",
                                LocalDate.of(2026, 9, 14),
                                "Một lịch trình tốt cần có điểm đến, thời gian, ngân sách, phương tiện và nhịp độ di chuyển hợp lý.",
                                "Bạn nên xác định số ngày, điểm đến chính, điểm phụ, thời gian di chuyển, nơi ở, chi phí dự kiến và hoạt động ưu tiên. WebDuLich có thể mở rộng để lưu lịch trình cá nhân trong database."
                        ),
                        new BlogPost(
                                "Top điểm đến Việt Nam nên có trong kế hoạch du lịch",
                                "Cẩm nang",
                                "https://images.unsplash.com/photo-1528127269322-539801943592?auto=format&fit=crop&w=1100&q=80",
                                "WebDuLich Team",
                                LocalDate.of(2026, 10, 17),
                                "Hà Nội, Hạ Long, Ninh Bình, Đà Nẵng, Hội An, Huế, Đà Lạt, Phú Quốc và miền Tây là các lựa chọn nổi bật.",
                                "Các điểm đến này đại diện cho nhiều phong cách: văn hóa, biển đảo, nghỉ dưỡng, di sản, núi rừng và sông nước. Người dùng có thể mua tour trọn gói hoặc tự tạo lịch trình theo sở thích."
                        )
                ));
            }
        };
    }
}
