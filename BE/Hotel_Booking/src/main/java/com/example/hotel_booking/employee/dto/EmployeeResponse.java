package com.example.hotel_booking.employee.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee response data details")
public class EmployeeResponse {

    @Schema(description = "Unique identifier of the employee", example = "1")
    private Long id;

    @Schema(description = "User account ID linked to the employee", example = "10")
    private Long userId;

    @Schema(description = "Account username", example = "receptionist_01")
    private String username;

    @Schema(description = "Full name of the employee", example = "Nguyen Van B")
    private String fullName;

    @Schema(description = "Phone number of the employee", example = "0987654321")
    private String phoneNumber;

    @Schema(description = "Work email address", example = "receptionist01@hotel.com")
    private String email;

    @Schema(description = "Active status of the employee account", example = "true")
    private Boolean active;

    @Schema(description = "Assigned role name", example = "RECEPTIONIST")
    private String roleName;

    @Schema(description = "Timestamp when the employee profile was created", example = "2026-07-24T10:00:00")
    private LocalDateTime createdAt;
}