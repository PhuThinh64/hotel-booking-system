package com.example.hotel_booking.employee.service;

import com.example.hotel_booking.employee.dto.EmployeeAdminUpdateRequest;
import com.example.hotel_booking.employee.dto.EmployeeCreateRequest;
import com.example.hotel_booking.employee.dto.EmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {
    Page<EmployeeResponse> getAllEmployees(String keyword, Boolean active, Pageable pageable);
    EmployeeResponse createEmployee(EmployeeCreateRequest request);
    EmployeeResponse updateEmployee(Long employeeId, EmployeeAdminUpdateRequest request);
    void deleteEmployee(Long employeeId);
}