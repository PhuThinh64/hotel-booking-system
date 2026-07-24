package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "A single room type entry within a booking request.")
public class BookingRoomRequest {

    @Schema(
            description = "Unique identifier of the room type to be booked.",
            example = "3"
    )
    @NotNull(message = "ROOM_TYPE_REQUIRED")
    private Long roomTypeId;

    @Schema(
            description = "Number of rooms of this type to book.",
            example = "2"
    )
    @Min(value = 1, message = "INVALID_QUANTITY")
    private int quantity;
}