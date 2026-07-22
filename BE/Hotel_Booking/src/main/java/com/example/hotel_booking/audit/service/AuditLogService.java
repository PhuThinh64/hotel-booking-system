package com.example.hotel_booking.audit.service;

import com.example.hotel_booking.audit.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {
    void saveLog(String module, String action, Long targetId, String description);

    
    Page<AuditLogResponse> searchLogs(
            String module,
            Long targetId,
            String action,
            Long employeeId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) ;

}