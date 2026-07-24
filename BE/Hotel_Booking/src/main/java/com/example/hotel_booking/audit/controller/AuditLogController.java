package com.example.hotel_booking.audit.controller;

import com.example.hotel_booking.audit.dto.AuditLogResponse;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.config.swagger.constants.ApiInfoConstants;
import com.example.hotel_booking.config.swagger.constants.SwaggerResponseMessages;
import com.example.hotel_booking.config.swagger.constants.SwaggerTags;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = SwaggerTags.AUDIT_LOG,
        description = "Manage and query system audit logs, tracking actions performed across modules."
)
@SecurityRequirement(name = ApiInfoConstants.SECURITY_SCHEME)
public class AuditLogController {

    private final AuditLogService auditLogService;

    @Operation(
            summary = "Retrieve System Audit Logs",
            description = "Retrieve a paginated list of audit logs with optional filtering by module, target ID, action, performer, and date range."
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
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<Page<AuditLogResponse>> getLogs(
            @Parameter(description = "Target module name (e.g. BOOKING, CUSTOMER, SERVICE)", example = "BOOKING")
            @RequestParam(required = false) String module,

            @Parameter(description = "ID of the target entity being modified or interacted with", example = "100")
            @RequestParam(required = false) Long targetId,

            @Parameter(description = "Action performed (e.g. CREATE, UPDATE, DELETE, CANCEL)", example = "UPDATE")
            @RequestParam(required = false) String action,

            @Parameter(description = "ID of the employee/user who performed the action", example = "5")
            @RequestParam(required = false) Long employeeId,

            @Parameter(description = "Start timestamp filter (ISO format)", example = "2026-01-01T00:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,

            @Parameter(description = "End timestamp filter (ISO format)", example = "2026-12-31T23:59:59")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,

            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<AuditLogResponse> logs = auditLogService.searchLogs(module, targetId, action, employeeId, fromDate, toDate, pageable);

        return ApiResponse.<Page<AuditLogResponse>>builder()
                .result(logs)
                .build();
    }
}