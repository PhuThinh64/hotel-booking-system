package com.example.hotel_booking.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for updating employee profile details")
public class EmployeeProfileUpdateRequest {

    @Schema(description = "Full name", example = "Nguyen Van B")
    private String fullName;

    @Schema(description = "Contact phone number", example = "0987654321")
    private String phone;

    @Schema(description = "Work email address", example = "receptionist01@hotel.com")
    private String email;
}