package com.example.hotel_booking.customer.dto;

import com.example.hotel_booking.common.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class UpdateCustomerRequest {
    @NotBlank(message = "FULL_NAME_REQUIRED")
    private String fullName;

    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "PHONE_INVALID")
    private String phoneNumber;

    @NotBlank(message = "IDENTITY_CARD_REQUIRED")
    private String identityCard;

    @Email(message = "EMAIL_INVALID")
    private String email;

    private Gender gender;
    private String nationality;
    private String address;
    private LocalDate birthday;
    private Boolean active;
}