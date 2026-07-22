package com.example.hotel_booking.dashboard.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.dashboard.dto.DashboardDTO;
import com.example.hotel_booking.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    
    @GetMapping("/operational")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<DashboardDTO.OperationalStatsResponse> getOperationalStats() {
        return ApiResponse.<DashboardDTO.OperationalStatsResponse>builder()
                .result(dashboardService.getOperationalStats())
                .message("Lấy dữ liệu vận hành thành công")
                .build();
    }

    
    
    @GetMapping("/analytical")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardDTO.AnalyticalStatsResponse> getAnalyticalStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ApiResponse.<DashboardDTO.AnalyticalStatsResponse>builder()
                .result(dashboardService.getAnalyticalStats(startDate, endDate))
                .message("Lấy dữ liệu thống kê phân tích thành công")
                .build();
    }
}