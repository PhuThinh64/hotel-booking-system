package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
import com.example.hotel_booking.common.PaymentType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private String customerName;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentMethod method;
    private String transactionId;
    private PaymentStatus status;
    private LocalDateTime paidAt;
}