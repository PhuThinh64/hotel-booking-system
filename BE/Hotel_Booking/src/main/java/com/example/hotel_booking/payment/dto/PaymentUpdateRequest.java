package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentStatus;
import lombok.Data;

@Data
public class PaymentUpdateRequest {
    
    private PaymentStatus status;
    private String transactionId;
}