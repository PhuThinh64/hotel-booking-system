package com.example.hotel_booking.bookingservicedetail.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingServiceCreateRequest {
    @NotNull(message = "BOOKING_ID_REQUIRED")
    private Long bookingId;

    @NotNull(message = "SERVICE_ID_REQUIRED")
    private Long serviceId;

    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    private Integer quantity;
}