package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Request payload for updating an existing payment transaction.")
public class PaymentUpdateRequest {

    @Schema(
            description = "Updated status of the payment transaction.",
            example = "SUCCESS"
    )
    private PaymentStatus status;

    @Schema(
            description = "Updated transaction identifier from the payment gateway.",
            example = "VNP202607220001"
    )
    private String transactionId;
}