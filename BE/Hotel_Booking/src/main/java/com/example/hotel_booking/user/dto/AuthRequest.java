package com.example.hotel_booking.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Yêu cầu đăng nhập tài khoản")
public class AuthRequest {
    @Schema(description = "Tên đăng nhập / Tài khoản", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Mật khẩu bảo mật", example = "SecretPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}