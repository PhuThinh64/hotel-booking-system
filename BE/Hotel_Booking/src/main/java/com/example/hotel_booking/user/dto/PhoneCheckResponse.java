package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Response object for checking phone number status in system")
public class PhoneCheckResponse {

    @Schema(
            description = "Indicates whether the phone number is already registered with a User account",
            example = "false"
    )
    private boolean existsInUser;

    @Schema(
            description = "Indicates whether the phone number exists in Customer records (e.g. walk-in customer history)",
            example = "true"
    )
    private boolean existsInCustomer;

    @Schema(
            description = "Existing full name associated with the phone number if found in Customer records, null otherwise",
            example = "Nguyen Van A"
    )
    private String fullName;
}