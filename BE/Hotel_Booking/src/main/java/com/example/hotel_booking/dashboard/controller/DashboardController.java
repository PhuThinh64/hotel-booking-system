package com.example.hotel_booking.dashboard.controller;

import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import com.example.hotel_booking.dashboard.dto.DashboardDTO;
import com.example.hotel_booking.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(
        name = SwaggerTags.DASHBOARD,
        description = "Provide operational stats, daily check-in/out lists, occupancy metrics, and financial analytical reports."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Get Operational Statistics",
            description = "Retrieve real-time daily operational statistics, room status counters, and check-in/out schedules for Receptionists and Admins."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = SwaggerResponseMessages.FORBIDDEN
            )
    })
    @GetMapping("/operational")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<DashboardDTO.OperationalStatsResponse> getOperationalStats() {
        return ApiResponse.<DashboardDTO.OperationalStatsResponse>builder()
                .result(dashboardService.getOperationalStats())
                .message("Lấy dữ liệu vận hành thành công")
                .build();
    }

    @Operation(
            summary = "Get Analytical Statistics",
            description = "Retrieve high-level business analytics including revenue, occupancy rates, ADR, RevPAR, and status distributions over a date range (Admin only)."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = SwaggerResponseMessages.SUCCESS
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = SwaggerResponseMessages.UNAUTHORIZED
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = SwaggerResponseMessages.FORBIDDEN
            )
    })
    @GetMapping("/analytical")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardDTO.AnalyticalStatsResponse> getAnalyticalStats(
            @Parameter(description = "Start date for revenue and analytical metrics calculation", example = "2026-07-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date for revenue and analytical metrics calculation", example = "2026-07-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ApiResponse.<DashboardDTO.AnalyticalStatsResponse>builder()
                .result(dashboardService.getAnalyticalStats(startDate, endDate))
                .message("Lấy dữ liệu thống kê phân tích thành công")
                .build();
    }
}