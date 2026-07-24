package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for user password change")
public class ChangePasswordRequest {

    @Schema(description = "Current account password", example = "OldPassword123!")
    private String oldPassword;

    @Schema(description = "New account password to be set", example = "NewSecretPassword123!")
    private String newPassword;
}