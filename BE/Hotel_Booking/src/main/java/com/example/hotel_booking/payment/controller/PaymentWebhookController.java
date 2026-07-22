package com.example.hotel_booking.payment.controller;

import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.exception.ApiResponse;
import com.example.hotel_booking.common.exception.AppException;
import com.example.hotel_booking.common.exception.ErrorCode;
import com.example.hotel_booking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final BookingService bookingService;
    private final PaymentRepository paymentRepo; 

    @PostMapping("/webhook")
    public ApiResponse<String> handleBankWebhook(@RequestBody Map<String, Object> payload) {
        String description = (String) payload.get("description");
        String transactionId = payload.get("transactionId").toString(); 

        
        String txnRef = extractTransactionId(description);

        if (txnRef.isEmpty()) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND); 
        }

        
        return paymentRepo.findByTransactionId(txnRef)
                .map(payment -> {
                    
                    bookingService.confirmDeposit(
                            payment.getBooking().getId(),
                            PaymentMethod.BANK_TRANSFER,
                            txnRef
                    );

                    return ApiResponse.<String>builder()
                            .result("Xác nhận thanh toán thành công cho đơn " + payment.getBooking().getId())
                            .build();
                })
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    
    private String extractTransactionId(String desc) {
        
        Pattern pattern = Pattern.compile("(CASH|DEPO|VNPAY)-\\d+");
        Matcher matcher = pattern.matcher(desc.toUpperCase());
        return matcher.find() ? matcher.group() : "";
    }
}