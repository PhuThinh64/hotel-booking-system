package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for requesting a password reset email")
public class ForgotPasswordRequest {

    @Schema(description = "Username of the account requesting password reset", example = "john_doe")
    @NotBlank(message = "username không được để trống")
    private String username;
}