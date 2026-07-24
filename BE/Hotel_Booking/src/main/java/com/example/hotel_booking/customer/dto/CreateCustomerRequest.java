package com.example.hotel_booking.customer.dto;

import com.example.hotel_booking.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request body for creating a new customer profile")
public class CreateCustomerRequest {

    @Schema(description = "Full name of the customer", example = "Nguyen Van A")
    @NotBlank(message = "FULL_NAME_REQUIRED")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "NAME_INVALID")
    private String fullName;

    @Schema(description = "Phone number of the customer (10-11 digits)", example = "0912345678")
    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "PHONE_INVALID")
    private String phoneNumber;

    @Schema(description = "Identity card or Citizen ID number", example = "001200012345")
    private String identityCard;

    @Schema(description = "Email address of the customer", example = "nguyenvana@example.com")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @Schema(description = "Gender of the customer", example = "MALE")
    private Gender gender;

    @Schema(description = "Nationality of the customer", example = "Vietnam")
    private String nationality;

    @Schema(description = "Residential address", example = "123 Nguyen Hue, District 1, HCMC")
    private String address;

    @Schema(description = "Date of birth", example = "1995-05-15")
    private LocalDate birthday;
}