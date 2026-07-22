package com.example.hotel_booking.booking.service;

import com.example.hotel_booking.booking.dto.*;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface BookingService {

    BookingResponse createBooking(BookingCreateRequest request);
    Page<BookingResponse> getAllBookings(String code, BookingStatus status, LocalDateTime start, LocalDateTime end, Pageable pageable);
    BookingResponse getBookingById(Long id);
    BookingResponse confirmDeposit(Long bookingId, PaymentMethod method, String txnId);
    BookingResponse checkIn(Long bookingId);
    CheckoutResponse checkOut(Long bookingId, String paymentMethod, String refundMethod, String ipAddress);
    void finalizeCheckoutVnPay(Long bookingId, String transactionNo, String amount);

    BookingResponse processVnPayCallback(Map<String, String> vnpParams);
    BookingResponse getBookingByPhoneAndCode(String phone, String bookingCode);
    List<BookingResponse> getMyHistory();





    CheckoutPreviewResponse previewCheckout(Long bookingId);

    BookingCancelResponse cancelSingleRoom(Long bookingId, Long bookingRoomId, String refundMethod, String reason);
    BookingCancelResponse cancelFullBooking(Long bookingId, PaymentMethod refundMethod, String reason);
    void approveManualRefund(Long bookingId, PaymentMethod refundMethod );

}
