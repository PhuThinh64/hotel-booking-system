package com.example.hotel_booking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Phản hồi xác thực thành công chứa token JWT và thông tin người dùng")
public class AuthResponse {

    @Schema(description = "Token JWT xác thực dùng cho Bearer Authentication", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tên tài khoản người dùng", example = "john_doe")
    private String username;

    @Schema(description = "Vai trò (Role) của người dùng trong hệ thống", example = "ROLE_CUSTOMER")
    private String role;

    @Schema(description = "ID của tài khoản User (Dùng cho các tác vụ liên quan đến Account như Đổi mật khẩu, Khóa tài khoản)")
    private Long userId;

    @Schema(description = "ID của hồ sơ cá nhân Customer hoặc Employee (Dùng cho các tác vụ như Đặt phòng, Xem thông tin lương/nhân sự)")
    private Long profileId;

    @Schema(description = "Họ và tên người dùng", example = "John Doe")
    private String fullName;

    @Schema(description = "Số điện thoại người dùng", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Email người dùng", example = "nphuthinhvn@gmail.com")
    private String email;
}