package com.example.hotel_booking.bookingroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingRoomCreateRequest {
    @NotNull(message = "BOOKING_ID_REQUIRED")
    private Long bookingId;

    @NotNull(message = "ROOM_TYPE_ID_REQUIRED")
    private Long roomTypeId;

}