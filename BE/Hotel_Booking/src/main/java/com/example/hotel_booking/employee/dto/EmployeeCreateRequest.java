package com.example.hotel_booking.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request body for creating a new employee profile and account")
public class EmployeeCreateRequest {

    @Schema(description = "Account username for login", example = "receptionist_01")
    @NotBlank(message = "USERNAME_REQUIRED")
    private String username;

    @Schema(description = "Account initial password", example = "Password123!")
    @NotBlank(message = "PASSWORD_REQUIRED")
    private String password;

    @Schema(description = "Role identifier for the employee", example = "2")
    private Long roleId;

    @Schema(description = "Full name of the employee", example = "Nguyen Van B")
    @NotBlank(message = "FULL_NAME_REQUIRED")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "NAME_INVALID")
    private String fullName;

    @Schema(description = "Phone number of the employee (10-11 digits)", example = "0987654321")
    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "PHONE_INVALID")
    private String phoneNumber;

    @Schema(description = "Work email address", example = "receptionist01@hotel.com")
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;
}