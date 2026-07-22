package com.example.hotel_booking.user.service.impl;

import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.customer.entity.Customer;
import com.example.hotel_booking.customer.repository.CustomerRepository;
import com.example.hotel_booking.employee.entity.Employee;
import com.example.hotel_booking.employee.repository.EmployeeRepository;
import com.example.hotel_booking.user.dto.BaseProfileResponse;
import com.example.hotel_booking.user.dto.ChangePasswordRequest;
import com.example.hotel_booking.user.dto.CustomerProfileUpdateRequest;
import com.example.hotel_booking.user.dto.EmployeeProfileUpdateRequest;
import com.example.hotel_booking.user.entity.User;
import com.example.hotel_booking.user.mapper.UserMapper;
import com.example.hotel_booking.user.repository.UserRepository;
import com.example.hotel_booking.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper; 

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    @Transactional(readOnly = true)
    public BaseProfileResponse getProfile() {
        User currentUser = getCurrentUser();

        if (currentUser.isCustomer()) {
            return userMapper.toCustomerProfile(currentUser, currentUser.getCustomer());
        }

        if (currentUser.isEmployee()) {
            return userMapper.toEmployeeProfile(currentUser, currentUser.getEmployee());
        }

        throw new AppException(ErrorCode.PROFILE_LINK_NOT_FOUND);
    }

    @Override
    @Transactional
    public BaseProfileResponse updateCustomerProfile(CustomerProfileUpdateRequest request) {
        User currentUser = getCurrentUser();
        if (!currentUser.isCustomer()) throw new AppException(ErrorCode.INVALID_ROLE);

        Customer customer = currentUser.getCustomer();
        userMapper.updateCustomerFromRequest(request, customer);
        customerRepository.save(customer);
        return userMapper.toCustomerProfile(currentUser, customer);
    }

    @Override
    @Transactional
    public BaseProfileResponse updateEmployeeProfile(EmployeeProfileUpdateRequest request) {
        User currentUser = getCurrentUser();
        if (!currentUser.isEmployee()) throw new AppException(ErrorCode.INVALID_ROLE);

        Employee employee = currentUser.getEmployee();
        userMapper.updateEmployeeFromRequest(request, employee);
        employeeRepository.save(employee);
        return userMapper.toEmployeeProfile(currentUser, employee);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();

        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword())) {
            
            throw new AppException(ErrorCode.PASSWORD_INCORRECT);
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }



    @Override
    @Transactional
    public void resetPasswordForEmployee(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getEmployee() == null) {
            throw new AppException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        String defaultPassword = "123456";
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
    }
}