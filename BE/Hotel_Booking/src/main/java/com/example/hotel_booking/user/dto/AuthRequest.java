package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for user authentication login")
public class AuthRequest {

    @Schema(description = "Account username or phone number", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "Account secret password", example = "SecretPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}