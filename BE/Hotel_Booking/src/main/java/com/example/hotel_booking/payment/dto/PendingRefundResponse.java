package com.example.hotel_booking.payment.dto;

import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.PaymentStatus;
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
public class PendingRefundResponse {
    private Long paymentId;      
    private Long bookingId;      
    private String contactName;  
    private String contactPhone;        
    private BigDecimal amount;   
    private PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime createdAt; 
}