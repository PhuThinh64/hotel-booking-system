package com.example.hotel_booking.audit.mapper;

import com.example.hotel_booking.audit.dto.AuditLogResponse;
import com.example.hotel_booking.audit.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    @Mapping(source = "employee.id", target = "performedById")
    @Mapping(source = "operatorName", target = "performedBy")
    AuditLogResponse toDto(AuditLog auditLog);
}