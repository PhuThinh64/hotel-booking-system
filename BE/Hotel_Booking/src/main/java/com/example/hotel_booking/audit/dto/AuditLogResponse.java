package com.example.hotel_booking.audit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response details for system audit logs")
public class AuditLogResponse {

    @Schema(description = "Unique identifier of the audit log entry", example = "1")
    private Long id;

    @Schema(description = "Target system module name", example = "BOOKING")
    private String module;

    @Schema(description = "Action performed on the target entity", example = "UPDATE_STATUS")
    private String action;

    @Schema(description = "ID of the affected target entity", example = "102")
    private Long targetId;

    @Schema(description = "Detailed description or payload of the action performed", example = "Updated booking status from PENDING to CONFIRMED")
    private String description;

    @Schema(description = "User ID of the employee who executed the action", example = "5")
    private Long performedById;

    @Schema(description = "Full name or username of the actor who performed the action", example = "Nguyen Van B")
    private String performedBy;

    @Schema(description = "Timestamp when the action was recorded", example = "2026-07-24T14:30:00")
    private LocalDateTime createdAt;
}