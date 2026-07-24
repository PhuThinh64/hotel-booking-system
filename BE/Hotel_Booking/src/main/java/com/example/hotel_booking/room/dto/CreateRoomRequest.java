package com.example.hotel_booking.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for creating a new room.")
public class CreateRoomRequest {

    @Schema(
            description = "Unique room identifier code/number.",
            example = "101"
    )
    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @Schema(
            description = "Unique identifier of the associated room type.",
            example = "3"
    )
    @NotNull(message = "Room type id is required")
    private Long roomTypeId;

    @Schema(
            description = "Floor number where the room is located.",
            example = "1"
    )
    @NotNull(message = "Floor is required")
    private Integer floor;
}