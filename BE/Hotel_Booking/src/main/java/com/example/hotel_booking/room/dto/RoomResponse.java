package com.example.hotel_booking.room.dto;

import com.example.hotel_booking.common.RoomStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Schema(description = "Details of a room returned after creation or retrieval.")
public class RoomResponse {

    @Schema(description = "Unique identifier of the room.", example = "5")
    private Long id;

    @Schema(description = "Unique room identifier code/number.", example = "101")
    private String roomNumber;

    @Schema(description = "URL pointing to the image representation of the room.", example = "http://example.com/images/room101.jpg")
    private String imageUrl;

    @Schema(description = "Current status of the room.", example = "AVAILABLE")
    private RoomStatus status;

    @Schema(description = "Name/type of the room.", example = "DELUXE")
    private String roomType;

    @Schema(description = "Price per night for booking the room.", example = "1200000")
    private BigDecimal price;

    @Schema(description = "Floor number where the room is located.", example = "1")
    private Integer floor;
}