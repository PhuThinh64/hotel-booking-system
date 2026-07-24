package com.example.hotel_booking.roomtype.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Details of a room type returned after creation or retrieval.")
public class RoomTypeResponse {

    @Schema(description = "Unique room type identifier.", example = "3")
    private Long id;

    @Schema(description = "Name of the room type.", example = "DELUXE")
    private String name;

    @Schema(description = "Price per night for the room type.", example = "1200000")
    private BigDecimal price;

    @Schema(description = "Maximum number of guests allowed in the room type.", example = "2")
    private Integer maxGuest;

    @Schema(description = "Detailed description of the room type amenities.", example = "A spacious room with a king-size bed, sea view, and free Wi-Fi.")
    private String description;

    @Schema(description = "URL pointing to the room type cover image.", example = "http://example.com/images/deluxe.jpg")
    private String imageUrl;

    @Schema(description = "Number of rooms of this type currently available for booking.", example = "8")
    private long availableCount;
}
