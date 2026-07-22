package com.example.hotel_booking.customer.dto;

import com.example.hotel_booking.common.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String identityCard;
    private String email;
    private Gender gender;
    private String nationality;
    private String address;
    private LocalDate birthday;
    private Boolean active;
}