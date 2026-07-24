package com.example.hotel_booking.bookingservicedetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request body for adding an extra service to a booking")
public class BookingServiceCreateRequest {

    @Schema(description = "Target booking ID", example = "100")
    @NotNull(message = "BOOKING_ID_REQUIRED")
    private Long bookingId;

    @Schema(description = "ID of the extra service to order", example = "1")
    @NotNull(message = "SERVICE_ID_REQUIRED")
    private Long serviceId;

    @Schema(description = "Quantity of the service ordered", example = "2")
    @NotNull(message = "QUANTITY_REQUIRED")
    @Min(value = 1, message = "QUANTITY_MIN_1")
    private Integer quantity;
}