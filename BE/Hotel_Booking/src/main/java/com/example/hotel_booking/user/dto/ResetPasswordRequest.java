package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request body for resetting password using reset token")
public class ResetPasswordRequest {

    @Schema(description = "Password reset token received via email", example = "d9b2e8f1-3c4a-4e2b-91d8-5f6e7a8b9c0d")
    @NotBlank(message = "Token không được để trống")
    private String token;

    @Schema(description = "New password to set for account (minimum 6 characters)", example = "NewSecretPassword123!")
    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, message = "Mật khẩu phải từ 6 ký tự trở lên")
    private String newPassword;
}