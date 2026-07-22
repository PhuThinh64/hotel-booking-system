package com.example.hotel_booking.room.dto;

import com.example.hotel_booking.common.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoomRequest {

    @NotBlank(message = "Room number is required")
    @Size(max = 20, message = "Số phòng không được vượt quá 10 ký tự")
    private String roomNumber;

    @NotNull(message = "Room type id is required")
    private Long roomTypeId;

    @NotNull(message = "Floor is required")
    private Integer floor;

    private RoomStatus status;
}