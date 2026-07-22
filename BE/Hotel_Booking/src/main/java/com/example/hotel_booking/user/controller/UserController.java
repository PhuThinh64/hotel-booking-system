package com.example.hotel_booking.user.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.user.dto.BaseProfileResponse;
import com.example.hotel_booking.user.dto.ChangePasswordRequest;
import com.example.hotel_booking.user.dto.CustomerProfileUpdateRequest;
import com.example.hotel_booking.user.dto.EmployeeProfileUpdateRequest;
import com.example.hotel_booking.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<BaseProfileResponse> getProfile() { 
        return ApiResponse.<BaseProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message(ErrorCode.SUCCESS.getMessage())
                .result(userService.getProfile())
                .build();
    }

    @PutMapping("/profile/customer")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ApiResponse<BaseProfileResponse> updateCustomerProfile(@RequestBody @Valid CustomerProfileUpdateRequest request) {
        return ApiResponse.<BaseProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Cập nhật hồ sơ khách hàng thành công!")
                .result(userService.updateCustomerProfile(request))
                .build();
    }

    
    @PutMapping("/profile/employee")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<BaseProfileResponse> updateEmployeeProfile(@RequestBody @Valid EmployeeProfileUpdateRequest request) {
        
        return ApiResponse.<BaseProfileResponse>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Cập nhật hồ sơ nhân viên thành công!")
                .result(userService.updateEmployeeProfile(request))
                .build();
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.<String>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Đổi mật khẩu thành công!")
                .build();
    }

}