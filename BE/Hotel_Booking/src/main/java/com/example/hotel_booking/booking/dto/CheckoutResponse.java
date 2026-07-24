package com.example.hotel_booking.booking.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Checkout result returned after completing the guest checkout process.")
public class CheckoutResponse {

    @Schema(description = "Unique identifier of the booking that was checked out.", example = "25")
    private Long bookingId;

    @Schema(description = "Full name of the guest.", example = "Nguyen Van A")
    private String customerName;

    @Schema(description = "Full name of the contact person.", example = "Tran Thi B")
    private String contactName;

    @Schema(description = "Phone number of the contact person.", example = "0987654321")
    private String contactPhone;

    @Schema(description = "Actual check-in date and time recorded for the stay.", example = "2026-08-15T14:25:00")
    private LocalDateTime checkInDate;

    @Schema(description = "Actual check-out date and time recorded for the stay.", example = "2026-08-17T12:10:00")
    private LocalDateTime checkOutDate;

    @Schema(description = "Total charge for all booked rooms based on actual stay duration.", example = "2000000")
    private BigDecimal roomAmount;

    @Schema(description = "Total charge for all additional services consumed during the stay.", example = "300000")
    private BigDecimal serviceAmount;

    @Schema(description = "Amount refunded to the guest if the deposit exceeded the total charge.", example = "0")
    private BigDecimal refundAmount;

    @Schema(description = "Remaining balance paid by the guest at checkout.", example = "1450000")
    private BigDecimal remainingAmount;

    @Schema(description = "Deposit amount originally paid by the guest.", example = "1000000")
    private BigDecimal depositAmount;

    @Schema(description = "Additional surcharge applied for late check-out or other policy violations.", example = "150000")
    private BigDecimal surchargeAmount;

    @Schema(description = "Grand total including room charges, service charges, and surcharges.", example = "2450000")
    private BigDecimal totalAmount;

    @Schema(description = "Payment gateway URL for online payment. Populated when payment is processed via VNPay.", example = "https://sandbox.vnpayment.vn/paymentv2/...")
    private String paymentUrl;
}