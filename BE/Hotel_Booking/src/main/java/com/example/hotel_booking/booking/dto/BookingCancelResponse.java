package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Result of a booking or room cancellation operation.")
public class BookingCancelResponse {

    @Schema(description = "Booking status after the cancellation.", example = "CANCELLED")
    private String status;

    @Schema(description = "Amount refunded to the guest as a result of the cancellation.", example = "800000")
    private BigDecimal refundedAmount;

    @Schema(description = "Remaining cost still owed by the guest after the cancellation, if any.", example = "0")
    private BigDecimal remainingCost;

    @Schema(description = "Human-readable message describing the cancellation outcome.", example = "Booking cancelled successfully.")
    private String message;
}
