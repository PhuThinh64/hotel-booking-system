package com.example.hotel_booking.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAdminUpdateRequest {
    @NotBlank(message = "FULL_NAME_REQUIRED")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "NAME_INVALID")
    private String fullName;

    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "PHONE_INVALID")
    private String phoneNumber;

    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    private Long roleId;
}