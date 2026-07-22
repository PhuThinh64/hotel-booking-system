package com.example.hotel_booking.bookingservicedetail.controller;

import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceCreateRequest;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceDetailResponse;
import com.example.hotel_booking.bookingservicedetail.dto.BookingServiceUpdateRequest;
import com.example.hotel_booking.bookingservicedetail.service.BookingServiceDetailService;
import com.example.hotel_booking.common.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking-service-details")
@RequiredArgsConstructor
public class BookingServiceDetailController {

    private final BookingServiceDetailService serviceDetailService;

    @PostMapping
    public ApiResponse<BookingServiceDetailResponse> add(@RequestBody @Valid BookingServiceCreateRequest request) {
        return ApiResponse.<BookingServiceDetailResponse>builder()
                .result(serviceDetailService.addService(request))
                .build();
    }

    @GetMapping("/booking/{bookingId}")
    public ApiResponse<List<BookingServiceDetailResponse>> getByBooking(@PathVariable Long bookingId) {
        return ApiResponse.<List<BookingServiceDetailResponse>>builder()
                .result(serviceDetailService.getByBookingId(bookingId))
                .build();
    }

    @PatchMapping("/cancel/{id}")
    public ApiResponse<BookingServiceDetailResponse> cancel(@PathVariable Long id) {
        return ApiResponse.<BookingServiceDetailResponse>builder()
                .result(serviceDetailService.cancelService(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<BookingServiceDetailResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid BookingServiceUpdateRequest request) {
        return ApiResponse.<BookingServiceDetailResponse>builder()
                .result(serviceDetailService.updateService(id, request))
                .build();
    }
}