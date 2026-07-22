package com.example.hotel_booking.roomtype.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class RoomTypeResponse {
    private Long id;

    private String name;

    private BigDecimal price;

    private Integer maxGuest;

    private String description;

    private String imageUrl;

    private long availableCount;
}
