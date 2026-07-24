package com.example.hotel_booking.bookingservicedetail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload containing details of an extra service ordered within a booking")
public class BookingServiceDetailResponse {

    @Schema(description = "Unique identifier of the booking service detail record", example = "15")
    private Long id;

    @Schema(description = "Associated booking ID", example = "100")
    private Long bookingId;

    @Schema(description = "Name of the ordered extra service", example = "Laundry Service")
    private String serviceName;

    @Schema(description = "Type category of the service", example = "REGULAR")
    private String serviceType;

    @Schema(description = "Quantity ordered", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price recorded at the time of ordering", example = "150000.00")
    private BigDecimal priceAtOrder;

    @Schema(description = "Total calculated price (quantity * priceAtOrder)", example = "300000.00")
    private BigDecimal totalPrice;

    @Schema(description = "Timestamp when the service was ordered", example = "2026-07-24T14:30:00")
    private LocalDateTime orderDate;

    @Schema(description = "Current status of the ordered service", example = "ACTIVE")
    private String status;
}