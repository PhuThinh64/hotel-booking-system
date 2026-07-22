package com.example.hotel_booking.booking.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCancelResponse {
    private String status;
    private BigDecimal refundedAmount;
    private BigDecimal remainingCost;
    private String message;
}
