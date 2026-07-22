package com.example.hotel_booking.payment.service;

import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.payment.dto.PaymentResponse;
import com.example.hotel_booking.payment.dto.PendingRefundResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PaymentService {
    
    List<PaymentResponse> getPaymentsByBookingId(Long bookingId);

    void processRefund(
            Long bookingId,
            BigDecimal refundAmount,
            String reason,
            PaymentMethod method
    );

    BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end);

    void processVnPaySuccess(String txnRef, String transactionNo, String amountStr);
    String handleVnPayCallback(Map<String, String> fields, String secureHash);
    Page<PendingRefundResponse> getPendingRefunds(
            String keyword,
            String status,
            String method,
            String startDate,
            String endDate,
            Pageable pageable
    );
}