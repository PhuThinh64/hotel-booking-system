package com.example.hotel_booking.user.service;

import com.example.hotel_booking.user.dto.BaseProfileResponse;
import com.example.hotel_booking.user.dto.ChangePasswordRequest;
import com.example.hotel_booking.user.dto.CustomerProfileUpdateRequest;
import com.example.hotel_booking.user.dto.EmployeeProfileUpdateRequest;

public interface UserService {
    BaseProfileResponse getProfile();
    BaseProfileResponse updateCustomerProfile(CustomerProfileUpdateRequest request);
    BaseProfileResponse updateEmployeeProfile(EmployeeProfileUpdateRequest request);
    void changePassword(ChangePasswordRequest request);
    void resetPasswordForEmployee(Long userId);
}