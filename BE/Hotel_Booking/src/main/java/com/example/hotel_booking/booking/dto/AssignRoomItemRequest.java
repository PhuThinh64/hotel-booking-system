package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "A single room assignment entry linking a booking room slot to a physical room.")
public class AssignRoomItemRequest {

    @Schema(
            description = "Unique identifier of the booking room slot to be assigned.",
            example = "12"
    )
    private Long bookingRoomId;

    @Schema(
            description = "Unique identifier of the physical room to assign.",
            example = "7"
    )
    private Long roomId;
}
