package com.example.hotel_booking.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type id is required")
    private Long roomTypeId;

    @NotNull(message = "Floor is required")
    private Integer floor;
}