package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Schema(description = "Detailed profile response for customer users")
public class CustomerProfileResponse extends BaseProfileResponse {

    @Schema(description = "Residential address", example = "123 Nguyen Hue, District 1, HCMC")
    private String address;

    @Schema(description = "Identity card or Citizen ID number", example = "001200012345")
    private String identityCard;

    @Schema(description = "Gender", example = "MALE")
    private String gender;

    @Schema(description = "Nationality", example = "Vietnam")
    private String nationality;

    @Schema(description = "Date of birth", example = "1995-05-15")
    private LocalDate birthday;
}