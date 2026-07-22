package com.example.hotel_booking.user.dto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String phone;
    private String email;
}