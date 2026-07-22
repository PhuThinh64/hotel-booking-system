package com.example.hotel_booking.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private Long userId;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Boolean active;
    private String roleName;
    private LocalDateTime createdAt;
}