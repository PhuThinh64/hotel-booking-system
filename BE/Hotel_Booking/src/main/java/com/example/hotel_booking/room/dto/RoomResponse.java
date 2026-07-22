package com.example.hotel_booking.room.dto;

import com.example.hotel_booking.common.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private String imageUrl;
    private RoomStatus status;
    private String roomType;
    private BigDecimal price;
    private Integer floor;


}