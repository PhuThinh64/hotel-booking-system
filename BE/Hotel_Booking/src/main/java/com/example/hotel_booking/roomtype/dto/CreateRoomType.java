package com.example.hotel_booking.roomtype.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateRoomType {
    @NotBlank(message = "NAME_REQUIRED")
    private String name;

    @NotNull(message = "PRICE_REQUIRED")
    @Min(value = 0, message = "PRICE_INVALID")
    private BigDecimal price;

    @NotNull(message = "MAX_GUEST_REQUIRED")
    @Min(value = 1, message = "MAX_GUEST_INVALID")
    private Integer maxGuest;

    @NotBlank(message = "DESCRIPTION_REQUIRED")
    private String description;

    @NotBlank(message = "IMAGE_URL_REQUIRED")
    @Pattern(regexp = ".*\\.(jpg|jpeg|png|gif|webp)$", message = "INVALID_IMAGE_FORMAT")
    private String imageUrl;
}
