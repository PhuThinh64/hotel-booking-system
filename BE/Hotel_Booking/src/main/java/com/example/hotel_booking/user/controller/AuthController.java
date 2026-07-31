package com.example.hotel_booking.user.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.user.dto.*;
import com.example.hotel_booking.user.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.AUTH,
        description = "Public authentication APIs including customer registration, login, forgot password, and reset password."
)
public class AuthController {

    private final AuthServiceImpl authService;

    @Operation(
            summary = "Check Phone Number Existence",
            description = "Check if the provided phone number is already registered in User account or exists in Customer records for auto-fill/validation purposes.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            )
    })
    @GetMapping("/check-phone")
    public ApiResponse<PhoneCheckResponse> checkPhone(
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "Phone number to check (10 digits starting with 0 or +84)",
                    example = "0912345678",
                    required = true
            )
            @RequestParam String phoneNumber
    ) {
        PhoneCheckResponse responseData = authService.checkPhone(phoneNumber);
        return ApiResponse.<PhoneCheckResponse>builder()
                .code(200)
                .message("Kiểm tra số điện thoại thành công")
                .result(responseData)
                .build();
    }

    @Operation(
            summary = "Customer Registration",
            description = "Register a new customer account and initialize profile information.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = SwaggerResponseMessages.CONFLICT
            )
    })
    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        AuthResponse responseData = authService.register(request);
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Đăng ký tài khoản thành công")
                .result(responseData)
                .build();
    }

    @Operation(
            summary = "User Login",
            description = "Authenticate user credentials and return JWT access token along with user details.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            )
    })
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody AuthRequest request
    ) {

        AuthResponse responseData = authService.authenticate(request);
        return ApiResponse.<AuthResponse>builder()
                .code(200)
                .message("Đăng nhập thành công")
                .result(responseData)
                .build();
    }

    @Operation(
            summary = "Forgot Password Request",
            description = "Request password reset link/token to be sent to registered user email.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = SwaggerResponseMessages.NOT_FOUND
            )
    })
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {

        authService.sendForgotPasswordToken(request);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Yêu cầu thành công")
                .result("Hệ thống đã gửi liên kết đặt lại mật khẩu vào Email của bạn!")
                .build();
    }

    @Operation(
            summary = "Reset Password with Token",
            description = "Validate password reset token and update user account password.",
            security = {}
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = SwaggerResponseMessages.BAD_REQUEST
            )
    })
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {

        authService.resetPassword(request);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Cập nhật mật khẩu thành công")
                .result("Đặt lại mật khẩu mới thành công!")
                .build();
    }
}