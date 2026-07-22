package com.example.hotel_booking.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
public class CustomerProfileResponse extends BaseProfileResponse {
    private String address;
    private String identityCard;
    private String gender;
    private String nationality;
    private LocalDate birthday;
}
