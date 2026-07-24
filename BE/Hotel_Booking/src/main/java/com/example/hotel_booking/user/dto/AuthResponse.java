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
@Schema(description = "Authentication response containing JWT token and authenticated user profile details")
public class AuthResponse {

    @Schema(description = "JWT Bearer access token for authorization", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Account username", example = "john_doe")
    private String username;

    @Schema(description = "User role in system", example = "ROLE_USER")
    private String role;

    @Schema(description = "User account ID", example = "10")
    private Long userId;

    @Schema(description = "Associated profile ID (Customer or Employee)", example = "1")
    private Long profileId;

    @Schema(description = "User full name", example = "John Doe")
    private String fullName;

    @Schema(description = "User contact phone number", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "User email address", example = "johndoe@example.com")
    private String email;
}