package com.example.hotel_booking.employee.service.impl;

import com.example.hotel_booking.audit.service.AuditLogService;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.employee.dto.EmployeeAdminUpdateRequest;
import com.example.hotel_booking.employee.dto.EmployeeCreateRequest;
import com.example.hotel_booking.employee.dto.EmployeeResponse;
import com.example.hotel_booking.employee.entity.Employee;
import com.example.hotel_booking.employee.mapper.EmployeeMapper;
import com.example.hotel_booking.employee.repository.EmployeeRepository;
import com.example.hotel_booking.employee.service.EmployeeService;
import com.example.hotel_booking.role.entity.Role;
import com.example.hotel_booking.role.repository.RoleRepository;
import com.example.hotel_booking.user.entity.User;
import com.example.hotel_booking.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeMapper employeeMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(String keyword, Boolean active, Pageable pageable) {

        
        if (StringUtils.hasText(keyword)) {
            return employeeRepository.searchEmployees(active, keyword, pageable)
                    .map(employeeMapper::toResponse);
        }

        
        if (active == null) {
            return employeeRepository.findAll(pageable)
                    .map(employeeMapper::toResponse);
        }

        
        return employeeRepository.findByActive(active, pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role defaultRole = roleRepository.findByName("ROLE_RECEPTIONIST")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(defaultRole)
                .active(true)
                .build();
        user = userRepository.save(user);

        
        Employee employee = Employee.builder()
                .user(user)
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .build();
        employee.setActive(true);

        employee = employeeRepository.save(employee);

        auditLogService.saveLog("EMPLOYEE", "CREATE", employee.getId(),
                "Tạo mới nhân viên: " + employee.getFullName() + " (Tài khoản: " + request.getUsername() + ")");

        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long employeeId, EmployeeAdminUpdateRequest request) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        employee.setFullName(request.getFullName());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setEmail(request.getEmail());

        User user = employee.getUser();
        if (user != null && request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.setRole(role);
            userRepository.save(user);
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        auditLogService.saveLog("EMPLOYEE", "UPDATE", employeeId,
                "Cập nhật thông tin nhân viên: " + updatedEmployee.getFullName());

        return employeeMapper.toResponse(updatedEmployee);
    }

    
    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        
        boolean newStatus = !employee.getActive();
        employee.setActive(newStatus);
        employeeRepository.save(employee);

        
        User user = employee.getUser();
        if (user != null) {
            user.setActive(newStatus);
            userRepository.save(user);
        }

        
        String action = newStatus ? "RESTORE" : "DELETE";
        String logMessage = newStatus ? "Khôi phục nhân viên: " : "Xóa mềm (vô hiệu hóa) nhân viên: ";

        auditLogService.saveLog("EMPLOYEE", action, employeeId, logMessage + employee.getFullName());
    }
}