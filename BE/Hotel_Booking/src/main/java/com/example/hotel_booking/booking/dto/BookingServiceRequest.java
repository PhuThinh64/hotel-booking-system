package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "An additional service requested as part of a booking.")
public class BookingServiceRequest {

    @Schema(
            description = "Unique identifier of the service.",
            example = "5"
    )
    private Long serviceId;

    @Schema(
            description = "Number of units of the service requested.",
            example = "2"
    )
    private Integer quantity;
}