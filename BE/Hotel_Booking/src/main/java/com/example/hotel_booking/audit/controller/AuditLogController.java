package com.example.hotel_booking.audit.controller;

import com.example.hotel_booking.audit.dto.AuditLogResponse;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.common.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService; 

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<Page<AuditLogResponse>> getLogs(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Long targetId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLogResponse> logs = auditLogService.searchLogs(module, targetId, action, employeeId, fromDate, toDate, pageable);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .result(logs)
                .build();
    }
}