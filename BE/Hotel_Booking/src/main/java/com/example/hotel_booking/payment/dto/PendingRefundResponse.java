package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
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
@Schema(description = "Details of a pending refund transaction.")
public class PendingRefundResponse {

    @Schema(description = "Unique payment identifier.", example = "10")
    private Long paymentId;

    @Schema(description = "Unique identifier of the associated booking.", example = "25")
    private Long bookingId;

    @Schema(description = "Full name of the contact person.", example = "Tran Thi B")
    private String contactName;

    @Schema(description = "Phone number of the contact person.", example = "0987654321")
    private String contactPhone;

    @Schema(description = "Amount to be refunded.", example = "500000")
    private BigDecimal amount;

    @Schema(description = "Refund payment method.", example = "CASH")
    private PaymentMethod method;

    @Schema(description = "Current status of the refund transaction.", example = "PENDING")
    private PaymentStatus status;

    @Schema(description = "Date and time the refund record was created.", example = "2026-07-22T10:00:00")
    private LocalDateTime createdAt;
}