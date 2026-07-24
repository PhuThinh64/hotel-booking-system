package com.example.hotel_booking.customer.dto;

import com.example.hotel_booking.common.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer response data details")
public class CustomerResponse {

    @Schema(description = "Unique identifier of the customer", example = "1")
    private Long id;

    @Schema(description = "Full name of the customer", example = "Nguyen Van A")
    private String fullName;

    @Schema(description = "Phone number of the customer", example = "0912345678")
    private String phoneNumber;

    @Schema(description = "Identity card or Citizen ID number", example = "001200012345")
    private String identityCard;

    @Schema(description = "Email address of the customer", example = "nguyenvana@example.com")
    private String email;

    @Schema(description = "Gender of the customer", example = "MALE")
    private Gender gender;

    @Schema(description = "Nationality of the customer", example = "Vietnam")
    private String nationality;

    @Schema(description = "Residential address", example = "123 Nguyen Hue, District 1, HCMC")
    private String address;

    @Schema(description = "Date of birth", example = "1995-05-15")
    private LocalDate birthday;

    @Schema(description = "Active status of customer account", example = "true")
    private Boolean active;
}