package com.example.hotel_booking.booking.dto;

import lombok.Data;

@Data
public class AssignRoomItemRequest {
    private Long bookingRoomId; 
    private Long roomId;        
}
