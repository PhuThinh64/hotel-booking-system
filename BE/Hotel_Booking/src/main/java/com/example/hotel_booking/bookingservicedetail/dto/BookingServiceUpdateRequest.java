package com.example.hotel_booking.bookingservicedetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for updating the quantity of an ordered extra service")
public class BookingServiceUpdateRequest {

    @Schema(description = "New quantity for the ordered service", example = "3")
    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    private Integer quantity;
}