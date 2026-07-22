package com.example.hotel_booking.booking.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingUpdateRequest {
    
    private String status;
    private BigDecimal depositAmount;
}