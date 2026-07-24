package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Details of a payment transaction returned after creation or retrieval.")
public class PaymentResponse {

    @Schema(description = "Unique payment identifier.", example = "10")
    private Long id;

    @Schema(description = "Unique identifier of the associated booking.", example = "25")
    private Long bookingId;

    @Schema(description = "Full name of the associated customer.", example = "Nguyen Van A")
    private String customerName;

    @Schema(description = "Payment transaction amount.", example = "1000000")
    private BigDecimal amount;

    @Schema(description = "Type of the payment transaction.", example = "DEPOSIT")
    private PaymentType paymentType;

    @Schema(description = "Payment method used for the transaction.", example = "VNPAY")
    private PaymentMethod method;

    @Schema(description = "Transaction identifier returned by the payment gateway.", example = "VNP202607220001")
    private String transactionId;

    @Schema(description = "Current status of the payment transaction.", example = "SUCCESS")
    private PaymentStatus status;

    @Schema(description = "Date and time the payment was completed.", example = "2026-07-22T10:05:00")
    private LocalDateTime paidAt;
}