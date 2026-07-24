package com.example.hotel_booking.roomtype.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request payload for creating a new room type.")
public class CreateRoomType {

    @Schema(
            description = "Name of the room type.",
            example = "DELUXE"
    )
    @NotBlank(message = "NAME_REQUIRED")
    private String name;

    @Schema(
            description = "Price per night for the room type.",
            example = "1200000"
    )
    @NotNull(message = "PRICE_REQUIRED")
    @Min(value = 0, message = "PRICE_INVALID")
    private BigDecimal price;

    @Schema(
            description = "Maximum number of guests allowed in the room type.",
            example = "2"
    )
    @NotNull(message = "MAX_GUEST_REQUIRED")
    @Min(value = 1, message = "MAX_GUEST_INVALID")
    private Integer maxGuest;

    @Schema(
            description = "Detailed description of the room type amenities.",
            example = "A spacious room with a king-size bed, sea view, and free Wi-Fi."
    )
    @NotBlank(message = "DESCRIPTION_REQUIRED")
    private String description;

    @Schema(
            description = "URL pointing to the room type cover image.",
            example = "http://example.com/images/deluxe.jpg"
    )
    @NotBlank(message = "IMAGE_URL_REQUIRED")
    @Pattern(regexp = ".*\\.(jpg|jpeg|png|gif|webp)$", message = "INVALID_IMAGE_FORMAT")
    private String imageUrl;
}
