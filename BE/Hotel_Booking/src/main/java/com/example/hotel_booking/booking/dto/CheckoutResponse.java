package com.example.hotel_booking.booking.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CheckoutResponse {
    private Long bookingId;
    private String customerName;
    private String contactName;
    private String contactPhone;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private BigDecimal roomAmount;
    private BigDecimal serviceAmount;
    private BigDecimal refundAmount;
    private BigDecimal remainingAmount;
    private BigDecimal depositAmount;
    private BigDecimal surchargeAmount;
    private BigDecimal totalAmount; 
    private String paymentUrl; 
}