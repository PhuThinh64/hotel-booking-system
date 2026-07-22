package com.example.hotel_booking.payment.controller;

import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.service.VnPayService;
import com.example.hotel_booking.payment.dto.PaymentResponse;
import com.example.hotel_booking.payment.dto.PendingRefundResponse;
import com.example.hotel_booking.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final BookingService bookingService;
    private final VnPayService vnPayService;

    @GetMapping("/vnpay-callback")
    public void vnpayCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            fields.put(fieldName, request.getParameter(fieldName));
        }

        
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        
        String resultRedirect = paymentService.handleVnPayCallback(fields, vnp_SecureHash);

        
        response.sendRedirect(resultRedirect);
    }

    @GetMapping("/booking/{bookingId}")
    public ApiResponse<List<PaymentResponse>> getByBooking(@PathVariable Long bookingId) {
        return ApiResponse.<List<PaymentResponse>>builder()
                .result(paymentService.getPaymentsByBookingId(bookingId))
                .build();
    }

    @GetMapping("/revenue")
    public ApiResponse<BigDecimal> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ApiResponse.<BigDecimal>builder()
                .result(paymentService.getTotalRevenue(start, end))
                .build();
    }

    @GetMapping("/pending-refunds")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ApiResponse<Page<PendingRefundResponse>> getPendingRefunds(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ApiResponse.<Page<PendingRefundResponse>>builder()
                .result(paymentService.getPendingRefunds(keyword, status, method, startDate, endDate, pageable))
                .build();
    }
}