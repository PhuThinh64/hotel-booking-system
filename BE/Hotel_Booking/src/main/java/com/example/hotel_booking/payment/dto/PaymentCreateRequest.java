package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new payment transaction.")
public class PaymentCreateRequest {

    @Schema(
            description = "Unique identifier of the booking associated with the payment.",
            example = "25"
    )
    @NotNull(message = "Booking ID không được để trống")
    private Long bookingId;

    @Schema(
            description = "Payment amount transaction value.",
            example = "1000000"
    )
    @Positive(message = "Số tiền phải lớn hơn 0")
    private BigDecimal amount;

    @Schema(
            description = "Type of payment transaction.",
            example = "DEPOSIT"
    )
    private PaymentType paymentType;

    @Schema(
            description = "Payment method used for the transaction.",
            example = "VNPAY"
    )
    private PaymentMethod method;

    @Schema(
            description = "Initial status of the payment transaction.",
            example = "PENDING"
    )
    private PaymentStatus status;
}