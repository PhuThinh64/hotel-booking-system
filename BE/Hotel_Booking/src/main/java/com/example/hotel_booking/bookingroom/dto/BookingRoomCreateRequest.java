package com.example.hotel_booking.bookingroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for adding a room item to an existing booking")
public class BookingRoomCreateRequest {

    @Schema(description = "ID of the target booking", example = "100")
    @NotNull(message = "BOOKING_ID_REQUIRED")
    private Long bookingId;

    @Schema(description = "ID of the requested room type", example = "2")
    @NotNull(message = "ROOM_TYPE_ID_REQUIRED")
    private Long roomTypeId;
}