package com.example.hotel_booking.audit.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogResponse {
    private Long id;
    private String module;
    private String action;
    private Long targetId;
    private String description;
    private Long performedById;
    private String performedBy;
    private LocalDateTime createdAt;
}