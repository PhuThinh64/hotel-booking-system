package com.example.hotel_booking.booking.controller;

import com.example.hotel_booking.booking.dto.*;
import com.example.hotel_booking.booking.service.BookingService;
import com.example.hotel_booking.common.BookingStatus;
import com.example.hotel_booking.common.PaymentMethod;
import com.example.hotel_booking.common.exception.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
@Tag(name = "Booking Management", description = "APIs dành cho quản lý và điều phối các đơn đặt phòng (Booking) khách sạn")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<List<BookingResponse>> getMyHistory() {
        return ApiResponse.<List<BookingResponse>>builder()
                .result(bookingService.getMyHistory())
                .build();
    }

    @GetMapping("/lookup")
    public ApiResponse<BookingResponse> lookupBooking(
            @RequestParam String phone,
            @RequestParam String bookingCode
    ) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingByPhoneAndCode(phone, bookingCode))
                .build();
    }

    @PostMapping
    @Operation(
            summary = "Tạo đơn đặt phòng mới (Đảm bảo an toàn Concurrency)",
            description = "Thực hiện tạo một đơn đặt phòng mới cho khách hàng...",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "201",
                            description = "Đơn đặt phòng được tạo thành công",
                            content = @Content(schema = @Schema(implementation = BookingResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Tham số đầu vào không hợp lệ...",
                            content = @Content(schema = @Schema(implementation = com.example.hotel_booking.common.exception.ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Chưa xác thực...",
                            content = @Content(schema = @Schema(implementation = com.example.hotel_booking.common.exception.ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403",
                            description = "Không có quyền truy cập...",
                            content = @Content(schema = @Schema(implementation = com.example.hotel_booking.common.exception.ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "409",
                            description = "Xung đột dữ liệu...",
                            content = @Content(schema = @Schema(implementation = com.example.hotel_booking.common.exception.ApiResponse.class))
                    )
            }
    )
    public ApiResponse<BookingResponse> createBooking(@RequestBody @Valid BookingCreateRequest request) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.createBooking(request))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<BookingResponse>> getAllBookings(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            Pageable pageable) {

        return ApiResponse.<Page<BookingResponse>>builder()
                .result(bookingService.getAllBookings(code, status, start, end, pageable))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingResponse> getBookingById(@PathVariable Long id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.getBookingById(id))
                .build();
    }

    @PostMapping("/{id}/cancel-room/{bookingRoomId}")
    public ApiResponse<BookingCancelResponse> cancelSingleRoom(
            @PathVariable Long id,
            @PathVariable Long bookingRoomId,
            @RequestParam(required = false) String refundMethod,
            @RequestParam(required = false) String reason
    ) {
        return ApiResponse.<BookingCancelResponse>builder()
                .result(bookingService.cancelSingleRoom(id, bookingRoomId, refundMethod, reason))
                .build();
    }

    
    @PostMapping("/{id}/cancel-full")
    public ApiResponse<BookingCancelResponse> cancelFullBooking(
            @PathVariable Long id,
            @RequestParam(required = false) PaymentMethod refundMethod,
            @RequestParam(required = false) String reason
    ) {
        return ApiResponse.<BookingCancelResponse>builder()
                .result(bookingService.cancelFullBooking(id, refundMethod, reason))
                .build();
    }

    
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @PostMapping("/{id}/approve-manual-refund")
    public ApiResponse<String> approveManualRefund(
            @PathVariable Long id,
            @RequestParam PaymentMethod refundMethod
    ) {
        bookingService.approveManualRefund(id, refundMethod);

        return ApiResponse.<String>builder()
                .result("Hoàn tiền thành công")
                .build();
    }

    @PostMapping("/{bookingId}/confirm-deposit")
    public ApiResponse<BookingResponse> confirmDeposit(
            @PathVariable Long bookingId,
            @RequestParam PaymentMethod method,
            @RequestParam(required = false) String transactionId) {

        
        
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.confirmDeposit(bookingId, method, transactionId))
                .build();
    }

    @PostMapping("/{id}/check-in")
    public ApiResponse<BookingResponse> checkIn(@PathVariable Long id) {
        return ApiResponse.<BookingResponse>builder()
                .result(bookingService.checkIn(id))
                .build();
    }

    @GetMapping("/{id}/checkout-preview")
    public ApiResponse<CheckoutPreviewResponse> previewCheckout(
            @PathVariable Long id
    ) {
        return ApiResponse.<CheckoutPreviewResponse>builder()
                .result(bookingService.previewCheckout(id))
                .build();
    }

    @PostMapping("/{id}/check-out")
    public ApiResponse<CheckoutResponse> checkOut(
            @PathVariable Long id, 
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String refundMethod,
            jakarta.servlet.http.HttpServletRequest request) {
        
        String ipAddress = request.getRemoteAddr();
        
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        
        return ApiResponse.<CheckoutResponse>builder()
                .result(bookingService.checkOut(id, paymentMethod, refundMethod,ipAddress))
                .build();
    }
















}