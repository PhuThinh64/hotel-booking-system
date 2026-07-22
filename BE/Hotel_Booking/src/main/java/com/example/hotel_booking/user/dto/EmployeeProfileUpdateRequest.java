package com.example.hotel_booking.user.dto;

import lombok.Data;

@Data
public class EmployeeProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String email;
}