package com.example.hotel_booking.audit.service.impl;

import com.example.hotel_booking.audit.dto.AuditLogResponse;
import com.example.hotel_booking.audit.entity.AuditLog;
import com.example.hotel_booking.audit.mapper.AuditLogMapper;
import com.example.hotel_booking.audit.repository.AuditLogRepository;
import com.example.hotel_booking.audit.repository.AuditLogSpecification;
import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.employee.entity.Employee;
import com.example.hotel_booking.employee.repository.EmployeeRepository;
import com.example.hotel_booking.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public void saveLog(String module, String action, Long targetId, String description) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Employee employee = null;
            String operatorName = "Hệ thống"; 

            
            if (auth != null && auth.getPrincipal() instanceof User) {
                User currentUser = (User) auth.getPrincipal();
                employee = employeeRepository.findByUser(currentUser).orElse(null);
                operatorName = (employee != null) ? employee.getFullName() : currentUser.getUsername();
            }
            
            else if ("PAYMENT".equalsIgnoreCase(module)) {
                operatorName = "VNPay System";
            }

            AuditLog auditLog = AuditLog.builder()
                    .employee(employee)
                    .operatorName(operatorName)
                    .module(module)
                    .action(action)
                    .targetId(targetId)
                    .description(description)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            System.err.println("❌ LỖI GHI AUDIT LOG: " + e.getMessage());
        }
    }

    public Page<AuditLogResponse> searchLogs(
            String module,
            Long targetId,
            String action,
            Long employeeId,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            Pageable pageable
    ) {
        var spec = AuditLogSpecification.withFilters(module, targetId, action, employeeId, fromDate, toDate);
        return auditLogRepository.findAll(spec, pageable).map(auditLogMapper::toDto);
    }
}