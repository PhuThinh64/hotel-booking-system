package com.example.hotel_booking.booking.dto;

import lombok.Data;
import java.util.List;

@Data
public class AssignRoomsRequest {
    private List<AssignRoomItemRequest> roomAssignments;
}
