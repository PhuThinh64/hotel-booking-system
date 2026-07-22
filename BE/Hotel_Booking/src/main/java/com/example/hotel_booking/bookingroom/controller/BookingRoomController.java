package com.example.hotel_booking.bookingroom.controller;

import com.example.hotel_booking.bookingroom.dto.BookingRoomCreateRequest;
import com.example.hotel_booking.bookingroom.dto.BookingRoomResponse;
import com.example.hotel_booking.bookingroom.service.BookingRoomService;
import com.example.hotel_booking.common.exception.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/booking-rooms")
@RequiredArgsConstructor
public class BookingRoomController {

    private final BookingRoomService bookingRoomService;

    @PostMapping("/add")
    public ApiResponse<BookingRoomResponse> addRoom(@RequestBody @Valid BookingRoomCreateRequest request) {
        return ApiResponse.<BookingRoomResponse>builder()
                .result(bookingRoomService.addRoom(request))
                .build();
    }

    @PutMapping("/{id}/assign")
    public ApiResponse<BookingRoomResponse> assignRoom(
            @PathVariable Long id,
            @RequestParam Long roomId) {
        return ApiResponse.<BookingRoomResponse>builder()
                .result(bookingRoomService.assignRoom(id, roomId))
                .build();
    }

    
    @PutMapping("/{id}/change-type")
    public ApiResponse<BookingRoomResponse> changeRoomType(
            @PathVariable Long id,
            @RequestParam Long roomTypeId) {
        return ApiResponse.<BookingRoomResponse>builder()
                .result(bookingRoomService.changeRoomType(id, roomTypeId))
                .build();
    }


}