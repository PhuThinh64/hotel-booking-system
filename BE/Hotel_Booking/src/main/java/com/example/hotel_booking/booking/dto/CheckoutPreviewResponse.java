package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "Preview of the charges and refunds calculated before completing checkout.")
public class CheckoutPreviewResponse {

    @Schema(description = "Unique identifier of the booking being checked out.", example = "25")
    private Long bookingId;

    @Schema(description = "Total charge for all booked rooms based on actual stay duration.", example = "2000000")
    private BigDecimal roomAmount;

    @Schema(description = "Total charge for all additional services consumed during the stay.", example = "300000")
    private BigDecimal serviceAmount;

    @Schema(description = "Additional surcharge applied for late check-out or other policy violations.", example = "150000")
    private BigDecimal surchargeAmount;

    @Schema(description = "Deposit amount already paid by the guest.", example = "1000000")
    private BigDecimal depositAmount;

    @Schema(description = "Remaining balance the guest must pay at checkout.", example = "1450000")
    private BigDecimal remainingAmount;

    @Schema(description = "Amount to be refunded to the guest if the deposit exceeds the total charge.", example = "0")
    private BigDecimal refundAmount;

    @Schema(description = "Grand total including room charges, service charges, and surcharges.", example = "2450000")
    private BigDecimal totalAmount;

    @Schema(description = "Indicates whether the original deposit was paid via VNPay. Used to determine refund channel.", example = "true")
    private Boolean originalVnpay;
}