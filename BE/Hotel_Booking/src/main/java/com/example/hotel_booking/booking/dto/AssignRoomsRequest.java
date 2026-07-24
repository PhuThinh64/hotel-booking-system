package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Request payload for assigning physical rooms to a booking.")
public class AssignRoomsRequest {

    @Schema(
            description = "List of room assignment entries mapping booking room slots to physical rooms."
    )
    private List<AssignRoomItemRequest> roomAssignments;
}
