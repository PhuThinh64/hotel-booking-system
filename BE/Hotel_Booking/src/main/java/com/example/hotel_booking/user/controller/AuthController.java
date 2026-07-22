package com.example.hotel_booking.user.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.user.dto.AuthRequest;
import com.example.hotel_booking.user.dto.AuthResponse;
import com.example.hotel_booking.user.dto.RegisterRequest;
import com.example.hotel_booking.user.dto.ForgotPasswordRequest;
import com.example.hotel_booking.user.dto.ResetPasswordRequest;
import com.example.hotel_booking.user.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "APIs dành cho việc quản lý xác thực")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản khách hàng mới")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse responseData = authService.register(request);
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Đăng ký tài khoản thành công")
                .result(responseData)
                .build();
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống (Lấy JWT Token)")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse responseData = authService.authenticate(request);
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .result(responseData)
                .build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Yêu cầu đặt lại mật khẩu (Gửi Token qua Mail)")
    public ApiResponse<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.sendForgotPasswordToken(request);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Yêu cầu thành công")
                .result("Hệ thống đã gửi liên kết đặt lại mật khẩu vào Email của bạn!")
                .build();
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Xác thực token và cập nhật mật khẩu mới")
    public ApiResponse<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Cập nhật mật khẩu thành công")
                .result("Đặt lại mật khẩu mới thành công!")
                .build();
    }
}