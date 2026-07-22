package com.example.hotel_booking.customer.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.customer.dto.CreateCustomerRequest;
import com.example.hotel_booking.customer.dto.CustomerResponse;
import com.example.hotel_booking.customer.dto.UpdateCustomerRequest;
import com.example.hotel_booking.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/phone/{phoneNumber}")
    public ApiResponse<CustomerResponse> getByPhone(@PathVariable String phoneNumber) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getByPhoneNumber(phoneNumber))
                .build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<CustomerResponse> create(@RequestBody @Valid CreateCustomerRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.createCustomer(request))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerResponse> getById(@PathVariable Long id) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.getById(id))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<CustomerResponse>> getAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10) Pageable pageable) {
        return ApiResponse.<Page<CustomerResponse>>builder()
                .result(customerService.getAllCustomers(keyword, active, pageable))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CustomerResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCustomerRequest request) {
        return ApiResponse.<CustomerResponse>builder()
                .result(customerService.updateCustomer(id, request))
                .build();
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleleCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng thái khách hàng thành công.")
                .build();
    }

    @GetMapping("/stats/count-new")
    public ApiResponse<Long> getNewCustomersCount(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        long count = customerService.countNewCustomers(startDate, endDate);
        return ApiResponse.<Long>builder()
                .code(1000)
                .message("Lấy thống kê số lượng khách hàng mới thành công")
                .result(count)
                .build();
    }
}