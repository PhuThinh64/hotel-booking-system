package com.example.hotel_booking.user.dto;

import com.example.hotel_booking.common.Gender;
import lombok.Data;

import java.time.LocalDate;


@Data
public class CustomerProfileUpdateRequest {
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String identityCard;
    private Gender gender; 
    private String nationality;
    private LocalDate birthday;
}

