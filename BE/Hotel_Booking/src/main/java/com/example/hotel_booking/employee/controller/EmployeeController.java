package com.example.hotel_booking.employee.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.employee.dto.EmployeeCreateRequest;
import com.example.hotel_booking.employee.dto.EmployeeResponse;
import com.example.hotel_booking.employee.dto.EmployeeAdminUpdateRequest;
import com.example.hotel_booking.employee.service.EmployeeService;
import com.example.hotel_booking.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<Page<EmployeeResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<Page<EmployeeResponse>>builder()
                .result(employeeService.getAllEmployees(keyword, active, pageable))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EmployeeResponse> create(@RequestBody @Valid EmployeeCreateRequest request) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.createEmployee(request))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<EmployeeResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid EmployeeAdminUpdateRequest request) {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployee(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng thái nhân viên thành công")
                .build();
    }

    @PostMapping("/{userId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> resetPassword(@PathVariable Long userId) {
        userService.resetPasswordForEmployee(userId);
        return ApiResponse.<String>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .message("Reset mật khẩu thành công!")
                .build();
    }
}