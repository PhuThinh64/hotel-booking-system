package com.example.hotel_booking.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
    @NotBlank(message = "username không được để trống")
    private String username;
}