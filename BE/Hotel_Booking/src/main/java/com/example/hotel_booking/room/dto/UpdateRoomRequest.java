package com.example.hotel_booking.room.dto;

import com.example.hotel_booking.common.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for updating room details.")
public class UpdateRoomRequest {

    @Schema(
            description = "Unique room identifier code/number.",
            example = "101"
    )
    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Số phòng không được vượt quá 10 ký tự")
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

    @Schema(
            description = "Current status of the room.",
            example = "AVAILABLE"
    )
    private RoomStatus status;
}