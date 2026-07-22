package com.example.hotel_booking.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hotelBookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel Booking Management API")
                        .version("1.0.0")
                        .description("### OpenAPI Spec - Hotel Booking Management System\n\n" +
                                "Tài liệu đặc tả API chuẩn cho hệ thống Quản lý đặt phòng khách sạn.\n\n" +
                                "#### Các đặc tính nổi bật của hệ thống:\n" +
                                "1. **Giải quyết Race Condition (Overbooking)**: Áp dụng khóa bi quan `@Lock(LockModeType.PESSIMISTIC_WRITE)` cho các giao dịch đặt phòng đồng thời để kiểm soát tranh chấp cao.\n" +
                                "2. **Xử lý N+1 Query**: Tối ưu hóa hiệu năng nạp dữ liệu bằng cách sử dụng `@EntityGraph` khi tải danh sách booking và các phòng liên quan.\n" +
                                "3. **VNPay Payment Integration**: Hỗ trợ tích hợp cổng thanh toán trực tuyến VNPay kết hợp kiểm tra tính toàn vẹn dữ liệu bằng cơ chế Checksum SHA-512.\n" +
                                "4. **Stateless Security**: Bảo mật hệ thống thông qua JWT (JSON Web Token) stateless authentication."))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Vui lòng nhập token JWT vào đây để thực hiện gọi các API yêu cầu quyền hạn cao (ROLE_USER, ROLE_ADMIN).")));
    }
}
