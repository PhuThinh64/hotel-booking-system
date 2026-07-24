package com.example.hotel_booking.user.dto;

import com.example.hotel_booking.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Request body for updating customer profile details")
public class CustomerProfileUpdateRequest {

    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Contact phone number", example = "0987654321")
    private String phone;

    @Schema(description = "Email address", example = "johndoe@example.com")
    private String email;

    @Schema(description = "Residential address", example = "123 Nguyen Hue, District 1, HCMC")
    private String address;

    @Schema(description = "Identity card / Citizen ID number", example = "001200012345")
    private String identityCard;

    @Schema(description = "Gender", example = "MALE")
    private Gender gender;

    @Schema(description = "Nationality", example = "Vietnam")
    private String nationality;

    @Schema(description = "Date of birth", example = "1995-05-15")
    private LocalDate birthday;
}