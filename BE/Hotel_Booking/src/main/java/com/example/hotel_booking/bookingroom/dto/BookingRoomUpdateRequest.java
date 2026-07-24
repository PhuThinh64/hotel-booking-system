package com.example.hotel_booking.bookingroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request body for updating booking room details")
public class BookingRoomUpdateRequest {

    @Schema(description = "ID of the physical room to assign", example = "101")
    private Long roomId;

    @Schema(description = "ID of the new room type", example = "2")
    private Long roomTypeId;
}