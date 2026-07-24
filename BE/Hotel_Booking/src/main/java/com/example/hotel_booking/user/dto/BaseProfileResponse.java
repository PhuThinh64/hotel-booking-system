package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Base profile response data shared across user types")
public abstract class BaseProfileResponse {

    @Schema(description = "Unique identifier", example = "1")
    private Long id;

    @Schema(description = "Account username", example = "john_doe")
    private String username;

    @Schema(description = "Full name", example = "John Doe")
    private String fullName;

    @Schema(description = "Contact phone number", example = "0987654321")
    private String phone;

    @Schema(description = "Email address", example = "johndoe@example.com")
    private String email;
}