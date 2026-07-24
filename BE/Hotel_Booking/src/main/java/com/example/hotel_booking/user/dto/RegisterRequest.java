package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request body for new customer registration")
public class RegisterRequest {

    @Schema(description = "Account username (4 to 30 characters)", example = "john_doe")
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 4, max = 30, message = "Tên đăng nhập phải từ 4 đến 30 ký tự")
    private String username;

    @Schema(description = "Account password (at least 6 characters)", example = "SecretPassword123!")
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @Schema(description = "Full name of the customer", example = "Nguyen Van A")
    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @Schema(description = "Customer email address", example = "nguyenvana@example.com")
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @Schema(description = "Customer phone number (10 digits starting with 0 or +84)", example = "0912345678")
    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)(\\d{9})$", message = "Số điện thoại không đúng định dạng")
    private String phoneNumber;
}