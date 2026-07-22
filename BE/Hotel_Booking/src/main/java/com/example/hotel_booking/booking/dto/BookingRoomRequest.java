package com.example.hotel_booking.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRoomRequest {

    @NotNull(message = "ROOM_TYPE_REQUIRED")
    private Long roomTypeId;

    @Min(value = 1, message = "INVALID_QUANTITY")
    private int quantity;
}