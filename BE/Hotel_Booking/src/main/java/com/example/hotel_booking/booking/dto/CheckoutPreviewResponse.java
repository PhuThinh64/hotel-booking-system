package com.example.hotel_booking.booking.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutPreviewResponse {

    private Long bookingId;

    private BigDecimal roomAmount;

    private BigDecimal serviceAmount;

    private BigDecimal surchargeAmount;

    private BigDecimal depositAmount;

    private BigDecimal remainingAmount;

    private BigDecimal refundAmount;

    private BigDecimal totalAmount;

    private Boolean originalVnpay;

}